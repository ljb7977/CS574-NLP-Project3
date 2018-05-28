lines = None

with open("acc.txt") as f:
	lines = f.read().splitlines()

for i in range(len(lines)):
	if i%7 == 0:
		print(lines[i], end=", ")
	elif i%7 == 3:
		print(lines[i].split()[0])