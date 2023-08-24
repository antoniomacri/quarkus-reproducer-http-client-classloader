#!/usr/bin/env bash

./mvnw clean package -DskipTests=true \
  -Dquarkus.container-image.registry=localhost:5000 \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.push=false \
  -Dquarkus.container-image.insecure=true
