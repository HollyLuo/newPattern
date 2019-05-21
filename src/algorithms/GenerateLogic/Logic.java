package algorithms.GenerateLogic;

import org.json.simple.JSONObject;

public class Logic {
	String ID;	
	String logicType;
	String logicModelAddress;
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getLogicType() {
		return logicType;
	}
	public void setLogicType(String logicType) {
		this.logicType = logicType;
	}
	public String getLogicModelAddress() {
		return logicModelAddress;
	}
	public void setLogicModelAddress(String logicModelAddress) {
		this.logicModelAddress = logicModelAddress;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJsonObject() {
		JSONObject output = new JSONObject();
		output.put("logicID", this.getID());
		output.put("logicType",this.getLogicType());
		output.put("logicModelAddress", this.getLogicModelAddress());
		return output;
		
	}

}
