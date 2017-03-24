package com.larperdoodle.redditcrawler.datastructures;

import com.larperdoodle.redditcrawler.datastructures.node.Node;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Graph {
	private final HashMap<Node, HashSet<Edge>> adjacencyList;
	private final HashMap<String, Node> nodes;
	private String graphName;
	public Graph(String graphName) {
		this.graphName = graphName;
		adjacencyList = new HashMap<>();
		nodes = new HashMap<>();
	}

	/**
	 * Convert this graph into two .csv files representing Nodes and Edges
	 */
	public void outputCSV() {
		try {
			File nodes = new File("nodes(" + graphName + ").csv");
			File edges = new File("edges(" + graphName + ").csv");
			if(nodes.createNewFile() && edges.createNewFile()) {
				PrintWriter nodeWriter = new PrintWriter(new BufferedWriter(new FileWriter(nodes, true)));
				PrintWriter edgeWriter = new PrintWriter(new BufferedWriter(new FileWriter(edges, true)));
				Queue<Node> q = new LinkedList<>();
				q.addAll(adjacencyList.keySet());
				do {
					Node n = q.remove();
					nodeWriter.println(n);
					adjacencyList.get(n).forEach(edgeWriter::println);
				} while (!q.isEmpty());
				nodeWriter.close();
				edgeWriter.close();
			}else{
				throw new IOException("Could not create files");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addPair(Node node1, Node node2) {
		//TODO: Add a better progress indicator
		System.out.println(node1.getName() + "<->" + node2.getName());
		nodes.put(node1.getName(), node1);
		nodes.put(node2.getName(), node2);
		Edge e = new Edge(node1, node2, 1);
		addEdge(node1, e);
		addEdge(node2, e);
	}

	public void addNode(Node m) {
		nodes.put(m.getName(), m);
	}

	public Node getNode(String name) {
		return nodes.get(name);
	}

	private void addEdge(Node src, Edge edge) {
		if (adjacencyList.containsKey(src)) {
			HashSet<Edge> edges = adjacencyList.get(src);
			if (edges.contains(edge)) {
				for (Edge e : edges)
					if (e.equals(edge)) {
						e.increaseWeight();
						break;
					}
			} else {
				edges.add(edge);
			}
		} else {
			HashSet<Edge> edges = new HashSet<>();
			edges.add(edge);
			adjacencyList.put(src, edges);
		}
	}

	private class Edge implements Comparable<Edge> {
		final Node n1;
		final Node n2;
		Integer weight;

		Edge(Node n1, Node n2, int weight) {
			this.n1 = n1;
			this.n2 = n2;
			this.weight = weight;
		}

		void increaseWeight() {
			weight++;
		}

		@Override
		public int hashCode() {
			return n1.hashCode();
		}

		@Override
		public int compareTo(Edge e) {
			return weight.compareTo(e.weight);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Edge) {
				Edge e = (Edge) o;
				return n1.equals(e.n1) && n2.equals(e.n2);
			}
			return false;
		}

		@Override
		public String toString() {
			return n1.getID() + "," + n2.getID() + "," + weight;
		}
	}

}