#!/bin/bash
j=1
for i in $(eval echo {1..$2})
do
java -Djava.library.path="." -jar testrun.jar -submission=player27 -evaluation=Function$1 -seed=$RANDOM >> result.txt
printf "\r$i runs done over $2 total run"
done
