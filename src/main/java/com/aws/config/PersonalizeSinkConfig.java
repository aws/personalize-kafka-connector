package com.aws.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.aws.config.validators.ArnValidator;
import com.aws.config.validators.PersonalizeCredentialsProviderValidator;
import com.aws.config.validators.PersonalizeDataTypeValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.types.Password;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;

import java.util.Map;

import static com.aws.util.Constants.*;

/****************
 * This class is used for connector specific configuration.
 */
public class PersonalizeSinkConfig extends AbstractConfig {

    private final String awsAccessKey;
    private final String awsSecretKey;
    private final String awsRegionName;
    private final String eventTrackingId;
    private final String dataSetArn;
    private final String dataType;
    private final int maxRetries;
    private final String name;


    public static final ConfigDef CONFIG_DEF = new ConfigDef().define(
            AWS_ACCESS_KEY,
            ConfigDef.Type.STRING,
            DEFAULT_STRING_VALUE,
            ConfigDef.Importance.HIGH,
            AWS_ACCESS_KEY_DOC,
            AWS_ACCOUNT_DETAILS_GROUP,
            1,
            ConfigDef.Width.LONG,
            AWS_ACCESS_KEY_DISPLAY
    ).define(
            AWS_SECRET_KEY,
            ConfigDef.Type.PASSWORD,
            AWS_SECRET_KEY_DEFAULT_VALUE,
            ConfigDef.Importance.HIGH,
            AWS_SECRET_KEY_DOC,
            AWS_ACCOUNT_DETAILS_GROUP,
            2,
            ConfigDef.Width.LONG,
            AWS_SECRET_KEY_DISPLAY
    ).define(
            AWS_REGION_NAME,
            ConfigDef.Type.STRING,
            ConfigDef.NO_DEFAULT_VALUE,
            ConfigDef.Importance.HIGH,
            AWS_REGION_NAME_DOC,
            AWS_ACCOUNT_DETAILS_GROUP,
            3,
            ConfigDef.Width.LONG,
            AWS_REGION_NAME_DISPLAY
    ).define(
            AMAZON_PERSONALIZE_DATA_TYPE,
            ConfigDef.Type.STRING,
            PUT_EVENTS_REQUEST_TYPE,
            new PersonalizeDataTypeValidator(),
            ConfigDef.Importance.HIGH,
            AMAZON_PERSONALIZE_EVENT_TYPE_DOC,
            AWS_ACCOUNT_DETAILS_GROUP,
            4,
            ConfigDef.Width.LONG,
            AMAZON_PERSONALIZE_EVENT_TYPE_DISPLAY
    ).define(
            AMAZON_PERSONALIZE_DATASET_ARN,
            ConfigDef.Type.STRING,
            DEFAULT_STRING_VALUE,
            new ArnValidator(),
            ConfigDef.Importance.HIGH,
            AMAZON_PERSONALIZE_DATASET_ARN_DOC,
            AWS_ACCOUNT_DETAILS_GROUP,
            6,
            ConfigDef.Width.LONG,
            AMAZON_PERSONALIZE_DATASET_ARN_DISPLAY
    ).define(
            AMAZON_PERSONALIZE_EVENT_TRACKING_ID,
            ConfigDef.Type.STRING,
            DEFAULT_STRING_VALUE,
            ConfigDef.Importance.HIGH,
            AMAZON_PERSONALIZE_EVENT_TRACKING_ID_DOC,
            AWS_ACCOUNT_DETAILS_GROUP,
            5,
            ConfigDef.Width.LONG,
            AMAZON_PERSONALIZE_EVENT_TRACKING_ID_DISPLAY
    ).define(
            MAX_RETRIES,
            ConfigDef.Type.INT,
            2,
            ConfigDef.Importance.HIGH,
            MAX_RETRIES,
            null,
            8,
            ConfigDef.Width.LONG,
            MAX_RETRIES
    ).define(
            AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS,
            ConfigDef.Type.CLASS,
            AWS_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_DEFAULT,
            new PersonalizeCredentialsProviderValidator(),
            ConfigDef.Importance.LOW,
            AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_DOC,
            AWS_ACCOUNT_DETAILS_GROUP,
            7,
            ConfigDef.Width.LONG,
            AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_DISPLAY
    );

