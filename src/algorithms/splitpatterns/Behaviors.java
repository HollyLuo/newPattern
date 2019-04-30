package algorithms.splitpatterns;

public class Behaviors {
	private String videoId;
	private String uniqueId;
	private String behaviorID;
	
	public Behaviors(String uniqueId,String behaviorID,String videoId){
		this.uniqueId = uniqueId;
		this.behaviorID = behaviorID;
		this.videoId = videoId;
	}
	
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getBehaviorID() {
		return behaviorID;
	}
	public void setBehaviorID(String behaviorID) {
		this.behaviorID = behaviorID;
	}
	public String toString() {
		return "uniqueId: " + uniqueId + "; behaviorID: " + behaviorID + "; videoId: " + videoId;		
	}

}
