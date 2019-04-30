//package test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import algorithms.splitpatterns.SplitBehaviors;
//import algorithms.splitpatterns.Behaviors;
//import algorithms.splitpatterns.GetBehaviors;
//
//public class MainTestSplitPatterns {
//
//	public static void main(String [] arg) throws Exception{
//		Map<String, List<Behaviors>> map = new HashMap<>();
//		map = GetBehaviors.getBehaviorsFromMongoDB("localhost", "IRA_multiVideo","behaviors");
////		float support = 0.5f;
////		SplitBehaviors.runAlgorithm(behaviorsMap,2);
//		
//		System.out.println(map.keySet());
////		List<String> videoList = Arrays.asList("H04TAFI02PS0109_10.108.129.41_cg81319_1552879410515", "H04TAFI02PS0109_10.108.129.41_cg81319_1552881404435",
////				"H04TAFI02PS0109_10.108.129.41_cg81319_1552881495252","H04TAFI02PS0109_10.108.129.41_cg81319_1552881613885",
////				"H04TAFI02PS0109_10.108.129.41_cg81319_1552881735869");
//		List<String> videoList = Arrays.asList("H04TAFI02PS0109_10.108.129.41_cg81319_1552991735869", "H04TAFI02PS0109_10.108.129.41_cg81319_1552992735869");
//		for (Map.Entry<String, List<Behaviors>> entry : map.entrySet()) { 
//			  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().toString()); 
//			  Map<String, String> behaviorsMap = new LinkedHashMap<>();
//			  for(Behaviors behaviors : entry.getValue()) {
//				  behaviorsMap.put(behaviors.getUniqueId(), behaviors.getBehaviorID());
//				}
//			  if(videoList.contains(entry.getKey())){
//				  SplitBehaviors.runAlgorithm(behaviorsMap);
////				  System.out.println("xxx");
//			  }
////			  
////			  System.out.println(behaviorsMap.toString());
//			}
//		
//
////		SplitBehaviors.runAlgorithm(behaviorsMap);
//		
//	}
//	
////	public static String fileToPath(String filename) throws UnsupportedEncodingException{
////		URL url = MainTestSplitPatterns.class.getResource(filename);
////		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
////	}
//}
