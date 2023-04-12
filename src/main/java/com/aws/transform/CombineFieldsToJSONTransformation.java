package com.aws.transform;

import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.cache.LRUCache;
import org.apache.kafka.common.cache.SynchronizedCache;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.*;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SchemaUtil;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.jose4j.json.internal.json_simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.connect.transforms.util.Requirements.requireMap;
import static org.apache.kafka.connect.transforms.util.Requirements.requireStruct;

/***********
 * This is custom transformation used for constructing properties field value by including multiple fields in kafka records.
 * @param <R>
 */
public abstract class CombineFieldsToJSONTransformation <R extends ConnectRecord<R>> implements Transformation<R> {

    public static final String FIELDS_TO_INCLUDE = "fieldsToInclude";
    public static final String TARGET_FIELD_NAME = "targetFieldName";
    public static final String DEFAULT_FIELD_NAME = "properties";


    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(FIELDS_TO_INCLUDE, ConfigDef.Type.LIST, Collections.emptyList(), ConfigDef.Importance.HIGH,
                    "Fields to be included in transformation")
            .define(TARGET_FIELD_NAME, ConfigDef.Type.STRING, DEFAULT_FIELD_NAME, ConfigDef.Importance.MEDIUM,
                    "Name of the transformed field");

    private  static  final String PURPOSE = "Merge Properties Field";
    private List<String> include;
    private String combinedFieldName;
    private Map<String, List<String>> mappedFieldMap;
    private Cache<Schema, Schema> schemaUpdateCache;

    @Override
    public void configure(Map<String, ?> configs){
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, configs);
        include = config.getList(FIELDS_TO_INCLUDE);
        combinedFieldName = config.getString(TARGET_FIELD_NAME);

        if(null != include && include.isEmpty())
            throw new ConfigException("Field List cant be empty");

        mappedFieldMap = prepareMappedFieldMap(combinedFieldName, include);
        schemaUpdateCache = new SynchronizedCache<>(new LRUCache<>(16));
    }

    private Map<String, List<String>> prepareMappedFieldMap(String combinedFieldName, List<String> include) {
        Map<String, List<String>> mappedFieldMap = new HashMap<>();
        mappedFieldMap.put(combinedFieldName,include);
        return mappedFieldMap;
    }

    @Override
    public R apply(R record){
        if (getValue(record) == null) {
            return record;
        } else if (getSchema(record) == null) {
            return processDataWithoutSchema(record);
        } else {
            return processDataWithSchema(record);
        }
    }

    private R processDataWithSchema(R record) {
        final Struct value = requireStruct(getValue(record), PURPOSE);

        Schema updatedSchema = schemaUpdateCache.get(value.schema());
        if (updatedSchema == null) {
            updatedSchema = makeUpdatedSchema(value.schema());
            schemaUpdateCache.put(value.schema(), updatedSchema);
        }

        final Struct updatedValue = new Struct(updatedSchema);

        for (Field field : updatedSchema.fields()) {
            if(field.name().equals(combinedFieldName)){
                JSONObject jsonObject = new JSONObject();
                for(String oldFieldName : mappedFieldMap.get(combinedFieldName)){
                    jsonObject.put(oldFieldName,value.get(oldFieldName));
                }
                if(!jsonObject.isEmpty())
                    updatedValue.put(field.name(), jsonObject.toJSONString());
            }else {
                final Object fieldValue = value.get(field.name());
                updatedValue.put(field.name(), fieldValue);
            }
        }

        return createRecord(record, updatedSchema, updatedValue);
    }

    private R processDataWithoutSchema(R record) {
        final Map<String, Object> value = requireMap(getValue(record), PURPOSE);

        final Map<String, Object> updatedValue = new HashMap<>(value.size());
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> e : value.entrySet()) {
            final String fieldName = e.getKey();
            if(null != include &&  !include.isEmpty()){
                if(include.contains(fieldName)){
                    jsonObject.put(fieldName, e.getValue());
                }else{
                    updatedValue.put(fieldName,e.getValue());
                }
            }else{
                updatedValue.put(fieldName,e.getValue());
            }
        }

        if(!jsonObject.isEmpty())
            updatedValue.put(combinedFieldName, jsonObject.toJSONString());

        return createRecord(record, null, updatedValue);
    }

    private Schema makeUpdatedSchema(Schema schema) {
        final SchemaBuilder builder = SchemaUtil.copySchemaBasics(schema, SchemaBuilder.struct());
        boolean isCombinedFieldAdded = false;
        for (Field field : schema.fields()) {
            if (mappedFieldMap.get(combinedFieldName).contains(field.name())) {
                if(!isCombinedFieldAdded) {
                    builder.field(combinedFieldName, new ConnectSchema(Schema.Type.STRING));
                    isCombinedFieldAdded= true;
                }
            }else {
                builder.field(field.name(), field.schema());
            }
        }
        return builder.build();
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() {
        schemaUpdateCache = null;
    }

    protected abstract Schema getSchema(R record);

    protected abstract Object getValue(R record);

    protected abstract R createRecord(R record, Schema updatedSchema, Object updatedValue);


    /**************
     * This class is used for Transformation of Key in Kafka record
     * @param <R>
     */
    public static class Key<R extends ConnectRecord<R>> extends CombineFieldsToJSONTransformation<R> {

        @Override
        protected Schema getSchema(R record){
            return  record.keySchema();
        }

        @Override
        protected Object getValue(R record) {
            return record.key();
        }

        @Override
        protected R createRecord(R record, Schema updatedSchema, Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), updatedSchema, updatedValue, record.valueSchema(), record.value(), record.timestamp());
        }
    }

    /****************
     * This class is used for Transformation of Value in Kafka record
     * @param <R>
     */
    public static class Value<R extends ConnectRecord<R>> extends CombineFieldsToJSONTransformation<R> {

        @Override
        protected Schema getSchema(R record) {
            return record.valueSchema();
        }

        @Override
        protected Object getValue(R record) {
            return record.value();
        }

        @Override
        protected R createRecord(R record, Schema updatedSchema, Object updatedValue) {
            return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), updatedSchema, updatedValue, record.timestamp());
        }
    }
}
