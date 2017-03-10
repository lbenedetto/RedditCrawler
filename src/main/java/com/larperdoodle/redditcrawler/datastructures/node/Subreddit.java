package com.larperdoodle.redditcrawler.datastructures.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.larperdoodle.redditcrawler.Main;
import com.larperdoodle.redditcrawler.datastructures.Graph;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.larperdoodle.redditcrawler.Main.calendar;


public class Subreddit extends Node {
	private static int sID = 0;
	//A subreddit is a node
	private HashSet<Subreddit> links;
	private int year;
	private int month;
	private boolean NSFW;

	public Subreddit(String name) {
		super(name, sID++);
		links = new HashSet<>();
	}

	private JsonObject getSubredditJson() throws Exception {
		//Get neccessary overhead
		String sURL = "https://www.reddit.com/r/" + getName() + "/about.json";
		URL url = new URL(sURL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestProperty("User-Agent", Main.USER_AGENT);
		request.connect();
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		return root.getAsJsonObject().getAsJsonObject("data");
	}

	public void getInfo() {
		try {
			JsonObject data = getSubredditJson();
			//Get data
			NSFW = data.get("over18").getAsBoolean();
			setSubscribers(data.get("subscribers").getAsInt());
			String description = data.get("description").getAsString();
			long timestamp = data.get("created_utc").getAsInt();
			calendar.setTimeInMillis(timestamp * 1000);
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH) + 1;
			//Add related subs to list
			Matcher m = Pattern.compile("/r/([A-Za-z0-9_-]{1,20})").matcher(description);
			while (m.find()) {
				String sName = m.group(1).toLowerCase();
				if (sName.equals(getName()) || sName.equals("")) continue;
				Main.id++;
				int Oid = Main.id;
				if (Main.subreddits.containsKey(sName)) {
					Oid = Main.subreddits.get(sName);
					Main.id--;
				}
				Main.subreddits.put(sName, Oid);
				links.add(new Subreddit(sName));
			}
			if (getSubscribers() > Main.SUBSCRIBER_THRESHOLD) {
				addModeratorsToGraph(Main.moderatorGraph);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private JsonArray getModeratorJson() throws Exception {
		Thread.sleep(2000);
		//Get neccessary overhead
		String sURL = "https://www.reddit.com/r/" + getName() + "/about/moderators.json";
		URL url = new URL(sURL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestProperty("User-Agent", Main.USER_AGENT);
		request.connect();
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		return root.getAsJsonObject().getAsJsonObject("data").getAsJsonArray("children");
	}

	private String[] getModeratorNames(JsonArray data) {
		HashSet<String> moderatorNames = new HashSet<>();
		data.forEach(x -> moderatorNames.add(x.getAsJsonObject().getAsJsonPrimitive("name").getAsString()));
		moderatorNames.remove("AutoModerator");
		return moderatorNames.toArray(new String[moderatorNames.size()]);
	}

	public void addModeratorsToGraph(Graph graph) {
		try {
			JsonArray data = getModeratorJson();
			String[] moderators = getModeratorNames(data);
			//TODO: Only add moderators if they have "all" or "posts" permissions
			if (moderators.length == 1) {
				Moderator m1 = (Moderator) graph.getNode(moderators[0]);
				m1.addSubscribers(getSubscribers());
				m1.subreddits++;
				graph.addNode(m1);
			} else {
				for (int j = 0; j < moderators.length; j++) {
					Moderator m1 = (Moderator) graph.getNode(moderators[j]);
					m1.addSubscribers(getSubscribers());
					m1.subreddits++;
					for (int k = 0; k < moderators.length; k++) {
						if (k == j) continue;
						Moderator m2 = (Moderator) graph.getNode(moderators[k]);
						graph.addPair(m1, m2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addRelatedSubsToQueue() {
		links.stream().filter(x -> !Main.vistedSubreddits.contains(x)).forEach(Main.subredditQueue::add);
	}


	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public boolean isNSFW() {
		return NSFW;
	}

	public HashSet<Subreddit> getLinks() {
		return links;
	}

	@Override
	public String toString() {
		return getID() + "," + getName() + "," + getSubscribers() + "," + getMonth() + "," + getYear() + "," + isNSFW();
	}

	@Override
	public Node getNode(String name) {
		Subreddit n = (Subreddit) Main.subredditGraph.getNode(name);
		if (n == null) n = new Subreddit(name);
		return n;
	}
}
