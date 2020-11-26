@echo off
color 02

java -Dfile.encoding=UTF-8 -Xbootclasspath/a:. -jar ../target/new-asr-engine-1.0.0.jar -Dspring.config.file=application.properties