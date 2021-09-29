package com.biit.activiti.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
//import org.flowable.engine.task.Task;
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

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
@Test(groups = "activitiTasks")
public class SimpleProcessTest extends AbstractTransactionalTestNGSpringContextTests {
	private static final String ONE_TASK_PROCESS_NAME = "oneTaskProcess";
	private static final String HOLIDAY_PROCESS_NAME = "holidayRequest";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_NAME = "Approve Request";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_2_NAME = "Send Request Approved";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_USER = "kermit";
	private static final String HOLIDAY_PROCESS_APPROVE_TASK_USER2 = "fozzie";

	@Inject
	private ProcessEngine processEngine;

	private String holidayRequestId;

	@Test
	// Deployment is not working?? File added by ApplicationContext too.
	@Deployment(resources = { "process/oneTaskProcess.bpmn20.xml" })
	public void simpleProcessTest() {
		Assert.assertNotNull(processEngine);
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Assert.assertNotNull(runtimeService);
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_NAME);
		Assert.assertNotNull(processInstance);

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(processInstance.getId()).count());
	}

	@Test
	public void fullTest() {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		// Start a process instance
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 4);
		variables.put("eMail", "dummy@biit-solutions.com");
		variables.put("startDate", "01-01-2020");

		// Get all available deployed process and select the correct one.
		// List<org.activiti.engine.repository.Deployment> processesDeployed = processEngine.getRepositoryService()
		// .createDeploymentQuery().list();
		// org.activiti.engine.repository.Deployment deploymentToStart = null;
		// for (org.activiti.engine.repository.Deployment deployment : processesDeployed) {
		// if (deployment.getName().equals(HOLIDAY_PROCESS_NAME)) {
		// deploymentToStart = deployment;
		// }
		// }
		//
		// Assert.assertNotNull(deploymentToStart);
		// String procId = runtimeService.startProcessInstanceById(deploymentToStart.getId(), variables).getId();

		// Other simple way to start the process.
		String procId = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables).getId();

		// Get the first task
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(procId).list();
		for (Task task : tasks) {
			// claim it
			taskService.claim(task.getId(), HOLIDAY_PROCESS_APPROVE_TASK_USER2);
		}
		Assert.assertEquals(1, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER2).count());

		// Verify Fozzie can now retrieve the task
		tasks = taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER2).list();
		for (Task task : tasks) {
			// Complete the task
			variables = new HashMap<String, Object>();
			variables.put("approved", true);
			taskService.complete(task.getId(), variables);
		}
		Assert.assertEquals(0, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER2).count());

		// Retrieve and claim the second task
		tasks = taskService.createTaskQuery().processInstanceId(procId).list();
		for (Task task : tasks) {
			taskService.claim(task.getId(), HOLIDAY_PROCESS_APPROVE_TASK_USER);
		}
		Assert.assertEquals(1, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).count());

		// Completing the second task ends the process
		for (Task task : tasks) {
			taskService.complete(task.getId());
		}

		Assert.assertEquals(0, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).count());

		// verify that the process is actually finished
		HistoryService historyService = processEngine.getHistoryService();
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(procId).singleResult();
		Assert.assertNotNull(historicProcessInstance.getEndTime());
	}

	/**
	 * Holidays with less than 2 days, are automatically processed.
	 */
	@Test
	@Deployment(resources = { "process/holidayRequest.bpmn20.xml" })
	@Rollback(value = false)
	public void holidayRequestsAutomaticAnswer() {
		Assert.assertNotNull(processEngine);
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Assert.assertNotNull(runtimeService);
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 2);
		variables.put("eMail", "dummy@biit-solutions.com");
		variables.put("startDate", "01-01-2020");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables);
		Assert.assertNotNull(processInstance);

		// Check if a task is available for the given process
		Assert.assertEquals(0, taskService.createTaskQuery().processInstanceId(processInstance.getId()).count());
	}

	/**
	 * Holidays with more than 2 days, needs human approval.
	 */
	@Test
	@Deployment(resources = { "process/holidayRequest.bpmn20.xml" })
	@Rollback(value = false)
	public void holidayRequestsManualTask() {
		Assert.assertNotNull(processEngine);
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Assert.assertNotNull(runtimeService);
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 4);
		variables.put("startDate", "01-01-2020");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables);
		Assert.assertNotNull(processInstance);

		holidayRequestId = processInstance.getId();

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId).count());
	}

	@Test(dependsOnMethods = { "holidayRequestsManualTask" })
	@Rollback(value = false)
	public void listAllAvailableTasks() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId).count());

		Assert.assertEquals(HOLIDAY_PROCESS_APPROVE_TASK_NAME,
				taskService.createTaskQuery().processInstanceId(holidayRequestId).list().get(0).getName());
		Assert.assertEquals(holidayRequestId, taskService.createTaskQuery().processInstanceId(holidayRequestId).list()
				.get(0).getProcessInstanceId());
		// Not assigned yet!
		Assert.assertEquals(null, taskService.createTaskQuery().processInstanceId(holidayRequestId).list().get(0)
				.getAssignee());
	}

	/**
	 * This task has two user actions. Assign and resolve both of them.
	 */
	@Test(dependsOnMethods = { "listAllAvailableTasks" })
	@Rollback(value = false)
	public void assignFirstTask() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId).count());

		Task requestApproval = taskService.createTaskQuery().processInstanceId(holidayRequestId).list().get(0);
		Assert.assertEquals(0, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).count());
		requestApproval.setAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER);
		taskService.saveTask(requestApproval);
		// OR can be assigned as:
		// taskService.setAssignee(requestApproval.getId(), HOLIDAY_PROCESS_APPROVE_TASK_USER);
		Assert.assertEquals(1, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).count());
	}

	@Test(dependsOnMethods = { "assignFirstTask" })
	@Rollback(value = false)
	public void finishFirstTask() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);
		// One task assigned.
		Assert.assertEquals(1, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).count());
		Task requestApproval = taskService.createTaskQuery().processInstanceId(holidayRequestId).list().get(0);
		// Holidays are approved... Task is resolved!
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("approved", true);
		taskService.complete(requestApproval.getId(), variables);
		// taskService.saveTask(requestApproval);
		// No tasks assigned.
		Assert.assertEquals(0, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).count());
	}

	@Test(dependsOnMethods = { "finishFirstTask" })
	@Rollback(value = false)
	public void claimSecondTask() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);
		Assert.assertEquals(0, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER2).count());

		Task sendRequestApproval = taskService.createTaskQuery().processInstanceId(holidayRequestId).list().get(0);
		Assert.assertEquals(HOLIDAY_PROCESS_APPROVE_TASK_2_NAME, sendRequestApproval.getName());
		taskService.claim(sendRequestApproval.getId(), HOLIDAY_PROCESS_APPROVE_TASK_USER2);
		// taskService.saveTask(sendRequestApproval);
		Assert.assertEquals(1, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER2).count());
	}

	@Test(dependsOnMethods = { "claimSecondTask" })
	@Rollback(value = false)
	public void finishSecondTask() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);
		// One task assigned.
		Assert.assertEquals(1, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER2).count());
		Task sendRequestApproval = taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER2)
				.list().get(0);
		// Task is resolved!
		taskService.complete(sendRequestApproval.getId());
		// No tasks assigned.
		Assert.assertEquals(0, taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER2).count());
	}

	@Test(dependsOnMethods = { "finishSecondTask" })
	@Rollback(value = false)
	public void checkProcessEnded() {
		HistoryService historyService = processEngine.getHistoryService();
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(holidayRequestId).singleResult();
		Assert.assertNotNull(historicProcessInstance.getEndTime());
	}
}
