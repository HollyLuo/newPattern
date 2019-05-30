package algorithms.GenerateLogic;

import java.util.List;
import java.util.Set;

public class ReplyBehavior {
//	private int uniqueId;
	private List<String> patternIds;
	private int index;
	private String behaviorId;
	
//	public int getUniqueId() {
//		return uniqueId;
//	}
//	public void setUniqueId(int uniqueId) {
//		this.uniqueId = uniqueId;
//	}
	public ReplyBehavior(){
		
	}
	public ReplyBehavior(List<String> patternIds, int index, String behaviorId) {
		this.patternIds = patternIds;
		this.index = index;
		this.behaviorId = behaviorId;
	}
	public List<String> getPatternIds() {
		return patternIds;
	}
	public void setPatternIds(List<String> patternIds) {
		this.patternIds = patternIds;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getBehaviorIds() {
		return behaviorId;
	}
	public void setBehaviorIds(String behaviorIds) {
		this.behaviorId = behaviorIds;
	}
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{" 
		+ "patternIds:" + this.patternIds
		+ ", index:" + this.index
		+ ", behaviorId:" + this.behaviorId + "}");
		
		return stringBuffer.toString();
	}
	
	@Override
	public int hashCode() {
		return patternIds.hashCode() * Long.hashCode(index) * behaviorId.hashCode();
	}
	
	@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			if (obj instanceof ReplyBehavior) {
				ReplyBehavior replyBehavior = (ReplyBehavior) obj;
				if (replyBehavior.patternIds.equals(this.patternIds) && (replyBehavior.index == this.index)
						&& replyBehavior.behaviorId.equals(this.behaviorId)) {
					return true;
				}
			}
			return super.equals(obj);
		}


}
