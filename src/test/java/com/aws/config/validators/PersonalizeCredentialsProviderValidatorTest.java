package com.aws.config.validators;

import com.aws.auth.AssumeRoleCredentialsProvider;
import com.aws.util.TestConstants;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PersonalizeCredentialsProviderValidatorTest {

    private static final String CREDENTIALS_PROVIDER_CONFIG_NAME = "amazon.personalize.credentials.provider.class";
    private static PersonalizeCredentialsProviderValidator personalizeCredentialsProviderValidator;

    @BeforeAll
    public static void setUp() {
        personalizeCredentialsProviderValidator = new PersonalizeCredentialsProviderValidator();
    }


    @Test
    public void testValidClass() {
        //Testing with valid class with implements AWSCredentialsProvider
        Assertions.assertDoesNotThrow(() -> personalizeCredentialsProviderValidator.ensureValid(CREDENTIALS_PROVIDER_CONFIG_NAME, AssumeRoleCredentialsProvider.class));
    }

    @Test
    public void testInValidClass() {
        //Testing with invalid class by passing String constant instead of class which implements AWSCredentialsProvider
        Assertions.assertThrows(ConfigException.class, () -> personalizeCredentialsProviderValidator.ensureValid(CREDENTIALS_PROVIDER_CONFIG_NAME, TestConstants.INVALID_DATASET_TYPE));
    }
}
