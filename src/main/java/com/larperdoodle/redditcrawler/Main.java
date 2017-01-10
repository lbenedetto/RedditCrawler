package com.larperdoodle.redditcrawler;

import java.util.*;

public class Main {
	public static HashSet<Subreddit> vistedSubreddits;
	public static LinkedHashSet<Subreddit> subredditQueue;
	public static HashMap<String, Integer> subreddits;
	public static int id = 0;
	public static OutputCSV CSV;
	public static int autosave = 0;
	public static int nodeNum = 0;

	public static void main(String[] args) throws InterruptedException {
		vistedSubreddits = new HashSet<>();
		subreddits = new HashMap<>();
		subredditQueue = new LinkedHashSet<>();
		subredditQueue.add(new Subreddit("dankmemes", id));
		subreddits.put("dankmemes", 0);
		CSV = new OutputCSV();
		Scanner kb = new Scanner(System.in);
		Timer timer = new Timer();
		timer.schedule(new AnalyzeSubreddit(), 0, 2000);
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
