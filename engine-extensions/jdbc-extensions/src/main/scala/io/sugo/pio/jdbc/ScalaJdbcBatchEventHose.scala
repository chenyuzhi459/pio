package io.sugo.pio.jdbc

import java.sql.{DriverManager, ResultSet}

import io.sugo.pio.engine.data.input.{BatchEventHose, Event}
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import scala.collection.JavaConverters._

/**
  */
class ScalaJdbcBatchEventHose(timeColumn: String, url: String, table: String, username: String, password: String, count: Int, par: Int, pNames: java.util.Set[String]) extends BatchEventHose with Serializable {
  override def find(sc: JavaSparkContext, starttime: Long, endTime: Long): JavaRDD[Event] = {
    find(sc)
  }

  override def find(sc: JavaSparkContext): JavaRDD[Event] = {
    var resRDD :JavaRDD[Event] = null;
    if (0 == url.indexOf("jdbc:postgresql://")){
      resRDD = findPostgres(sc)
    }
    if (0 == url.indexOf("jdbc:mysql://")){
      resRDD = findMysql(sc)
    }
    resRDD
  }

  private def findMysql(sc: JavaSparkContext): JavaRDD[Event] = {
    val sqlRDD = new MysqlPagedJdbcRdd(
      sc,
      () => {
        DriverManager.getConnection(url, username, password)
      },
      s"SELECT * from $table limit ?,?",
      0,
      count - 1,
      par,
      (r: ResultSet) => {
        new Event(System.currentTimeMillis, pNames.asScala.map(name=> (name, r.getObject(name))).toMap.asJava)
      }
    )
    sqlRDD
  }

  private def findPostgres(sc: JavaSparkContext): JavaRDD[Event] = {
    val sqlRDD = new PostgresPagedJdbcRdd(
      sc,
      () => {
        DriverManager.getConnection(url, username, password)
      },
      s"SELECT * from $table limit ? offset ?",
      0,
      count - 1,
      par,
      (r: ResultSet) => {
        new Event(System.currentTimeMillis, pNames.asScala.map(name=> (name, r.getObject(name))).toMap.asJava)
      }
    );
    sqlRDD
  }

  def getTimeColumn = timeColumn

  def getUrl = url

  def getTable = table

  def getUsername = username

  def getPassword = password

  def getCount = count

  def getPar = par

  def getPNames = pNames
}
