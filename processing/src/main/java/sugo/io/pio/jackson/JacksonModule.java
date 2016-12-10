package sugo.io.pio.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import sugo.io.pio.guice.LazySingleton;
import sugo.io.pio.guice.annotations.Json;

/**
 */
public class JacksonModule implements Module
{
    @Override
    public void configure(Binder binder)
    {
        binder.bind(ObjectMapper.class).to(Key.get(ObjectMapper.class, Json.class));
    }

    @Provides
    @LazySingleton @Json
    public ObjectMapper jsonMapper()
    {
        return new DefaultObjectMapper();
    }

}
