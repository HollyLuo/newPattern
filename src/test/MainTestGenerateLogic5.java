package test;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.netlib.util.booleanW;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import algorithms.Convert.XLSX2CSV;
import algorithms.GenerateLogic.ActionInfo;
import algorithms.GenerateLogic.BehaviorInfo;
import algorithms.GenerateLogic.FrequencyPattern;
import algorithms.GenerateLogic.GetPatterns;
import algorithms.GenerateLogic.Knowledge;
import algorithms.GenerateLogic.Logic;
import algorithms.GenerateLogic.PatternBehaviors;
import algorithms.GenerateLogic.ReplyActionInfo;
import algorithms.GenerateLogic.ReplyBehavior;
import algorithms.GenerateLogic.ReplyEntry;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;;

public class MainTestGenerateLogic5 {
	static int uniqueIndex = 0;
	static int uniqueId= 0;
	static List<ReplyEntry> replyEntriesList = new LinkedList<>();

	public static void main(String[] args) throws Exception {
		List<PatternBehaviors> patternBehaviorsList = new ArrayList<>();
		patternBehaviorsList = GetPatterns.getAllPatterns("localhost", "IRA_multiVideo");
		float support = 2;
		Map<String,Knowledge> knowledgesMap = new LinkedHashMap<>();
		knowledgesMap = GetPatterns.getKnowledgeMap("localhost", "IRA_multiVideo");
		Knowledge knowledge = knowledgesMap.get("1");
		
		List<PatternBehaviors> frequencyPatternBehaviorsList = new ArrayList<>();
		List<String> frequencyPatternIds = new ArrayList<>();
		int size = 0;
		for(PatternBehaviors pattern : patternBehaviorsList){
			size += pattern.getUniqueIdsList().size();
		}

	    System.out.println("--------------Frequecy Pattern -----------------");
        for(PatternBehaviors new_pattern : patternBehaviorsList){
    	    if(new_pattern.isFrequencyPattern(size, support)){
    	    	frequencyPatternBehaviorsList.add(new_pattern);
    	    	frequencyPatternIds.add(new_pattern.getPatternId());
    	    }
        }  
        System.out.println("Frequecy Pattern IDs : " + frequencyPatternIds.toString());

        if(compare(knowledge.getPatternIds(),frequencyPatternIds)){
        	System.out.println("no update for knowledge");
        }else {
        	knowledge.setPatternIds(frequencyPatternIds);
        	GetPatterns.updateKnowledge("localhost", "IRA_multiVideo", knowledge);
        	System.out.println("updated knowledge");
		}    
        
        System.out.println("--------------Store Frequecy Pattern To Database-----------------");
        Map<String, FrequencyPattern> frequencyPatternsMap = new HashMap<>();
        frequencyPatternsMap = StoreFrequencyPatterns(frequencyPatternBehaviorsList);
        
        System.out.println("--------------生成uniqueId;-----------------");
//      List<ReplyBehavior> replyBehaviorsList = new LinkedList<>();
        Map<ReplyBehavior, Integer> replyBehaviorsUniqueIdsMap = new LinkedHashMap<>(); 
        Boolean existBehaviorLogic = false;
        GenerateReplyUniqueIDs(0, frequencyPatternBehaviorsList, frequencyPatternsMap, replyBehaviorsUniqueIdsMap, existBehaviorLogic);
        for(Map.Entry<ReplyBehavior, Integer> entry: replyBehaviorsUniqueIdsMap.entrySet()){
      	  System.out.println("key: " + entry.getKey().toString() + "; " + "value: " + entry.getValue());   	
        }
        
        System.out.println("--------------Logic Judgement -----------------");
//        Map<String, Logic> logicMaps = new HashMap<>(); 
        Boolean existBehaviorLogic2 = false;
        judgeLogic(0, frequencyPatternBehaviorsList, frequencyPatternsMap, replyBehaviorsUniqueIdsMap, existBehaviorLogic2);
        
        System.out.println("--------------Reply  Entries-----------------");
        for(ReplyEntry replyEntry : replyEntriesList){
        	System.out.println(replyEntry.toJsonObject());   	
        }      
	}

