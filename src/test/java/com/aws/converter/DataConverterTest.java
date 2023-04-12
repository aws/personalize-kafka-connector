package com.aws.converter;

import com.amazonaws.services.personalizeevents.model.Event;
import com.amazonaws.services.personalizeevents.model.Item;
import com.amazonaws.services.personalizeevents.model.User;
import com.aws.util.Constants;
import com.aws.util.TestConstants;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class DataConverterTest {

    @Test
    public void testEventDataConverter(){
        SinkRecord sinkRecord = createRecord(TestConstants.sampleValsForEventData);
        Map<String, String> additionalValues = new HashMap<>();
        Event eventInfo = DataConverter.convertSinkRecordToEventInfo(sinkRecord, additionalValues);
        Assertions.assertTrue(eventInfo.getEventType().equals(TestConstants.TEST_EVENT_TYPE_VALUE));
        Assertions.assertTrue(additionalValues.get(Constants.FIELD_SESSION_ID).equals(TestConstants.TEST_SESSION_ID_VALUE));
        Assertions.assertTrue(additionalValues.get(Constants.FIELD_USER_ID).equals(TestConstants.TEST_USER_ID_VALUE));
        Assertions.assertTrue(eventInfo.getItemId().equals(TestConstants.TEST_ITEM_ID_VALUE));
        Assertions.assertTrue(eventInfo.getSentAt().getTime() == TestConstants.TEST_EVENT_TIME_VALUE);
    }

    @Test
    public void testItemDataConverter(){
        SinkRecord sinkRecord = createRecord(TestConstants.sampleValsForItemData);
        Item item = DataConverter.convertSinkRecordToItemInfo(sinkRecord);
        Assertions.assertTrue(item.getItemId().equals(TestConstants.TEST_ITEM_ID_VALUE));
        Assertions.assertTrue(item.getProperties().equals(TestConstants.TEST_PROPERTIES_VALUE));

    }

    @Test
    public void testUserDataConverter(){
        SinkRecord sinkRecord = createRecord(TestConstants.sampleValsForUserData);
        User user = DataConverter.convertSinkRecordToUserInfo(sinkRecord);
        Assertions.assertTrue(user.getUserId().equals(TestConstants.TEST_USER_ID_VALUE));
        Assertions.assertTrue(user.getProperties().equals(TestConstants.TEST_PROPERTIES_VALUE));
    }

    private SinkRecord createRecord(Map<String, Object> valueMap) {
        return new SinkRecord("topic", 0, null, null, null, valueMap, 1);
    }

}
