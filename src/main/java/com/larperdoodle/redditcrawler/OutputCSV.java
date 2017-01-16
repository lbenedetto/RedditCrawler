package com.larperdoodle.redditcrawler;

import java.io.*;

public class OutputCSV {
	private PrintWriter Nodes;
	private PrintWriter Edges;

	OutputCSV() {
		start();
	}

	void writeSubreddit(Subreddit s) {
		String name = s.getName();
		int id = s.getId();
		//id,name,subs,mm,yyyy,nsfw
		Nodes.println(id + "," + name + "," + s.getSubscribers() + "," + s.getMonth() + "," + s.getYear() + "," + s.isNSFW());
		for (Subreddit sub : s.getLinks()) {
			Edges.println(id + "," + sub.getId());
		}
	}

	public void shutdown() {
		Nodes.close();
		Edges.close();
	}

	public void restart() {
		shutdown();
		start();
	}

	public void start() {
		try {
			File n = new File("nodes.csv");
			File e = new File("edges.csv");
			n.createNewFile();
			e.createNewFile();
			Nodes = new PrintWriter(new BufferedWriter(new FileWriter(n, true)));
			Edges = new PrintWriter(new BufferedWriter(new FileWriter(e, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
