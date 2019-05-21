package algorithms.GenerateLogic;

import java.util.List;

public class Knowledge {
	String knowledgeId;	
	String knownledgeName;
	List<String> patternIds;
	public Knowledge(String knowledgeId, String knownledgeName, List<String> patternIds) {
		this.knowledgeId = knowledgeId;
		this.knownledgeName = knownledgeName;
		this.patternIds = patternIds;
	}
	
	public String getKnowledgeId() {
		return knowledgeId;
	}
	public void setKnowledgeId(String knowledgeId) {
		this.knowledgeId = knowledgeId;
	}
	public String getKnownledgeName() {
		return knownledgeName;
	}
	public void setKnownledgeName(String knownledgeName) {
		this.knownledgeName = knownledgeName;
	}
	public List<String> getPatternIds() {
		return patternIds;
	}
	public void setPatternIds(List<String> patternIds) {
		this.patternIds = patternIds;
	}
	
	
}
