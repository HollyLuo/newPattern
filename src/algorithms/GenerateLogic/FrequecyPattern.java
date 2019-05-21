package algorithms.GenerateLogic;

import java.util.ArrayList;
import java.util.List;

public class FrequecyPattern {
	private String patternID;
	private int weights;
	private List<BehaviorInfo> behaviors = new ArrayList<>();
	public String getPatternID() {
		return patternID;
	}

	public void setPatternID(String patternID) {
		this.patternID = patternID;
	}

	public int getWeights() {
		return weights;
	}

	public void setWeights(int weights) {
		this.weights = weights;
	}

	public List<BehaviorInfo> getBehaviors() {
		return behaviors;
	}

	public void setBehaviors(List<BehaviorInfo> behaviors) {
		this.behaviors = behaviors;
	}

	public void printPatternBehaviors() {
//		System.out.println("patternId: " + patternId + ", patternBehaviors: " + patternBehaviors + ", " + "uniqueIdsList" + uniqueIdsList.toString());	
	}
	

}