	private static Map<ReplyBehavior, Integer>  GenerateReplyUniqueIDs(int firstIndex, List<PatternBehaviors> frequencyPatternsList,
			Map<String, FrequencyPattern> frequencyPatternsMap, Map<ReplyBehavior, Integer> replyBehaviorsUniqueIdsMap, Boolean existBehaviorLogic) {
//		List<ReplyBehavior> replyBehaviorsList = new LinkedList<>();
		int length = 0;
		List<String> patternIdsList = new ArrayList<>();
		for (PatternBehaviors item : frequencyPatternsList) {
			item.printPatternBehaviors();
//			判断最长的pattern 长度
			length = item.getPatternBehaviors().size() > length ? item.getPatternBehaviors().size() : length;
			patternIdsList.add(item.getPatternId());
		}
		for (int i = firstIndex; i < length; i++) {
			if(!existBehaviorLogic){
			Boolean hasCurrentBehaviorLogic = false;
			List<String> currentUniqueIds = new ArrayList<>();
			Map<String, List<PatternBehaviors>> currentPatternsMap = new HashMap<>();
			currentPatternsMap = getPatternsByBehaviorId(i,frequencyPatternsList,currentUniqueIds);
			hasCurrentBehaviorLogic = judgeBehaviorLogic(i, frequencyPatternsList, currentPatternsMap);
			System.out.println("----------------index: " + i + "----------------");
			System.out.println("hasCurrentBehaviorLogic: " + hasCurrentBehaviorLogic + "; currentUniqueIds: " + currentUniqueIds);
			
			if(hasCurrentBehaviorLogic){				
				for(Map.Entry<String, List<PatternBehaviors>> entry: currentPatternsMap.entrySet()){
					GenerateReplyUniqueIDs(i, entry.getValue(), frequencyPatternsMap, replyBehaviorsUniqueIdsMap,existBehaviorLogic);
				}
				existBehaviorLogic = true;
			}else {
				ReplyBehavior replyBehavior = new ReplyBehavior();

				List<String> patternIds = new LinkedList<>();
//				System.out.println("currentPatternsMap.keySet(): " + currentPatternsMap.keySet());
				for(PatternBehaviors patternBehaviors : currentPatternsMap.get(currentPatternsMap.keySet().iterator().next())){
					patternIds.add(patternBehaviors.getPatternId());
				}
				replyBehavior.setPatternIds(patternIds);
				replyBehavior.setIndex(i);
				replyBehavior.setBehaviorIds(currentPatternsMap.keySet().iterator().next());
				replyBehaviorsUniqueIdsMap.put(replyBehavior, uniqueId);
				
				ReplyActionInfo replyActionInfo = new ReplyActionInfo();
				replyActionInfo.setUniqueId(uniqueId);
				replyActionInfo.setBehaviorId(Integer.parseInt(replyBehavior.getBehaviorIds()));
//				replyActionInfo.setActionInfo(actionInfo);
				
				uniqueId++;
			}

		  }
	   }
	   return replyBehaviorsUniqueIdsMap;
		
	}

