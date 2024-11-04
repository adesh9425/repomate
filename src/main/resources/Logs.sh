#!/bin/bash



# Function to ensure directory and file permissions
ensure_permissions() {
  local dir=$1
  local file=$2

  # Ensure log directory exists and has appropriate permissions
  mkdir -p $dir
  chmod 755 $dir

  # Ensure log file has appropriate permissions
  if [ -f "$file" ]; then
    rm "$file"
    echo "Previous log file deleted."
  fi
  touch $file
  chmod 644 $file
}


# Check if environment variables are set, otherwise prompt for input
if [ -z "$ENV" ]; then
  read -p "Enter env name: " ENV
fi

if [ -z "$name" ]; then
  read -p "Enter part of the pod name to search for: " name
fi

if [ -z "$type" ]; then
  read -p "Enter part of the pod type to search for: " type
fi

# Update kubeconfig


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




# Append "web" to the given substring
pod_name_search="${name}-${type}"

# Fetch all pods and filter by the provided substring
pods_info=$(kubectl get pods --all-namespaces | grep $pod_name_search)

# Check if any pods were found
if [ -z "$pods_info" ]; then
  echo "No pods found with the substring '$pod_name_search'."
  exit 1
fi

# Extract the namespace and name of the first matching pod
namespace=$(echo $pods_info | awk '{print $1}')
pod_name=$(echo $pods_info | awk '{print $2}')

# Concatenate namespace and pod name
result="${namespace}+${pod_name}"

# Print the result
echo $result

# Stream logs from the pod
eval kubectl logs $pod_name -n $namespace -f