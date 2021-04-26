#!/bin/sh

set -x

if [ -z "${AWS_LAMBDA_RUNTIME_API}" ]; then
  exec /usr/local/bin/aws-lambda-rie /code/lambda/target/universal/stage/bin/lambda $@
else
  exec /code/lambda/target/universal/stage/bin/lambda $@
fi