#!/usr/bin/env python
import pika
from random import random, randint
from time import sleep

import fake_data_generators_nd as gen

def main():
	
	connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
	channel = connection.channel()

	# in case there's only one queue, comment lines 18-24 and uncomment this:
	# channel.queue_declare(queue='queue')
	# generators = [gen.fake_co2, gen.fake_body_temp, gen.fake_people_counter]
	
	# every queue name must start with queue!
	queues=['queue_co2', 'queue_body_temp', 'queue_people_counter']
	for i in range(len(queues)):
		channel.queue_declare(queue=queues[i])

	queue2gen = dict()
	for q in queues:
		queue2gen[q]=getattr(gen, "fake"+q[5:])
		
	while True:
		
		# comment these 2 lines if only one queue is used:
		random_queue = queues[randint(0,len(queues))]
		random_generator = queue2gen[random_queue]

		if randint(0,1):
			channel.basic_publish(
				exchange='', 
				routing_key=random_queue, # or 'queue', if there's only one queue
				body=random_generator # or generators[randint(0, len(generators))], if there's only one queue 
			)

		sleep(random(0,5))

	connection.close()
	

if __name__=="__main__":
	main()
