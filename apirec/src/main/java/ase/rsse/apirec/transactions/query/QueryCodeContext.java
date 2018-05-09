package ase.rsse.apirec.transactions.query;

import java.util.HashMap;

import ase.rsse.apirec.transactions.codecontext.CodeContext;

public class QueryCodeContext extends CodeContext {
	
	private HashMap<Integer, Float> weightOfScope;
	private HashMap<Integer, Float> weightOfDataDependency;
	
	public QueryCodeContext() {
		super();
		weightOfScope = new HashMap<>();
		weightOfDataDependency = new HashMap<>();
	}

	public int getDistance(String token) {
		return 1;
	}

	public Float getWeightOfScope(int distance) {
		return weightOfScope.get(distance);
	}

	public void setWeightOfScope(HashMap<Integer, Float> weightOfScope) {
		this.weightOfScope = weightOfScope;
	}
	
	public void addWeightOfScope(Integer token, float weightOfScope) {
		this.weightOfScope.put(token, weightOfScope);
	}

	public Float getWeightOfDataDependency(int distance) {
		return weightOfDataDependency.get(distance);
	}

	public void setWeightOfDataDependency(HashMap<Integer, Float> weightOfDataDependency) {
		this.weightOfDataDependency = weightOfDataDependency;
	}
	
	public void addWeightOfDataDependency(Integer distanceOfToken, float weightOfDataDependency) {
		this.weightOfDataDependency.put(distanceOfToken, weightOfDataDependency);
	}
}
