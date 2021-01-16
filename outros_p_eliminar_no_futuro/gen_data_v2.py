import pika
import random
import time
import json
import threading

import fake_data_generators_nd as gen

# salas = ['loja_s7', 'hosp_s22', 'hosp_s33', 'escola_s1', 'escola_s2']

sensores = [
        {'type': 'people_counter', 'sensor_id': 22222, 'center': 10},
        {'type': 'body_temperature', 'sensor_id': 33333},
        {'type': 'co2', 'sensor_id': 11111},
        {'type': 'people_counter', 'sensor_id': 44444, 'center': 2},
        {'type': 'body_temperature', 'sensor_id': 55555},
        {'type': 'co2', 'sensor_id': 66666},
        {'type': 'people_counter', 'sensor_id': 77777, 'center': 5},
        {'type': 'body_temperature', 'sensor_id': 88888},
        {'type': 'co2', 'sensor_id': 99999},
        {'type': 'people_counter', 'sensor_id': 12222, 'center': 20},
        {'type': 'people_counter', 'sensor_id': 13333, 'center':20},
        {'type': 'body_temperature', 'sensor_id': 14444}
    ]

lock = threading.Lock()


def thread_function(sensor, dictt, connection=None, channel=None):
    # connection = pika.BlockingConnection(pika.ConnectionParameters('192.168.160.211'))
    # channel = connection.channel()

    # offset = random.uniform(0, 7)
    offset = random.uniform(0, 34)
    time.sleep(offset)
    period = random.uniform(32, 87)
    while True:
        if sensor['type'] == 'co2':
            data = gen.fake_co2(sensor['sensor_id'])
            time.sleep(period)

        elif sensor['type'] == 'people_counter':
            data = gen.fake_people_counter(sensor['sensor_id'], sensor['center'])
            time.sleep(period)

        elif sensor['type'] == 'body_temperature':
            data = gen.fake_body_temp(sensor['sensor_id'])
            random_sleep_seconds = random.uniform(period-2 if period-2>=0 else 0, period+2)
            time.sleep(random_sleep_seconds)

        lock.acquire()
        try:
            #channel.basic_publish(
            dictt[sensor['sensor_id']]['channel'].basic_publish(
                exchange='',
                routing_key= sensor['type'],
                body= str(data)
            )
            print('sent ' + sensor['type'] +' to sensorid: ' + str(sensor['sensor_id']), ' \t\tvalue: ' + str(data['value']) )
        except Exception as e:
            print('exception here')
            print(e)
            # connection.close()
            # time.sleep(5)
# 
            # connection = pika.BlockingConnection(pika.ConnectionParameters('192.168.160.211'))
            # channel = connection.channel()
        lock.release()
'''    
def execute():
    connection = pika.BlockingConnection(pika.ConnectionParameters('192.168.160.211'))
    channel = connection.channel()

    for s in sensores:
        x = threading.Thread(target=thread_function, args=(s, connection, channel))
        x.start()
'''

def execute():
    d = {}
    for s in sensores:
        connection = pika.BlockingConnection(pika.ConnectionParameters('192.168.160.211'))
        channel = connection.channel()
        d[s['sensor_id']] = {'connection': connection, 'channel': channel }

    for s in sensores:

        x = threading.Thread(target=thread_function, args=(s, d))
        x.start()


def main():
    while True:
        try:
            execute()
        except Exception as e:
            print(e)
            print('exception in main')
            time.sleep(1)

main()
