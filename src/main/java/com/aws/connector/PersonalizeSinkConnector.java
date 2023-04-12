package com.aws.connector;

import com.aws.config.PersonalizeSinkConfig;
import com.aws.task.PersonlizeSinkTask;
import com.aws.util.Constants;
import com.aws.util.Version;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****************
 * This is main class which used for Sink connector functionality by overriding required methods.
 */
public class PersonalizeSinkConnector extends SinkConnector {

    private static final Logger log = LoggerFactory.getLogger(PersonalizeSinkConnector.class);

    private Map<String, String> configProps;

    private PersonalizeSinkConfig config;

    @Override
    public void start(Map<String, String> props) {
        configProps = new HashMap<>(props);
        addRecordRateLimitConfiguration(configProps);
        config = new PersonalizeSinkConfig(props);
        log.info("Starting Personalize connector with name :", config.getName());
    }

    private void addRecordRateLimitConfiguration(Map<String, String> configProps) {
        if (!configProps.containsKey(Constants.RECORD_RATE_LIMIT_PROPERTY_NAME)) {
            int maxTask = 1;
            if (configProps.containsKey(Constants.MAX_TASKS_PROPERTY_NAME)) {
                maxTask = Integer.parseInt(configProps.get(Constants.MAX_TASKS_PROPERTY_NAME));
            }
            String dataType = configProps.get(Constants.AMAZON_PERSONALIZE_DATA_TYPE);
            if (null == dataType || dataType.equals(Constants.PUT_EVENTS_REQUEST_TYPE)) {
                configProps.put(Constants.RECORD_RATE_LIMIT_PROPERTY_NAME, String.valueOf(Constants.DEFAULT_VALUE_FOR_EVENT_RECORD_RATE / maxTask));
            } else {
                configProps.put(Constants.RECORD_RATE_LIMIT_PROPERTY_NAME, String.valueOf(Constants.DEFAULT_VALUE_FOR_ITEM_AND_USER_RECORD_RATE / maxTask));
            }
        }
    }

    @Override
    public Class<? extends Task> taskClass() {
        return PersonlizeSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        log.info("Setting task configurations for {} workers.", maxTasks);
        final List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; ++i) {
            configs.add(configProps);
        }
        return configs;
    }

    @Override
    public void stop() {
        log.info("Shutting Down Personalize Connector with name :" + config.getName());
    }

    @Override
    public ConfigDef config() {
        return PersonalizeSinkConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return new Version().getVersion();
    }
}
