package com.aws.config.validators;

import com.amazonaws.auth.AWSCredentialsProvider;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
/*********
 * This is used for validation Credential Provider class
 */
public class PersonalizeCredentialsProviderValidator implements ConfigDef.Validator {
    @Override
    public void ensureValid(String name, Object provider) {
        if (provider != null && provider instanceof Class
                && AWSCredentialsProvider.class.isAssignableFrom((Class<?>) provider)) {
            return;
        }
        throw new ConfigException(
                name,
                provider,
                "Class must extend: " + AWSCredentialsProvider.class
        );
    }
}