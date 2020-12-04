# sudo python3 -m pip install pika --upgrade

import pika 

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue='data')

channel.basic_publish(
    exchange='',
    routing_key = 'data',
    body = 'omg q giro'
)

print(" [x] Sent 'please send help'")

# to make sure that the network buffers were flushed and 
# our message was actually delivered to RabitMQ:
connection.close()