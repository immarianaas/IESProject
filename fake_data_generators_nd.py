import numpy as np
import datetime
import yaml

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

def fake_co2(precision=3):
	co2 = dict()

	# in case there is only one queue:
	# co2["data_type"] = "co2"
	co2["value"] = proper_round(np.random.normal(loc=700, scale=0.4), 3)
	co2["timestamp"] = datetime.datetime.now().isoformat() #.replace(microsecond=0)
	print(co2['timestamp'])
	co2["local"] = "Building A Room A"
	co2["sensorId"] = 1
	# co2["type"] = "co2"

	return co2
	
def fake_body_temp(precision=3):
	body_temp = dict()

	# in case there is only one queue:
	# co2["data_type"] = "body_temp"
	body_temp["value"] = proper_round(np.random.normal(loc=36.8, scale=0.4), 3)
	body_temp["timestamp"] = datetime.datetime.now().isoformat()
	body_temp["local"] = "Building A Room B"
	body_temp["sensorId"] = 2
	# body_temp["type"] = "body_temperature"

	return body_temp

def fake_people_counter(area_range=1):
	people_counter = dict()

	# in case there is only one queue:
	# co2["data_type"] = "people_counter"
	people_counter["value"] = int(f"{proper_round(np.random.normal(loc=5, scale=0.4) * area_range)}"[:-2])
	people_counter["timestamp"] = datetime.datetime.now().isoformat()
	people_counter["local"] = "Building A Room B"
	people_counter["sensorId"] = 3
	#people_counter["type"] = "people_counter"

	return people_counter

def main():

	# testing timestamp....

	co2 = fake_co2()
	co2_2 = fake_co2()
	print(type(co2["timestamp"]))
	print(yaml.dump(co2_2,default_flow_style=False))
	print(co2["timestamp"]<co2_2["timestamp"])
	

if __name__=="__main__":
	main()
