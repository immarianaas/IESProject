import pika
import random
import time
import json

import fake_data_generators_nd as gen

def main():
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()

    queue_names = ['co2', 'body_temperature', 'people_counter']
    for q in queue_names:
        channel.queue_declare(q)

    while True:

        data = [gen.fake_co2(), gen.fake_body_temp(), gen.fake_people_counter()]
        for d in range(len(data)):
            channel.basic_publish(
                exchange='',
                routing_key = queue_names[d],
                body=str(data[d])
                )
            time.sleep(1)

        time.sleep(3)

    connection.close()


if __name__ == "__main__":
    main()
