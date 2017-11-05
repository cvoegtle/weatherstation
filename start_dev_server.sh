#!/usr/bin/env bash
gradle clean build
java_dev_appserver.sh --generated_dir=../../dev_server --runtime=java8 --jvm_flag=-Xdebug --jvm_flag=-agentlib:jdwp=transport=dt_socket,address=9999,server=y,suspend=n ./build/exploded-weatherstation/