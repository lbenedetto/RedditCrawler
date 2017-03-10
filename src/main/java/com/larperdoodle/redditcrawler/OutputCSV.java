package com.larperdoodle.redditcrawler;

import com.larperdoodle.redditcrawler.datastructures.Graph;
import com.larperdoodle.redditcrawler.datastructures.node.Subreddit;

import java.io.*;

public class OutputCSV {
	private PrintWriter Nodes;
	private PrintWriter Edges;

	OutputCSV() {
		start();
	}

	void writeSubreddit(Subreddit s) {
		String name = s.getName();
		int id = s.getID();
		//id,name,subs,mm,yyyy,nsfw
		Nodes.println(s.toString());
		for (Subreddit sub : s.getLinks()) {
			Edges.println(id + "," + sub.getID());
		}
	}

	void writeModeratorGraph(Graph g) {
		g.breadthFirstPrint(Nodes, Edges);
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
			File n = new File("nodes(Moderators).csv");
			File e = new File("edges(Moderators).csv");
			n.createNewFile();
			e.createNewFile();
			Nodes = new PrintWriter(new BufferedWriter(new FileWriter(n, true)));
			Edges = new PrintWriter(new BufferedWriter(new FileWriter(e, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
