#!/bin/bash

# sshdの機能
sudo /usr/sbin/sshd -D &

# コンテナの停止を抑止
sleep infinity
