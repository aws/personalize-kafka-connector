package com.aws.config.validators;

import com.aws.util.TestConstants;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PersonalizeDataTypeValidatorTest {

    private static final String DATA_TYPE_CONFIG_NAME = "amazon.personalize.data.type";

    private static PersonalizeDataTypeValidator personalizeDataTypeValidator;

    @BeforeAll
    public static void setUp(){
        personalizeDataTypeValidator=  new PersonalizeDataTypeValidator();
    }
    @Test
    public void testValidUsersDataType(){
        // Testing by passing valid User DataSet Type value
        Assertions.assertDoesNotThrow(() -> personalizeDataTypeValidator.ensureValid(DATA_TYPE_CONFIG_NAME, TestConstants.VALID_USER_DATASET_TYPE));
    }

    @Test
    public void testValidItemsDataType(){
        // Testing by passing valid Items DataSet Type value
        Assertions.assertDoesNotThrow(() -> personalizeDataTypeValidator.ensureValid(DATA_TYPE_CONFIG_NAME, TestConstants.VALID_ITEMS_DATASET_TYPE));
    }
    @Test
    public void testValidEventsDataType(){
        // Testing by passing valid Event DataSet Type value
        Assertions.assertDoesNotThrow(() -> personalizeDataTypeValidator.ensureValid(DATA_TYPE_CONFIG_NAME, TestConstants.VALID_EVENT_DATASET_TYPE));
    }
    @Test
    public void testInvalidDataType(){
        // Testing by passing Invalid DataSet Type value which is not supported
        Assertions.assertThrows(ConfigException.class,() -> personalizeDataTypeValidator.ensureValid(DATA_TYPE_CONFIG_NAME, TestConstants.INVALID_DATASET_TYPE));
    }

}
