#!/bin/bash

minikube status | grep -q 'host: Running' || minikube start
eval $(minikube docker-env)

export JAVA_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED"
./mvnw clean package -DskipTests

docker build -t legal-management-system:latest .

if kubectl get deployment legal-management-system &> /dev/null; then
  echo "Scaling down existing legal-management-system deployment to 0 replicas..."
  kubectl scale deployment legal-management-system --replicas=0

  echo "Waiting for existing pods to terminate..."
  while kubectl get pods -l app=legal-management-system 2>/dev/null | grep -q .; do
    echo "Pods still exist..."
    sleep 2
  done
fi

kubectl apply -f k8-config/mysql.yaml
kubectl apply -f k8-config/redis.yaml
kubectl apply -f k8-config/app.yaml

kubectl get pods -l app=legal-management-system
echo "Waiting for legal-management-system pods to be Running..."
while ! kubectl get pods -l app=legal-management-system --field-selector=status.phase=Running | grep -q .; do
  echo "No running pods yet..."
  sleep 2
done

kubectl port-forward service/legal-management-system 8080:80