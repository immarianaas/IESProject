import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
               '192.168.160.211'))
channel = connection.channel()

channel.queue_delete(queue='co2')
channel.queue_delete(queue='body_temperature')
channel.queue_delete(queue='people_counter')

connection.close()
