# Kafka Connector for Amazon Personalize

Amazon Personalize is a fully managed machine learning service that uses your data to generate item recommendations for your users. When you import data, you can choose to import records in bulk, or with our [real-time APIs](https://docs.aws.amazon.com/personalize/latest/dg/data-prep.html). For customers that use Kafka, we've set up the Amazon Personalize Kafka Connector to send real-time data from kafka topics to your Amazon Personalize Dataset.

## Usage

### Prerequisites:
1. Follow the steps listed in the [Amazon Personalize Documentation](https://docs.aws.amazon.com/personalize/latest/dg/personalize-workflow-full.html) to make sure you have the required Amazon Personalize resources created.
2. Create a new, or use an existing, self-managed kafka cluster.

### Setup:
1. Clone this repository, and run the ```mvn clean package``` command locally to create the uber jar/zip file.
2. Place the jar/extracted zip in your Kafka plugin directory. Please refer to the [Kafka Documentation](https://kafka.apache.org/documentation/#connectconfigs_plugin.path) for the plugin path configuration.
3. Create a configuration file for the connector. Please refer to the Configuration section below for more details.
4. Create connector by following the [Kafka Connector Rest API documentation](https://kafka.apache.org/0100/documentation.html#connect_rest). Once the connector starts running, it will ingest data from your Kafka topic into the Amazon Personalize Dataset you specified in your configuration file.
6. To verify the data is populated in Amazon Personalize as expected, [run an analysis](https://docs.aws.amazon.com/personalize/latest/dg/analyzing-data.html) on the ingested dataset or follow the steps to [export a dataset](https://docs.aws.amazon.com/personalize/latest/dg/export-data.html) from Amazon Personalize.
7. Congrats! Your connector setup is complete 🎉

## Configuration

This is a sample configuration which is required for configuring your connector.

Add the following details:
1. Preferred AWS region.
2. The Amazon Personalize Dataset type for data ingestion, should be "items", "users" or "events".
3. The Amazon Personalize Dataset [event tracking ID](https://docs.aws.amazon.com/personalize/latest/dg/importing-interactions.html#event-tracker-console), which is required if the Dataset Type is "events".
4. The Amazon Personalize Dataset ARN, which is required if the Dataset Type is "items" or "users".

```
    aws.region.name=<AWS region>
    # Amazon Personalize Data type for which this connector sending data. Valid Values : events, users, items. Default value is events if not specified.
    amazon.personalize.data.type = events/users/items
    # Amazon Personalize Event Tracking Id , it is required when Data Type value configured as events
    amazon.personalize.event.tracking.id = <Amazon Personalize Event Tracking ID>
    # ARN for Items/Users Dataset. Required when Data type configured as items or users
    amazon.personalize.dataset.arn = <ARN for Item/User Dataset>
```

## AWS Credentials

The following sections provide information on how to configure AWS credentials for the Amazon Personalize Kafka Connector.

### Credentials Provider Chain

AWS credentials should be provided through one of the methods provided in the [AWS SDK for Java documentation](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-default).


### Using Trusted account credentials

This connector can assume a role and use credentials from a separate trusted account. If this is intended, you need to provide addititional configuration parameters, specified below.

    amazon.personalize.credentials.provider.class=com.aws.auth.AssumeRoleCredentialsProvider
    amazon.personalize.sts.role.arn=<ARN for assume role>
    amazon.personalize.sts.role.external.id=<STS External Id>
    amazon.personalize.sts.role.session.name=<Session Name for assumed role>

## Expected Data Formats for Different Data Set

The Amazon Personalize Kafka Connector expects data in a specific format.

If you want to transform data to match the required data format, use the transformations techniques provided within the [Apache Kafka documentation](https://kafka.apache.org/documentation/#connect_included_transformation).

### Expected Data format for events data type.

See the [Amazon Personalize Event API documentation](https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_Event.html) for more details.

    { 
         "eventId": "string",
         "eventType": "string",
         "eventValue": number,
         "impression": [ "string" ],
         "itemId": "string",
         "eventAttributionSource": "string",
         "properties": "string",
         "recommendationId": "string",
         "sentAt": number,
         "sessionId": "string",
         "userId": "string"
    }

### Expected Data format for users data type.

See the [Amazon Personalize User API documentation](https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_User.html) for more details.

        {
            "userId": "string",
            "properties": "string"
        }

### Expected Data format for items data type.

See the [Amazon Personalize Item API documentation](https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_Item.html) for more details.

        {
            "itemId": "string",
            "properties": "string"
        }

## Custom Transformations

Since the properties field needs to be a "stringified" JSON containing fields related to your Amazon Personalize Dataset, we have created a Custom Transformation to create the properties field to transform data from a regular JSON value such as:

    {
        "itemId" :"item1",
        "numberOfRatings": "12",
        "numberOfLikes" : "5"
    }

into a "stringified" JSON:

    {
        "itemId" :"item1",
        "properties":"{"numberOfRatings": "12","numberOfLikes" : "5"}"    
    }

Here are sample transformation properties to use the Custom Transformation.

    transforms = addproperties
    transforms.addproperties.type = com.aws.transform.CombineFieldsToJSONTransformation$Value
    transforms.addproperties.fieldsToInclude = numberOfRatings,numberofLikes
    transforms.addproperties.targetFieldName = properties

## Service quotas and limit

Amazon Personalize has limits on the rate at which you can send data. Update your configuration in order to conform to the limits. More details on limits can be found in our [documentation](https://docs.aws.amazon.com/personalize/latest/dg/limits.html#limits-table)

Please add the following configuration to throttle or limit data sent to Amazon Personalize if you have changed the service limit and quotas for your AWS account.

    record.rate.limit = <Maximum rate of Put* API (PutEvents/PutItems/PutUsers) requests / number of tasks>

The current defaults, preconfigured in the connector plugin, are 1000 requests/second for an Event Dataset and 10 requests/second for Users and Items Dataset.

## License

The Amazon Personalize Kafka Connector is available under [Apache License, Version 2.0](https://aws.amazon.com/apache2.0).


----

Copyright Amazon.com Inc. or its affiliates. All Rights Reserved.
