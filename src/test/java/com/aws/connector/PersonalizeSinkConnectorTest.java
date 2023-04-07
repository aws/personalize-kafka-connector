package com.aws.connector;

import com.aws.util.TestConstants;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.sink.SinkConnector;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static com.aws.util.TestConstants.configVals;
import static com.aws.util.Constants.AMAZON_PERSONALIZE_EVENT_TRACKING_ID;
import static com.aws.util.Constants.AWS_REGION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.junit.jupiter.api.Assertions.*;

public class PersonalizeSinkConnectorTest {

    @Mock
    private ConnectorContext context;

    @Test
    void testStartAndStop() {
        final PersonalizeSinkConnector connector = createConnector();
        connector.start(configVals);
        assertThat(connector.taskConfigs(1)).hasSize(1);
        assertThat(connector.taskConfigs(10)).hasSize(10);
    }

    @Test
    void testConfig() {
        final PersonalizeSinkConnector connector = createConnector();
        connector.start(configVals);
        assertThat(connector.taskConfigs(1).get(0)).contains(
                entry(AWS_REGION_NAME, TestConstants.TEST_AWS_REGION),
                entry(AMAZON_PERSONALIZE_EVENT_TRACKING_ID, TestConstants.TEST_EVENT_TRACKING_ID));
    }

    @Test
    public void testVersion() {
        String version = new PersonalizeSinkConnector().version();
        System.out.println(version);
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @Test
    public void connectorType() {
        Connector connector = new PersonalizeSinkConnector();
        assertTrue(SinkConnector.class.isAssignableFrom(connector.getClass()));
    }

    private PersonalizeSinkConnector createConnector() {
        final PersonalizeSinkConnector connector = new PersonalizeSinkConnector();
        connector.initialize(context);
        return connector;
    }
}
