package algorithms.GenerateLogic;
//import algorithms.splitpatterns.SplitBehaviors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import algorithms.splitpatterns.Pattern;

public class GetPatterns {
	public static List<PatternBehaviors> getPatternsByKnowledgeId(String clientName, String databaseName, String knowledgeId) throws Exception{
		Map<String, String> behaviorsMap = new LinkedHashMap<>();
		List<PatternBehaviors> PatternBehaviorsList = new ArrayList<>();
		try{		 			 
			 MongoClient mongoClient =  new MongoClient(clientName,27017);
			 MongoDatabase mongoDatabase =  mongoClient.getDatabase(databaseName);
			 System.out.println("Connect to database successfully");	
			 
			 MongoCollection<Document> knowledgeCollection =  mongoDatabase.getCollection("knowledge_test");	
//			 ------getPatternIdsFormKnowledge
//			 BasicDBObject query = new BasicDBObject("knowledgeId",knowledgeId);
//			 MongoCursor<Document> knowledgeCursor = knowledgeCollection.find(query).iterator();
//			 List<String> patternIdsList = new ArrayList<>();
//			 while(knowledgeCursor.hasNext()){
//				 Document document = knowledgeCursor.next();
//				 System.out.println(document.toJson());
//				 patternIdsList = (List<String>) document.get("patternIds");
////				 System.out.println(patternIds.get(0));
//			 }
	         
			 System.out.println();
			 MongoCollection<Document> patternCollection =  mongoDatabase.getCollection("patterns_test");	
			 List<String> patternIdsList = new ArrayList<>();
			 MongoCursor<Document> patternCursor1 = patternCollection.find().iterator();
			 while(patternCursor1.hasNext()){
				 Document document = patternCursor1.next();
				 String patternId = String.valueOf(document.get("patternId"));
				 patternIdsList.add(patternId);
			 }
			 for(String patternId : patternIdsList) {
				 System.out.println(patternId);
				 BasicDBObject query2 = new BasicDBObject("patternId",patternId);
				 MongoCursor<Document> patternCursor = patternCollection.find(query2).iterator();
				 List<String> patternBehaviors = new ArrayList<>();
				 List<List<String>> uniqueIdsList = new ArrayList<>();
				 int weights;
				 while(patternCursor.hasNext()){
					 Document document = patternCursor.next();
					 System.out.println(document.toJson());
					 patternBehaviors = (List<String>) document.get("patternBehaviors");
					 uniqueIdsList = (List<List<String>>) document.get("uniqueIdsList");
					 weights = document.getInteger("weights");
					 System.out.println("patternBehaviors: " + patternBehaviors.toString());
					 System.out.println("uniqueIdsList: " + uniqueIdsList.toString());
					 System.out.println("weights: " + weights);
					 PatternBehaviors pattern = new PatternBehaviors();
					 pattern.setPatternId(patternId);
					 pattern.setPatternBehaviors(patternBehaviors);
					 pattern.setUniqueIdsList(uniqueIdsList);
					 pattern.setWeights(weights);
					 PatternBehaviorsList.add(pattern);
				 }
			 }
			 mongoClient.close();
		 }catch(MongoException e){
			e.printStackTrace();
		}
		return PatternBehaviorsList; 
	}
	
	public static List<PatternBehaviors> getAllPatterns(String clientName, String databaseName) throws Exception{
		Map<String, String> behaviorsMap = new LinkedHashMap<>();
		List<PatternBehaviors> PatternBehaviorsList = new ArrayList<>();
		try{		 			 
			 MongoClient mongoClient =  new MongoClient(clientName,27017);
			 MongoDatabase mongoDatabase =  mongoClient.getDatabase(databaseName);
			 System.out.println("Connect to database successfully");	

			 MongoCollection<Document> patternCollection =  mongoDatabase.getCollection("patterns_test");	
			 MongoCursor<Document> patternCursor = patternCollection.find().iterator();
			 while(patternCursor.hasNext()){
				 Document document = patternCursor.next();
				 System.out.println(document.toJson());
				 List<String> patternBehaviors = new ArrayList<>();
				 List<List<String>> uniqueIdsList = new ArrayList<>();
				 patternBehaviors = (List<String>) document.get("patternBehaviors");
				 uniqueIdsList = (List<List<String>>) document.get("uniqueIdsList");	
				 int weights = (int) document.get("weights");
				 System.out.println("patternBehaviors: " + patternBehaviors.toString());
				 System.out.println("uniqueIdsList: " + uniqueIdsList.toString());
				 
				 PatternBehaviors pattern = new PatternBehaviors();
				 pattern.setPatternId(document.get("patternId").toString());
				 pattern.setPatternBehaviors(patternBehaviors);
				 pattern.setUniqueIdsList(uniqueIdsList);	
				 pattern.setWeights(weights);
				 PatternBehaviorsList.add(pattern);
			 }
			 mongoClient.close();
		 }catch(MongoException e){
			e.printStackTrace();
		}
		return PatternBehaviorsList; 
	}
	
	public static List<Pattern> getBehaviorChains(String clientName, String databaseName, String collectionName) throws Exception{
		Map<String, String> behaviorsMap = new LinkedHashMap<>();
		List<Pattern> patternList = new ArrayList<>();
		try{		 			 
			 MongoClient mongoClient =  new MongoClient(clientName,27017);
			 MongoDatabase mongoDatabase =  mongoClient.getDatabase(databaseName);
			 System.out.println("Connect to database successfully");	

			 MongoCollection<Document> patternCollection =  mongoDatabase.getCollection(collectionName);	
			 MongoCursor<Document> patternCursor = patternCollection.find().iterator();
			 System.out.println(collectionName);
			 while(patternCursor.hasNext()){
				 Document document = patternCursor.next();
				 
				 System.out.println(document.toJson());
				 List<String> behaviorChain = new ArrayList<>();
				 behaviorChain = (List<String>) document.get("behaviorChain");
				 List<List<String>> uniqueIdsList = new ArrayList<>();
				 uniqueIdsList = (List<List<String>>) document.get("uniqueIdsList");
				 int weight;
				 weight = (int)document.get("weights");
				 Pattern pattern = new Pattern(behaviorChain,uniqueIdsList,weight);
//				 pattern.setPatternId(document.get("patternId").toString());
//				 pattern.setPatternBehaviors(patternBehaviors);
//				 pattern.setUniqueIdsList(uniqueIdsList);	
				 patternList.add(pattern);
				 
//				 List<String> patternBehaviors = new ArrayList<>();
//				 List<List<String>> uniqueIdsList = new ArrayList<>();
//				 patternBehaviors = (List<String>) document.get("patternBehaviors");
//				 uniqueIdsList = (List<List<String>>) document.get("uniqueIdsList");					 
//				 System.out.println("patternBehaviors: " + patternBehaviors.toString());
//				 System.out.println("uniqueIdsList: " + uniqueIdsList.toString());
//				 
//				 PatternBehaviors pattern = new PatternBehaviors();
//				 pattern.setPatternId(document.get("patternId").toString());
//				 pattern.setPatternBehaviors(patternBehaviors);
//				 pattern.setUniqueIdsList(uniqueIdsList);	
//				 PatternList.add(pattern);
			 }
			 mongoClient.close();
		 }catch(MongoException e){
			e.printStackTrace();
		}
		return patternList; 
	}

	
}
