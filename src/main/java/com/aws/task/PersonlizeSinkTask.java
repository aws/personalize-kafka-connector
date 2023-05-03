package com.aws.task;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.retry.RetryPolicy;
import com.amazonaws.services.personalizeevents.AmazonPersonalizeEvents;
import com.amazonaws.services.personalizeevents.AmazonPersonalizeEventsClient;
import com.aws.util.Constants;
import com.aws.util.Version;
import com.aws.writer.DataWriter;
import com.aws.config.PersonalizeSinkConfig;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/****************
 * This is main class which used for Sink Task functionality by overriding required methods.
 */
public class PersonlizeSinkTask extends SinkTask {
    private static final String USER_AGENT_PREFIX = "PersonalizeKafkaSinkConnector";
    private static final Logger log = LoggerFactory.getLogger(PersonlizeSinkTask.class);

    ErrantRecordReporter reporter;
    PersonalizeSinkConfig config;
    DataWriter writer;
    int remainingRetries;

    @Override
    public String version() {
        return new Version().getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting Amazon Personalize Sink task");
        config = new PersonalizeSinkConfig(props);
        initWriter();
        remainingRetries = config.getMaxRetries();
        try {
            reporter = context.errantRecordReporter();
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            // Will occur in Connect runtimes earlier than 2.6
            reporter = null;
        }
    }

    private void initWriter() {
        ClientConfiguration configuration = new ClientConfiguration()
                .withRetryPolicy(new RetryPolicy(PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION, PredefinedRetryPolicies.DEFAULT_BACKOFF_STRATEGY,config.getMaxRetries(),true))
                .withUserAgentPrefix(USER_AGENT_PREFIX)
                .withMaxErrorRetry(config.getMaxRetries());

        AmazonPersonalizeEvents client = AmazonPersonalizeEventsClient.builder()
                .withCredentials(config.getCredentionalProvider())
                .withRegion(null != config.getAwsRegionName() ? config.getAwsRegionName() : Constants.DEFAULT_AWS_REGION_NAME)
                .withClientConfiguration(configuration)
                .build();

        writer = new DataWriter(config, client);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        final SinkRecord first = records.iterator().next();
        final int recordsCount = records.size();
        try {
            writer.write(records);
        } catch (Exception ex) {
            if (reporter != null) {
                unrollAndRetry(records);
            } else {
                log.error("Error in writing data:", ex);
            }
        }
    }

    private void unrollAndRetry(Collection<SinkRecord> records) {
        int retryAttempts = remainingRetries;
        if (retryAttempts > 0) {
            writer.closeQuietly();
            initWriter();
            for (SinkRecord record : records) {
                try {
                    writer.write(Collections.singletonList(record));
                } catch (Exception ex) {
                    reporter.report(record, ex);
                    writer.closeQuietly();
                }
            }
            retryAttempts--;
        }
    }

    @Override
    public void stop() {
        log.info("Stopping Amazon Personalize Sink task");
    }
}
