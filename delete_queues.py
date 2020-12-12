import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               'localhost'))
channel = connection.channel()

channel.queue_delete(queue='co2')

connection.close()
