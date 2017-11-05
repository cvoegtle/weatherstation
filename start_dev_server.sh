#!/usr/bin/env bash
gradle clean build
java_dev_appserver.sh --generated_dir=../../dev_server --runtime=java8 ./build/exploded-weatherstation/