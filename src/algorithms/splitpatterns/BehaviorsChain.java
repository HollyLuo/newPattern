package algorithms.splitpatterns;

import java.util.ArrayList;
import java.util.List;

public class behaviorsChain {
	 private List<String> behaviorsChain = new ArrayList<String>();
	 private List<String> uniqueIdsChain = new ArrayList<String>();

	public List<String> getBehaviorsChain() {
		return behaviorsChain;
	}

	public void setBehaviorsChain(List<String> behaviorsChain) {
		this.behaviorsChain = behaviorsChain;
	}

	public List<String> getUniqueIdsChain() {
		return uniqueIdsChain;
	}

	public void setUniqueIdsChain(List<String> uniqueIdsChain) {
		this.uniqueIdsChain = uniqueIdsChain;
	}

}
