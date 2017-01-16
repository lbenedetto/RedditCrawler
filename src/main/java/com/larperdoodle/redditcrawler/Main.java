package com.larperdoodle.redditcrawler;

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

	public static void main(String[] args) throws InterruptedException {
		//loadExistingNodes();
		//Load known subreddits into queue
		//loadSubsIntoQueue();
		subredditQueue.add(new Subreddit("dankmemes", 0));
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
	public static void loadExistingNodes(){
		try {
			BufferedReader oldNodes = new BufferedReader(new FileReader("nodes.csv"));
			String s;
			Pattern p = Pattern.compile("(^\\d+),(.*),(.*)");
			Matcher m;
			while ((s = oldNodes.readLine()) != null) {
				m = p.matcher(s);
				while (m.find()) {
					int id = Integer.parseInt(m.group(1));
					String name = m.group(2);
					subreddits.put(name, id);
					vistedSubreddits.add(new Subreddit(name, id));
				}
			}
			System.out.println("Old nodes loaded nodes");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not load old nodes");
		}
	}
	public static void loadSubsIntoQueue(){
		try {
			BufferedReader oldNodes = new BufferedReader(new FileReader("subs.txt"));
			String s;
			while ((s = oldNodes.readLine()) != null) {
				subredditQueue.add(new Subreddit(s, id));
				subreddits.put(s, id);
				id++;
			}
			System.out.println("Known subs loaded");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not load known subs");
		}
	}

}
