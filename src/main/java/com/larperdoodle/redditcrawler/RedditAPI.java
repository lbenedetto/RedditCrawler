package com.larperdoodle.redditcrawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.larperdoodle.redditcrawler.datastructures.node.Moderator;
import com.larperdoodle.redditcrawler.datastructures.node.Subreddit;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class RedditAPI {
	private static final String USER_AGENT = "RedditMapper v4.0";
	private static final int SUBSCRIBER_THRESHOLD = 1000;
	private static final Pattern PATTERN = Pattern.compile("/r/([A-Za-z0-9_-]{1,20})");
	private static final HashSet<String> VISITED = new HashSet<>();

	static void analyzeSubreddit(String name) {
		try {
			VISITED.add(name);
			JsonObject data = getSubredditJson(name);
			Subreddit sub = Subreddit.getSubreddit(name);
			sub.setData(data);
			processRelatedSubs(data, sub);
			if (sub.getSubscribers() > SUBSCRIBER_THRESHOLD) {
				String[] moderators = getModeratorNames(getModeratorJson(name));
				addModeratorsToGraph(moderators, sub.getSubscribers());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processRelatedSubs(JsonObject data, Subreddit sub1) {
		String description = data.get("description").getAsString();
		Matcher m = PATTERN.matcher(description);
		while (m.find()) {
			String sub2Name = m.group(1).toLowerCase();
			if (sub2Name.equals(sub1.getName()) || sub2Name.equals("")) continue;
			Subreddit sub2 = Subreddit.getSubreddit(sub2Name);
			Main.subGraph.addPair(sub1, sub2);
			if (!VISITED.contains(sub2Name)) Main.QUEUE.add(sub2Name);
		}
	}

	private static JsonObject getSubredditJson(String name) throws Exception {
		JsonElement root = getJson(name, "");
		return root.getAsJsonObject().getAsJsonObject("data");
	}

	private static JsonArray getModeratorJson(String name) throws Exception {
		JsonElement root = getJson(name, "/moderators");
		return root.getAsJsonObject().getAsJsonObject("data").getAsJsonArray("children");
	}

	private static JsonElement getJson(String name, String urlModifier) throws Exception {
		Thread.sleep(2000);
		String sURL = "https://www.reddit.com/r/" + name + "/about" + urlModifier + ".json";
		URL url = new URL(sURL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestProperty("User-Agent", USER_AGENT);
		request.connect();
		JsonParser jp = new JsonParser();
		return jp.parse(new InputStreamReader((InputStream) request.getContent()));
	}

	private static String[] getModeratorNames(JsonArray data) {
		HashSet<String> moderatorNames = new HashSet<>();
		for (JsonElement e : data) {
			String name = e.getAsJsonObject().getAsJsonPrimitive("name").getAsString();
			if(name.equals("AutoModerator")) continue;
			JsonArray permissions = e.getAsJsonObject().getAsJsonArray("mod_permissions");
			boolean hasPerms = false;
			for(JsonElement p : permissions){
				String permission = p.getAsJsonPrimitive().getAsString();
				if(permission.equals("all") || permission.equals("access"))
					hasPerms = true;
			}
			if(hasPerms) moderatorNames.add(name);
		}
		//Need to convert to array because O(1) access is needed later
		return moderatorNames.toArray(new String[moderatorNames.size()]);
	}

	private static void addModeratorsToGraph(String[] moderators, int subs) {
		for (int j = 0; j < moderators.length; j++) {
			Moderator m1 = Moderator.getModerator(moderators[j]);
			updateModerator(m1, subs);
			for (int k = 0; k < moderators.length; k++) {
				if (k == j) continue;
				Moderator m2 = Moderator.getModerator(moderators[j]);
				Main.modGraph.addPair(m1, m2);
			}
		}
	}

	private static void updateModerator(Moderator moderator, int subscribers) {
		moderator.addSubs(subscribers);
		moderator.incrementSubreddits();
	}

}
