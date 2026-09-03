#!/bin/bash

USER_ID=${PUID:-1000}
GROUP_ID=${PGID:-1000}

groupmod -o -g "$GROUP_ID" raven
usermod -o -u "$USER_ID" raven

mkdir -p /data
chown -R raven:raven /data /app

exec gosu raven java -jar app.jar