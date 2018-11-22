#!/usr/bin/env bash

cd `dirname "$0"`

java -jar -Dspring.config.location="./config.properties" target/dashboard-spring.war