	private static void judgeLogic(int firstIndex, List<PatternBehaviors> frequencyPatternsList,
		Map<String, FrequencyPattern> frequencyPatternsMap, Map<ReplyBehavior, Integer> replyBehaviorsUniqueIdsMap, Boolean existBehaviorLogic) throws Exception {
    	int length = 0;
		List<String> patternIdsList = new ArrayList<>();
		for (PatternBehaviors item : frequencyPatternsList) {
			item.printPatternBehaviors();
//			判断最长的pattern 长度
			length = item.getPatternBehaviors().size() > length ? item.getPatternBehaviors().size() : length;
			patternIdsList.add(item.getPatternId());
		}
		for (int i = firstIndex; i < length; i++) {
//			判断当前index是否有behaviorLogic／valueLogic
//			判断下一个index是否有behaviorLogic
			if(!existBehaviorLogic){
			    System.out.println("------------pattern index: " + i + "--------------");
			    Boolean hasCurrentBehaviorLogic = false;
			    List<String> currentUniqueIds = new ArrayList<>();
			    Map<String, List<PatternBehaviors>> currentPatternsMap = new HashMap<>();
			    currentPatternsMap = getPatternsByBehaviorId(i,frequencyPatternsList,currentUniqueIds);
			    hasCurrentBehaviorLogic = judgeBehaviorLogic(i, frequencyPatternsList, currentPatternsMap);
			    System.out.println("hasCurrentBehaviorLogic: " + hasCurrentBehaviorLogic + "; currentUniqueIds: " + currentUniqueIds);

//  当存在behavior logic的时候，nextBehaviorLogic有问题！！！
			    Boolean hasNextBehaviorLogic = false;
			    List<String> nextUniqueIds = new ArrayList<>();
			    Map<String, List<PatternBehaviors>> nextPatternsMap = new HashMap<>();
			    nextPatternsMap = getPatternsByBehaviorId(i+1,frequencyPatternsList,nextUniqueIds);
			    
			    Set<Integer> nextbehaviorIdsList = new HashSet<>();
//			    Map: <behaviorUniqueId, replyUniqueId>
                Map<String, Integer> behaviorToReplyUniqueIdMap = new LinkedHashMap<>();
			    
			    if(i+1 < length){			
				    hasNextBehaviorLogic = judgeBehaviorLogic(i+1, frequencyPatternsList, nextPatternsMap);
				    System.out.println("hasNextBehaviorLogic: " + hasNextBehaviorLogic + "; nextUniqueIds: " + nextUniqueIds);
				    
	                for(Map.Entry<String, List<PatternBehaviors>> entry: nextPatternsMap.entrySet()){
	                    List<String> nextPatternIdsList = new LinkedList<>();
	                    for(PatternBehaviors pattern: entry.getValue()){
                            nextPatternIdsList.add(pattern.getPatternId());                        
                        }
	                    ReplyBehavior replyBehavior = new ReplyBehavior(nextPatternIdsList, i+1, entry.getKey());  
	                    System.out.println(replyBehavior.toString());
	                    int nextbehaviorId = replyBehaviorsUniqueIdsMap.get(replyBehavior);
	                    nextbehaviorIdsList.add(nextbehaviorId);

                        for(PatternBehaviors pattern: entry.getValue()){
                            nextPatternIdsList.add(pattern.getPatternId());
                            for (List<String> uniqueIdsList : pattern.getUniqueIdsList()) {
                               behaviorToReplyUniqueIdMap.put(uniqueIdsList.get(i+1), nextbehaviorId);
                            }                           
                        }

	                }
	                for(Map.Entry<String, Integer> entry : behaviorToReplyUniqueIdMap.entrySet()){
	                    System.out.println("[behaviorToReplyUniqueIdMap] key: " + entry.getKey() + "; value: " + entry.getValue()); 
	                }
			    }
			    		
			    Boolean hasCurrentValueLogic = false;
			    hasCurrentValueLogic = hasValueLogic(currentUniqueIds);
			    System.out.println("hasCurrentValueLogic: " + hasCurrentValueLogic);
			
			    Logic behaviorLogic = new Logic();
			    if(hasNextBehaviorLogic){
				    behaviorLogic = dealWithBehaviorLogic(patternIdsList, i+1, nextPatternsMap, nextUniqueIds, behaviorToReplyUniqueIdMap); 	
			    }
			    Logic valueLogic = new Logic();
			    if(hasCurrentValueLogic){
			        valueLogic = dealWithCurrentValueLogic(patternIdsList, i, currentPatternsMap, currentUniqueIds, behaviorToReplyUniqueIdMap);
			    }
			   
			    String behaviorLogicItem = behaviorLogic.getID();
                String valueLogicItem = valueLogic.getID();
			    ReplyEntry replyEntry = generateReplyEntry(currentPatternsMap, behaviorLogicItem, valueLogicItem, replyBehaviorsUniqueIdsMap, frequencyPatternsList, frequencyPatternsMap, nextbehaviorIdsList, i);

                replyEntriesList.add(replyEntry);

			    if(hasNextBehaviorLogic){
			       
				    for(Map.Entry<String, List<PatternBehaviors>> entry: nextPatternsMap.entrySet()){
				   System.out.println("entry.getValue(): " + entry.getValue());
				   judgeLogic(i+1, entry.getValue(), frequencyPatternsMap, replyBehaviorsUniqueIdsMap, existBehaviorLogic);
					    
					}
				    existBehaviorLogic = true;
			    }
			}
		}
	}

