package com.aws.writer;

import com.amazonaws.services.personalizeevents.AmazonPersonalizeEvents;
import com.amazonaws.services.personalizeevents.model.*;
import com.aws.config.PersonalizeSinkConfig;
import com.aws.converter.DataConverter;
import com.aws.util.Constants;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/************
 * This class is used for writing Kafka Records into Personalize Event through PersonalizeEvent SDK
 */
public class DataWriter {
    private static final Logger log = LoggerFactory.getLogger(DataWriter.class);
    AmazonPersonalizeEvents client;
    String eventTrackingId;
    String dataSetArn;
    String dataType;

    public DataWriter(PersonalizeSinkConfig config, AmazonPersonalizeEvents client) {
        log.info("Initialing data writer for : " + config.getDataType());

        int maxRetries = config.getMaxRetries();

        this.client = client;

        dataType  = config.getDataType();

        eventTrackingId = config.getEventTrackingId();

        dataSetArn = config.getDataSetArn();

    }

    /**********************
     * This method is used for writing sink kafka record into personalize.
     * @param records of Sink records
     * @throws Exception
     */
    public void write(final Collection<SinkRecord> records) throws Exception {
        log.debug("DataWriter started writing record with size :" + records.size());
        try{
            for (SinkRecord record : records) {
                log.debug("Processing record with value:"+ record.value());
                if(Constants.PUT_EVENTS_REQUEST_TYPE.equals(dataType)){
                    Map<String, String> additionalValues = new HashMap<>();
                    Event eventInfo = DataConverter.convertSinkRecordToEventInfo(record, additionalValues);
                    if(eventInfo != null)
                        putEvents(eventInfo, additionalValues);
                }else if(Constants.PUT_ITEMS_REQUEST_TYPE.equals(dataType)){
                    Item itemInfo = DataConverter.convertSinkRecordToItemInfo(record);
                    if(itemInfo != null)
                        putItems(itemInfo);
                }else if(Constants.PUT_USERS_REQUEST_TYPE.equals(dataType)){
                    User userInfo = DataConverter.convertSinkRecordToUserInfo(record);
                    if(userInfo != null)
                        putUsers(userInfo);
                }else
                    log.error("Invalid Operation" );
            }
        }catch(Throwable e){
            log.error("Exception occurred while writing data to personalize", e);
            throw e;
        }

    }

    /***************************
     * This method to call put event APIs
     * @param eventInfo
     * @return
     */
    private void putEvents(Event eventInfo, Map<String, String> additionalValues) {
        try {
            PutEventsRequest putEventsRequest = new PutEventsRequest();
            putEventsRequest.setTrackingId(eventTrackingId);
            putEventsRequest.setUserId(additionalValues.get(Constants.FIELD_USER_ID));
            putEventsRequest.setSessionId(additionalValues.get(Constants.FIELD_SESSION_ID));
            putEventsRequest.setEventList(Collections.singletonList(eventInfo));

            client.putEvents(putEventsRequest);

        } catch (AmazonPersonalizeEventsException e) {
            log.error("Error in Put events API", e);
            throw e;
        }
    }


    /***********
     * This method to call put user APIs
     * @param userInfo
     */
    private void putUsers(User userInfo) {
        try {
            PutUsersRequest putUsersRequest = new PutUsersRequest();
            putUsersRequest.setUsers(Collections.singletonList(userInfo));
            putUsersRequest.setDatasetArn(dataSetArn);

            client.putUsers(putUsersRequest);
        } catch (AmazonPersonalizeEventsException e) {
            log.error("Error in Put Users API", e);
            throw e;
        }
    }


    /*******
     * This method to call put item APIs
     * @param itemInfo
     */
    private void putItems(Item itemInfo) {
        try {
            PutItemsRequest putItemsRequest = new PutItemsRequest();
            putItemsRequest.setItems(Collections.singletonList(itemInfo));
            putItemsRequest.setDatasetArn(dataSetArn);

            client.putItems(putItemsRequest);
        } catch (AmazonPersonalizeEventsException e) {
            log.error("Error in Put Items API", e);
            throw e;
        }

    }

    /*******************
     * This method is used to close AWS personalize client.
     */
    public void closeQuietly() {
       client.shutdown();
    }
}
