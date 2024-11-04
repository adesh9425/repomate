#!/bin/bash

# Check if environment variables are set, otherwise prompt for input
if [ -z "$env" ]; then
  read -p "Enter env name: " env
fi

if [ -z "$name" ]; then
  read -p "Enter part of the pod name to search for: " name
fi

if [ -z "$type" ]; then
  read -p "Enter part of the pod type to search for: " type
fi

# Update kubeconfig
sh update_kubeconfig.sh $env

# Append the type to the given substring
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

# Get the service associated with the pod
service_info=$(kubectl get svc --namespace $namespace | grep $name)

# Check if any services were found
if [ -z "$service_info" ]; then
  echo "No services found for the pod '$pod_name'."
  exit 1
fi

# Extract the service name and the node port
service_name=$(echo $service_info | awk '{print $1}')
node_port=$(kubectl get svc $service_name --namespace $namespace -o jsonpath='{.spec.ports[0].nodePort}')

# Get the cluster IP of the service
cluster_ip=$(kubectl get svc $service_name --namespace $namespace -o jsonpath='{.spec.clusterIP}')

# Print the hosted URL
hosted_url="http://$cluster_ip:$node_port"
echo "Hosted URL: $hosted_url"