    private static ReplyEntry generateReplyEntry(Map<String, List<PatternBehaviors>> currentPatternsMap,
            String behaviorLogicItem, String valueLogicItem, Map<ReplyBehavior, Integer> replyBehaviorsUniqueIdsMap, List<PatternBehaviors> frequencyPatternsList, Map<String, FrequencyPattern> frequencyPatternsMap, Set<Integer> nextbehaviorIdsList, int i) {
        List<String> currentPatternIdsList = new LinkedList<>();
        for(Map.Entry<String, List<PatternBehaviors>> entry: currentPatternsMap.entrySet()){
            for(PatternBehaviors pattern: entry.getValue()){
                currentPatternIdsList.add(pattern.getPatternId());
            }
        }
//      get behaviorId
        int behaviorId = Integer.parseInt(currentPatternsMap.keySet().iterator().next());
    
        ReplyEntry replyEntry = new ReplyEntry();
        
//      getUniqueId => currentPatternIdsList + i(index) + behaviorId 
        ReplyBehavior replyBehavior = new ReplyBehavior(currentPatternIdsList, i, String.valueOf(behaviorId));
        System.out.println("XXXX: " + replyBehavior.toString());
        int uniqueId = replyBehaviorsUniqueIdsMap.get(replyBehavior);
        System.out.println("uniqueId: " + uniqueId);

        replyEntry = new ReplyEntry(uniqueId, behaviorId, 
                    frequencyPatternsMap.get(frequencyPatternsList.get(0).getPatternId()).getBehaviors().get(i).getActionInfo(), 
                    behaviorLogicItem, valueLogicItem, nextbehaviorIdsList);
        
        System.out.println(replyEntry.toString());
        return replyEntry;
    }

    private static Logic dealWithCurrentValueLogic(List<String> patternIdsList, int i,
			Map<String, List<PatternBehaviors>> currentPatternsMap, List<String> currentUniqueIds, Map<String, Integer> behaviorToReplyUniqueIdMap) throws Exception {
    	System.out.println("[hasValueLogic = true] : " + "index: " + i + ", behaviors: " + currentPatternsMap.keySet()
		+ ", uniqueIds: " + currentUniqueIds.toString());
		String valueLogicId = "ValueLogic" + "_" + patternIdsList.toString() + "_" + i + "_" + currentPatternsMap.keySet();
//		System.out.println("logicId: " + valueLogicId);
		Logic valueLogic = new Logic();
		valueLogic.setID(valueLogicId);
		valueLogic.setLogicType("ValueLogic");
//		logicMaps.put(valueLogicId, valueLogic);
		System.out.println("logicId: " + valueLogic.getID() + "; LogicType: " + valueLogic.getLogicType());
		
		String csvPath = getConcepts(currentUniqueIds, "hasValueLogic", patternIdsList, i, currentPatternsMap, behaviorToReplyUniqueIdMap);
		String modelName = valueLogic.getID();
		valueLogic.setLogicModelAddress(System.getProperty("user.dir") + "/LogicModels/" + modelName);
		generateDesicionTree(csvPath,valueLogic.getLogicModelAddress());
		writeLogicToDatabase("localhost", "IRA_multiVideo", "logic", valueLogic);
		return valueLogic;
	}

	private static Logic dealWithBehaviorLogic(List<String> patternIdsList, int i,
			Map<String, List<PatternBehaviors>> nextPatternsMap, List<String> uniqueIds, Map<String, Integer> behaviorToReplyUniqueIdMap) throws Exception {
    	String behaviorLogicId = "BehaviorLogic" + "_" + patternIdsList.toString() + "_" + i + "_" + nextPatternsMap.keySet().toString();
		Logic behaviorLogic = new Logic();
		behaviorLogic.setID(behaviorLogicId);
		behaviorLogic.setLogicType("BehaviorLogic");
		System.out.println("logicId: " + behaviorLogic.getID() + "; LogicType: " + behaviorLogic.getLogicType());
		String csvPath = getConcepts(uniqueIds, "hasBehaviorLogic", patternIdsList, i, nextPatternsMap, behaviorToReplyUniqueIdMap);
		
		String modelName = behaviorLogic.getID();
		behaviorLogic.setLogicModelAddress(System.getProperty("user.dir") + "/LogicModels/" + modelName);
		generateDesicionTree(csvPath,behaviorLogic.getLogicModelAddress());
		writeLogicToDatabase("localhost", "IRA_multiVideo", "logic", behaviorLogic);
		return behaviorLogic;
	}

