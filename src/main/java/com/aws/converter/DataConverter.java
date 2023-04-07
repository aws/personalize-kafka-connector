package com.aws.converter;

import com.amazonaws.services.personalizeevents.model.Event;
import com.amazonaws.services.personalizeevents.model.Item;
import com.amazonaws.services.personalizeevents.model.MetricAttribution;
import com.amazonaws.services.personalizeevents.model.User;
import com.aws.util.Constants;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/*************
 * This class is used for Mapping Kafka records to Personalize Event Model Objects
 */
public class DataConverter {

    /************
     * This methods used to convert Sink Kafka Record into Event Model object.
     * @param record
     * @return
     */
    public static Event convertSinkRecordToEventInfo(SinkRecord record, Map<String, String> additionalValues) {
        Event eventInfo = null;
        if(record.value() instanceof Struct)
            eventInfo = prepareEventDataFromStruct((Struct) record.value(), additionalValues);
        else
            eventInfo = prepareEventDataFromMap((Map<String, Object>) record.value(), additionalValues);

        return eventInfo;
    }

    private static Event prepareEventDataFromStruct(Struct recordValue, Map<String, String> additionalValues) {
        Event eventInfo = new Event();
        //Processing required fields in request
        try {
            eventInfo.setEventType(recordValue.getString(Constants.FIELD_EVENT_TYPE));
            additionalValues.put(Constants.FIELD_SESSION_ID,recordValue.getString(Constants.FIELD_SESSION_ID));
            String eventTimeStr = null;
            if (recordValue.get(Constants.FIELD_EVENT_TIME) instanceof String) {
                eventTimeStr = recordValue.getString(Constants.FIELD_EVENT_TIME);
                eventInfo.setSentAt(new Date(Long.parseLong(eventTimeStr)));
            } else if (recordValue.get(Constants.FIELD_EVENT_TIME) instanceof Long) {
                eventInfo.setSentAt(new Date((Long) recordValue.get(Constants.FIELD_EVENT_TIME)));
            }
        } catch (DataException exception) {
            throw new DataException("Missing Required Data");
        }

        //Optional field for Event Object
        eventInfo.setEventId(null != getValueForOptionalField(recordValue, Constants.FIELD_EVENT_ID) ? (String) getValueForOptionalField(recordValue, Constants.FIELD_EVENT_ID) : null);
        eventInfo.setEventValue(null != getValueForOptionalField(recordValue, Constants.FIELD_EVENT_VALUE) ? (Float) getValueForOptionalField(recordValue, Constants.FIELD_EVENT_VALUE) : null);
        eventInfo.setImpression(null != getValueForOptionalField(recordValue, Constants.FIELD_EVENT_IMPRESSION) ? (Collection<String>) getValueForOptionalField(recordValue, Constants.FIELD_EVENT_IMPRESSION) : null);
        eventInfo.setItemId(null != getValueForOptionalField(recordValue, Constants.FIELD_ITEM_ID) ? (String) getValueForOptionalField(recordValue, Constants.FIELD_ITEM_ID) : null);
        if(null != getValueForOptionalField(recordValue, Constants.FIELD_EVENT_METRIC_ATTRIBUTION_SOURCE)) {
            MetricAttribution metricAttribution = new MetricAttribution();
            metricAttribution.setEventAttributionSource((String) getValueForOptionalField(recordValue, Constants.FIELD_EVENT_METRIC_ATTRIBUTION_SOURCE));
        }
        eventInfo.setProperties(null != getValueForOptionalField(recordValue, Constants.FIELD_PROPERTIES) ? (String) getValueForOptionalField(recordValue, Constants.FIELD_PROPERTIES) : null);
        eventInfo.setRecommendationId(null != getValueForOptionalField(recordValue, Constants.FIELD_EVENT_RECOMMENDATION_ID) ? (String) getValueForOptionalField(recordValue, Constants.FIELD_EVENT_RECOMMENDATION_ID) : null);

        //Another optional field in request
        if(null != getValueForOptionalField(recordValue, Constants.FIELD_USER_ID))
            additionalValues.put(Constants.FIELD_USER_ID , (String) getValueForOptionalField(recordValue, Constants.FIELD_USER_ID));

        return eventInfo;
    }

