package com.aws.config.validators;

import com.amazonaws.arn.Arn;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import static com.aws.util.Constants.*;

/*********
 * This is used for validation ARN for Items or Users Dataset
 */
public  class ArnValidator implements  ConfigDef.Validator {
    @Override
    public void ensureValid(String arnVar, Object value) {
        if (null != value && ((String) value).isEmpty()) {
            return;
        }

        try {
            String arnValue = (String) value;
            Arn arn = Arn.fromString(arnValue);

            if ((arn.getResourceAsString().endsWith(PERSONALIZE_USERS_DATASET_RESOURCE_NAME) || arn.getResourceAsString().endsWith(PERSONALIZE_ITEMS_DATASET_RESOURCE_NAME))
                    && arn.getResource().getResourceType().equals(PERSONALIZE_DATASET_RESOURCE) && arn.getService().equals(PERSONALIZE_SERVICE_NAME)) {
                return;
            } else {
                throw new ConfigException("Invalid Arn for User/Item dataset");
            }
        } catch (Throwable e) {
            throw new ConfigException("Invalid Arn for User/Item dataset");
        }

    }
}