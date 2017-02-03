package io.sugo.pio.engine.template.engine;

import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.template.data.TemplateModelData;
import io.sugo.pio.engine.training.Model;

import java.io.*;

/**
 */
public class TemplateModel implements Model<TemplateModelData> {
    private final String filename = "model";

    private Repository repository;

    public TemplateModel(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TemplateModelData templateModelData) {
            OutputStream outputStream = repository.openOutput(filename);
        try {
            ObjectOutputStream objectWriter = new ObjectOutputStream(outputStream);
            objectWriter.writeObject(templateModelData.getModel());
        } catch (IOException e) {

        }
    }

}
