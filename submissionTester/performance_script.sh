#!/bin/bash
rm result.txt
./script.sh $1 $2
printf "\r"
java Performance
