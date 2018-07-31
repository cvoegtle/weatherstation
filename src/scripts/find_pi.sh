#!/bin/bash
# search for devices with open ssh port starting from a certain IP

echo start IP adress ${1}.${2}


for last in `seq ${2} 255`;
do
    echo try pi@${1}.${last}
    ssh pi@${1}.${last}
done

