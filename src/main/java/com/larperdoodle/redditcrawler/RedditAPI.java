package com.larperdoodle.redditcrawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.larperdoodle.redditcrawler.datastructures.Graph;
import com.larperdoodle.redditcrawler.datastructures.node.Moderator;
import com.larperdoodle.redditcrawler.datastructures.node.Subreddit;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RedditAPI {

	public static void analyzeSubreddit(String name) {
		try {
			Main.vistedSubreddits.add(name);
			JsonObject data = getSubredditData(name);
			Subreddit sub = (Subreddit) Main.subGraph.getNode(name);
			if (sub == null) sub = new Subreddit(name);
			sub.setData(data);
			processRelatedSubs(data, sub);
			if (sub.getSubscribers() > Main.SUBSCRIBER_THRESHOLD) {
				String[] moderators = getModeratorNames(getModeratorJson(name));
				addModeratorsToGraph(moderators, sub.getSubscribers());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static JsonObject getSubredditData(String name) throws Exception {
		//Get neccessary overhead
		String sURL = "https://www.reddit.com/r/" + name + "/about.json";
		URL url = new URL(sURL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestProperty("User-Agent", Main.USER_AGENT);
		request.connect();
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		return root.getAsJsonObject().getAsJsonObject("data");
	}

	private static void processRelatedSubs(JsonObject data, Subreddit sub1) {
		String description = data.get("description").getAsString();
		Matcher m = Pattern.compile("/r/([A-Za-z0-9_-]{1,20})").matcher(description);
		while (m.find()) {
			String sub2Name = m.group(1).toLowerCase();
			if (sub2Name.equals(sub1.getName()) || sub2Name.equals("")) continue;
			Subreddit sub2 = (Subreddit) Main.subGraph.getNode(sub2Name);
			if (sub2 == null) sub1 = new Subreddit(sub2Name);
			Main.subGraph.addPair(sub1, sub2);
			if (!Main.vistedSubreddits.contains(sub2Name)) Main.subQ.add(sub2Name);
		}
	}

	private static JsonArray getModeratorJson(String name) throws Exception {
		Thread.sleep(2000);
		//Get neccessary overhead
		String sURL = "https://www.reddit.com/r/" + name + "/about/moderators.json";
		URL url = new URL(sURL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestProperty("User-Agent", Main.USER_AGENT);
		request.connect();
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		return root.getAsJsonObject().getAsJsonObject("data").getAsJsonArray("children");
	}

	private static String[] getModeratorNames(JsonArray data) {
		HashSet<String> moderatorNames = new HashSet<>();
		//TODO: Only add moderators if they have "all" or "access" permissions
		data.forEach(x -> moderatorNames.add(x.getAsJsonObject().getAsJsonPrimitive("name").getAsString()));
		moderatorNames.remove("AutoModerator");
		return moderatorNames.toArray(new String[moderatorNames.size()]);
	}

	private static void addModeratorsToGraph(String[] moderators, int subs) {
		try {
			if (moderators.length == 1) {
				Moderator m1 = (Moderator) Main.modGraph.getNode(moderators[0]);
				m1.addSubs(subs);
				m1.incrementSubreddits();
				Main.modGraph.addNode(m1);
			} else {
				for (int j = 0; j < moderators.length; j++) {
					Moderator m1 = (Moderator) Main.modGraph.getNode(moderators[j]);
					m1.addSubs(subs);
					m1.incrementSubreddits();
					for (int k = 0; k < moderators.length; k++) {
						if (k == j) continue;
						Moderator m2 = (Moderator) Main.modGraph.getNode(moderators[k]);
						Main.modGraph.addPair(m1, m2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
