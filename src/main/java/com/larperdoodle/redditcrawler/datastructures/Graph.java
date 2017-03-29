package com.larperdoodle.redditcrawler.datastructures;

import com.larperdoodle.redditcrawler.datastructures.node.Node;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Graph implements Serializable {
	private final HashMap<Node, HashSet<Edge>> edges;
	private final HashMap<String, Node> nodes;
	private final String graphName;

	private Graph(String graphName, int estimatedNumNodes) {
		this.graphName = graphName;
		edges = new HashMap<>(estimatedNumNodes * 10);
		nodes = new HashMap<>(estimatedNumNodes);
	}

	public static Graph load(String graphName, int estimatedNumNodes) {
		FileInputStream fis;
		ObjectInputStream in;
		Graph graph;
		try {
			fis = new FileInputStream(graphName);
			in = new ObjectInputStream(fis);
			graph = (Graph) in.readObject();
			in.close();
			if (graph == null) throw new FileNotFoundException("Could not load graph");
			return graph;
		} catch (Exception ex) {
			System.out.println("Could not find graph, creating new one");
			return new Graph(graphName, estimatedNumNodes);
		}
	}

	/**
	 * Convert this graph into two .csv files representing Nodes and Edges
	 */
	public void outputCSV() {
		try {
			File nodes = new File("nodes(" + graphName + ").csv");
			File edges = new File("edges(" + graphName + ").csv");
			if (nodes.createNewFile() && edges.createNewFile()) {
				PrintWriter nodeWriter = new PrintWriter(new BufferedWriter(new FileWriter(nodes, true)));
				PrintWriter edgeWriter = new PrintWriter(new BufferedWriter(new FileWriter(edges, true)));
				Queue<Node> q = new LinkedList<>();
				q.addAll(this.edges.keySet());
				do {
					Node n = q.remove();
					nodeWriter.println(n);
					this.edges.get(n).forEach(edgeWriter::println);
				} while (!q.isEmpty());
				nodeWriter.close();
				edgeWriter.close();
			} else {
				throw new IOException("Could not create files");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		FileOutputStream fos;
		ObjectOutputStream out;
		try {
			fos = new FileOutputStream(graphName);
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void addPair(Node node1, Node node2) {
		System.out.printf("%s:%d<->%s:%d\n", node1.getName(), node1.getID(), node2.getName(), node2.getID());
		nodes.put(node1.getName(), node1);
		nodes.put(node2.getName(), node2);
		Edge e = new Edge(node1, node2);
		addEdge(node1, e);
		addEdge(node2, e);
	}

	public void addNode(Node n) {
		nodes.put(n.getName(), n);
	}

	public void removeNode(Node n) {
		nodes.remove(n.getName());
		edges.remove(n);
	}

	public Node getNode(String name) {
		return nodes.get(name);
	}

	private void addEdge(Node src, Edge edge) {
		if (edges.containsKey(src)) {
			HashSet<Edge> edges = this.edges.get(src);
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
			this.edges.put(src, edges);
		}
	}

	private class Edge implements Comparable<Edge> {
		final Node n1;
		final Node n2;
		Integer weight;

		Edge(Node n1, Node n2) {
			this.n1 = n1;
			this.n2 = n2;
			this.weight = 1;
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