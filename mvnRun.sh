#!/usr/bin/env bash

cd `dirname "$0"`

mvn spring-boot:run -Dspring.config.location="./config.properties"
