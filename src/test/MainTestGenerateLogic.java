package test;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import algorithms.Convert.XLSX2CSV;
import algorithms.GenerateLogic.GetPatterns;
import algorithms.GenerateLogic.PatternBehaviors;
import algorithms.splitpatterns.cycle.Vertex;;

public class MainTestGenerateLogic {

	public static void main(String[] args) throws Exception {
		List<PatternBehaviors> patternBehaviorsList = new ArrayList<>();
		patternBehaviorsList = GetPatterns.getAllPatterns("localhost", "IRA_multiVideo");
		float support = 2;
		
		List<PatternBehaviors> frequencyPatternBehaviorsList = new ArrayList<>();
		int size = 0;
		for(PatternBehaviors new_pattern : patternBehaviorsList){
			size += new_pattern.getUniqueIdsList().size();
		}
		System.out.println("size: " + size);
	    System.out.println("--------------Frequecy Pattern -----------------");
        for(PatternBehaviors new_pattern : patternBehaviorsList){
    	    if(new_pattern.isFrequencyPattern(size, support)){
    	    	frequencyPatternBehaviorsList.add(new_pattern);
    	    }
      }    
		
		// System.out.println("dsssss");
		judgeLogic(0, frequencyPatternBehaviorsList);
		// int length = 0;
		// for (PatternBehaviors item : patternBehaviorsList) {
		// item.printPatternBehaviors();
		// length = item.getPatternBehaviors().size() > length ?
		// item.getPatternBehaviors().size() : length;
		// }
		// // Logic judge between patterns
		//
		// for (int i = 0; i < length; i++) {
		// boolean hasBehaviorLogic = false;
		// // boolean hasValueLogic = false;
		// List<String> behaviors = new ArrayList<>();
		// String baseBehaviorId =
		// patternBehaviorsList.get(0).getPatternBehaviors().get(i);
		// List<String> uniqueIds = new ArrayList<>();
		// boolean splitPattern = false;
		// if (splitPattern == false) {
		// for (PatternBehaviors pattern : patternBehaviorsList) {
		// String behaviorId = pattern.getPatternBehaviors().get(i);
		// if (i < pattern.getPatternBehaviors().size()) {
		// behaviors.add(behaviorId);
		// }
		// if (baseBehaviorId != behaviorId) {
		// hasBehaviorLogic = true;
		// }
		// for (List<String> uniqueId : pattern.getUniqueIdsList()) {
		// uniqueIds.add(uniqueId.get(i));
		// }
		// // System.out.println("index: " + i +", " +
		// // behaviors.toString() + ", " + uniqueIdsLists.toString());
		// }
		// }
		// if (hasBehaviorLogic) {
		// System.out.println("Logic: " + "index: " + i + ", behaviors: " +
		// behaviors.toString() + ", uniqueIds: "
		// + uniqueIds.toString());
		// getConcepts(behaviors, uniqueIds, "hasBehaviorLogic");
		// splitPattern = true;
		// } else {
		// // List<Map<String, Object>> behaviorsAllInfoList = new
		// // ArrayList<>();
		// // behaviorsAllInfoList =
		// // getBehaviorsAllInfoFromMongoDB("localhost", "IRA_test",
		// // "behaviors", uniqueIds);
		// System.out.println("index: " + i + ", " + behaviors.toString() + ", "
		// + uniqueIds.toString());
		// if (hasValueLogic(uniqueIds)) {
		// getConcepts(behaviors, uniqueIds, "hasValueLogic");
		// }
		//
		// }
		// }
	}

