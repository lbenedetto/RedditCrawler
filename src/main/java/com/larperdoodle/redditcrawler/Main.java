package com.larperdoodle.redditcrawler;

import com.larperdoodle.redditcrawler.datastructures.Graph;

import java.util.LinkedList;
import java.util.Queue;

public class Main {
	public static final Graph modGraph = new Graph("Moderators", 1000000);
	public static final Graph subGraph = new Graph("Subreddits", 10000);
	static final Queue<String> QUEUE = new LinkedList<>();

	public static void main(String[] args) {
		QUEUE.add("dankmemes");
		while (!QUEUE.isEmpty()) {
			String sub = QUEUE.remove();
			RedditAPI.analyzeSubreddit(sub);
			System.out.printf("%d remaining\n", QUEUE.size());
		}
		shutdown();
	}

	private static void shutdown() {
		System.out.println("Writing Moderators to CSV...");
		modGraph.outputCSV();
		System.out.println("Writing Subreddits to CSV...");
		subGraph.outputCSV();
	}
}
