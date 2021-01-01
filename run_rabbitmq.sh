#!/bin/bash

# para correr o servidor rabbitmq no docker (necessário para correr os programas que envolvem rabbitmq)
#docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
docker run -it --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

