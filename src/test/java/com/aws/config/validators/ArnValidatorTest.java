package com.aws.config.validators;

import com.aws.util.TestConstants;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ArnValidatorTest {

    private static final String ARN_CONFIG_NAME = "amazon.personalize.dataset.arn";
    private static ArnValidator arnValidator;

    @BeforeAll
    public static void setUp(){
        arnValidator=  new ArnValidator();
    }
    @Test
    public void testValidItemsDataSetArn(){
        //Testing with passing valid Item DataSet ARN
        Assertions.assertDoesNotThrow(() -> arnValidator.ensureValid(ARN_CONFIG_NAME, TestConstants.VALID_ITEM_DATASET_ARN));
    }

    @Test
    public void testValidUsersDataSetArn(){
        //Testing with passing valid User DataSet ARN
        Assertions.assertDoesNotThrow(() -> arnValidator.ensureValid(ARN_CONFIG_NAME, TestConstants.VALID_USER_DATASET_ARN));
    }

    @Test
    public void testInValidArnWithService(){
        //Testing with passing invalid service name
        Assertions.assertThrows(ConfigException.class ,() ->arnValidator.ensureValid(ARN_CONFIG_NAME, TestConstants.INVALID_ARN_INVALID_SERVICE));
    }

    @Test
    public void testInValidArnWithResource(){
        //Testing with passing invalid resource name
        Assertions.assertThrows(ConfigException.class ,() ->arnValidator.ensureValid(ARN_CONFIG_NAME, TestConstants.INVALID_ARN_INVALID_RESOURCE));
    }

    @Test
    public void testInValidArnWithDataSetType(){
        //Testing with passing invalid dataset name
        Assertions.assertThrows(ConfigException.class ,() ->arnValidator.ensureValid(ARN_CONFIG_NAME, TestConstants.INVALID_ARN_INVALID_DATASET));
    }
}
