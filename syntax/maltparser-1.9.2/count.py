filename = 'data/kaist_test.conll'
with open(filename) as f:
	print(sum(1 for line in f)-sum(line.isspace() for line in f))
with open(filename) as f:
	print(sum(line.isspace() for line in f))