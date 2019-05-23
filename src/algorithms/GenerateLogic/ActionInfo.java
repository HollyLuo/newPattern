package algorithms.GenerateLogic;


public class ActionInfo {
	private String action;
	private String actionedConcept;
	private String actionedPicture;
	private String actionedValue;
	
	public ActionInfo() {
		this.action = "actionType.type";
		this.actionedConcept = "title";
		this.actionedPicture = "element.coveredRegion";
		this.actionedValue = "value";
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
