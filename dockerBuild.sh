#!/usr/bin/env bash

gulp js sass
mvn clean package
docker build -t guzhiji/feili-dashboard .

