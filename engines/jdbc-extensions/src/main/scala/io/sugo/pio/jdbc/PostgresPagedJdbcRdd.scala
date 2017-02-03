package io.sugo.pio.jdbc

import java.sql.{Connection, ResultSet}

import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, SparkContext, TaskContext}

import scala.reflect.ClassTag

private class PostgresJdbcPartition(idx: Int, val lower: Long, val upper: Long) extends Partition {
  override def index: Int = idx
}

class PostgresPagedJdbcRdd[T: ClassTag](
                            sc: SparkContext,
                            getConnection: () => Connection,
                            sql: String,
                            lowerBound: Long,
                            upperBound: Long,
                            numPartitions: Int,
                            mapRow: (ResultSet) => T = PostgresPagedJdbcRdd.resultSetToObjectArray _)
  extends RDD[T](sc, Nil) {

  @DeveloperApi
  override def compute(thePart: Partition, context: TaskContext): Iterator[T] = new PostgresNextIterator[T]
  {
    context.addTaskCompletionListener{ context => closeIfNeeded() }
    val part = thePart.asInstanceOf[PostgresJdbcPartition]
    val conn = getConnection()
    val stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)

    // setFetchSize(Integer.MIN_VALUE) is a mysql driver specific way to force streaming results,
    // rather than pulling entire resultset into memory.
    // see http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-implementation-notes.html
    if (conn.getMetaData.getURL.matches("jdbc:mysql:.*")) {
      stmt.setFetchSize(Integer.MIN_VALUE)
      logInfo("statement fetch size set to: " + stmt.getFetchSize + " to force MySQL streaming ")
    }

    stmt.setLong(1, part.upper - part.lower + 1 )
    stmt.setLong(2, part.lower)

    val rs = stmt.executeQuery()

    override def getNext(): T = {
      if (rs.next()) {
        mapRow(rs)
      } else {
        finished = true
        null.asInstanceOf[T]
      }
    }

    override def close() {
      try {
        if (null != rs) {
          rs.close()
        }
      } catch {
        case e: Exception => logWarning("Exception closing resultset", e)
      }
      try {
        if (null != stmt) {
          stmt.close()
        }
      } catch {
        case e: Exception => logWarning("Exception closing statement", e)
      }
      try {
        if (null != conn) {
          conn.close()
        }
        logInfo("closed connection")
      } catch {
        case e: Exception => logWarning("Exception closing connection", e)
      }
    }
  }

  override def getPartitions: Array[Partition] = {
    // bounds are inclusive, hence the + 1 here and - 1 on end
    val length = BigInt(1) + upperBound - lowerBound
    (0 until numPartitions).map { i =>
      val start = lowerBound + ((i * length) / numPartitions)
      val end = lowerBound + (((i + 1) * length) / numPartitions) - 1
      new PostgresJdbcPartition(i, start.toLong, end.toLong)
    }.toArray
  }
}

object PostgresPagedJdbcRdd {
  def resultSetToObjectArray(rs: ResultSet): Array[Object] = {
    Array.tabulate[Object](rs.getMetaData.getColumnCount)(i => rs.getObject(i + 1))
  }

  trait ConnectionFactory extends Serializable {
    @throws[Exception]
    def getConnection: Connection
  }
}

abstract class PostgresNextIterator[U] extends Iterator[U] {

  private var gotNext = false
  private var nextValue: U = _
  private var closed = false
  protected var finished = false

  /**
    * Method for subclasses to implement to provide the next element.
    *
    * If no next element is available, the subclass should set `finished`
    * to `true` and may return any value (it will be ignored).
    *
    * This convention is required because `null` may be a valid value,
    * and using `Option` seems like it might create unnecessary Some/None
    * instances, given some iterators might be called in a tight loop.
    *
    * @return U, or set 'finished' when done
    */
  protected def getNext(): U

  /**
    * Method for subclasses to implement when all elements have been successfully
    * iterated, and the iteration is done.
    *
    * <b>Note:</b> `NextIterator` cannot guarantee that `close` will be
    * called because it has no control over what happens when an exception
    * happens in the user code that is calling hasNext/next.
    *
    * Ideally you should have another try/catch, as in HadoopRDD, that
    * ensures any resources are closed should iteration fail.
    */
  protected def close()

  /**
    * Calls the subclass-defined close method, but only once.
    *
    * Usually calling `close` multiple times should be fine, but historically
    * there have been issues with some InputFormats throwing exceptions.
    */
  def closeIfNeeded() {
    if (!closed) {
      // Note: it's important that we set closed = true before calling close(), since setting it
      // afterwards would permit us to call close() multiple times if close() threw an exception.
      closed = true
      close()
    }
  }

  override def hasNext: Boolean = {
    if (!finished) {
      if (!gotNext) {
        nextValue = getNext()
        if (finished) {
          closeIfNeeded()
        }
        gotNext = true
      }
    }
    !finished
  }

  override def next(): U = {
    if (!hasNext) {
      throw new NoSuchElementException("End of stream")
    }
    gotNext = false
    nextValue
  }
}
