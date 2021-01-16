#!/usr/bin/env python
import pika
from random import random, randint, choice
from time import sleep
import json

import fake_data_generators_nd as gen

def main():
	
	connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
	channel = connection.channel()

	queue2gen = {
		f.replace("fake","queue") : getattr(gen, f) 
			for f in dir(gen)
				if f.startswith("fake_")
	}

	for q in queue2gen:
		channel.queue_declare(queue=q)
	
	while True:
		if randint(0,1):
			random_queue = choice(list(queue2gen.keys()))
			random_generator = queue2gen[random_queue]()

			channel.basic_publish(
				exchange='', 
				routing_key=random_queue, 
				body=json.dumps(random_generator) 
			)
			print("The following was sent through the RabbitMQ:")
			print(f"queue: {random_queue}")
			print(f"data: {random_generator}\n")

		sleep(random()*5)

	connection.close()
	

if __name__=="__main__":
	main()
