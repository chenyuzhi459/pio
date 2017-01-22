package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sugo.pio.guice.ProcessPioModule;
import io.sugo.pio.guice.ProcessingPioModule;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.server.process.OperatorMapHelper;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class OperatorMapHelperTest {
    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper jsonMapper = new DefaultObjectMapper();

        ProcessingPioModule module = new ProcessingPioModule();
        for (Module m : module.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
        ProcessPioModule processPioModule = new ProcessPioModule();
        for (Module m : processPioModule.getJacksonModules()) {
            jsonMapper.registerModule(m);
        }
        System.out.println(
                jsonMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(
                                OperatorMapHelper.getAllOperatorMetas(jsonMapper).values()));
    }

    @Test
    public void testDeSerializable() throws IOException {
        ObjectMapper jsonMapper = new DefaultObjectMapper();
        Person p = new Person();
        p.setName("janpy");
        p.setAge(20);
        String json = jsonMapper.writeValueAsString(p);
        System.out.println(json);
        String json2 = "{\"name\":\"janpy12\",\"age\":20}";
        Person person = jsonMapper.readValue(json2, Person.class);
        System.out.println(person.getName());
        System.out.println(person.getAge());

        DateTime dt = new DateTime("2013-08-31 01:02:33");
        System.out.println(dt);
    }

    public static class Person {

        @JsonProperty
        private String name;
        @JsonProperty
        private int age;

//        @JsonCreator
//        Person() {
//
//        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Test
    public void jsonIgnoreTest() throws Exception {
        TestPOJO testPOJO = new TestPOJO();
        testPOJO.setId(111);
        testPOJO.setName("myName");
        testPOJO.setCount(22);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(testPOJO);
        System.out.println(jsonStr);
//        Assert.assertEquals("{\"id\":111}",jsonStr);

        String jsonStr2 = "{\"id\":111,\"name\":\"myName\",\"count\":22}";
        TestPOJO testPOJO2 = objectMapper.readValue(jsonStr2, TestPOJO.class);
        Assert.assertEquals(111, testPOJO2.getId());
        Assert.assertNull(testPOJO2.getName());
        Assert.assertEquals(0, testPOJO2.getCount());
    }

    public static class TestPOJO {
        private int id;
        private String name;
        private int count;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
