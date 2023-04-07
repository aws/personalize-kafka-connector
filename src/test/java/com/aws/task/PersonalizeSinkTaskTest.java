package com.aws.task;

import com.aws.util.TestConstants;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTaskContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class PersonalizeSinkTaskTest {

    private final static Map<String, Object> VALUE = TestConstants.sampleValsForEventData;
    private final static String KEY = "key";

    @Test
    void testTask() {
        final PersonlizeSinkTask task = createSinkTask();
        Assertions.assertDoesNotThrow(() -> task.start(TestConstants.configVals));
        Assertions.assertDoesNotThrow(() -> task.put(Collections.singletonList(createRecord())));
        Assertions.assertDoesNotThrow(() -> task.stop());
    }

    @Test
    void testValidConfig(){
        final PersonlizeSinkTask task = createSinkTask();
        Assertions.assertDoesNotThrow(() ->task.start(TestConstants.configVals));
        Assertions.assertDoesNotThrow(() -> task.stop());
    }

    @Test
    void testInValidConfig(){
        final PersonlizeSinkTask task = createSinkTask();
        Assertions.assertThrows(ConfigException.class, () ->task.start(TestConstants.invalidConfigVals));
        Assertions.assertDoesNotThrow(() -> task.stop());
    }

    private SinkRecord createRecord() {
        return new SinkRecord("topic", 0, null, KEY, null, VALUE, 1);
    }

    private PersonlizeSinkTask createSinkTask() {
        final PersonlizeSinkTask sinkTask = new PersonlizeSinkTask();
        SinkTaskContext mockContext = mock(SinkTaskContext.class);
        ErrantRecordReporter reporter = mock(ErrantRecordReporter.class);
        when(mockContext.errantRecordReporter()).thenReturn(reporter);
        sinkTask.initialize(mockContext);
        return sinkTask;
    }
}