    private static Event prepareEventDataFromMap(Map<String, Object> recordValue, Map<String, String> additionalValues) {
        Event eventInfo = new Event();
        //Processing required fields in request

        if(recordValue.containsKey(Constants.FIELD_EVENT_TYPE))
            eventInfo.setEventType((String)recordValue.get(Constants.FIELD_EVENT_TYPE));
        else
            throw new DataException("Missing Required Data");

        if(recordValue.containsKey(Constants.FIELD_EVENT_TYPE))
            additionalValues.put(Constants.FIELD_SESSION_ID, (String) recordValue.get(Constants.FIELD_SESSION_ID));
        else
            throw new DataException("Missing Required Data");

        if(recordValue.containsKey(Constants.FIELD_EVENT_TIME)){
            String eventTimeStr = null;
            if (recordValue.get(Constants.FIELD_EVENT_TIME) instanceof String) {
                eventTimeStr = (String) recordValue.get(Constants.FIELD_EVENT_TIME);
                eventInfo.setSentAt(new Date(Long.parseLong(eventTimeStr)));
            } else if (recordValue.get(Constants.FIELD_EVENT_TIME) instanceof Long) {
                eventInfo.setSentAt(new Date((Long) recordValue.get(Constants.FIELD_EVENT_TIME)));
            }
        }else
            throw new DataException("Missing Required Data");

        //Optional field for Event Object
        eventInfo.setEventId(null != getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_ID) ? (String) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_ID) : null);
        eventInfo.setEventValue (null != getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_VALUE) ? (Float) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_VALUE) : null);
        eventInfo.setImpression(null != getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_IMPRESSION) ? (Collection<String>) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_IMPRESSION) : null);
        eventInfo.setItemId(null != getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_ITEM_ID) ? (String) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_ITEM_ID) : null);
        if(null != getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_METRIC_ATTRIBUTION_SOURCE)) {
            MetricAttribution metricAttribution = new MetricAttribution();
            metricAttribution.setEventAttributionSource((String) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_METRIC_ATTRIBUTION_SOURCE));
        }

        eventInfo.setProperties(null != getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_PROPERTIES) ? (String) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_PROPERTIES) : null);
        eventInfo.setRecommendationId(null != getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_RECOMMENDATION_ID) ? (String) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_EVENT_RECOMMENDATION_ID) : null);

        //Another optional field in request
        if(null != getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_USER_ID))
            additionalValues.put(Constants.FIELD_USER_ID, (String) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_USER_ID));

        return eventInfo;
    }

    private static Object getValueForOptionalFieldFromMap(Map<String, Object> recordValue, String fieldName) {
        Object valueForField = null;
        if(recordValue != null){
            try{
                valueForField = recordValue.get(fieldName);
            }catch (DataException e){
                valueForField = null;
            }
        }
        return valueForField;
    }

    private static Object getValueForOptionalField(Struct recordValue, String fieldName) {
        Object valueForField = null;
        if(recordValue != null){
            try{
                valueForField = recordValue.get(fieldName);
            }catch (DataException e){
                valueForField = null;
            }
        }
        return valueForField;
    }

    /**************
     * This method is used to convert Sink record to Item Model object
     * @param record
     * @return
     */
    public static Item convertSinkRecordToItemInfo(SinkRecord record) {
        Item itemInfo = null;
        if(record.value() instanceof Struct)
            itemInfo =  prepareItemDataFromStruct((Struct) record.value());
        else
            itemInfo = prepareItemDataFromMap((Map<String, Object>) record.value());
        return  itemInfo;
    }

    private static Item prepareItemDataFromStruct(Struct recordValue) {
        Item itemInfo = new Item();
        try {
            itemInfo.setItemId(recordValue.getString(Constants.FIELD_ITEM_ID));
        } catch (DataException exception) {
            throw new DataException("Missing Required Data");
        }
        itemInfo.setProperties((String) getValueForOptionalField(recordValue, Constants.FIELD_PROPERTIES));
        return itemInfo;
    }

    private static Item prepareItemDataFromMap(Map<String,Object> recordValue) {
        Item itemInfo = new Item();
        try {
            itemInfo.setItemId( (String) recordValue.get(Constants.FIELD_ITEM_ID));
        } catch (Throwable exception) {
            throw new DataException("Missing Required Data");
        }
        itemInfo.setProperties((String) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_PROPERTIES));
        return itemInfo;
    }

    /***************
     * This method is used to convert Sink record to User Model object
     * @param record
     * @return
     */
    public static User convertSinkRecordToUserInfo(SinkRecord record) {
        User userInfo = null;
        if(record.value() instanceof Struct)
            userInfo = prepareUserDataFromStruct((Struct) record.value());
        else
            userInfo = prepareUserDataFromMap((Map<String, Object>) record.value());
        return  userInfo;
    }

    private static User prepareUserDataFromMap(Map<String, Object> recordValue) {
        User userInfo = new User();
        try {
            userInfo.setUserId( (String) recordValue.get(Constants.FIELD_USER_ID));
        } catch (Throwable exception) {
            throw new DataException("Missing Required Data");
        }
        userInfo.setProperties((String) getValueForOptionalFieldFromMap(recordValue, Constants.FIELD_PROPERTIES));
        return userInfo;
    }

    private static User prepareUserDataFromStruct(Struct recordValue) {
        User userInfo = new User();
        try {
            userInfo.setUserId(recordValue.getString(Constants.FIELD_USER_ID));
        }catch (DataException exception) {
            throw new DataException("Missing Required Data");
        }
        userInfo.setProperties( (String) getValueForOptionalField(recordValue, Constants.FIELD_PROPERTIES));
        return userInfo;
    }
}
