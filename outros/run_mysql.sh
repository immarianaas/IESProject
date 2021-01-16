#!/bin/bash


docker run --name mysql5 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=demo -e MYSQL_USER=demo -e MYSQL_PASSWORD=password -p 3306:3306 -d mysql/mysql-server:5.7


docker run --name mysql_iesproject -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=project -e MYSQL_USER=project -e MYSQL_PASSWORD=password -p 3306:3306 -d mysql/mysql-server:5.7



