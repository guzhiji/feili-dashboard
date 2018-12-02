#!/usr/bin/env bash

cd `dirname "$0"`

war=dashboard-spring.war
if [ ! -f "$war" ] ; then
    war="target/$war"
    if [ ! -f "$war" ] ; then
        echo cannot find war file >&2
        exit 1
    fi
fi

logpath="./log"
if [ ! -d "$logpath" ] ; then
    mkdir "$logpath"
fi

java -jar -Dspring.config.location="./config.properties" -Dlogging.path="$logpath" "$war"

