package com.ops.dto;

public class ResourceTO {
	private String name;
	private int cost;
	private int capecity;
	private int number;
	private int amount;

	public ResourceTO() {
	}

	public ResourceTO(String name, int cost, int capecity) {
		this.name = name;
		this.cost = cost;
		this.capecity = capecity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getCapecity() {
		return capecity;
	}

	public void setCapecity(int capecity) {
		this.capecity = capecity;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getAmount() {
		return cost * number;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "ResourceBean [name=" + name + ", cost=" + cost + ", capecity=" + capecity + ", number=" + number + ", amount=" + amount + "]";
	}
}