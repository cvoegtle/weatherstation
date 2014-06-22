#!/bin/sh
# Receive remote weather data from USB-WDE1 and send it to Google Appengine

echo URL: $1
echo Location: $2
echo Secret: $3
#register driver for weather logger
modprobe usbserial || echo "couldnt modprobe usbserial"
insmod cp210x.ko   || echo "couldnt insmod cp210x.ko"

# Loop forever to read data from USB-WDE1

while true
do
  LD_LIBRARY_PATH="/tmp/fritz" ./socat /dev/ttyUSB0,b9600 STDOUT | \
  while read line
    do
      if [[ "${line%%;*}" == '$1' ]] ; then
        tmp=`echo "${line}"`
        wget -O wetter_response.txt http://$1/weatherstation/incoming?data=$tmp\&location=$2\&secret=$3
      fi
    done
    sleep 5
done
