package sugo.io.pio.engine.template.engine;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.twitter.chill.KryoInstantiator;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.util.Utils;
import scala.tools.cmd.gen.AnyVals;
import sugo.io.pio.data.output.Repository;
import sugo.io.pio.engine.Model;
import sugo.io.pio.engine.template.data.TemplateModelData;

import java.io.*;

/**
 */
public class TemplateModel implements Model<TemplateModelData> {
    @Override
    public void save(TemplateModelData templateModelData, Repository repository) {
            OutputStream outputStream = repository.openOutput();
        try {
            ObjectOutputStream objectWriter = new ObjectOutputStream(outputStream);
            objectWriter.writeObject(templateModelData.getModel());
        } catch (IOException e) {

        }

    }

    @Override
    public TemplateModelData read(Repository repository) {
        InputStream inputStream = repository.openInput();
        try {
            ObjectInputStream objectReader = new ObjectInputStream(inputStream);
            return new TemplateModelData((ALSModel) objectReader.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
