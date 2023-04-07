package com.aws.writer;

import com.amazonaws.services.personalizeevents.AmazonPersonalizeEvents;
import com.amazonaws.services.personalizeevents.model.*;
import com.aws.config.PersonalizeSinkConfig;
import com.aws.util.TestConstants;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
public class DataWriterTest {

    @Mock
    AmazonPersonalizeEvents client;
    @Captor
    ArgumentCaptor<PutEventsRequest> putEventsRequestArgumentCaptor;
    @Captor
    ArgumentCaptor<PutItemsRequest> putItemsRequestArgumentCaptor;
    @Captor
    ArgumentCaptor<PutUsersRequest> putUsersRequestArgumentCaptor;

    @Test
    public void testWriterForEventData() throws Exception {
        PersonalizeSinkConfig config = new PersonalizeSinkConfig(TestConstants.configVals);
        DataWriter dataWriter = new DataWriter(config, client);
        SinkRecord sinkRecord = createRecord(TestConstants.sampleValsForEventData);
        dataWriter.write(Collections.singletonList(sinkRecord));
        verify(client).putEvents(putEventsRequestArgumentCaptor.capture());
        PutEventsRequest putEventsRequest = putEventsRequestArgumentCaptor.getValue();
        assertTrue(putEventsRequest.getSessionId().equals(TestConstants.TEST_SESSION_ID_VALUE));
        assertTrue(putEventsRequest.getUserId().equals(TestConstants.TEST_USER_ID_VALUE));
        assertTrue(!putEventsRequest.getEventList().isEmpty());
        Event event = putEventsRequest.getEventList().get(0);
        assertTrue(event.getEventType().equals(TestConstants.TEST_EVENT_TYPE_VALUE));
        assertTrue(event.getItemId().equals(TestConstants.TEST_ITEM_ID_VALUE));
        assertTrue(event.getSentAt().getTime() == TestConstants.TEST_EVENT_TIME_VALUE);
        assertTrue(putEventsRequest.getTrackingId().equals(TestConstants.TEST_EVENT_TRACKING_ID));
    }

    @Test
    public void testWriterForItemData() throws Exception {
        PersonalizeSinkConfig config = new PersonalizeSinkConfig(TestConstants.itemconfigVals);
        DataWriter dataWriter = new DataWriter(config, client);
        SinkRecord sinkRecord = createRecord(TestConstants.sampleValsForItemData);
        dataWriter.write(Collections.singletonList(sinkRecord));
        verify(client).putItems(putItemsRequestArgumentCaptor.capture());
        PutItemsRequest putItemsRequest = putItemsRequestArgumentCaptor.getValue();
        assertTrue(putItemsRequest.getDatasetArn().equals(TestConstants.VALID_ITEM_DATASET_ARN));
        assertTrue(!putItemsRequest.getItems().isEmpty());
        Item item = putItemsRequest.getItems().get(0);
        assertTrue(item.getItemId().equals(TestConstants.TEST_ITEM_ID_VALUE));
        assertTrue(item.getProperties().equals(TestConstants.TEST_PROPERTIES_VALUE));
    }

    @Test
    public void testWriterForUserData() throws Exception {
        PersonalizeSinkConfig config = new PersonalizeSinkConfig(TestConstants.userconfigVals);
        DataWriter dataWriter = new DataWriter(config, client);
        SinkRecord sinkRecord = createRecord(TestConstants.sampleValsForUserData);
        dataWriter.write(Collections.singletonList(sinkRecord));
        verify(client).putUsers(putUsersRequestArgumentCaptor.capture());
        PutUsersRequest putUsersRequest = putUsersRequestArgumentCaptor.getValue();
        assertTrue(putUsersRequest.getDatasetArn().equals(TestConstants.VALID_USER_DATASET_ARN));
        assertTrue(!putUsersRequest.getUsers().isEmpty());
        User user = putUsersRequest.getUsers().get(0);
        assertTrue(user.getUserId().equals(TestConstants.TEST_USER_ID_VALUE));
        assertTrue(user.getProperties().equals(TestConstants.TEST_PROPERTIES_VALUE));
    }

    private SinkRecord createRecord(Object value) {
        return new SinkRecord("topic", 0, null, null, null, value, 1);
    }

}
