package com.aws.config.validators;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import static com.aws.util.Constants.VALID_DATA_TYPE;

/*************
 * This class is used for validating Personalize Data Type configuration
 */
public class PersonalizeDataTypeValidator implements  ConfigDef.Validator {

    @Override
    public void ensureValid(String eventType, Object value) {
        if (null != value && ((String) value).isEmpty()) {
            return;
        }
        if (!VALID_DATA_TYPE.contains(((String) value).toLowerCase())) {
            throw new ConfigException(String.format(
                    "%s must be one out of %s",
                    eventType,
                    String.join(",", VALID_DATA_TYPE)
            ));
        }
    }
}