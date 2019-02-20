@ECHO OFF

javaw -jar -Dspring.config.location=config.properties -Dlogging.path=log dashboard-spring.war

