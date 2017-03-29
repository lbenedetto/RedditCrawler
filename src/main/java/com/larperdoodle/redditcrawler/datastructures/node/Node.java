package com.larperdoodle.redditcrawler.datastructures.node;

import java.io.Serializable;

public abstract class Node implements Comparable<Node>, Serializable {
	final private int id;
	private final String name;
	private int subscribers;

	Node(String name, int id) {
		this.name = name;
		this.id = id;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node n = (Node) o;
			return name.equals(n.getName());
		}
		return false;
	}

	@Override
	public int compareTo(Node n) {
		return name.compareTo(n.getName());
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	public int getSubscribers() {
		return subscribers;
	}

	void setSubscribers(int s) {
		subscribers = s;
	}

	public void addSubs(int s) {
		subscribers += s;
	}

	public abstract String toString();
}
