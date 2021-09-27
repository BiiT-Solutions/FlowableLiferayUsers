package com.biit.activiti.tests;

import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.test.Deployment;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
@Test(groups = "ownerTasks")
public class OwnerTaskTest extends AbstractTransactionalTestNGSpringContextTests {
	private static final String HOLIDAY_PROCESS_NAME = "HolidayRequest";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_USER = "sam@test.com";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_OWNER = "orbis";

	@Autowired
	private ProcessEngine processEngine;

	private String holidayRequestId1;
	private String holidayRequestId2;
	private String holidayRequestId3;

	@Test
	@Deployment(resources = { "process/holidayRequest.bpmn20.xml" })
	@Rollback(value = false)
	public void startThreeProcess() {
		Assert.assertNotNull(processEngine);
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Assert.assertNotNull(runtimeService);
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 4);
		variables.put("startDate", "01-01-2020");
		ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables);
		Assert.assertNotNull(processInstance1);
		holidayRequestId1 = processInstance1.getId();

		ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables);
		Assert.assertNotNull(processInstance2);
		holidayRequestId2 = processInstance2.getId();

		ProcessInstance processInstance3 = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables);
		Assert.assertNotNull(processInstance3);
		holidayRequestId3 = processInstance3.getId();

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId1).count());
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId2).count());
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId3).count());
	}

	/**
	 * One process instances would have an owner, the other not. The owner is assigned to a task, therefore next tasks
	 * of a process will lose the task.
	 */
	@Test(dependsOnMethods = { "startThreeProcess" })
	@Rollback(value = false)
	public void assignOwnerToTask() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId1).count());
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId2).count());
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId3).count());

		// No tasks with this owner.
		Assert.assertEquals(0, taskService.createTaskQuery().taskOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER).list()
				.size());

		// Assign first task to assignee
		Task requestApproval1 = taskService.createTaskQuery().processInstanceId(holidayRequestId1).list().get(0);
		requestApproval1.setAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER);
		taskService.saveTask(requestApproval1);

		// Assign second task to assignee.
		Task requestApproval2 = taskService.createTaskQuery().processInstanceId(holidayRequestId2).list().get(0);
		requestApproval2.setAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER);
		taskService.saveTask(requestApproval2);

		// Assign third task to assignee.
		Task requestApproval3 = taskService.createTaskQuery().processInstanceId(holidayRequestId3).list().get(0);
		requestApproval3.setAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER);
		taskService.saveTask(requestApproval3);

		Assert.assertEquals(3, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).list()
				.size());

		// Assign owner only to first and second task
		requestApproval1 = taskService.createTaskQuery().processInstanceId(holidayRequestId1).list().get(0);
		requestApproval1.setOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER);
		taskService.saveTask(requestApproval1);

		requestApproval2 = taskService.createTaskQuery().processInstanceId(holidayRequestId2).list().get(0);
		requestApproval2.setOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER);
		taskService.saveTask(requestApproval2);

		Assert.assertEquals(2, taskService.createTaskQuery().taskOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER).list()
				.size());

		// Get all tasks for a user from selected owner.
		Assert.assertEquals(
				2,
				taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER)
						.taskOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER).list().size());

		// Different way.
		Assert.assertEquals(2, taskService.createTaskQuery().taskOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER)
				.taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).list().size());
	}

}
