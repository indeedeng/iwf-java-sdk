#!/bin/bash

touch /dependencies/temporalite.log &&  \
    /dependencies/temporalite start --namespace default --ephemeral --ip 0.0.0.0 &> /dependencies/temporalite.log &

while ! netstat -tna | grep 'LISTEN\>' | grep -q ':7233\>'; do sleep 1; done

#start workflow server
./iwf-server start