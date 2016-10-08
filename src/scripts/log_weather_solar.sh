#!/bin/bash
# Receive remote weather data from USB-WDE1 and send it to Google Appengine

echo URL: $1
echo Location: $2
echo Secret: $3
#register driver for weather logger
# modprobe usbserial || echo "couldnt modprobe usbserial"
# insmod cp210x.ko   || echo "couldnt insmod cp210x.ko"

# Loop forever to read data from USB-WDE1

while true
do
  socat /dev/ttyUSB0,b9600 STDOUT | \
  while read line
    do
      if [[ "${line%%;*}" == '$1' ]] ; then
        timestamp=`date -Iseconds`
	watt=`wget http://192.168.199.113/a -q -T 10 -O - |grep Watt|sed s/\ Watt//|sed s/\,/\./|sed s/^\ //`
	kwh=`wget http://192.168.199.113/a -q -T 10 -O - |grep kWh|sed s/\ kWh//|sed s/^\ //`
        tmp=`echo "${line};${timestamp};${watt};${kwh}"`
        echo $tmp >>/tmp/sendbuffer.txt
	echo $tmp >/tmp/last_weather_watt.txt
#	wget --post-file=/tmp/sendbuffer.txt -O /tmp/wetter_response.txt http://instantwetter.appspot.com/weatherstation/upload?location=instant\&secret=$3
        wget --post-file=/tmp/sendbuffer.txt -O /tmp/wetter_response.txt http://$1/weatherstation/upload?location=$2\&secret=$3
        rc=$?
        if [[ $rc == 0 ]] ; then
          rm /tmp/sendbuffer.txt
        fi
      fi
    done
done
