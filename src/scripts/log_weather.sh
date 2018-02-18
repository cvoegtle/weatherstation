#!/bin/bash
# Receive remote weather data from USB-WDE1 and send it to Google Appengine

echo URL: $1
echo Location: $2
echo Secret: $3
tmp=$4
frequency=${tmp:=1}
echo Frequency: $frequency
wetter=/home/pi/wetter
test -f /etc/openwrt_release && wetter=/root
# Loop forever to read data from USB-WDE1

while true
do
  usbdevices=$(ls /dev/ttyUSB* | tr " " "\n")
  for device in $usbdevices
  do
    echo connect to device ${device}
    socat -T3600 ${device},b9600 STDOUT | \
    while read line
    do
      if [[ "${line%%;*}" == '$1' ]] ; then
        timestamp=`date -Iseconds`
        tmp=`echo "${line};${timestamp}"`
        echo $tmp >>${wetter}/sendbuffer.txt
        linecount=$(wc -l < "${wetter}/sendbuffer.txt")
        if [[ $(($linecount % $frequency)) == 0 ]] ; then
          wget --timeout=90 --post-file=${wetter}/sendbuffer.txt -O ${wetter}/wetter_response.txt http://$1/weatherstation/upload?location=$2\&secret=$3 2>${wetter}/wget_std_err.log
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
      fi
    done
  done
  restarttime=`date -Iseconds`
  echo $restarttime restart socat $? >>${wetter}/error.log
  sleep 10
done
