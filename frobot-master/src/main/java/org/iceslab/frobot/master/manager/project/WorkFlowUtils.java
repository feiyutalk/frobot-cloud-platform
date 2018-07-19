package org.iceslab.frobot.master.manager.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.iceslab.frobot.master.workflow.OneByOneWorkFlow;
import org.iceslab.frobot.master.workflow.WorkFlow;
import org.iceslab.frobot.cluster.TaskInfo;

/**
 * 将用户设置的task之间依赖关系,并利用拓扑排序,生成task运行的工作流.
 */
public class WorkFlowUtils {
	public static WorkFlow getWorkFlow(Map<TaskInfo, String> tasks) {
		Graph graph = new Graph();
		Set<Vertex> vertexSet = graph.getVertexSet();
		Map<Vertex, Vertex[]> edgeMap = graph.getAdjacencys();
		Map<String, Vertex> vertexs = new HashMap<String, Vertex>();
		Set<Entry<TaskInfo, String>> entrySet = tasks.entrySet();
		Iterator<Entry<TaskInfo, String>> iterator = entrySet.iterator();
		String projectID = null;
		while (iterator.hasNext()) {
			Entry<TaskInfo, String> next = iterator.next();
			String taskName = next.getKey().getTaskName();
			projectID = next.getKey().getProjectID();
			if (!vertexs.containsKey(taskName)) {
				vertexs.put(taskName, new Vertex(taskName));
				vertexSet.add(vertexs.get(taskName));
			}
			String dependency = next.getValue();
			Vertex[] dependenciesVertex = new Vertex[1];
			int count = 0;
			if (!dependency.equals("nodepend")) {// 存在依赖关系
				if (!vertexs.containsKey(dependency)) {
					vertexs.put(dependency, new Vertex(dependency));
					vertexSet.add(vertexs.get(dependency));
				}
				dependenciesVertex[count++] = vertexs.get(dependency);
				edgeMap.put(vertexs.get(taskName), dependenciesVertex);
			}
		}
		Vertex[] sortedVertexs = TimeRecorder.topologicalSort(graph);
		List<TaskInfo> sortedVeTask = new ArrayList<>();
		for (Vertex vertex : sortedVertexs) {
			sortedVeTask.add(TaskDBOperation.getTaskInfoByTaskIDAndSequence(projectID + "_" + vertex.getName(),0));
		}
		return new OneByOneWorkFlow(sortedVeTask);
	}
}

enum Color {
	WHITE, GRAY, BLACK;
}

class Graph {
	private Set<Vertex> vertexSet = new HashSet<Vertex>();
	// 相邻的节点
	private Map<Vertex, Vertex[]> adjacencys = new HashMap<Vertex, Vertex[]>();

	public Set<Vertex> getVertexSet() {
		return vertexSet;
	}

	public void setVertexSet(Set<Vertex> vertexSet) {
		this.vertexSet = vertexSet;
	}

	public Map<Vertex, Vertex[]> getAdjacencys() {
		return adjacencys;
	}

	public void setAdjacencys(Map<Vertex, Vertex[]> adjacencys) {
		this.adjacencys = adjacencys;
	}
}

class TimeRecorder {
	private int time = 0;

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public static Vertex[] topologicalSort(Graph graph) {
		Set<Vertex> vertexSet = graph.getVertexSet();
		if (vertexSet.size() < 2) {
			return vertexSet.toArray(new Vertex[0]);
		}

		LinkedList<Vertex> sortedList = new LinkedList<Vertex>();
		TimeRecorder recorder = new TimeRecorder();

		for (Vertex vertex : vertexSet) {
			if (vertex.getColor() == Color.WHITE) {
				visitVertex(graph, vertex, recorder, sortedList);
			}
		}
		return sortedList.toArray(new Vertex[0]);
	}

	public static void visitVertex(Graph graph, Vertex vertex, TimeRecorder recorder, LinkedList<Vertex> sortedList) {
		recorder.setTime(recorder.getTime() + 1);
		vertex.setColor(Color.GRAY);
		vertex.setDiscover(recorder.getTime());
		Map<Vertex, Vertex[]> edgeMap = graph.getAdjacencys();
		Vertex[] adjacencys = edgeMap.get(vertex);
		if (adjacencys != null && adjacencys.length > 0) {
			for (Vertex adjacency : adjacencys) {
				if (adjacency.getColor() == Color.WHITE) {
					adjacency.setParent(vertex);
					visitVertex(graph, adjacency, recorder, sortedList);
				}
			}
		}
		recorder.setTime(recorder.getTime() + 1);
		vertex.setColor(Color.BLACK);
		vertex.setFinish(recorder.getTime());
		sortedList.addLast(vertex);
	}
}

class Vertex {
	private String name;
	private Color color;
	private Vertex parent;
	private int discover;
	private int finish;

	public Vertex(String name) {
		this.name = name;
		this.color = Color.WHITE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Vertex getParent() {
		return parent;
	}

	public void setParent(Vertex parent) {
		this.parent = parent;
	}

	public int getDiscover() {
		return discover;
	}

	public void setDiscover(int discover) {
		this.discover = discover;
	}

	public int getFinish() {
		return finish;
	}

	public void setFinish(int finish) {
		this.finish = finish;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
