package org.iceslab.frobot.master.workflow;

import org.iceslab.frobot.cluster.TaskInfo;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class OneByOneWorkFlowTest {
    private List<TaskInfo> taskList = new ArrayList<TaskInfo>();
	private OneByOneWorkFlow workFlow;
	
	@Before
	public void setup() {
		//add three tasks to the taskList by order
		TaskInfo download = new TaskInfo();
		download.setTaskID("download");
		taskList.add(download);
		TaskInfo processor = new TaskInfo();
		processor.setTaskID("processor");
		taskList.add(processor);
		TaskInfo pipeline = new TaskInfo();
		pipeline.setTaskID("pipeline");
		taskList.add(pipeline);
		
		workFlow = new OneByOneWorkFlow(taskList);
	}
	
	@Test
	public void testNextAndCurrent() {
		
		TaskInfo firstTask = workFlow.next();
		assertEquals("download",firstTask.getTaskID());
		TaskInfo current1 = workFlow.current();
		assertEquals("download",current1.getTaskID());
		
		TaskInfo secondTask = workFlow.next();
		assertEquals("processor",secondTask.getTaskID());
		TaskInfo current2 = workFlow.current();
		assertEquals("processor",current2.getTaskID());
		
		TaskInfo threeTask = workFlow.next();
		assertEquals("pipeline",threeTask.getTaskID());
		TaskInfo current3 = workFlow.current();
		assertEquals("pipeline", current3.getTaskID());
	}
	
	@Test
	public void testGetNext() {
		assertEquals("processor", workFlow.getNext("download").getTaskID());
		assertEquals("pipeline", workFlow.getNext("processor").getTaskID());
		assertNull(workFlow.getNext("pipeline"));
	}
}