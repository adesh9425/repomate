#!/bin/bash

check_and_install() {
  if ! command -v $1 &> /dev/null; then
    echo "$1 could not be found, installing..."
    brew install $1
  else
    echo "$1 is already installed."
  fi
}

# Check if required tools are installed
check_and_install awscli
check_and_install kubectl

# Decode ACCESS_KEY and SECRET_KEY only if needed
ACCESS_KEY=$(echo "$ACCESS_KEY_ID" | base64 --decode)
SECRET_KEY=$(echo "$SECRET_KEY_ID" | base64 --decode)

# AWS CLI configuration using the provided profile
aws configure set aws_access_key_id "$ACCESS_KEY" --profile "$PROFILE_NAME"
aws configure set aws_secret_access_key "$SECRET_KEY" --profile "$PROFILE_NAME"
aws configure set region us-east-1 --profile "$PROFILE_NAME"

# Update kubeconfig for EKS
aws eks --region us-east-1 update-kubeconfig --name "$CLUSTER_NAME" --profile "$PROFILE_NAME"

# Append "web" to the given substring if needed
pod_name_search="${name}${type}"

# Fetch all running pods and filter by the provided substring
pods_info=$(kubectl get pods --all-namespaces --field-selector=status.phase=Running | grep "$pod_name_search")

# Check if any running pods were found
if [ -z "$pods_info" ]; then
  echo "No running pods found with the substring '$pod_name_search'."
  exit 1
fi

# Extract the namespace and pod name
namespace=$(echo "$pods_info" | awk '{print $1}')
pod_name=$(echo "$pods_info" | awk '{print $2}')

# If extraction fails, add a fallback check
if [ -z "$namespace" ] || [ -z "$pod_name" ]; then
  echo "Failed to extract namespace or pod name."
  exit 1
fi

# Concatenate namespace and pod name and print it
result="${namespace}+${pod_name}"
echo "Result: $result"

# Stream logs from the running pod
kubectl logs "$pod_name" -n "$namespace" -f