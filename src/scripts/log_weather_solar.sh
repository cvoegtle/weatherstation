#!/bin/bash
# Receive remote weather data from USB-WDE1 and send it to Google Appengine

echo URL: $1
echo Location: $2
echo Secret: $3
wetter=/home/pi/wetter
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
      echo $tmp >>${wetter}/sendbuffer.txt
      echo $tmp >/tmp/last_weather_watt.txt
      wget --timeout=90 --post-file=${wetter}/sendbuffer.txt -O ${wetter}/wetter_response.txt http://$1/weatherstation/upload?location=$2\&secret=$3
      rc=$?
      if [[ $rc == 0 ]] ; then
        rm ${wetter}/sendbuffer.txt
      else
        endtime=`date -Iseconds`
        echo start: $timestamp, end: $endtime, rc: $rc >>${wetter}/error.log
      fi
      read response < ${wetter}/wetter_response.txt 
      echo response = $response
      if [[ $response != 'ACK' ]] ; then
        echo reset receiver
        break 
      fi
    fi
  done
  echo restart socat
  sleep 10
done
