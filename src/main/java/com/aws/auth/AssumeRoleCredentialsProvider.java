package com.aws.auth;

import com.amazonaws.auth.*;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;


import java.util.Map;

import static com.aws.util.Constants.AWS_ACCESS_KEY;
import static com.aws.util.Constants.AWS_SECRET_KEY;

/******************
 * This class provide default implementation for if client using trusted account for providing authentication
 */
public class AssumeRoleCredentialsProvider implements AWSCredentialsProvider, Configurable {

    public static final String AMAZON_PERSONALIZE_STS_ROLE_EXTERNAL_ID_CONFIG = "amazon.personalize.sts.role.external.id";
    public static final String AMAZON_PERSONALIZE_ROLE_ARN_CONFIG = "amazon.personalize.sts.role.arn";
    public static final String AMAZON_PERSONALIZE_ROLE_SESSION_NAME_CONFIG = "amazon.personalize.sts.role.session.name";

    private static final ConfigDef STS_CONFIG_DEF = new ConfigDef()
            .define(
                    AMAZON_PERSONALIZE_STS_ROLE_EXTERNAL_ID_CONFIG,
                    ConfigDef.Type.STRING,
                    ConfigDef.Importance.MEDIUM,
                    "Amazon Personalize external role ID used when retrieving session credentials under an assumed role."
            ).define(
                    AMAZON_PERSONALIZE_ROLE_ARN_CONFIG,
                    ConfigDef.Type.STRING,
                    ConfigDef.Importance.HIGH,
                    "Amazon Personalize Role ARN for creating AWS session."
            ).define(
                    AMAZON_PERSONALIZE_ROLE_SESSION_NAME_CONFIG,
                    ConfigDef.Type.STRING,
                    ConfigDef.Importance.HIGH,
                    "Amazon Personalize Role session name"
            );

    private String roleArn;
    private String roleExternalId;
    private String roleSessionName;

    private BasicAWSCredentials basicCredentials;

    private STSAssumeRoleSessionCredentialsProvider stsAssumeRoleCredentialsProvider;

    @Override
    public void configure(Map<String, ?> configs) {
        AbstractConfig config = new AbstractConfig(STS_CONFIG_DEF, configs);
        roleArn = config.getString(AMAZON_PERSONALIZE_ROLE_ARN_CONFIG);
        roleExternalId = config.getString(AMAZON_PERSONALIZE_STS_ROLE_EXTERNAL_ID_CONFIG);
        roleSessionName = config.getString(AMAZON_PERSONALIZE_ROLE_SESSION_NAME_CONFIG);
        final String accessKeyId = (String) configs.get(AWS_ACCESS_KEY);
        final String secretKey = (String) configs.get(AWS_SECRET_KEY);
        if (StringUtils.isNotBlank(accessKeyId) && StringUtils.isNotBlank(secretKey)) {
            BasicAWSCredentials basicCredentials = new BasicAWSCredentials(accessKeyId, secretKey);

            stsAssumeRoleCredentialsProvider = new STSAssumeRoleSessionCredentialsProvider
                    .Builder(roleArn, roleSessionName)
                    .withStsClient(AWSSecurityTokenServiceClientBuilder
                            .standard()
                            .withCredentials(new AWSStaticCredentialsProvider(basicCredentials)).build()
                    )
                    .withExternalId(roleExternalId)
                    .build();
        } else {
            basicCredentials = null;
            stsAssumeRoleCredentialsProvider = new STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, roleSessionName)
                    .withStsClient(AWSSecurityTokenServiceClientBuilder.defaultClient())
                    .withExternalId(roleExternalId).build();
        }
    }


    @Override
    public AWSCredentials getCredentials() {
        return stsAssumeRoleCredentialsProvider.getCredentials();
    }

    @Override
    public void refresh() {
        if(stsAssumeRoleCredentialsProvider != null)
            stsAssumeRoleCredentialsProvider.refresh();
    }
}
