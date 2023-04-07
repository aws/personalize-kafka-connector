package com.aws.transfrom;

import com.aws.transform.CombineFieldsToJSONTransformation;
import com.aws.util.TestConstants;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CombineFieldsToJSONTransformationTest {

    @Test
    public void testValidTransformationConfiguration(){
        CombineFieldsToJSONTransformation.Value value = new CombineFieldsToJSONTransformation.Value();
        Assertions.assertDoesNotThrow(() -> value.configure(TestConstants.validTransformationConfig));
    }
    @Test
    public void testInValidTransformationConfiguration(){
        CombineFieldsToJSONTransformation.Value value = new CombineFieldsToJSONTransformation.Value();
        Assertions.assertThrows(ConfigException.class, () -> value.configure(TestConstants.inValidTransformationConfig));
    }

    @Test
    public void testValueTransformation(){
        CombineFieldsToJSONTransformation.Value value = new CombineFieldsToJSONTransformation.Value();
        value.configure(TestConstants.validTransformationConfig);
        ConnectRecord record = value.apply(createRecord(null, TestConstants.sampleValsForTransformation));
        Map<String, Object> valueMap = (Map<String, Object>) record.value();
        assertFalse(valueMap.containsKey(TestConstants.FIELD_1));
        assertFalse(valueMap.containsKey(TestConstants.FIELD_2));
        assertTrue(valueMap.containsKey(CombineFieldsToJSONTransformation.DEFAULT_FIELD_NAME));
        assertTrue(valueMap.get(CombineFieldsToJSONTransformation.DEFAULT_FIELD_NAME).equals(TestConstants.PROPERTIES_FIELD_WITH_FIELDS));

    }

    @Test
    public void testKeyTransformation(){
        CombineFieldsToJSONTransformation.Key key = new CombineFieldsToJSONTransformation.Key();
        key.configure(TestConstants.validTransformationConfig);
        ConnectRecord record = key.apply(createRecord(TestConstants.sampleValsForTransformation, null));
        Map<String, Object> keyMap = (Map<String, Object>) record.key();
        assertFalse(keyMap.containsKey(TestConstants.FIELD_1));
        assertFalse(keyMap.containsKey(TestConstants.FIELD_2));
        assertTrue(keyMap.containsKey(CombineFieldsToJSONTransformation.DEFAULT_FIELD_NAME));
        assertTrue(keyMap.get(CombineFieldsToJSONTransformation.DEFAULT_FIELD_NAME).equals(TestConstants.PROPERTIES_FIELD_WITH_FIELDS));

    }

    private ConnectRecord<SinkRecord> createRecord(Object key, Object value) {
        return new SinkRecord("topic", 0, null, key, null, value, 1);
    }

}
