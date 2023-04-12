package com.aws.config;

import com.aws.util.Constants;
import com.aws.util.TestConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PersonalizeSinkConfigTest {

    @Test
    public void testEventConfig(){
        PersonalizeSinkConfig config = new PersonalizeSinkConfig(TestConstants.configVals);
        Assertions.assertTrue(StringUtils.isNotBlank(config.getName()));
        Assertions.assertTrue(config.getName().equals(Constants.DEFAULT_CONNECTOR_NAME));
        Assertions.assertTrue(config.getDataType().equals(Constants.PUT_EVENTS_REQUEST_TYPE));
        Assertions.assertTrue(config.getEventTrackingId().equals(TestConstants.TEST_EVENT_TRACKING_ID));
        Assertions.assertTrue(config.getAwsRegionName().equals(TestConstants.TEST_AWS_REGION));
        Assertions.assertFalse(config.getDataSetArn().equals(TestConstants.VALID_ITEM_DATASET_ARN));
    }

    @Test
    public void testItemConfig(){
        PersonalizeSinkConfig config = new PersonalizeSinkConfig(TestConstants.itemconfigVals);
        Assertions.assertTrue(config.getDataType().equals(Constants.PUT_ITEMS_REQUEST_TYPE));
        Assertions.assertFalse(StringUtils.isNotBlank(config.getEventTrackingId()));
        Assertions.assertFalse(config.getEventTrackingId().equals(TestConstants.TEST_EVENT_TRACKING_ID));
        Assertions.assertTrue(config.getAwsRegionName().equals(TestConstants.TEST_AWS_REGION));
        Assertions.assertTrue(config.getDataSetArn().equals(TestConstants.VALID_ITEM_DATASET_ARN));
    }

    @Test
    public void testUserConfig(){
        PersonalizeSinkConfig config = new PersonalizeSinkConfig(TestConstants.userconfigVals);
        Assertions.assertTrue(config.getDataType().equals(Constants.PUT_USERS_REQUEST_TYPE));
        Assertions.assertFalse(StringUtils.isNotBlank(config.getEventTrackingId()));
        Assertions.assertFalse(config.getEventTrackingId().equals(TestConstants.TEST_EVENT_TRACKING_ID));
        Assertions.assertTrue(config.getAwsRegionName().equals(TestConstants.TEST_AWS_REGION));
        Assertions.assertTrue(config.getDataSetArn().equals(TestConstants.VALID_USER_DATASET_ARN));
    }

    @Test
    public void testInvalidConfig(){
        Assertions.assertThrows(ConfigException.class,() -> new PersonalizeSinkConfig(TestConstants.invalidConfigVals));
    }
}
