package com.aws.util;

import com.aws.transform.CombineFieldsToJSONTransformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestConstants {

    public static final Map<String, String> configVals;

    public static final Map<String, String> itemconfigVals;

    public static final Map<String, String> userconfigVals;

    public static final Map<String, String> invalidConfigVals;
    public static final Map<String, Object> validTransformationConfig;
    public static final Map<String, Object> inValidTransformationConfig;
    public static final Map<String, Object> sampleValsForEventData;

    public static final Map<String, Object> sampleValsForUserData;

    public static final Map<String, Object> sampleValsForItemData;
    public static final Map<String, Object> sampleValsForTransformation;
    public static final String TEST_USER_ID_VALUE= "test-user1";
    public static final String TEST_ITEM_ID_VALUE = "test-item1";
    public static final String TEST_SESSION_ID_VALUE = "test-session1";
    public static final String TEST_EVENT_TYPE_VALUE = "test-event1";
    public static final Long TEST_EVENT_TIME_VALUE = 123456789L;
    public static final String TEST_EVENT_TRACKING_ID ="test-tracking-id";
    public static final String TEST_AWS_REGION ="test-region";
    public static final String VALID_ITEM_DATASET_ARN = "arn:aws:personalize:<REGION>:<ACCOUNT_ID>:dataset/<DATASET_GROUP_NAME>/ITEMS";
    public static final String VALID_USER_DATASET_ARN = "arn:aws:personalize:<REGION>:<ACCOUNT_ID>:dataset/<DATASET_GROUP_NAME>/USERS";
    public static final String TEST_PROPERTIES_VALUE = "{}";
    public static final String FIELD_1 ="field1";
    public static final String FIELD_2 ="field2";
    public static final String PROPERTIES_FIELD_WITH_FIELDS = "{\"field1\":\"field1\",\"field2\":\"field2\"}";

    static {
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put(Constants.AMAZON_PERSONALIZE_EVENT_TRACKING_ID,TEST_EVENT_TRACKING_ID);
        configMap.put(Constants.AWS_REGION_NAME,TEST_AWS_REGION);
        configMap.put(Constants.AMAZON_PERSONALIZE_DATA_TYPE,Constants.PUT_EVENTS_REQUEST_TYPE);
        configVals = Collections.unmodifiableMap(configMap);

        configMap = new HashMap<String, String>();
        configMap.put(Constants.AMAZON_PERSONALIZE_DATASET_ARN, VALID_ITEM_DATASET_ARN);
        configMap.put(Constants.AWS_REGION_NAME,TEST_AWS_REGION);
        configMap.put(Constants.AMAZON_PERSONALIZE_DATA_TYPE,Constants.PUT_ITEMS_REQUEST_TYPE);
        itemconfigVals = Collections.unmodifiableMap(configMap);

        configMap = new HashMap<String, String>();
        configMap.put(Constants.AMAZON_PERSONALIZE_DATASET_ARN, VALID_USER_DATASET_ARN);
        configMap.put(Constants.AWS_REGION_NAME,TEST_AWS_REGION);
        configMap.put(Constants.AMAZON_PERSONALIZE_DATA_TYPE,Constants.PUT_USERS_REQUEST_TYPE);
        userconfigVals = Collections.unmodifiableMap(configMap);

        Map transformConfig = new HashMap<String, Object>();
        transformConfig.put(CombineFieldsToJSONTransformation.FIELDS_TO_INCLUDE, Arrays.asList("field1", "field2"));
        transformConfig.put(CombineFieldsToJSONTransformation.TARGET_FIELD_NAME, CombineFieldsToJSONTransformation.DEFAULT_FIELD_NAME);
        validTransformationConfig = Collections.unmodifiableMap(transformConfig);

        transformConfig = new HashMap<String, Object>();
        transformConfig.put(Constants.AWS_SECRET_KEY, Arrays.asList("field1", "field2"));
        transformConfig.put(CombineFieldsToJSONTransformation.TARGET_FIELD_NAME, CombineFieldsToJSONTransformation.DEFAULT_FIELD_NAME);
        inValidTransformationConfig = Collections.unmodifiableMap(transformConfig);

        Map<String, Object> valuesMap = new HashMap<String, Object>();
        valuesMap.put(Constants.FIELD_USER_ID, TEST_USER_ID_VALUE);
        valuesMap.put(Constants.FIELD_ITEM_ID, TEST_ITEM_ID_VALUE);
        valuesMap.put(Constants.FIELD_EVENT_TYPE,TEST_EVENT_TYPE_VALUE);
        valuesMap.put(Constants.FIELD_SESSION_ID,TEST_SESSION_ID_VALUE);
        valuesMap.put(Constants.FIELD_EVENT_TIME,TEST_EVENT_TIME_VALUE);
        sampleValsForEventData = Collections.unmodifiableMap(valuesMap);

        valuesMap = new HashMap<String, Object>();
        valuesMap.put(Constants.FIELD_ITEM_ID,TEST_ITEM_ID_VALUE);
        valuesMap.put(Constants.FIELD_PROPERTIES,TEST_PROPERTIES_VALUE);
        sampleValsForItemData = Collections.unmodifiableMap(valuesMap);

        valuesMap = new HashMap<String, Object>();
        valuesMap.put(Constants.FIELD_USER_ID,TEST_USER_ID_VALUE);
        valuesMap.put(Constants.FIELD_PROPERTIES,TEST_PROPERTIES_VALUE);
        sampleValsForUserData = Collections.unmodifiableMap(valuesMap);

        valuesMap = new HashMap<String, Object>();
        valuesMap.put(Constants.FIELD_USER_ID,TEST_USER_ID_VALUE);
        valuesMap.put(FIELD_1,FIELD_1);
        valuesMap.put(FIELD_2,FIELD_2);
        sampleValsForTransformation = Collections.unmodifiableMap(valuesMap);

        Map<String, String> invalidConfigMap = new HashMap<String, String>();
        invalidConfigMap.put("amazon.personalize.event.tracking.id.invalid","test-tracking-id");
        invalidConfigMap.put("amazon.region.name","test-region");
        invalidConfigMap.put("amazon.personalize.data.type","invalidAction");
        invalidConfigVals = Collections.unmodifiableMap(invalidConfigMap);
    }

    public static final String INVALID_ARN_INVALID_SERVICE = "arn:aws:ec2:<REGION>:<ACCOUNT_ID>:dataset/<DATASET_GROUP_NAME>/ITEMS";
    public static final String INVALID_ARN_INVALID_RESOURCE = "arn:aws:personalize:<REGION>:<ACCOUNT_ID>:test/<DATASET_GROUP_NAME>/ITEMS";
    public static final String INVALID_ARN_INVALID_DATASET = "arn:aws:personalize:<REGION>:<ACCOUNT_ID>:dataset/<DATASET_GROUP_NAME>/INTERACTIONS";
    public static final String VALID_USER_DATASET_TYPE = "users";
    public static final String VALID_EVENT_DATASET_TYPE = "events";
    public static final String VALID_ITEMS_DATASET_TYPE = "items";
    public static final String INVALID_DATASET_TYPE = "invalid";

}
