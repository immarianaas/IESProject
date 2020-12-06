#!/usr/bin/env python
import pika
from random import random, randint
import fake_data_generators_nd as gen

def main():
	connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
	channel = connection.channel()

	channel.queue_declare(queue='queue_co2')
	channel.queue_declare(queue='queue_body_temp')
	channel.queue_declare(queue='queue_people_counter')

	channel.basic_publish(
		exchange='', 
		routing_key='queue_co2', 
		body=gen.fake_co2()
	)

	channel.basic_publish(
		exchange='', 
		routing_key='queue_body_temp', 
		body=gen.fake_body_temp()
	)

	channel.basic_publish(
		exchange='', 
		routing_key='queue_people_counter', 
		body=gen.fake_people_counter()
	)

	connection.close()

	# testing data generators...
	'''
	print("CO2:")
	for i in range(50):
		print(fake_co2(), end="")

	print("\n\nBody temperature:")
	for i in range(50):
		print(fake_body_temp(), end="")

	print("\n\nPeople counter:")
	for i in range(50):
		print(fake_people_counter(),end="")

	print()
	'''

if __name__=="__main__":
	main()
