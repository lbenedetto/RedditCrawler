package com.larperdoodle.redditcrawler.datastructures.node;


import com.larperdoodle.redditcrawler.Main;

public class Moderator extends Node {
	private static int mID = 0;
	private int subreddits;//Alt Node size

	public Moderator(String name) {
		super(name, mID++);
		subreddits = 0;
	}

	public int getSubreddits() {
		return subreddits;
	}

	public void incrementSubreddits() {
		subreddits++;
	}

	@Override
	public String toString() {
		return getID() + "," + getName() + "," + getSubscribers() + "," + subreddits;
	}

	@Override
	public Node getNode(String name) {
		Moderator m = (Moderator) Main.modGraph.getNode(name);
		if (m == null) m = new Moderator(name);
		return m;
	}
}
