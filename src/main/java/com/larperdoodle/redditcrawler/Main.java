package com.larperdoodle.redditcrawler;

import com.larperdoodle.redditcrawler.datastructures.Graph;
import com.larperdoodle.redditcrawler.datastructures.node.Subreddit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static HashSet<Subreddit> vistedSubreddits = new HashSet<>();
	public static LinkedHashSet<Subreddit> subredditQueue = new LinkedHashSet<>();
	public static HashMap<String, Integer> subreddits = new HashMap<>();
	public static int id = 0;
	public static OutputCSV CSV;
	public static int autosave = 0;
	public static int nodeNum = 0;
	public static Calendar calendar = Calendar.getInstance();
	public static final Graph moderatorGraph = new Graph();
	public static final Graph subredditGraph = new Graph();
	public static final String USER_AGENT = "RedditMapper v4.0";
	public static final int SUBSCRIBER_THRESHOLD = 1000;

	public static void main(String[] args) throws InterruptedException {
		subredditQueue.add(new Subreddit("dankmemes"));
		subreddits.put("dankmemes", 0);
		CSV = new OutputCSV();
		Scanner kb = new Scanner(System.in);
		Timer timer = new Timer();
		timer.schedule(new AnalyzeSubreddit(), 0, 4000);
		try {
			while (true) {
				if (subredditQueue.isEmpty() || kb.hasNext()) {
					timer.cancel();
					CSV.shutdown();
					break;
				}
			}
		} catch (Exception e) {
			//Save on crash
			CSV.shutdown();
		}
	}

	public static class AnalyzeSubreddit extends TimerTask {
		@Override
		public void run() {
			if (subredditQueue.isEmpty()) return;
			Iterator i = subredditQueue.iterator();
			Subreddit subreddit = (Subreddit) i.next();
			i.remove();
			subreddit.getInfo();
			if (subreddit.getSubscribers() > 10000)
				subreddit.addRelatedSubsToQueue();
			CSV.writeSubreddit(subreddit);
			vistedSubreddits.add(subreddit);
			System.out.println("#" + nodeNum + " " + subreddit.getName() + " " + subredditQueue.size() + " subreddits in queue");
			nodeNum++;
			autosave++;
			if (autosave % 100 == 0) {
				CSV.restart();
				autosave = 0;
			}
		}
	}
}
