package com.ops.managers;

public enum Resource {
	TATA_ACE ("Tata Ace", 15, 5000),
	PICK_UP ("Pick Up", 10, 2500),
	AUTO ("Auto", 8, 800);

	private String name;
	private int cost;
	private int capecity;

	Resource(String name, int cost, int capecity) {
		this.name = name;
		this.cost = cost;
		this.capecity = capecity;
	}

	public String getName() {
		return name;
	}

	public int getCost() {
		return cost;
	}

	public int getCapecity() {
		return capecity;
	}
}