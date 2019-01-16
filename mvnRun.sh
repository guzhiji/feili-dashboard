#!/usr/bin/env bash

cd `dirname "$0"`

gulp js

mvn spring-boot:run -Dspring.config.location="./config.properties"
