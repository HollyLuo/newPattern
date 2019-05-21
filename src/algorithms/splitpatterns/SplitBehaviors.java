package algorithms.splitpatterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.UpdateDescription;
import com.mongodb.BasicDBObject;  
import com.mongodb.DB;  
import com.mongodb.DBCollection;  
import com.mongodb.DBCursor;  
import com.mongodb.DBObject;  
import com.mongodb.Mongo;  
import com.mongodb.MongoException;  

import algorithms.GenerateLogic.GetPatterns;
import algorithms.GenerateLogic.PatternBehaviors;
import algorithms.splitpatterns.cycle.Cycle;
import algorithms.splitpatterns.cycle.Edge;
import algorithms.splitpatterns.cycle.Graph;  
import algorithms.splitpatterns.cycle.Vertex;

public class SplitBehaviors {
	public  static void runAlgorithm(Map<String, String> behaviorsMap) throws Exception  {
		List<String> behaviorsList = new ArrayList<>();
		String iString = "";

		for (Map.Entry<String, String> entry : behaviorsMap.entrySet()) {
			behaviorsList.add(entry.getValue());
			iString += entry.getValue();
			iString += ",";
			
		}	
		String inputString = iString.substring(0, iString.length()-1);
		System.out.println(inputString);		
        System.out.println("--------------scan the behavior chain-----------------");
       
		Vertex currentVertex;
		Vertex nextVertex;
		Vertex vertex;
		Edge edge;
		ArrayList<String> nameList = new ArrayList<>();	
		Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();	
		Graph graph = new Graph();
		
		for(Iterator<String> behaviorId=behaviorsList.iterator();behaviorId.hasNext();){    
//			 System.out.println(behaviorId.next());
			String behavior = behaviorId.next();
			vertex = (Vertex) vertexMap.get(behavior);
			if (vertex == null){
				vertex = new Vertex();	
				vertex.setName(behavior);
				vertexMap.put(behavior, vertex);
				nameList.add(behavior);
			}
		}
		
		for(int i=0;i<behaviorsList.size();i++){
			currentVertex = (Vertex) vertexMap.get(behaviorsList.get(i));
			if(i>0){
				currentVertex.addInNumber();
			}
			if(i<behaviorsList.size()-1){
				currentVertex.addOutNumber();
			}
			if(i<behaviorsList.size()-1){
				nextVertex = (Vertex) vertexMap.get(behaviorsList.get(i+1));		
				edge = new Edge(currentVertex, nextVertex);
				
				if(currentVertex.getEdge(edge)!=null){
					currentVertex.getEdge(edge).addWeight();
				}else {
					edge.addWeight();
					currentVertex.setEdge(edge);
					currentVertex.setNeighbour(nextVertex);
					currentVertex.setNeighbourName(behaviorsList.get(i+1));
				}
			}		
		}
		
		for(int i=0;i<vertexMap.size();i++){		
			vertex = (Vertex) vertexMap.get(nameList.get(i));
			vertex.printVertex();
			graph.addVertex(vertex);
		}
		
		System.out.println("--------------Cycle Detection-----------------");
		ArrayList<Cycle> cycleList = new ArrayList<>();
		CycleDetection cycleDetection = new CycleDetection(inputString,graph);
		Boolean hasCycle = cycleDetection.hasCycle();
		cycleList = cycleDetection.getCycleList();
		String start  = cycleDetection.getStart();
		
		
		System.out.println();
		
		System.out.println("--------------Behavior split-----------------");
//		List<List<String>> pattList = new ArrayList<List<String>>();
		List<BehaviorsChain> behaviorsChainsList = new ArrayList<BehaviorsChain>();
		behaviorsChainsList =  SplitInputStringByStartVertex(behaviorsMap,start);
		
	    int size = behaviorsChainsList.size();
	    System.out.println();	    
	      
	    System.out.println("--------------All Behavior Chains -----------------");  
	    List<Pattern> patternList =  CountDuplicatedList(behaviorsChainsList);
		
	    for(Pattern pattern : patternList){	    	
	    	pattern.printPattern(); 	
	    	pattern.foundInternalCycle(cycleList);
	    	pattern.removeInternalCycle();
	    	if(pattern.hasInternalCycle){		
	    		System.out.println("--new_trace: "+ pattern.getNewBehaviorChain());
	    	}
	    	//判断behaviorChain库是否存在----need to do. if exist,update; if not exist, create.
	    	// pattern的behaviorChain是否一致。
	    	updateBehaviorChain(pattern,"localhost","IRA_multiVideo","behavior_chains_test");
	    }
	    
	    System.out.println();		  
//	    System.out.println("-----------------afterCountPatternList-------------");
	    List<Pattern> afterCountPatternList =  CountPattern(patternList);
	    generatePatternJson(afterCountPatternList,patternList);
	    
	    
//	    System.out.println("--------------Frequecy Pattern -----------------");
//        for(Pattern new_pattern : afterCountPatternList){
//      	    if(new_pattern.isFrequencyPattern(size, support)){
//      	    	new_pattern.printPattern(); 
//      	    	generatePatternJson(afterCountPatternList,patternList);
//      	    }
//        }    
	    
//	    System.out.println();
//        System.out.println("--------------Similarity consider: merge?--------------");
//        
//        //pattern是否需要合并？ 12345  123745
////        计算两者的相似度，然后计算是否为包含关系。
//        List<Pattern> frequencyPattern = new ArrayList();
//        for(Pattern new_pattern : afterCountPatternList){
//        	if(new_pattern.isFrequencyPattern(size, support)){
//        		frequencyPattern.add(new_pattern);
//        	}
//        }
//        for(int i=0;i<frequencyPattern.size();i++){
//        	List<String> list1=new ArrayList<>();
//        	list1.addAll(frequencyPattern.get(i).getBehaviorChain());
//        	System.out.println(list1);
//        	for(int j=i+1;j<frequencyPattern.size();j++){
//        		List<String> list2=new ArrayList<>();
//        		list2.addAll(frequencyPattern.get(j).getBehaviorChain());
//        		System.out.println(list2);
//        		levenshtein(list1,list2);
//        		
////        		boolean isSubSequence = isSubSequence(string1,string2);
////        	       if(isSubSequence){
////        	    	   System.out.println(string1 + " is the subsequence of " + string2);
////        	       }
//        	        
//        	}
//        }
	}
	
