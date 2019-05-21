package algorithms.GenerateLogic;

import java.util.List;

public class BehaviorInfo {
	private String behavaviorID;
	private int index;
	private List<String> uniqueIDs;
	private String action;
	private String actionedConcept;
	private String actionedPicture;
	private String actionedValue;
	
	public String getBehavaviorID() {
		return behavaviorID;
	}
	public void setBehavaviorID(String behavaviorID) {
		this.behavaviorID = behavaviorID;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public List<String> getUniqueIDs() {
		return uniqueIDs;
	}
	public void setUniqueIDs(List<String> uniqueIDs) {
		this.uniqueIDs = uniqueIDs;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getActionedConcept() {
		return actionedConcept;
	}
	public void setActionedConcept(String actionedConcept) {
		this.actionedConcept = actionedConcept;
	}
	public String getActionedPicture() {
		return actionedPicture;
	}
	public void setActionedPicture(String actionedPicture) {
		this.actionedPicture = actionedPicture;
	}
	public String getActionedValue() {
		return actionedValue;
	}
	public void setActionedValue(String actionedValue) {
		this.actionedValue = actionedValue;
	}

}
