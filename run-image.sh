#!/usr/bin/env bash

docker run -p 8080:8080 -p 5005:5005 --cpus=1 localhost:5000/antonio/quarkus-reproducer-http-client-classloader:1.0.0-SNAPSHOT
