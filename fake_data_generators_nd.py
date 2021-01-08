import numpy as np
import datetime
import yaml
import time

# np.random.normal(loc=0.0, scale=1.0, size=None)
# loc : float
#		Mean (“centre”) of the distribution.
#
# scale : float
#		Standard deviation (spread or “width”) of the distribution.
#
# size : tuple of ints
#		Output shape. If the given shape is, e.g., (m, n, k), then m * n * k samples are drawn.


def proper_round(num, dec=0):
     num = str(num)[:str(num).index('.')+dec+2]
     if num[-1]>='5':
         return float(num[:-2-(not dec)]+str(int(num[-2-(not dec)])+1))
     return float(num[:-1])

def fake_co2(sensorId, precision=3):
	co2 = {}
	# co2["value"] = proper_round(np.random.normal(loc=700, scale=0.4), 3)
	co2["value"] = proper_round(np.random.normal(loc=500, scale=50), 3)
	co2["timestamp"] = datetime.datetime.now().isoformat()
	co2["sensorId"] = sensorId
	return co2
	
def fake_body_temp(sensorId, precision=3):
	body_temp = {}
	body_temp["value"] = proper_round(np.random.normal(loc=37, scale=0.4), 3)
	body_temp["timestamp"] = datetime.datetime.now().isoformat()
	body_temp["sensorId"] = sensorId
	return body_temp

def fake_people_counter(sensorId, center, area_range=1):
	people_counter = {}
	people_counter["value"] = int(f"{proper_round(np.random.normal(loc=center, scale=1) * area_range)}"[:-2])
	people_counter["timestamp"] = datetime.datetime.now().isoformat()
	people_counter["sensorId"] = sensorId
	return people_counter

def main():

	#while True:
		#print(fake_co2(1111))
		#print(fake_body_temp(22222))
		#print(fake_people_counter(33333, 9))
		#time.sleep(1)

	# testing timestamp....
	'''
	co2 = fake_co2()
	co2_2 = fake_co2()
	print(type(co2["timestamp"]))
	print(yaml.dump(co2_2,default_flow_style=False))
	print(co2["timestamp"]<co2_2["timestamp"])
	'''
	

if __name__=="__main__":
	main()
