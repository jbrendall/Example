"""
LSI analysis module for TraceDynamo
"""
import gensim
import sys
import json
import logging
import time

from gensim import corpora, matutils
from gensim.models import lsimodel
from os import path

LOGGER = logging.getLogger(__name__)
LOGGER.setLevel(logging.DEBUG)

def run(*args, **kwargs):
    """Run link completion
    """
    LOGGER.info('input args: %s, kwargs: %s', args, kwargs)
    
    input = list()
    for value in args:
    	input.append(value)
    	
    attributes = json.dumps(input[0])
    data = json.dumps(input[1])
 
    att = json.loads(attributes)
    docs = json.loads(data)
    
    sim_metric = get_similarity_metric(att['similarity'])
    
    train_docs = docs['source']
    test_docs = docs['target']
    
    train_map, train_corpus = format_data(train_docs)
    test_map, test_corpus = format_data(test_docs)
    
    train_corpus_tok = tokenize(train_corpus)
    
    lsi = LSI()
    lsi.train(att['path'], att['dict'], train_corpus_tok, num_topics=att['num_topics'], chunksize=att['chunk_size'])
    
    #my_list = lsi._inner_model.show_topics(num_topics=20, num_words=15, formatted=True)
    
    #topic_words = [(tp[0], [wd[0] for wd in tp[1]]) for tp in my_list]
    
    #with open('C:/Users/jrendal1/Desktop/OSS_LSI_topics.txt', 'w') as f:
    #	for topic in my_list:
    #		f.write('{}\n'.format(topic))

    scores = lsi.compare_corpi(train_corpus, test_corpus, sim_metric)
    formatted_results = format_scores(train_map, test_map, scores)
    
    LOGGER.info('got: %s', formatted_results)
      
    result = json.dumps({'lsi_result': formatted_results, 'model_time': lsi.model_time, 'dict_time': lsi.dict_time})
    LOGGER.info('result: %s', result)

    return result
    
class LSI(object):
	def __init__(self):
		self._inner_model = None
		self._dict = None
		self.model_time = -1
		self.dict_time = -1

	def train(self, filepath, dict_path, docs, num_topics = 5, chunksize = 2000):
		if(path.exists(filepath)):
			LOGGER.info('Model already exists...load model')
			self._inner_model = lsimodel.LsiModel.load(filepath)
		else:
			start = time.time()
			clean_docs = [d for d in docs]
			if(path.exists(dict_path)):
				LOGGER.info('Dictionary already exists...loading dictionary')
				self._dict = corpora.Dictionary.load(dict_path)
			else:
				self._dict = corpora.Dictionary(clean_docs)
				self._dict.save(dict_path)
				self.dict_time = (time.time() - start)
			corpus_dict = self._dict
			corpus = [self._dict.doc2bow(x) for x in clean_docs]
			#tfidf = tfidfmodel.TfidfModel(corpus)
			#corpus_tfidf = tfidf[corpus]
			self._inner_model = lsimodel.LsiModel(corpus, num_topics=num_topics, id2word=corpus_dict, chunksize=chunksize)
			self._inner_model.save(filepath)
			self.model_time = (time.time() - start)
		return self

	def get_topic_distrb(self, doc):
		bow_doc = self._inner_model.id2word.doc2bow(doc.split())
		return bow_doc

	def get_doc_similarity(self, doc1, doc2, sim_metric,):
		vec1 = self._inner_model[self.get_topic_distrb(doc1)]
		vec2 = self._inner_model[self.get_topic_distrb(doc2)]
		
		return sim_metric(vec1, vec2)

	def compare_corpi(self, corpus1, corpus2, sim_metric):
		score_tuples = list()
		for i, doc1 in enumerate(corpus1):
			for j, doc2 in enumerate(corpus2):
				score_tuples.append((i, j, self.get_doc_similarity(doc1, doc2, sim_metric)))
		return score_tuples

def tokenize(data):
	tok_data = list()
	for s in data:
		tok_data.append(s.split())
	return tok_data

def format_data(data):
	map = dict()
	file_contents = list(data.values())
	
	for i, key in enumerate(data):
		map[i] = key
		
	return map, file_contents

def format_scores(master_map_1, master_map_2, score_tuples):
	formatted_scores = list()
	for tup in score_tuples:
		formatted_scores.append((master_map_1[tup[0]], master_map_2[tup[1]], tup[2]))
	return formatted_scores

def get_similarity_metric(sim):
	metric = None
	if(sim == 0):
		metric = matutils.cossim
	elif(sim == 1):
		metric = matutils.jaccard
	return metric

###############################################################################

	
if __name__ == "__main__":
	main()