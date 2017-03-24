package com.larperdoodle.redditcrawler.datastructures.node;

import com.google.gson.JsonObject;
import com.larperdoodle.redditcrawler.Main;

import java.util.Calendar;

import static com.larperdoodle.redditcrawler.Main.calendar;

public class Subreddit extends Node {
	private static int sID = 0;
	//A subreddit is a node
	private int year;
	private int month;
	private boolean NSFW;

	public Subreddit(String name) {
		super(name, sID++);
	}
	public void setData(JsonObject data){
		long timestamp = data.get("created_utc").getAsInt();
		calendar.setTimeInMillis(timestamp * 1000);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		NSFW = data.get("over18").getAsBoolean();
		setSubscribers(data.get("subscribers").getAsInt());
	}

	@Override
	public String toString() {
		return getID() + "," + getName() + "," + getSubscribers() + "," + month + "," + year + "," + NSFW;
	}

	@Override
	public Node getNode(String name) {
		Subreddit n = (Subreddit) Main.subGraph.getNode(name);
		if (n == null) n = new Subreddit(name);
		return n;
	}
}
