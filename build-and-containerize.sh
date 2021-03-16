#!/bin/sh

./mvnw -am clean package

VERSION=$( ./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout )
echo "Project version is $VERSION"

docker image build . -t transactions-validation-service:"${VERSION}"