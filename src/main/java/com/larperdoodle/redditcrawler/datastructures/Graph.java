package com.larperdoodle.redditcrawler.datastructures;

import com.larperdoodle.redditcrawler.Main;
import com.larperdoodle.redditcrawler.datastructures.node.Moderator;
import com.larperdoodle.redditcrawler.datastructures.node.Node;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Graph {
	private final HashMap<Node, HashSet<Edge>> adjacencyList;
	private final HashMap<String, Node> nodes;

	public Graph() {
		adjacencyList = new HashMap<>();
		nodes = new HashMap<>();
	}

	public void breadthFirstPrint(PrintWriter Nodes, PrintWriter Edges) {
		Queue<Node> q = new LinkedList<>();
		q.addAll(adjacencyList.keySet());
		do {
			Node n = q.remove();
			Nodes.println(n.toString());
			adjacencyList.get(n).forEach(x -> Edges.println(n.getID() + "," + x.dest.getID() + "," + x.weight));
		} while (!q.isEmpty());

	}

	public void addPair(Node src, Node dest) {
		//TODO: Add progress indicator
		System.out.println(src.getName() + "->" + dest.getName());
		nodes.put(src.getName(), src);
		nodes.put(dest.getName(), dest);
		addEdge(src, new Edge(dest, 1));
		addEdge(dest, new Edge(src, 1));
	}

	public void addNode(Node m) {
		nodes.put(m.getName(), m);
	}

	public Node getNode(String name) {
		return nodes.get(name);
	}

	private void addEdge(Node src, Edge edge) {
		if (adjacencyList.containsKey(src)) {//If the node already exists
			HashSet<Edge> edges = adjacencyList.get(src);
			if (edges.contains(edge)) {//If it already contains the edge
				for (Edge e : edges) {//Increase the edge weight
					if (e.dest.equals(edge.dest)) {
						e.increaseWeight();
						break;
					}
				}
			} else {
				edges.add(edge);//Add the new edge
			}
		} else {//Create the node and add its new edge
			HashSet<Edge> edges = new HashSet<>();
			edges.add(edge);
			adjacencyList.put(src, edges);
		}
	}

	private class Edge implements Comparable<Edge> {
		final Node dest;
		Integer weight;

		Edge(Node dest, int weight) {
			this.dest = dest;
			this.weight = weight;
		}

		public void increaseWeight() {
			weight++;
		}

		@Override
		public int hashCode() {
			return dest.hashCode();
		}

		@Override
		public int compareTo(Edge e) {
			return dest.compareTo(e.dest);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Edge) {
				Edge e = (Edge) o;
				return dest.equals(e.dest);
			}
			return false;
		}
	}

}