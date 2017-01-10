package com.larperdoodle.redditcrawler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subreddit {
	private final String name;
	private final int id;
	private final int hashCode;
	//A subreddit is a node
	private HashSet<Subreddit> links;
	private int subscribers;

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
			JsonObject rootobj = root.getAsJsonObject();
			//Get subscribers
			subscribers = rootobj.getAsJsonObject("data").get("subscribers").getAsInt();
			//Get description
			String description = rootobj.getAsJsonObject("data").get("description").getAsString();
			//Add related subs to list
			Matcher m = Pattern.compile("/r/([A-Za-z]*)").matcher(description);
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

	public HashSet<Subreddit> getLinks() {
		return links;
	}

	public String getName() {
		return name;
	}

	public int getSubscribers() {
		return subscribers;
	}

	public int getId() {
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
