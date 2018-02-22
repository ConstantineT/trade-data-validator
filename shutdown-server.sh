#!/bin/bash
COMMAND="curl -s -X POST http://localhost:8080/shutdown"

RESPONSE=`$COMMAND`
echo -e "Response:\n$RESPONSE"