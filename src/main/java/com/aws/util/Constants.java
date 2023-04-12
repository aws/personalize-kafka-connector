package com.aws.util;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.apache.kafka.common.config.types.Password;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/***********
 * This class consists of Constants used.
 */
public class Constants {
    //Event Related Fields
    public static final String FIELD_EVENT_TYPE ="eventType";
    public static final String FIELD_EVENT_ID ="eventId";
    public static final String FIELD_EVENT_VALUE ="eventValue";
    public static final String FIELD_EVENT_IMPRESSION ="impression";
    public static final String FIELD_ITEM_ID ="itemId";
    public static final String FIELD_EVENT_METRIC_ATTRIBUTION_SOURCE ="eventAttributionSource";
    public static final String FIELD_PROPERTIES = "properties";
    public static final String FIELD_EVENT_RECOMMENDATION_ID = "recommendationId";
    public static final String FIELD_EVENT_TIME ="sentAt";
    public static final String FIELD_SESSION_ID ="sessionId";
    public static final String FIELD_USER_ID ="userId";
    public static final Set<String> VALID_DATA_TYPE = new HashSet<String>(Arrays.asList("events", "items","users"));
    public static final String PUT_EVENTS_REQUEST_TYPE = "events";
    public static final String PUT_ITEMS_REQUEST_TYPE = "items";
    public static final String PUT_USERS_REQUEST_TYPE = "users";
    public static final String PERSONALIZE_SERVICE_NAME = "personalize";
    public static final String PERSONALIZE_DATASET_RESOURCE = "dataset";
    public static final String PERSONALIZE_USERS_DATASET_RESOURCE_NAME = "USERS";
    public static final String PERSONALIZE_ITEMS_DATASET_RESOURCE_NAME = "ITEMS";
    public static final Class<? extends AWSCredentialsProvider> AWS_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_DEFAULT = DefaultAWSCredentialsProviderChain.class;
    public static final String AWS_ACCESS_KEY = "aws.access.key";
    public static final String DEFAULT_STRING_VALUE = "";
    public static final String AWS_ACCESS_KEY_DOC = "AWS Access key";
    public static final String AWS_ACCESS_KEY_DISPLAY = "AWS Access key";
    public static final String AWS_SECRET_KEY = "aws.secret.key";
    public static final Password AWS_SECRET_KEY_DEFAULT_VALUE = new Password(null);
    public static final String AWS_SECRET_KEY_DOC = "AWS Secret Access Key";
    public static final String AWS_SECRET_KEY_DISPLAY = "AWS Secret Access Key";
    public static final String AWS_REGION_NAME = "aws.region.name";
    public static final String DEFAULT_AWS_REGION_NAME = "us-east-1";
    public static final String AWS_REGION_NAME_DOC = "AWS Region";
    public static final String AWS_REGION_NAME_DISPLAY = "AWS Region";
    public static final String AMAZON_PERSONALIZE_EVENT_TRACKING_ID = "amazon.personalize.event.tracking.id";
    public static final String AMAZON_PERSONALIZE_EVENT_TRACKING_ID_DOC = "Amazon Personalize Event tracking ID";
    public static final String AMAZON_PERSONALIZE_EVENT_TRACKING_ID_DISPLAY = "Amazon Personalize Event tracking ID";
    public static final String AMAZON_PERSONALIZE_DATA_TYPE = "amazon.personalize.data.type";
    public static final String AMAZON_PERSONALIZE_EVENT_TYPE_DISPLAY = "Amazon Personalize Data Type";
    public static final String AMAZON_PERSONALIZE_EVENT_TYPE_DOC = "Valid data types are events,items and users corresponding to interactions, items and users datasets.";
    public static final String AMAZON_PERSONALIZE_DATASET_ARN = "amazon.personalize.dataset.arn";
    public static final String AMAZON_PERSONALIZE_DATASET_ARN_DISPLAY = "ARN for Users/Items dataset";
    public static final String AMAZON_PERSONALIZE_DATASET_ARN_DOC = "The Amazon Resource Name (ARN) of the Users/Items dataset you are adding the user(s)/item(s) to.";
    public static final String AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS = "amazon.personalize.credentials.provider.class";
    public static final String AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_PREFIX = AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS.substring(0, AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS.lastIndexOf(".")+1);
    public static final String AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_DOC = "AWS Credential Provider Class";
    public static final String AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_DISPLAY = "AWS Credential Provider Class";
    public static final String AWS_ACCOUNT_DETAILS_GROUP = "AWS Account Details";
    public static final String MAX_RETRIES = "max.retries";
    public static final String NAME_PROPERTY = "name";
    public static final String DEFAULT_CONNECTOR_NAME = "Personalize-sink";
    public static final String CONNECTOR_VERSION_PROPERTIES_FILE_NAME ="/kafka-connect-personalize-version.properties";
    public static final String CONNECTOR_VERSION_PROPERTY_NAME ="version";
    public static final String RECORD_RATE_LIMIT_PROPERTY_NAME ="record.rate.limit";
    public static final String MAX_TASKS_PROPERTY_NAME ="tasks.max";
    public static final Integer DEFAULT_VALUE_FOR_EVENT_RECORD_RATE = 1000;
    public static final Integer DEFAULT_VALUE_FOR_ITEM_AND_USER_RECORD_RATE = 10;


}
