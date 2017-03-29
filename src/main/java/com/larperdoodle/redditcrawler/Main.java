package com.larperdoodle.redditcrawler;

import com.larperdoodle.redditcrawler.datastructures.Graph;

import java.util.LinkedList;
import java.util.Queue;

public class Main {
	public static final Graph modGraph = Graph.load("Subreddits", 1000000);
	public static final Graph subGraph = Graph.load("Moderators", 10000);
	static final Queue<String> QUEUE = new LinkedList<>();
	private static final long RATE_LIMIT = 60000;
	private static int LIMIT = 100;

	public static void main(String[] args) throws InterruptedException {
		try {
			QUEUE.add("dankmemes");
			int requests = 0;
			long before;
			long after;
			while (!QUEUE.isEmpty() && LIMIT-- > 0) {
				String sub = QUEUE.remove();
				before = System.currentTimeMillis();
				RedditAPI.analyzeSubreddit(sub);
				System.out.printf("%d remaining\n", QUEUE.size());
				requests += 2;
				if (requests >= 30) {
					after = System.currentTimeMillis();
					long offset = after - before;
					long wait = RATE_LIMIT - offset;
					System.out.printf("Saved %d milliseconds, waiting %d seconds for API cooldown\n", offset, wait);
					Thread.sleep(wait);
					requests = 0;
				}
			}
			shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			shutdown();
		}
	}

	/**
	 * Outputs the graph to CSV and serializable
	 * The next time the program starts, it will try to load the graphs from serializable
	 * If you don't want this, move or delete the files
	 */
	private static void shutdown() {
		System.out.println("Writing Moderators to CSV...");
		modGraph.outputCSV();
		System.out.println("Writing Subreddits to CSV...");
		subGraph.outputCSV();
		subGraph.save();
		modGraph.save();
	}
}
