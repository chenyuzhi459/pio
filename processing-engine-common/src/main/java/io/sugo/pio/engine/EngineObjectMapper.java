package io.sugo.pio.engine;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 */
public class EngineObjectMapper extends ObjectMapper {
    public EngineObjectMapper()
    {
        this((JsonFactory)null);
    }

    public EngineObjectMapper(EngineObjectMapper mapper)
    {
        super(mapper);
    }

    public EngineObjectMapper(JsonFactory factory)
    {
        super(factory);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(MapperFeature.AUTO_DETECT_GETTERS, false);
        configure(MapperFeature.AUTO_DETECT_FIELDS, false);
        configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
        configure(MapperFeature.AUTO_DETECT_SETTERS, false);
        configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS, false);
        configure(SerializationFeature.INDENT_OUTPUT, false);
    }

    @Override
    public ObjectMapper copy()
    {
        return new EngineObjectMapper(this);
    }
}
