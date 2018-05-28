from sklearn.feature_extraction.text import CountVectorizer
import operator, pickle
import glove, os

for filename in os.listdir("../UCorpus-DP_SR/"):
	if filename.endswith(".txt"):
		with open(os.path.join("../UCorpus-DP_SR/", filename), encoding="utf-16-le") as input_file:
			corpus = input_file.read().splitlines()
print("corpus")
print(corpus)

vectorizer = CountVectorizer(min_df=10, ngram_range=(1,1))
X = vectorizer.fit_transform(corpus)
Xc = X.T * X             # co-occurrence matrix
Xc.setdiag(0)			 # 대각성분을 0으로
result = Xc.toarray()    # array로 변환
dic = {}
for idx1, word1 in enumerate(result):
	tmpdic = {}
	for idx2, word2 in enumerate(word1):
		if word2 > 0:
			tmpdic[idx2] = word2
	dic[idx1] = tmpdic

vocab = sorted(vectorizer.vocabulary_.items(), key=operator.itemgetter(1))
vocab = [word[0] for word in vocab]

model = glove.Glove(dic, d=100, alpha=0.75, x_max=100.0)
for epoch in range(25):
	err = model.train(batch_size=200, workers=4)
	print("epoch %d, error %.3f" % (epoch, err), flush=True)

# 단어벡터 추출
wordvectors = model.W

# 저장
with open('glove', 'wb') as f:
	pickle.dump([vocab,wordvectors],f)