package algorithms.GenerateLogic;

import java.awt.print.Printable;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;


public class ReplyEntry {
	private int uniqueId;
	private int id;
	private ActionInfo actionInfo;
	private String behaviorLogic;
	private String valueLogic;
	private Set<Integer> nextActionIds;

	
	public ReplyEntry() {
		
	}
	public ReplyEntry(int uniqueId, int id, ActionInfo actionInfo, String behaviorLogic, String valueLogic, Set<Integer> nextActionIds) {
		this.uniqueId = uniqueId;
		this.id = id;
		this.actionInfo = actionInfo;
		this.behaviorLogic = behaviorLogic;
		this.valueLogic = valueLogic;
		this.nextActionIds = nextActionIds;
	}
	public int getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ActionInfo getActionInfo() {
		return actionInfo;
	}
	public void setActionInfo(ActionInfo actionInfo) {
		this.actionInfo = actionInfo;
	}
	public String getValueLogic() {
		return valueLogic;
	}
	public void setHasValueLogic(String valueLogic) {
		this.valueLogic = valueLogic;
	}
	public String getBehaviorLogic() {
		return behaviorLogic;
	}
	public void setHasBehaviorLogic(String behaviorLogic) {
		this.behaviorLogic = behaviorLogic;
	}
	public Set<Integer> getNextActionIds() {
		return nextActionIds;
	}
	public void setNextActionIds(Set<Integer> set) {
		this.nextActionIds = set;
	}
	
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{" + "uniqueId:" + this.getUniqueId() 
		+ ", id:" + this.getId() 
		+ ", action:" + this.getActionInfo().getAction() 
		+ ", actioned_concept:" + this.getActionInfo().getActionedConcept()
		+ ", actioned_picture:" + this.getActionInfo().getActionedPicture() 
		+ ", actioned_value:" + this.getActionInfo().getActionedValue()
		+ ", behavior_logic:" + this.getBehaviorLogic()
		+ ", value_logic:" + this.getValueLogic()
		+ ", default_next_action_ids:" + this.getNextActionIds() + "}");
		
		return stringBuffer.toString();
	}
	
	@SuppressWarnings("unchecked")
    public JSONObject toJsonObject() {
        JSONObject output = new JSONObject();
        output.put("uniqueId", this.getUniqueId());
        output.put("id", this.getId());
        output.put("action", this.getActionInfo().getAction());
        output.put("actioned_concept",this.getActionInfo().getActionedConcept());
        output.put("actioned_picture",this.getActionInfo().getActionedPicture());
        output.put("actioned_value", this.getActionInfo().getActionedValue());
        output.put("behavior_logic", this.getBehaviorLogic());
        output.put("value_logic", this.getValueLogic());
        output.put("default_next_action_ids", this.getNextActionIds());
        return output;
        
    }

}
