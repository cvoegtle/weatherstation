#!/usr/bin/env bash
curl --data-binary @dev_server/sendbuffer.txt http://localhost:8080/weatherstation/upload?location=development\&secret=UHU-Hellas