	private static void judgeLogic(int firstIndex, List<PatternBehaviors> patternBehaviorsList) throws Exception {
		int length = 0;
		for (PatternBehaviors item : patternBehaviorsList) {
			item.printPatternBehaviors();
//			System.out.println("dddd");
			length = item.getPatternBehaviors().size() > length ? item.getPatternBehaviors().size() : length;
		}
		for (int i = firstIndex; i < length; i++) {
			boolean hasBehaviorLogic = false;
			// boolean hasValueLogic = false;
			//判断前面没有Logic分支。
			List<String> behaviors = new ArrayList<>();
			List<String> uniqueIds = new ArrayList<>();

			Map<String, List<PatternBehaviors>> map = new HashMap<>();
			List<PatternBehaviors> newPatternBehaviorsList = new ArrayList<>();
			for (PatternBehaviors pattern : patternBehaviorsList) {
				if (i < pattern.getPatternBehaviors().size()) {
					String behaviorId = pattern.getPatternBehaviors().get(i);
					if (!map.containsKey(behaviorId)) {
						newPatternBehaviorsList = new ArrayList<>();
						newPatternBehaviorsList.add(pattern);
						map.put(behaviorId, newPatternBehaviorsList);
						System.out.println("111 : " + behaviorId);
					} else {
						newPatternBehaviorsList.add(pattern);
						map.put(behaviorId, newPatternBehaviorsList);
						System.out.println("222 : " + behaviorId);
					}

					// behaviors.add(behaviorId);
					for (List<String> uniqueId : pattern.getUniqueIdsList()) {
						uniqueIds.add(uniqueId.get(i));
					}
				}
				// System.out.println("index: " + i +", " + behaviors.toString()
				// + ", " + uniqueIdsLists.toString());
			}
			System.out.println("map.keySet: " + map.keySet());
			if (map.keySet().size() > 1) {
				hasBehaviorLogic = true;
				System.out.println("[hasBehaviorLogic = true] : " + "index: " + i + ", behaviors: " + map.keySet()
				+ ", uniqueIds: " + uniqueIds.toString());	
				getConcepts(behaviors, uniqueIds, "hasBehaviorLogic");
				for(Map.Entry<String, List<PatternBehaviors>> entry: map.entrySet()){
//					System.out.println("XXXXX");
					List<PatternBehaviors> newlist= entry.getValue();
//					for(PatternBehaviors patternBehaviors:newlist){
//						System.out.println(patternBehaviors.getPatternId());
//					}
//					System.out.println(newlist.size());
					if(entry.getValue().size()>1){
						judgeLogic(i+1,entry.getValue());
					}else {
//						System.out.println("oooo");
						judgeLogic(i+1,entry.getValue());
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
					 getConcepts(behaviors,uniqueIds,"hasValueLogic");
//					 for(Map.Entry<String, List<PatternBehaviors>> entry: map.entrySet()){
////							System.out.println("XXXXX");
//							List<PatternBehaviors> newlist= entry.getValue();
////							for(PatternBehaviors patternBehaviors:newlist){
////								System.out.println(patternBehaviors.getPatternId());
////							}
////							System.out.println(newlist.size());
//							if(entry.getValue().size()>1){
//								judgeLogic(i+1,entry.getValue());
//							}else {
////								System.out.println("oooo");
//								judgeLogic(i+1,entry.getValue());
//							}				
////							System.out.println("XXXXX");
//						}
				}

			}
		}

	}

	public static Boolean hasValueLogic(List<String> uniqueIds) throws Exception {
		Boolean hasValueLogic = false;
		List<Map<String, Object>> behaviorsAllInfoList = new ArrayList<>();
		behaviorsAllInfoList = getBehaviorsAllInfoFromMongoDB("localhost", "IRA_multiVideo", "behaviors", uniqueIds);
		// hasValueLogic(uniqueIds);

		String baseTitle = (String) behaviorsAllInfoList.get(0).get("title");
		String baseValue = (String) behaviorsAllInfoList.get(0).get("value");
//		System.out.println("baseTitle: " + baseTitle);
//		System.out.println("baseValue: " + baseValue);
		int i=0;
		for (Map<String, Object> behaviorsAllInfo : behaviorsAllInfoList) {
			String title = (String) behaviorsAllInfo.get("title");
			String value = (String) behaviorsAllInfo.get("value");
//			System.out.println("Title: " + title);
//			System.out.println("Value: " + value);
			i++;
			if ((baseTitle.equals(title)) && (!baseValue.equals(value))) {
				System.out.println(i);
				System.out.println("baseTitle: " + baseTitle);
				System.out.println("baseValue: " + baseValue);
				System.out.println("Title: " + title);
				System.out.println("Value: " + value);
				return hasValueLogic = true;
			
			}
		}
		return hasValueLogic;

	}

	public static void getConcepts(List<String> behaviors, List<String> uniqueIds, String logicType) throws Exception {
		List<Map<String, Object>> behaviorsAllInfoList = new ArrayList<>();
		behaviorsAllInfoList = getBehaviorsAllInfoFromMongoDB("localhost", "IRA_multiVideo", "behaviors", uniqueIds);
		String path = "/Users/ling/Documents/Eclipseworkspace/Weka/NewPattern/src/test/" + System.currentTimeMillis() + logicType
				+ uniqueIds.toString() + ".xlsx";
		System.out.println(path);
		createExcel(behaviorsAllInfoList, logicType, path);
		String csvPath = "/Users/ling/Documents/Eclipseworkspace/Weka/NewPattern/src/test/" + System.currentTimeMillis() + logicType + uniqueIds
				+ ".csv";
		 XLSX2CSV xlsx2csv = new XLSX2CSV(path, csvPath);
		 xlsx2csv.process();
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