	private static Boolean judgeBehaviorLogic(int i, List<PatternBehaviors> frequencyPatternsList, Map<String, List<PatternBehaviors>> patternsMap) {
    	boolean hasBehaviorLogic = false;		
//		若index相同，但不同pattern的behaviorID不同
		if (patternsMap.keySet().size() > 1) {
			hasBehaviorLogic = true;
		}
		return hasBehaviorLogic;
	}
    

/**
 * 存储频繁的pattern
  patternID: ""
	weights: 
	behaviors: {
	  behaviorID: 
	  index:
	  uniqueIDs:[]
	  action: ""
	  actioned_concept: ""
	  actioned_picture: ""
	  actioned_value: ""
	},
	{}
 * @param frequencyPatternsList
 */
	private static Map<String, FrequencyPattern> StoreFrequencyPatterns(List<PatternBehaviors> frequencyPatternsList) {
		Map<String, FrequencyPattern> frequencyPatternMap = new HashMap<>();
		for(PatternBehaviors pattern : frequencyPatternsList){
			FrequencyPattern frequencyPattern = new FrequencyPattern();
			frequencyPattern.setPatternID(pattern.getPatternId());
			frequencyPattern.setWeights(pattern.getWeights());
			List<String> behaviorIDLists = pattern.getPatternBehaviors();
			List<BehaviorInfo> behaviorInfoLists = new ArrayList<>();
			for(int i = 0; i<behaviorIDLists.size(); i++){
				String behavior = behaviorIDLists.get(i);
				BehaviorInfo behaviorInfo = new BehaviorInfo();
				behaviorInfo.setBehavaviorID(behavior);
				behaviorInfo.setIndex(i);
				List<String> uniqueIDs = new ArrayList<>();
//				获取behaviorID 对应的所有 uniqueId 
				for(int j=0; j<pattern.getUniqueIdsList().size(); j++){
					uniqueIDs.add(pattern.getUniqueIdsList().get(j).get(i));
				}
				behaviorInfo.setUniqueIDs(uniqueIDs);
				Boolean isAllUniqueIDEqual = true;
				for(String uniqueID : uniqueIDs){
					//取action; actioned_concept; actioned_picture; actioned_value;;
					

				}
				ActionInfo actionInfo = new ActionInfo();
				behaviorInfo.setActionInfo(actionInfo);
				behaviorInfoLists.add(behaviorInfo);
			}
			frequencyPattern.setBehaviors(behaviorInfoLists);
			generateFrequencyPatternsJson(frequencyPattern);
			frequencyPatternMap.put(pattern.getPatternId(), frequencyPattern);
			
		}
		return frequencyPatternMap;
	}

	private static void generateFrequencyPatternsJson(FrequencyPattern frequencyPattern) {
		JSONObject jsonObject = frequencyPattern.toJsonObject();
		System.out.println(jsonObject.toJSONString());
		saveJsonObjectToMongoDb(jsonObject, "localhost", "IRA_multiVideo", "frequency_pattern");
	}

	private static void saveJsonObjectToMongoDb(JSONObject jsonObject,String clientName,String databaseName,String collectionName) {
		try{		 
			 MongoClient mongoClient =  new MongoClient(clientName, 27017);
			 MongoDatabase mongoDatabase =  mongoClient.getDatabase(databaseName);
//			 System.out.println("Connect to database successfully");
			 
			 MongoCollection<Document> collection =  mongoDatabase.getCollection(collectionName);
//			 System.out.println(jsonObject.toJSONString());
			 Document document = Document.parse(jsonObject.toJSONString());
			 
			 collection.insertOne(document);
			 mongoClient.close();

		 }catch(MongoException e){
			e.printStackTrace();
		} 
		
	}

	public static <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {
		  if(a.size() != b.size())
		    return false;
		  Collections.sort(a);
		  Collections.sort(b);
		  for(int i=0;i<a.size();i++){
		    if(!a.get(i).equals(b.get(i)))
		      return false;
		  }
		  return true;
	}

