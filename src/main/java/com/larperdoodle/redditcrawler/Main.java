package com.larperdoodle.redditcrawler;

import com.larperdoodle.redditcrawler.datastructures.Graph;

import java.util.*;

public class Main {
	public static HashSet<String> vistedSubreddits = new HashSet<>();
	public static Queue<String> subQ = new LinkedList<>();
	public static int id = 0;
	public static int autosave = 0;
	public static int nodeNum = 0;
	public static Calendar calendar = Calendar.getInstance();
	public static final Graph modGraph = new Graph("Moderators");
	public static final Graph subGraph = new Graph("Subreddits");
	public static final String USER_AGENT = "RedditMapper v4.0";
	public static final int SUBSCRIBER_THRESHOLD = 1000;

	public static void main(String[] args) throws InterruptedException {
		subQ.add("dankmemes");
		while(!subQ.isEmpty()){
			String sub = subQ.remove();
			RedditAPI.analyzeSubreddit(sub);
		}
	}
	private static void shutdown(){
		System.out.println("Writing Moderators to CSV...");
		modGraph.outputCSV();
		System.out.println("Writing Subreddits to CSV...");
		subGraph.outputCSV();
	}
}