    public String awsAccessKey() {
        return getString(AWS_ACCESS_KEY);
    }

    public Password awsSecretKey() {
        return getPassword(AWS_SECRET_KEY);
    }

    public PersonalizeSinkConfig(Map<?, ?> props) {
        super(CONFIG_DEF, props);
        awsAccessKey =awsAccessKey();
        awsSecretKey = awsSecretKey().value();
        awsRegionName = getString(AWS_REGION_NAME);
        maxRetries = getInt(MAX_RETRIES);
        dataType = getString(AMAZON_PERSONALIZE_DATA_TYPE);
        if(dataType.equals(PUT_EVENTS_REQUEST_TYPE)) {
            eventTrackingId = getVaildConfigValue(props, AMAZON_PERSONALIZE_EVENT_TRACKING_ID);
            dataSetArn = getString(AMAZON_PERSONALIZE_DATASET_ARN);
        }else {
            dataSetArn = getVaildConfigValue(props,AMAZON_PERSONALIZE_DATASET_ARN);
            eventTrackingId = getString(AMAZON_PERSONALIZE_EVENT_TRACKING_ID);
        }
        name = getConnectorName(props);
    }

    /*******
     * This method is used for getting name for connector which is configured
     * @param props
     * @return
     */
    private String getConnectorName(Map<?, ?> props) {
        String connectorName = null;
        if (props.containsKey(NAME_PROPERTY)) {
            String nameProp = (String) props.get(NAME_PROPERTY);
            connectorName = null != nameProp ? nameProp : DEFAULT_CONNECTOR_NAME;
        } else {
            connectorName = DEFAULT_CONNECTOR_NAME;
        }
        return connectorName;
    }

    private String getVaildConfigValue(Map<?, ?> props, String configName){
        if(props.containsKey(configName)) {
            String validValue = getString(configName) ;
            if (!StringUtils.isEmpty(validValue)) {
                return validValue;
            }else
                throw new ConfigException("You should have non-empty value for Configuration" + configName);
        } else
            throw new ConfigException("Missing required configuration " + configName);
    }

    public String getName() {
        return name;
    }

    public String getAwsRegionName() {
        return awsRegionName;
    }

    public String getEventTrackingId() {
        return eventTrackingId;
    }

    public String getDataSetArn() {
        return dataSetArn;
    }

    public String getDataType() {
        return dataType;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    /**************
     * This method is used for providing AWS credentials which will be used by personalize client
     * @return AWS credentials provider
     */
    public AWSCredentialsProvider getCredentionalProvider() {
        try {
            AWSCredentialsProvider provider = ((Class<? extends AWSCredentialsProvider>) getClass(AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS)).newInstance();

            if (provider instanceof Configurable) {
                Map<String, Object> configs = originalsWithPrefix(AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_PREFIX);
                configs.remove(AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS.substring(
                        AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS_PREFIX.length()
                ));

                configs.put(AWS_ACCESS_KEY, awsAccessKey());
                configs.put(AWS_SECRET_KEY, awsSecretKey().value());

                ((Configurable) provider).configure(configs);
            } else {
                final String accessKeyId = awsAccessKey();
                final String secretKey = awsSecretKey().value();
                if (StringUtils.isNotBlank(accessKeyId) && StringUtils.isNotBlank(secretKey)) {
                    BasicAWSCredentials basicCredentials = new BasicAWSCredentials(accessKeyId, secretKey);
                    provider = new AWSStaticCredentialsProvider(basicCredentials);
                }
            }
            return provider;
        } catch (Exception e) {
            throw new ConnectException("Invalid class for: " + AMAZON_PERSONALIZE_CREDENTIALS_PROVIDER_CLASS, e);
        }
    }
}