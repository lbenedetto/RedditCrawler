package com.larperdoodle.redditcrawler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.larperdoodle.redditcrawler.Main.calendar;


public class Subreddit {
	private final String name;
	private final int id;
	private final int hashCode;
	//A subreddit is a node
	private HashSet<Subreddit> links;
	private int subscribers;
	private int year;
	private int month;
	private boolean NSFW;

	Subreddit(String name, int id) {
		links = new HashSet<>();
		this.name = name;
		this.id = id;
		hashCode = this.name.hashCode();
	}

	void getInfo() {
		try {
			//Get neccessary overhead
			String sURL = "https://www.reddit.com/r/" + name + "/about.json";
			URL url = new URL(sURL);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.setRequestProperty("User-Agent", "RelatedSubredditMapper v2.0");
			request.connect();
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			JsonObject data = root.getAsJsonObject().getAsJsonObject("data");
			//Get data
			NSFW = data.get("over18").getAsBoolean();
			subscribers = data.get("subscribers").getAsInt();
			String description = data.get("description").getAsString();
			long timestamp = data.get("created_utc").getAsInt();
			calendar.setTimeInMillis(timestamp*1000);
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH) + 1;
			//Add related subs to list
			Matcher m = Pattern.compile("/r/([A-Za-z0-9_-]{1,20})").matcher(description);
			while (m.find()) {
				String sName = m.group(1).toLowerCase();
				if (sName.equals(name) || sName.equals("")) continue;
				Main.id++;
				int Oid = Main.id;
				if (Main.subreddits.containsKey(sName)) {
					Oid = Main.subreddits.get(sName);
					Main.id--;
				}
				Main.subreddits.put(sName, Oid);
				links.add(new Subreddit(sName, Oid));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void addRelatedSubsToQueue() {
		for (Subreddit sub : links) {
			if (Main.vistedSubreddits.contains(sub)) continue;
			Main.subredditQueue.add(sub);
		}
	}


	int getYear() {
		return year;
	}

	int getMonth() {
		return month;
	}

	boolean isNSFW() {
		return NSFW;
	}

	HashSet<Subreddit> getLinks() {
		return links;
	}

	String getName() {
		return name;
	}

	int getSubscribers() {
		return subscribers;
	}

	int getId() {
		return id;
	}


	@Override
	public boolean equals(Object o) {
		return o instanceof Subreddit && name.equals(((Subreddit) o).getName());
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
