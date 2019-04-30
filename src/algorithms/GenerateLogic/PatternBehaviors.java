package algorithms.GenerateLogic;

import java.util.ArrayList;
import java.util.List;

public class PatternBehaviors {
	private String patternId;
	private List<String> patternBehaviors = new ArrayList<>();
	private List<List<String>> uniqueIdsList = new ArrayList<>();
	private int weights;
	
	public List<String> getPatternBehaviors() {
		return patternBehaviors;
	}
	public void setPatternBehaviors(List<String> patternBehaviors) {
		this.patternBehaviors = patternBehaviors;
	}
	public List<List<String>> getUniqueIdsList() {
		return uniqueIdsList;
	}
	public void setUniqueIdsList(List<List<String>> uniqueIdsList) {
		this.uniqueIdsList = uniqueIdsList;
	}
	public void addUniqueIdsToList(List<List<String>> uniqueIdsList) {
		for(List<String> uniqueIds:uniqueIdsList){
			this.uniqueIdsList.add(uniqueIds);
		}	
	}
	
	public void printPatternBehaviors() {
		System.out.println("patternId: " + patternId + ", patternBehaviors: " + patternBehaviors + ", " + "uniqueIdsList" + uniqueIdsList.toString());
		
	}
	public String getPatternId() {
		return patternId;
	}
	public void setPatternId(String patternId) {
		this.patternId = patternId;
	}
	public int getWeights() {
		return weights;
	}
	public void setWeights(int weights) {
		this.weights = weights;
	}
	
//	public boolean isFrequencyPattern(int size, float support){
//		float a= 0.0f;
//		a = this.weights/(float)size;
//		if(a >= support){
//			return true;
//		}else {
//			return false;
//		}
//		
//	}
	public boolean isFrequencyPattern(int size, float support){
		int minsuppAbsolute = 0;
		if(support<1){
			minsuppAbsolute = (int) Math.ceil(size*support);
			if(minsuppAbsolute == 0){
				minsuppAbsolute = 1;
			}
		}else {
			minsuppAbsolute = (int)Math.ceil(support);	
		}
		if(weights >= minsuppAbsolute){
			return true;
		}else {
			return false;
		}
	}
	

}
