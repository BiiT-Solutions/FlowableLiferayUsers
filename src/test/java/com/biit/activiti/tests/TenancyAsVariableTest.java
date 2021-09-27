package com.biit.activiti.tests;

import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
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
@Test(groups = "tenancyAsVariable")
public class TenancyAsVariableTest extends AbstractTransactionalTestNGSpringContextTests {

	private static final String HOLIDAY_PROCESS_NAME = "HolidayRequest";
	private static final String HOLIDAY_PROCESS_TENANCY_1 = "orbis";
	private static final String HOLIDAY_PROCESS_TENANCY_2 = "usmo";

	@Autowired
	private ProcessEngine processEngine;

	private String holidayRequestId1;
	private String holidayRequestId2;

	@Test
	@Rollback(value = false)
	public void defineTwoTenancyVariables() {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Assert.assertNotNull(runtimeService);
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 4);
		variables.put("startDate", "01-01-2020");
		variables.put("tenant", HOLIDAY_PROCESS_TENANCY_1);
		ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables);
		Assert.assertNotNull(processInstance1);
		holidayRequestId1 = processInstance1.getId();

		// Change the tenant.
		variables.put("tenant", HOLIDAY_PROCESS_TENANCY_2);
		ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey(HOLIDAY_PROCESS_NAME, variables);
		Assert.assertNotNull(processInstance2);
		holidayRequestId2 = processInstance2.getId();

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId1).count());
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId2).count());
	}

	@Test(dependsOnMethods = { "defineTwoTenancyVariables" })
	@Rollback(value = false)
	public void checkTenancy() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		// Check get process by tenant using a variable as separator.
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId1).count());
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId2).count());

		Assert.assertEquals(1,
				taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_1).list()
						.size());
		Assert.assertEquals(1,
				taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_2).list()
						.size());
	}

	@Test(dependsOnMethods = { "checkTenancy" })
	@Rollback(value = false)
	public void finishTaskOfTenancy1() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		Task task1 = taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_1)
				.singleResult();
		// Holidays are approved... Task is resolved!
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("approved", true);
		taskService.complete(task1.getId(), variables);

		// Finish second task
		Task task2 = taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_1)
				.singleResult();
		taskService.complete(task2.getId());
		// No tasks assigned.

		Assert.assertEquals(0,
				taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_1).list()
						.size());
		Assert.assertEquals(1,
				taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_2).list()
						.size());
	}
}
