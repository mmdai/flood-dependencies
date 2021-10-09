package cn.flood.pulsar.util;

import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.apache.pulsar.client.impl.schema.StringSchema;

public class SchemaUtils {

    private static final String STRING_CLASS_NAME = String.class.getName();

    public static Schema schemaInstance(Class c) {
        if (STRING_CLASS_NAME.equals(c.getName())) {
            return new StringSchema();
        }
        return JSONSchema.of(c);
    }

}
