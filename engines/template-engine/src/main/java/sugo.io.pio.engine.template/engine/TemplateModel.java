package sugo.io.pio.engine.template.engine;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.twitter.chill.KryoInstantiator;
import org.apache.spark.ml.recommendation.ALSModel;
import sugo.io.pio.data.output.Repository;
import sugo.io.pio.engine.Model;
import sugo.io.pio.engine.template.data.TemplateModelData;

import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public class TemplateModel implements Model<TemplateModelData> {
    @Override
    public void save(TemplateModelData templateModelData, Repository repository) {
            OutputStream outputStream = repository.openOutput();
            new KryoInstantiator().newKryo().writeObject(new Output(outputStream), templateModelData.getModel());
    }

    @Override
    public TemplateModelData read(Repository repository) {
        InputStream inputStream = repository.openInput();
        return new TemplateModelData(new KryoInstantiator().newKryo().readObject(new Input(inputStream), ALSModel.class));
    }
}
