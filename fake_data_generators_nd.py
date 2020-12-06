import numpy as np

# np.random.normal(loc=0.0, scale=1.0, size=None)
# loc : float
#		Mean (“centre”) of the distribution.
#
# scale : float
#		Standard deviation (spread or “width”) of the distribution.
#
# size : tuple of ints
#		Output shape. If the given shape is, e.g., (m, n, k), then m * n * k samples are drawn.

def fake_co2(precision=3):
	return f"{np.random.normal(loc=700, scale=0.4):.{precision}f}"
	
def fake_body_temp(precision=3):
	return f"{np.random.normal(loc=36.8, scale=0.4):.{precision}f}"

def fake_people_counter(area_range=1):
	return f"{int(np.random.normal(loc=5, scale=0.4) * area_range)}"

def main():	

	# testing data generators...

	print(f'CO2: {fake_co2()}')
	print(f'Body temperature: {fake_body_temp()}')
	print(f'Number of people: {fake_people_counter()}')


if __name__=="__main__":
	main()