	private static Map<String, List<PatternBehaviors>> getPatternsByBehaviorId(int i, List<PatternBehaviors> patternBehaviorsList,
			List<String> uniqueIds) {
//      Map< key=behaviorId, value=[pattern1, pattern2] >  eg.<0, [pattern1, pattern2]>
		Map<String, List<PatternBehaviors>> map = new HashMap<>();
		List<PatternBehaviors> newPatternBehaviorsList = new ArrayList<>();
		for (PatternBehaviors pattern : patternBehaviorsList) {
			if (i < pattern.getPatternBehaviors().size()) {
//				取位置为i的behaviorId
				String behaviorId = pattern.getPatternBehaviors().get(i);
				if (!map.containsKey(behaviorId)) {
					newPatternBehaviorsList = new ArrayList<>();
					newPatternBehaviorsList.add(pattern);
					map.put(behaviorId, newPatternBehaviorsList);
				} else {
					newPatternBehaviorsList.add(pattern);
					map.put(behaviorId, newPatternBehaviorsList);
				}
//              对index＝i 的 behavior 的 uniqueIds
				for (List<String> uniqueId : pattern.getUniqueIdsList()) {
					uniqueIds.add(uniqueId.get(i));
				}
			}
			// System.out.println("index: " + i +", " + behaviors.toString()
			// + ", " + uniqueIdsLists.toString());
		}
		return map;
	}

	private static void writeLogicToDatabase(String clientName, String databaseName, String collectionName, Logic logic) {
		try {
			MongoClient mongoClient = new MongoClient(clientName, 27017);
			MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
			System.out.println("Connect to database successfully");
			MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);		
			
			JSONObject jsonObject = logic.toJsonObject();
			Document document = Document.parse(jsonObject.toJSONString());
			collection.insertOne(document);
			mongoClient.close();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
	}

	public static Boolean hasValueLogic(List<String> uniqueIds) throws Exception {
		Boolean hasValueLogic = false;
		List<Map<String, Object>> behaviorsAllInfoList = new ArrayList<>();
		behaviorsAllInfoList = getBehaviorsAllInfoFromMongoDB("localhost", "IRA_multiVideo", "behaviors", uniqueIds);
		// hasValueLogic(uniqueIds);

		String baseTitle = (String) behaviorsAllInfoList.get(0).get("title");
		String baseValue = (String) behaviorsAllInfoList.get(0).get("value");

		int i=0;
		for (Map<String, Object> behaviorsAllInfo : behaviorsAllInfoList) {
			String title = (String) behaviorsAllInfo.get("title");
			String value = (String) behaviorsAllInfo.get("value");
			i++;
			if ((baseTitle.equals(title)) && (!baseValue.equals(value))) {
//				System.out.println(i);
//				System.out.println("---baseTitle: " + baseTitle);
//				System.out.println("---baseValue: " + baseValue);
//				System.out.println("---Title: " + title);
//				System.out.println("---Value: " + value);
				return hasValueLogic = true;
			
			}
		}
		return hasValueLogic;

	}

	public static String getConcepts(List<String> uniqueIds, String logicType, List<String> patternIdsList, int i, Map<String, List<PatternBehaviors>> nextPatternsMap, Map<String, Integer> behaviorToReplyUniqueIdMap) throws Exception {
		List<Map<String, Object>> behaviorsAllInfoList = new ArrayList<>();
		behaviorsAllInfoList = getBehaviorsAllInfoFromMongoDB("localhost", "IRA_multiVideo", "behaviors", uniqueIds);
		String path = System.getProperty("user.dir") + "/ConceptCSV/" + logicType
				+ uniqueIds.toString() + ".xlsx";
//		 System.currentTimeMillis() +
		System.out.println(path);
		createExcel(behaviorsAllInfoList, logicType, path, patternIdsList, i, nextPatternsMap, behaviorToReplyUniqueIdMap);
		String csvPath = System.getProperty("user.dir") + "/ConceptCSV/" + logicType + uniqueIds
				+ ".csv";
		 XLSX2CSV xlsx2csv = new XLSX2CSV(path, csvPath);
		 xlsx2csv.process();
		 return csvPath;
	}

	public static void generateDesicionTree(String csvPath, String name) throws Exception {
		Instances data = DataSource.read(csvPath);
	    if (data.classIndex() == -1)
	    	data.setClassIndex(data.numAttributes() - 1);    
	    String[] options=weka.core.Utils.splitOptions("-U -M 1");
	    System.out.println(data.classAttribute());
	    J48 tree = new J48();
	    tree.setOptions(options);		    
		tree.buildClassifier(data);
		System.out.println("train end");		
		SerializationHelper.write(name, tree);//参数一为模型保存文件，classifier4为要保存的模型	
	}
	

