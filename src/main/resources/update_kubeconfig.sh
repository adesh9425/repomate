#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <environment>"
    exit 1
fi

ENV=$1

# Base64 encoded keys
INTEGRATION_ACCESS_KEY_ID="QUtJQTZRTjRONlM2TTdOT0dNQlQ="
INTEGRATION_SECRET_KEY="L0hvTlFESG1WWHZjQ2lRNEhPRnEwdXlWU2kvbjQ4a1V4ZytpUXd1cA=="

TEST_ACCESS_KEY_ID="QUtJQTQ3Q1IzQUZNM0JVWVZZWkk="
TEST_SECRET_KEY="OFJvYXlzWloyV3dtSFN2bHhXWTVyZnp3anE4cVBYQ3JTR3RNU2FCaQ=="

RELEASE_ACCESS_KEY_ID="QUtJQVZSVVZQM0xIUkhNNlRCT1g="
RELEASE_SECRET_KEY="MUl1UCtuVGVud2VtYVl6VHlta0xUWW84UnMvUUtmSWRTNStncUpKVg=="

REN_ACCESS_KEY_ID="QUtJQVpCWTdTS0taQUE3UUtLVTI="
REN_SECRET_KEY="L3lJMTRvTTI4cFRaQitIb0llZGJmT09kTVVpcUk5SXdTQkhOVEg5OA=="

if [ "$ENV" == "integration" ]; then
    ACCESS_KEY_ID=$(echo $INTEGRATION_ACCESS_KEY_ID | base64 --decode)
    SECRET_KEY=$(echo $INTEGRATION_SECRET_KEY | base64 --decode)
    CLUSTER_NAME="integration-core-product-eks-cluster"
    PROFILE_NAME="integration-profile"
elif [ "$ENV" == "test" ]; then
    ACCESS_KEY_ID=$(echo $TEST_ACCESS_KEY_ID | base64 --decode)
    SECRET_KEY=$(echo $TEST_SECRET_KEY | base64 --decode)
    CLUSTER_NAME="test-core-product-eks-cluster"
    PROFILE_NAME="test-profile"
elif [ "$ENV" == "release" ]; then
    ACCESS_KEY_ID=$(echo $RELEASE_ACCESS_KEY_ID | base64 --decode)
    SECRET_KEY=$(echo $RELEASE_SECRET_KEY | base64 --decode)
    CLUSTER_NAME="release-core-product-eks-cluster"
    PROFILE_NAME="release-profile"
elif [ "$ENV" == "ren-test" ]; then
    ACCESS_KEY_ID=$(echo $REN_ACCESS_KEY_ID | base64 --decode)
    SECRET_KEY=$(echo $REN_SECRET_KEY | base64 --decode)
    CLUSTER_NAME="ren-test-brokerportal-eks-cluster"
    PROFILE_NAME="ren-test-profile"
else
    echo "Unknown environment: $ENV"
    exit 1
fi

aws configure set aws_access_key_id $ACCESS_KEY_ID --profile $PROFILE_NAME
aws configure set aws_secret_access_key $SECRET_KEY --profile $PROFILE_NAME
aws configure set region us-east-1 --profile $PROFILE_NAME

aws eks --region us-east-1 update-kubeconfig --name $CLUSTER_NAME --profile $PROFILE_NAME