	public static void levenshtein(List<String> str1, List<String> str2) {
		// 计算两个字符串的长度。
		int len1 = str1.size();
		int len2 = str2.size();
		// 建立上面说的数组，比字符长度大一个空间
		int[][] dif = new int[len1 + 1][len2 + 1];
		// 赋初值，步骤B。
		for (int a = 0; a <= len1; a++) {
			dif[a][0] = a;
		}
		for (int a = 0; a <= len2; a++) {
			dif[0][a] = a;
		}
		// 计算两个字符是否一样，计算左上的值
		int temp;
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
 
//				System.out.println("i = " + i + " j = " + j + " str1 = "
//						+ str1.get(i - 1) + " str2 = " + str2.get(j - 1));
				if (str1.get(i - 1).equals(str2.get(j - 1)) ) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 取三个值中最小的
				dif[i][j] = Math.min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1);
 
//				System.out.println("i = " + i + ", j = " + j + ", dif[i][j] = "
//						+ dif[i][j]);
			}
		}
		System.out.println("Compare\"" + str1 + "\" and \"" + str2 + "\"");
		// 取数组右下角的值，同样不同位置代表不同字符串的比较
		System.out.println("Different step：" + dif[len1][len2]);
		// 计算相似度
		float similarity = 1 - (float) dif[len1][len2]
				/ Math.max(str1.size(), str2.size());
		System.out.println("Similarity：" + similarity);
	}
	
	
	private static void updateBehaviorChain(Pattern pattern, String clientName,String databaseName,String collectionName) throws Exception {
		List<Pattern> patternList = new ArrayList<>();
		patternList = GetPatterns.getBehaviorChains(clientName, databaseName, collectionName);
		if(patternList.size()!=0){
			System.out.println("222");
			boolean existBehaviorChain = false;
			for(Pattern tempPattern : patternList){
				if((pattern.getBehaviorChain()).equals(tempPattern.getBehaviorChain()) && !existBehaviorChain){
					System.out.println("333");
					//判断UniqueIdsList是否存在
					List<List<String>> uniqueIdsList2 = new ArrayList();
					for(List<String> uniqueIds:pattern.getUniqueIdsList()){
						if(!tempPattern.getUniqueIdsList().contains(uniqueIds)){
							uniqueIdsList2.add(uniqueIds);
						}
					}
					tempPattern.addUniqueIdsToList(uniqueIdsList2);	
					try{		 			 
						 MongoClient mongoClient =  new MongoClient("localhost",27017);
						 MongoDatabase mongoDatabase =  mongoClient.getDatabase("IRA_multiVideo");
						 System.out.println("Connect to database successfully");	

						 MongoCollection<Document> patternCollection =  mongoDatabase.getCollection("behavior_chains_test");	
						 BasicDBObject old = new BasicDBObject();
					     old.put("behaviorChain",tempPattern.getBehaviorChain());
					     System.out.println(old.toJson());
					     BasicDBObject newObj = new BasicDBObject();
					     newObj.put("uniqueIdsList",tempPattern.getUniqueIdsList());
					     newObj.put("weights",tempPattern.getUniqueIdsList().size());
					     System.out.println(newObj.toJson());
					     BasicDBObject update = new BasicDBObject("$set",newObj);
					     patternCollection.updateOne(old,update);	
					     existBehaviorChain = true;
						 }
					catch (Exception e) {
						
					}
				}
			}
		    if(!existBehaviorChain){
		    	JSONObject jsonObject = pattern.toJsonObject();
		    	System.out.println(jsonObject.toJSONString());
				saveJsonObjectToMongoDb(jsonObject,"localhost","IRA_multiVideo","behavior_chains_test");
		    }	
		}else {
	    	JSONObject jsonObject = pattern.toJsonObject();
	    	System.out.println(jsonObject.toJSONString());
			saveJsonObjectToMongoDb(jsonObject,"localhost","IRA_multiVideo","behavior_chains_test");
		}
		
	}

	

	@SuppressWarnings("unchecked")
	private static void generatePatternJson(List<Pattern> afterCountPatternList, List<Pattern> patternList) throws Exception {
		
		int mergePatternId = 0;
	    for(Pattern mergePattern : afterCountPatternList){
	    	List<Integer> chainBranchIds = new ArrayList<Integer>();
	    	mergePatternId += 1;
	    	mergePattern.printPattern(); 
	    	System.out.println("---------including---------");
	    	
	    	for(Pattern oriPattern : patternList){
	    		if(oriPattern.getNewBehaviorChain().equals(mergePattern.getBehaviorChain())){	    			
	    			oriPattern.printPattern();
	    			chainBranchIds.add(oriPattern.getPatternName());
	    			
    			}
	    	}
	    	//判断pattern库中是否存在
		    List<PatternBehaviors> patternBehaviorsList = new ArrayList<>();
			patternBehaviorsList = GetPatterns.getAllPatterns("localhost", "IRA_multiVideo");
	    	
			if(patternBehaviorsList.size()!=0){
				boolean existPattern = false;
			  for(PatternBehaviors patternBehaviors : patternBehaviorsList){
				if((patternBehaviors.getPatternBehaviors()).equals(mergePattern.getBehaviorChain()) && !existPattern){
					
					List<List<String>> uniqueIdsList2 = new ArrayList();
					for(List<String> uniqueIds:mergePattern.getUniqueIdsList()){
						if(!patternBehaviors.getUniqueIdsList().contains(uniqueIds)){
							uniqueIdsList2.add(uniqueIds);
						}
					}
					
					patternBehaviors.addUniqueIdsToList(uniqueIdsList2);
					try{		 			 
						 MongoClient mongoClient =  new MongoClient("localhost",27017);
						 MongoDatabase mongoDatabase =  mongoClient.getDatabase("IRA_multiVideo");
						 System.out.println("Connect to database successfully");	

						 MongoCollection<Document> patternCollection =  mongoDatabase.getCollection("patterns_test");	
						 BasicDBObject filter = new BasicDBObject();
						 filter.put("patternBehaviors",mergePattern.getBehaviorChain());
//					     System.out.println("updated patternBehaviors: " + old.toJson());
					     BasicDBObject newObj = new BasicDBObject();
					     newObj.put("uniqueIdsList",patternBehaviors.getUniqueIdsList());
					     newObj.put("weights",patternBehaviors.getUniqueIdsList().size());
//					     System.out.println("updated uniqueIdsList: " + newObj.toJson());
					     BasicDBObject update = new BasicDBObject("$set",newObj);
					     patternCollection.updateOne(filter,update);	
					     existPattern = true;
						 }
					catch (Exception e) {
						
					}
				}
			}
			if(!existPattern){
			    JSONObject mergePatternJson = new JSONObject();
			    System.out.println("xxx");
			    mergePatternJson.put("patternId", String.valueOf(System.currentTimeMillis()%10));
			    mergePatternJson.put("patternBehaviors", mergePattern.getBehaviorChain());
			    mergePatternJson.put("weights", mergePattern.getWeight());
			    mergePatternJson.put("chainBranches", chainBranchIds);
			    mergePatternJson.put("uniqueIdsList", mergePattern.getUniqueIdsList());
			    saveJsonObjectToMongoDb(mergePatternJson,"localhost","IRA_multiVideo","patterns_test");		
			}	

	    	System.out.println("");
	    	System.out.println("");
	    	
	    }else {
	    	JSONObject mergePatternJson = new JSONObject();
	    	System.out.println("xxx");
	    	mergePatternJson.put("patternId", String.valueOf(System.currentTimeMillis()%10));
	    	mergePatternJson.put("patternBehaviors", mergePattern.getBehaviorChain());
	    	mergePatternJson.put("weights", mergePattern.getWeight());
	    	mergePatternJson.put("chainBranches", chainBranchIds);
	    	mergePatternJson.put("uniqueIdsList", mergePattern.getUniqueIdsList());
	    	saveJsonObjectToMongoDb(mergePatternJson,"localhost","IRA_multiVideo","patterns_test");
			
		}
	    }
	}

	private static List<BehaviorsChain> SplitInputStringByStartVertex(Map<String, String> behaviorsMap, String start) {
		
		  List<BehaviorsChain> behaviorsChainsList = new ArrayList<BehaviorsChain>();
		  List<String> behaviorsList = new ArrayList<>();
		  List<String> keyChainList = new ArrayList<>();
		  for (Map.Entry<String, String> entry : behaviorsMap.entrySet()) {
			  behaviorsList.add(entry.getValue());
			  keyChainList.add(entry.getKey());
		  }

		  ArrayList<Integer> findStart = new ArrayList<>();

		  for(int i=0;i<behaviorsList.size();i++){
			  if(behaviorsList.get(i).equals(start)){			
				  findStart.add(i);
//				  System.out.println(i);
			  }
		  }	  

		  for(int i=0;i<findStart.size()-1;i++){
			  BehaviorsChain behaviorsChain = new BehaviorsChain();
			  List<String> behaviorIds = new ArrayList<>();
			  List<String> uniqueIds = new ArrayList<>();
			  if(i==0 && (findStart.get(i)!=0)){
				  behaviorIds = behaviorsList.subList(0, findStart.get(i));
				  uniqueIds = keyChainList.subList(0, findStart.get(i));
//				  behaviorChainList.add(behaviorIds);			  
//				  uniqueIdChainList.add(uniqueIds);
				  behaviorsChain.setBehaviorsChain(behaviorIds);
				  behaviorsChain.setUniqueIdsChain(uniqueIds);
				  behaviorsChainsList.add(behaviorsChain);
				  System.out.println("behaviorIds: " + behaviorIds);
				  System.out.println("uniqueIds: " + uniqueIds);
			  }
			  behaviorIds = behaviorsList.subList(findStart.get(i), findStart.get(i+1));
			  uniqueIds = keyChainList.subList(findStart.get(i), findStart.get(i+1));
//			  behaviorChainList.add(behaviorIds);
//			  uniqueIdChainList.add(uniqueIds);
			  behaviorsChain.setBehaviorsChain(behaviorIds);
			  behaviorsChain.setUniqueIdsChain(uniqueIds);
			  behaviorsChainsList.add(behaviorsChain);
			  System.out.println("behaviorIds: " + behaviorIds);
			  System.out.println("uniqueIds: " + uniqueIds);
		  }
		  if(findStart.get(findStart.size()-1) < behaviorsList.size()){
			  BehaviorsChain behaviorsChain = new BehaviorsChain();
			  List<String> behaviorIds = new ArrayList<>();
			  List<String> uniqueIds = new ArrayList<>();
			  behaviorIds = behaviorsList.subList(findStart.get(findStart.size()-1),behaviorsList.size());
			  uniqueIds = keyChainList.subList(findStart.get(findStart.size()-1),behaviorsList.size());
//			  behaviorChainList.add(behaviorIds);
//			  uniqueIdChainList.add(uniqueIds);
			  behaviorsChain.setBehaviorsChain(behaviorIds);
			  behaviorsChain.setUniqueIdsChain(uniqueIds);
			  behaviorsChainsList.add(behaviorsChain);
			  System.out.println("behaviorIds: " + behaviorIds);
			  System.out.println("uniqueIds: " + uniqueIds);
		  }
		  return behaviorsChainsList;
	}
	
	private static List<Pattern> CountDuplicatedList(List<BehaviorsChain> behaviorsChainsList) {
		Map<List<String>, Pattern> map = new HashMap<List<String>, Pattern>();
		List<Pattern> patternList = new ArrayList<>();	
		Pattern pattern;
		int pattern_name=1;
		for (BehaviorsChain chain : behaviorsChainsList) {
			pattern = map.get(chain.getBehaviorsChain());
//			System.out.println(chain.getUniqueIdsChain());
			if(pattern==null){				
				pattern = new Pattern();
				pattern.setPatternName(pattern_name);
				pattern.setBehaviorChain(chain.getBehaviorsChain());
				pattern.getUniqueIdsList().add(chain.getUniqueIdsChain());
				pattern.setWeight(1);
				pattern_name+=1;
				map.put(chain.getBehaviorsChain(), pattern);
				patternList.add(pattern);				
			}else {
				pattern.setWeight(pattern.getWeight()+1);
				pattern.getUniqueIdsList().add(chain.getUniqueIdsChain());
			}
		}
		return patternList;
	}
	
	private static void saveJsonObjectToMongoDb(JSONObject jsonObject,String clientName,String databaseName,String collectionName) {
		try{		 
			 MongoClient mongoClient =  new MongoClient(clientName, 27017);
			 MongoDatabase mongoDatabase =  mongoClient.getDatabase(databaseName);
//			 System.out.println("Connect to database successfully");
			 
			 MongoCollection<Document> collection =  mongoDatabase.getCollection(collectionName);
			 System.out.println(jsonObject.toJSONString());
			 Document document = Document.parse(jsonObject.toJSONString());
			 
			 collection.insertOne(document);
			 mongoClient.close();

		 }catch(MongoException e){
			e.printStackTrace();
		} 
		
	}
	
	private static List<Pattern> CountPattern(List<Pattern> patternList) {
		List<Pattern> afterCountPatternList = new ArrayList<>();	
		Map<List<String>, Pattern> map = new HashMap<List<String>, Pattern>();
		Pattern pattern;
		int pattern_name = 0;
		for(Pattern item :patternList){
			pattern = map.get(item.getNewBehaviorChain());
			
//			System.out.println("item.getNewTrace():"+item.getNewTrace());
			if(pattern == null){
				pattern = new Pattern();
				pattern.setPatternName(pattern_name);
				pattern.setBehaviorChain(item.getNewBehaviorChain());
				pattern.setWeight(item.getWeight());
				pattern.getUniqueIdsList().addAll(item.getUniqueIdsList());
				pattern_name+=1;
				map.put(item.getNewBehaviorChain(), pattern);
				afterCountPatternList.add(pattern);	
			}else {
				pattern.setWeight(pattern.getWeight()+item.getWeight());
				pattern.getUniqueIdsList().addAll(item.getUniqueIdsList());
			}
		}
		return afterCountPatternList;
	}

}
