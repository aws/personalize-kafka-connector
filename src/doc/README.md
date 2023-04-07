# Kafka Connector for Amazon Personalize

Kafka connector used to send real time data to different dataset from kafka topics to Amazon Personalize.

## Usage

This connector is used to send real time data for different dataset to Amazon Personalize. Data should be in structured format.

## Configuration

These are sample configuration which is required for configuring connector.

Configure personalize region and event tracking id/dataset ARN for which personalize data needs to be sent.

    aws.region.name=<AWS region>
    # Amazon Personalize Data type for which this connector sending data. Valid Values : events, users, items. Default value is events if not specified.
    amazon.personalize.data.type = events/users/items
    # Amazon Personalize Event Tracking Id , it is required when Data Type value configured as events
    amazon.personalize.event.tracking.id = <Amazon Personalize Event Tracking ID>
    # ARN for Items/Users Dataset. Required when Data type configured as items or users
    amazon.personalize.dataset.arn = <ARN for Item/User Dataset>

## AWS Credentials

This following sections provide information about how to configure credentials for Amazon Personalize Connector to provide AWS credentials.

### Credentials Provider Chain

AWS credentials should be provided by referring to following way.

https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-default

### Using Trusted account credentials

This connector can assume a role and use credentials from a separate trusted account. If this is intended you need to provide below additional configuration.

    amazon.personalize.credentials.provider.class=com.aws.auth.AssumeRoleCredentialsProvider
    amazon.personalize.sts.role.arn=<ARN for assume role>
    amazon.personalize.sts.role.external.id=<STS External Id>
    amazon.personalize.sts.role.session.name=<Session Name for assumed role>

## Expected Data Formats for Different Data Set

This connector expect data in specific format.

If you want to transform data to match the required data format, use the transformations in the following link https://kafka.apache.org/documentation/#connect_included_transformation.

### Expected Data format for events data type.

Please refer - https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_Event.html for field description.

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

Please refer - https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_User.html for field description.

        {
            "userId": "string",
            "properties": "string"
        }

### Expected Data format for items data type.

Please refer - https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_Item.html for field description.

        {
            "itemId": "string",
            "properties": "string"
        }

## Custom Transformation

As properties field should be a string map of data-set-specific metadata. 
Each element in the map consists of a key-value pair. 
For example, {"numberOfRatings": "12","numberOfLikes" : "5"}. 

We have added Custom transformation to create properties field value shown as above.

e.g. if Kafka topic has data like

    {
        "itemId" :"item1",
        "numberOfRatings": "12",
        "numberOfLikes" : "5"
    }

We can use below transformation send data like

    {
        "itemId" :"item1",
        "properties":"{"numberOfRatings": "12","numberOfLikes" : "5"}"    
    }

Sample transformation Properties to achieve this.

    transforms = addproperties
    transforms.addproperties.type = com.aws.transform.CombineFieldsToJSONTransformation$Value
    transforms.addproperties.fieldsToInclude = numberOfRatings,numberofLikes
    transforms.addproperties.targetFieldName = properties

## Service quotas and limit

As Amazon Personalize has limits on the rate at which you can send data, add below property in configuration in order to conform to the limits. More details on limits can be found here - https://docs.aws.amazon.com/personalize/latest/dg/limits.html#limits-table

Please add below property in configuration to throttle or limit data send to personalize if you have changed service limit and quotas for your AWS account.

    record.rate.limit = <Maximum rate of Put* API (PutEvents/PutItems/PutUsers) requests / number of tasks>

Defaults are 1000 for Event DataSet and 10 for User and Items Dataset which is automatically set in connector plugin.

## License

Kafka connect personalize is available under [Apache License, Version 2.0](https://aws.amazon.com/apache2.0).


----

Copyright Amazon.com Inc. or its affiliates. All Rights Reserved.
