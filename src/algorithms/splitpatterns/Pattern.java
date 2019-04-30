package algorithms.splitpatterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import algorithms.splitpatterns.cycle.Cycle;
import algorithms.splitpatterns.cycle.InternalCycle;
//import pfv.spmf.algorithms.splitpatterns.cycle.Vertex;
//import pfv.spmf.algorithms.splitpatterns.SplitBehaviorChain;
public class Pattern {
	 private int pattern_name;
	 private List<String> behaviorChain; //从出发节点到当前节点的轨迹
	 private int weight;
	 private InternalCycle internalCycle;
	 boolean hasInternalCycle ;
	 private List<String> newBehaviorChain;
	 private List<List<String>> uniqueIdsList;
	 
	 
	 public Pattern() {
	     this.setBehaviorChain(new ArrayList<>());//路径
	     this.weight=0;
	     this.internalCycle = new InternalCycle();
	     this.hasInternalCycle = false;
	     this.newBehaviorChain = new ArrayList<>();
	     this.uniqueIdsList = new ArrayList<List<String>>();
	}
	 
	 public Pattern(List<String> behaviorChain, List<List<String>> uniqueIdsList, int weight) {
	     this.behaviorChain = behaviorChain;
	     this.weight = weight;
	     this.uniqueIdsList = uniqueIdsList;
	     
//	     this.internalCycle = new InternalCycle();
//	     this.hasInternalCycle = false;
//	     this.newBehaviorChain = new ArrayList<>();	     
	}
	
	 
	 public int getWeight() {
	    return this.weight;	
	 }
	
	 public void setWeight(int weight) {
		  this.weight = weight;	
	 }

	public int getPatternName() {
		return pattern_name;
	}

	public void setPatternName(int pattern_name) {
		this.pattern_name = pattern_name;
	}
	
	public void foundInternalCycle(ArrayList<Cycle> cycleList){
		
//		String patt1.clone()patt2.replace(" ", "");
	    for(Cycle cycle: cycleList){
	    	ArrayList<String> cycleTrace = cycle.getTrace();
	    	if(!behaviorChain.equals(cycleTrace)){
	    	    boolean hasInternalCycle1 = findInternalCycleFromPattern(cycle);
	    	    if(hasInternalCycle1){
//	    	    	System.out.println("hasInternalCycle");
	    	    	this.internalCycle.printInternalCycle();
	    	    	hasInternalCycle = hasInternalCycle1;
//	    	    	List<String> newTrace = removeInternalCycle();
	    	    	
	    	    }
	    	   
//	    	    findInternalCycleFromPattern(trace,cycle);
	    	    
	    	}
	    }
	}
	private static String convertToString(List<String> cycleTrace) {
		String stringCycle = "";
		for(int i=0;i<cycleTrace.size();i++){
			if(i==cycleTrace.size()-1){
				stringCycle +=cycleTrace.get(i);
			}else{
				stringCycle +=cycleTrace.get(i) + ",";
			}
		}
		return stringCycle;
	}
	
	private boolean findInternalCycleFromPattern(Cycle cycle) {
		
		String patt1 = behaviorChain.toString().replace(" ", "");
		String cycleChain= convertToString(cycle.getTrace());
		
        int num = 0;
//        System.out.println("--patt1:" + patt1);
//        System.out.println("--Cycle:"+ cycleChain);
        String newString = patt1;
        int index = 0;
        boolean hasInternalCycle=false;
        while (newString.contains(cycleChain)) {
        	
        	if(patt1.indexOf(cycleChain) == 1){
        		index = patt1.indexOf(cycleChain);
//        		System.err.println("1111");
        	}else{
        		
        		index = (patt1.indexOf(cycleChain)+1)/2;
//        		System.err.println("patt1.indexOf(cycleChain): "+patt1.indexOf(cycleChain));
			}
//        	System.out.println(index-1);
      	
//        	System.out.println("start index: "+ index);
//        	System.out.println("indexOf: "+ oriString.indexOf(sToFind));
        	newString = newString.substring(newString.indexOf(cycleChain) + cycleChain.length());
            num ++;
            hasInternalCycle = true;
        }
        
        if(hasInternalCycle){
        	this.internalCycle.setSerialNumber(1);
        	this.internalCycle.setCycleStart(index);
        	this.internalCycle.setTrace(cycle.getTrace());
        	this.internalCycle.setWeight(num);
        }
        return hasInternalCycle;
    }
	
	public void removeInternalCycle() {
//		List<String> newTrace = new ArrayList<String>();
		if(hasInternalCycle){	
//			System.out.println("lodTrace222: "+ this.trace);
			for(int i=0;i<behaviorChain.size();i++){
				if(i<internalCycle.getCycleStart()-1 || i>=internalCycle.getCycleEnd()-1){
					this.newBehaviorChain.add(behaviorChain.get(i));
				}
			}
		}else {
			this.newBehaviorChain.addAll(behaviorChain);
		}
//		System.out.println("newTrace: "+ newTrace);
//		System.out.println("lodTrace: "+ this.trace);
		
	}
	
//	private static int findInternalCycleFromPattern(List<String> trace,Cycle cycle) {
//        int num = 0;
//        List<String> pattern_trace = trace;
//        List<String> cycle_trace = cycle.getTrace();
//        if(pattern_trace!=cycle_trace){
//        	System.out.println(pattern_trace.containsAll(cycle_trace));
//        }
//        
//        return num;
//    }
		
	
	public void printPattern() {
		System.out.println("Pattern: "+ this.behaviorChain);
		System.out.println("Weight: " + this.weight);
	}
	
	public boolean isFrequencyPattern(int size, float support){
		float a= 0.0f;
		a = weight/(float)size;
		if(a >= support){
			return true;
		}else {
			return false;
		}
		
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJsonObject() {
		JSONObject output = new JSONObject();
		output.put("chainId", String.valueOf(System.currentTimeMillis()));
		output.put("behaviorChain",this.getBehaviorChain());
		output.put("weights", this.weight);
		output.put("uniqueIdsList", uniqueIdsList);
		JSONObject internal = new JSONObject();
		internal.put("cycleChain", this.internalCycle.getTrace());
		internal.put("cycleTimes", this.internalCycle.getCountInfo());
		internal.put("cycleStart", this.internalCycle.getCycleStart());
		output.put("internalCycle", internal);
		output.put("newBehaviorChain", this.newBehaviorChain);
		return output;
		
	}

	public List<List<String>> getUniqueIdsList() {
		return uniqueIdsList;
	}

	public void setUniqueIdsList(List<List<String>> uniqueIdsList) {
		this.uniqueIdsList = uniqueIdsList;
	}



	public List<String> getBehaviorChain() {
		return behaviorChain;
	}



	public void setBehaviorChain(List<String> behaviorChain) {
		this.behaviorChain = behaviorChain;
	}


	public List<String> getNewBehaviorChain() {
		return newBehaviorChain;
	}


	public void setNewBehaviorChain(List<String> newBehaviorChain) {
		this.newBehaviorChain = newBehaviorChain;
	}

	public void addUniqueIdsToList(List<List<String>> uniqueIdsList2) {
		for(List<String> uniqueIds:uniqueIdsList2){
			this.uniqueIdsList.add(uniqueIds);
		}	
		
	}
	   

}
