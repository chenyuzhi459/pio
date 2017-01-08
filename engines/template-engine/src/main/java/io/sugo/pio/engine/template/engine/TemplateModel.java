package io.sugo.pio.engine.template.engine;

import io.sugo.pio.spark.engine.Model;
import io.sugo.pio.spark.engine.data.output.Repository;
import org.apache.spark.ml.recommendation.ALSModel;
import io.sugo.pio.engine.template.data.TemplateModelData;

import java.io.*;

/**
 */
public class TemplateModel implements Model<TemplateModelData, TemplateModelData> {
    private final String filename = "model";

    @Override
    public void save(TemplateModelData templateModelData, Repository repository) {
            OutputStream outputStream = repository.openOutput(filename);
        try {
            ObjectOutputStream objectWriter = new ObjectOutputStream(outputStream);
            objectWriter.writeObject(templateModelData.getModel());
        } catch (IOException e) {

        }
    }

    @Override
    public TemplateModelData load(Repository repository) {
        InputStream inputStream = repository.openInput(filename);
        try {
            ObjectInputStream objectReader = new ObjectInputStream(inputStream);
            return new TemplateModelData((ALSModel) objectReader.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
