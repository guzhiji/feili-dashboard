#!/usr/bin/env bash

cd `dirname "$0"`

gulp clean js sass

mvn spring-boot:run -Dspring.config.location="./config.properties"
