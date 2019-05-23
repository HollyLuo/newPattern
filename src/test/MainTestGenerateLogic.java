package test;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.formula.functions.Index;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import algorithms.Convert.XLSX2CSV;
import algorithms.GenerateLogic.ActionInfo;
import algorithms.GenerateLogic.BehaviorInfo;
import algorithms.GenerateLogic.FrequecyPattern;
import algorithms.GenerateLogic.GetPatterns;
import algorithms.GenerateLogic.Knowledge;
import algorithms.GenerateLogic.Logic;
import algorithms.GenerateLogic.PatternBehaviors;
import algorithms.splitpatterns.cycle.Vertex;
import java_cup.internal_error;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;;

public class MainTestGenerateLogic {

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
		for(PatternBehaviors new_pattern : patternBehaviorsList){
			size += new_pattern.getUniqueIdsList().size();
		}
//		System.out.println("size: " + size);
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
        StoreFrequencyPatterns(frequencyPatternBehaviorsList);
        
        System.out.println("--------------Logic Judgement -----------------");
        Map<String, Logic> logicMaps = new HashMap<>();
        judgeLogic(0, frequencyPatternBehaviorsList, logicMaps);
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
	private static void StoreFrequencyPatterns(List<PatternBehaviors> frequencyPatternsList) {
		for(PatternBehaviors pattern : frequencyPatternsList){
			FrequecyPattern frequecyPattern = new FrequecyPattern();
			frequecyPattern.setPatternID(pattern.getPatternId());
			frequecyPattern.setWeights(pattern.getWeights());
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
					//取action; actioned_concept; actioned_picture; actioned_value;
					
				}
				ActionInfo actionInfo = new ActionInfo();
				behaviorInfo.setActionInfo(actionInfo);
				behaviorInfoLists.add(behaviorInfo);
			}
			frequecyPattern.setBehaviors(behaviorInfoLists);
			generateFrequencyPatternsJSon(frequecyPattern);
			
		}
	}

	private static void generateFrequencyPatternsJSon(FrequecyPattern frequecyPattern) {
		JSONObject jsonObject = frequecyPattern.toJsonObject();
		System.out.println(jsonObject.toJSONString());
		saveJsonObjectToMongoDb(jsonObject, "localhost", "IRA_multiVideo", "frequency_pattern");
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
	private static void judgeLogic(int firstIndex, List<PatternBehaviors> patternBehaviorsList, Map<String, Logic> logicMaps) throws Exception {
		int length = 0;
		List<String> patternList = new ArrayList<>();
		for (PatternBehaviors item : patternBehaviorsList) {
			item.printPatternBehaviors();
//			判断最长的pattern 长度
			length = item.getPatternBehaviors().size() > length ? item.getPatternBehaviors().size() : length;
			patternList.add(item.getPatternId());
			
		}
//		遍历index，对比
		for (int i = firstIndex; i < length; i++) {
			boolean hasBehaviorLogic = false;
			//判断前面没有Logic分支。
			List<String> behaviors = new ArrayList<>();
			List<String> uniqueIds = new ArrayList<>();
			
//          Map< key=behaviorId, value=[pattern1, pattern2] >  eg.<0, [pattern1, pattern2]>
			Map<String, List<PatternBehaviors>> map = new HashMap<>();
			List<PatternBehaviors> newPatternBehaviorsList = new ArrayList<>();
			for (PatternBehaviors pattern : patternBehaviorsList) {
				if (i < pattern.getPatternBehaviors().size()) {
//					取位置为i的behaviorId
					String behaviorId = pattern.getPatternBehaviors().get(i);
					if (!map.containsKey(behaviorId)) {
						newPatternBehaviorsList = new ArrayList<>();
						newPatternBehaviorsList.add(pattern);
						map.put(behaviorId, newPatternBehaviorsList);
					} else {
						newPatternBehaviorsList.add(pattern);
						map.put(behaviorId, newPatternBehaviorsList);
					}
//                  对index＝i 的 behavior 的 uniqueIds
					for (List<String> uniqueId : pattern.getUniqueIdsList()) {
						uniqueIds.add(uniqueId.get(i));
					}
				}
				// System.out.println("index: " + i +", " + behaviors.toString()
				// + ", " + uniqueIdsLists.toString());
			}
			System.out.println("------------map.keySet: " + map.keySet() + "--------------");
//			若index相同，但不同pattern的behaviorID不同
			if (map.keySet().size() > 1) {
				hasBehaviorLogic = true;
				System.out.println("[hasBehaviorLogic = true] : " + "index: " + i + ", behaviors: " + map.keySet()
				+ ", uniqueIds: " + uniqueIds.toString());
//				behaviorLogicId命名： [1,2]_5_[6,9]
				String behaviorLogicId = patternList.toString() + "_" + i + "_" + map.keySet().toString();
				Logic behaviorLogic = new Logic();
				behaviorLogic.setID(behaviorLogicId);
				behaviorLogic.setLogicType("BehaviorLogic");
				logicMaps.put(behaviorLogicId, behaviorLogic);
				System.out.println("logicId: " + behaviorLogic.getID() + "; LogicType: " + behaviorLogic.getLogicType());
				String csvPath = getConcepts(behaviors, uniqueIds, "hasBehaviorLogic");
				String modelName = "BehaviorLogic" + behaviorLogic.getID();
				generateDesicionTree(csvPath,modelName);
				behaviorLogic.setLogicModelAddress(System.getProperty("user.dir") + "/" + modelName);
				writeLogicToDatabase("localhost", "IRA_multiVideo", "logic", behaviorLogic);
				
				for(Map.Entry<String, List<PatternBehaviors>> entry: map.entrySet()){
					System.out.println("XXXXX key: " + entry.getKey());
					//uniqueIds.
					List<String> subUniqueIds = new ArrayList<>();
					for(PatternBehaviors pattern: entry.getValue()){
						for (List<String> uniqueId : pattern.getUniqueIdsList()) {
							subUniqueIds.add(uniqueId.get(i));
						}
					}
					System.out.println("subUniqueIds" + subUniqueIds);
					if(entry.getValue().size()>1){
						judgeLogic(i+1,entry.getValue(),logicMaps);
					}else {
						System.out.println("oooo");
						boolean hasValueLogic = hasValueLogic(subUniqueIds);
						System.out.println("[hasValueLogic = " + hasValueLogic +"]");
						if (hasValueLogic) {				
							System.out.println("[hasValueLogic = true] : " + "index: " + i + ", behaviors: " + entry.getKey()
							+ ", uniqueIds: " + subUniqueIds.toString());
							
							String valueLogicId = patternList.toString() + "_" + i + "_" + map.keySet().toString();
							Logic valueLogic = new Logic();
							valueLogic.setID(valueLogicId);
							valueLogic.setLogicType("ValueLogic");
							logicMaps.put(valueLogicId, valueLogic);
							System.out.println("logicId: " + valueLogic.getID() + "; LogicType: " + valueLogic.getLogicType());
							String csvPath2 = getConcepts(behaviors,subUniqueIds,"hasValueLogic");
							String modelName2 = valueLogic.getLogicType() + valueLogic.getID();
							generateDesicionTree(csvPath2,modelName2);
							valueLogic.setLogicModelAddress(System.getProperty("user.dir") + "/" + modelName2);
							writeLogicToDatabase("localhost", "IRA_multiVideo", "logic", valueLogic);
						}
						judgeLogic(i+1,entry.getValue(),logicMaps);
					}				
//					System.out.println("XXXXX");
				}
			} else {
				System.out.println("[hasBehaviorLogic = false] : " + "index: " + i + ", behaviors: " + map.keySet()
				+ ", uniqueIds: " + uniqueIds.toString());
				boolean hasValueLogic = hasValueLogic(uniqueIds);
				System.out.println("[hasValueLogic = " + hasValueLogic +"]");
				if (hasValueLogic) {				
					System.out.println("[hasValueLogic = true] : " + "index: " + i + ", behaviors: " + map.keySet()
					+ ", uniqueIds: " + uniqueIds.toString());
					String valueLogicId = patternList.toString() + "_" + i + "_" + map.keySet().toString();
//					System.out.println("logicId: " + valueLogicId);
					Logic valueLogic = new Logic();
					valueLogic.setID(valueLogicId);
					valueLogic.setLogicType("ValueLogic");
					logicMaps.put(valueLogicId, valueLogic);
					System.out.println("logicId: " + valueLogic.getID() + "; LogicType: " + valueLogic.getLogicType());
					
					String csvPath = getConcepts(behaviors,uniqueIds,"hasValueLogic");
					String modelName = valueLogic.getLogicType() + valueLogic.getID();
					generateDesicionTree(csvPath,modelName);
					valueLogic.setLogicModelAddress(System.getProperty("user.dir") + "/" + modelName);
					writeLogicToDatabase("localhost", "IRA_multiVideo", "logic", valueLogic);
				}

			}
		}

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
				System.out.println("---baseTitle: " + baseTitle);
				System.out.println("---baseValue: " + baseValue);
				System.out.println("---Title: " + title);
				System.out.println("---Value: " + value);
				return hasValueLogic = true;
			
			}
		}
		return hasValueLogic;

	}

	public static String getConcepts(List<String> behaviors, List<String> uniqueIds, String logicType) throws Exception {
		List<Map<String, Object>> behaviorsAllInfoList = new ArrayList<>();
		behaviorsAllInfoList = getBehaviorsAllInfoFromMongoDB("localhost", "IRA_multiVideo", "behaviors", uniqueIds);
		String path = "/Users/ling/Documents/Eclipseworkspace/Weka/NewPattern/src/test/" + logicType
				+ uniqueIds.toString() + ".xlsx";
//		 System.currentTimeMillis() +
		System.out.println(path);
		createExcel(behaviorsAllInfoList, logicType, path);
		String csvPath = "/Users/ling/Documents/Eclipseworkspace/Weka/NewPattern/src/test/" + logicType + uniqueIds
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

	public static void createExcel(List<Map<String, Object>> behaviorsAllInfoList, String logicType, String path)
			throws IOException {
		// 存储Excel的路径
		System.out.println(path);

		// excel title
		Map<String, Map<String, String>> firstConceptsMap = (Map<String, Map<String, String>>) behaviorsAllInfoList
				.get(0).get("concepts");

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
			row0.createCell(headIndex).setCellValue("behavior_ID");
		} else {
			row0.createCell(headIndex).setCellValue("title_value");
		}

		int j = 1;
		int valueIndex = 0;

		for (Map<String, Object> behaviorsAllInfo : behaviorsAllInfoList) {
			XSSFRow row = sheet.createRow(j);
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
				String behaviorId = (String) behaviorsAllInfo.get("behaviorID");
				row.createCell(valueIndex).setCellValue("behavior_" + behaviorId);
			} else {
				String title = (String) behaviorsAllInfo.get("title");
				String value = (String) behaviorsAllInfo.get("value");
				row.createCell(valueIndex).setCellValue(title + "=" + value);
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