	public static List<Map<String, Object>> getBehaviorsAllInfoFromMongoDB(String clientName, String databaseName,
			String collectionName, List<String> uniqueIds) throws Exception {
		List<Map<String, Object>> behaviorsAllInfoList = new ArrayList<>();
		try {
			MongoClient mongoClient = new MongoClient(clientName, 27017);
			MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
			System.out.println("Connect to database successfully");
			MongoCollection<Document> conceptCollection = mongoDatabase.getCollection(collectionName);
			BasicDBObject query2 = new BasicDBObject("$in", uniqueIds);
			MongoCursor<Document> conceptCursor = conceptCollection.find(new BasicDBObject("uniqueId", query2))
					.iterator();
			while (conceptCursor.hasNext()) {
				Document document = conceptCursor.next();
				Map<String, Object> map = new HashMap<>();
				map.putAll(document);
				behaviorsAllInfoList.add(map);
				System.out.println(map.toString());
			}
			mongoClient.close();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return behaviorsAllInfoList;
	}

	public static void createExcel(List<Map<String, Object>> behaviorsAllInfoList, String logicType, String path, List<String> patternIdsList, int i, Map<String, List<PatternBehaviors>> nextPatternsMap, Map<String, Integer> behaviorToReplyUniqueIdMap)
			throws IOException {
		// 存储Excel的路径
		System.out.println(path);

		// excel title
		Map<String, Map<String, String>> firstConceptsMap = (Map<String, Map<String, String>>) behaviorsAllInfoList.get(0).get("concepts");

		XSSFWorkbook wb = new XSSFWorkbook(); // 创建工作薄
		XSSFSheet sheet = wb.createSheet("sheet1"); // 创建工作表
		XSSFRow row0 = sheet.createRow(0); // 行
		XSSFCell cell; // 单元格

		Set<String> MetaConceptsKey = firstConceptsMap.keySet();
		System.out.println(MetaConceptsKey);
		// 添加表头数据
		int headIndex = 0;
		for (String key : MetaConceptsKey) {
			Map<String, String> levalTwoConcepts = firstConceptsMap.get(key);
			// System.out.println(levalTwoConcepts);
			Set<String> levalTwoConceptsKey = levalTwoConcepts.keySet();

			for (String key2 : levalTwoConceptsKey) {
				// String value = levalTwoConcepts.get(key2);
				String string = key + "." + key2;
				// System.out.println("title: " + string + ", value: " + value);
				row0.createCell(headIndex).setCellValue(string);
				// row.createCell(i).setCellValue(value);
				headIndex++;
			}
		}
		if (logicType == "hasBehaviorLogic") {
			row0.createCell(headIndex).setCellValue("replyUniqueId");
		} else {
			row0.createCell(headIndex).setCellValue("actionedValue");
		}

		int j = 1;
		int valueIndex = 0;
// 对behaviors表进行遍历
		for (Map<String, Object> behaviorsAllInfo : behaviorsAllInfoList) {
			XSSFRow row = sheet.createRow(j);
//			拿到behaviors 表中behavior中的concept数据
			Map<String, Map<String, String>> conceptsMap = (Map<String, Map<String, String>>) behaviorsAllInfo
					.get("concepts");

			for (String key : MetaConceptsKey) {
				Map<String, String> levalTwoConcepts = conceptsMap.get(key);
				Set<String> levalTwoConceptsKey = levalTwoConcepts.keySet();

				for (String key2 : levalTwoConceptsKey) {
					String string = key + "." + key2;
					String value = levalTwoConcepts.get(key2);
					row.createCell(valueIndex).setCellValue(value);
					valueIndex++;
					// System.out.println("valueIndex: " + valueIndex);
				}
			}
			if (logicType == "hasBehaviorLogic") {
//取uniqueId
//			    System.out.println("(String) behaviorsAllInfo.get(uniqueId) :" + (String) behaviorsAllInfo.get("uniqueId"));
				row.createCell(valueIndex).setCellValue("uniqueId_" + behaviorToReplyUniqueIdMap.get((String) behaviorsAllInfo.get("uniqueId")));
				
			} else {
				String title = (String) behaviorsAllInfo.get("title");
				String value = (String) behaviorsAllInfo.get("value");
				row.createCell(valueIndex).setCellValue("actionedValue_" + value);
			}
			j++;
			valueIndex = 0;
		}
		FileWriter outputStream1 = new FileWriter(path);
		FileOutputStream outputStream = new FileOutputStream(path);
		wb.write(outputStream);
		outputStream.flush();
		outputStream.close();
		System.out.println("写入成功");
	}

}
