package caching;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import chromosome.Chromosome;
import dictionaries.Dictionary;
import links.Link;

public final class Cache {

	public static ConcurrentHashMap<String, Double> timeMap;

	public static void createFolder(String path) {
	    File dir = new File(path);
	    if (!dir.exists()){
	        dir.mkdirs();
	    }
	}
	
	public static void saveMap(HashMap<String, String> map, String path) throws IOException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))) {
            os.writeObject(map);
        }
	}
	
	public static void saveMapLinks(HashMap<Link, Double> map, String path) throws IOException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))) {
            os.writeObject(map);
        }
	}
	
	public static void saveMapResults(HashMap<Link, Integer> map, String path) throws IOException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))) {
            os.writeObject(map);
        }
	}
	
	public static void saveMapTime(ConcurrentHashMap<String, Double> timeMap2, String path) throws IOException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))) {
            os.writeObject(timeMap2);
        }
	}
	
	public static HashMap<String, String> loadMap(String path) throws ClassNotFoundException, IOException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(path))) {
            return (HashMap<String, String>) is.readObject();
        }
    }
	
	public static HashMap<Link, Double> loadMapLinks(String path) throws ClassNotFoundException, IOException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(path))) {
            return (HashMap<Link, Double>) is.readObject();
        }
    }
	
	public static HashMap<Link, Integer> loadMapResults(String path) throws ClassNotFoundException, IOException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(path))) {
            return (HashMap<Link, Integer>) is.readObject();
        }
    }
	
	public static ConcurrentHashMap<String, Double> loadMapTime(String path) throws ClassNotFoundException, IOException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(path))) {
            return (ConcurrentHashMap<String, Double>) is.readObject();
        }
    }
	
	public static void saveDictionary(Dictionary dictionary, String path) throws IOException {
		try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path))) {
			os.writeObject(dictionary);
		}
	}
	
	public static Dictionary readDictionary(String path) throws ClassNotFoundException, IOException {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(path))) {
            return (Dictionary) is.readObject();
        }
	}
	
	public static Double getTime(ConcurrentHashMap<String, Double> timeMap, Chromosome c, String directory) {
		double time = 0;
		String key = "";
		String dir = directory+"TracingModels/";
		
		String preprocessors = Integer.toString(c.stopwords)+Integer.toString(c.cleanup)+Integer.toString(c.stemmer)+Integer.toString(c.code)+Integer.toString(c.ngram);
		
		if(c.vsm == 1) {
			//dictionary
			key = dir+"VSM/"+preprocessors+".dict";
			time += timeMap.get(key);
			
			//map
			key = dir+"VSM/"+preprocessors+"_"+Integer.toString(c.similarity)+".map";
			time += timeMap.get(key);
		}
		
		if(c.vsm_ngram == 1) {
			//dictionary
			key = dir+"VSMnGram/"+preprocessors+"_"+Integer.toString(c.ngram_max)+".dict";
			time += timeMap.get(key);
			
			//map
			key = dir+"VSMnGram/"+preprocessors+"_"+Integer.toString(c.ngram_max)+"_"+Integer.toString(c.similarity)+".map";
			time += timeMap.get(key);
		}
		
		if(c.lda == 1) {
			String parameters = Integer.toString(c.num_topics)+"_"+Integer.toString(c.passes)+"_"+Integer.toString(c.chunk_size)+"_"+Double.toString(c.alpha)+"_"+Double.toString(c.beta);
			
			//dictionary
			key = dir+"LDA/"+preprocessors+".dict";
			time += timeMap.get(key);
			
			//model
			key = dir+"LDA/"+preprocessors+"_"+parameters+".model";
			time += timeMap.get(key);
			
			//map
			key = dir+"LDA/"+preprocessors+"_"+parameters+"_"+Integer.toString(c.similarity)+".map";
			time += timeMap.get(key);
		}
		
		if(c.lsi == 1) {
			String parameters = Integer.toString(c.num_topics)+"_"+Integer.toString(c.chunk_size);
			
			//dictionary
			key = dir+"LSI/"+preprocessors+".dict";
			time += timeMap.get(key);
			
			//model
			key = dir+"LSI/"+preprocessors+"_"+parameters+".model";
			time += timeMap.get(key);
			
			//map
			key = dir+"LSI/"+preprocessors+"_"+parameters+"_"+Integer.toString(c.similarity)+".map";
			time += timeMap.get(key);
		}
		
		return time;
	}
}
