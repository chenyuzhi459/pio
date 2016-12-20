package io.sugo.pio.engine.template.engine;

import org.apache.spark.ml.recommendation.ALSModel;
import io.sugo.pio.data.output.Repository;
import io.sugo.pio.engine.Model;
import io.sugo.pio.engine.template.data.TemplateModelData;

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
