package com.biit.activiti.tests;

import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
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
public class TenancyTest extends AbstractTransactionalTestNGSpringContextTests {

	private static final String HOLIDAY_PROCESS_NAME = "HolidayRequest";
	private static final String HOLIDAY_PROCESS_TENANCY_1 = "orbis";
	private static final String HOLIDAY_PROCESS_TENANCY_2 = "usmo";

	@Autowired
	private ProcessEngine processEngine;

	private String holidayRequestId1;
	private String holidayRequestId2;

	/**
	 * For using tenancy we need to deploy the model as different process for each tenant.
	 */
	@Test
	@Rollback(value = false)
	public void startTwoProcess() {
		// Deploy two process with different tenancy.
		processEngine.getRepositoryService().createDeployment()
				.addClasspathResource("process/holidayRequest.bpmn20.xml").tenantId(HOLIDAY_PROCESS_TENANCY_1).deploy();
		processEngine.getRepositoryService().createDeployment()
				.addClasspathResource("process/holidayRequest.bpmn20.xml").tenantId(HOLIDAY_PROCESS_TENANCY_2).deploy();

		Assert.assertNotNull(processEngine);
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Assert.assertNotNull(runtimeService);
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 4);
		variables.put("startDate", "01-01-2020");
		ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKeyAndTenantId(HOLIDAY_PROCESS_NAME,
				variables, HOLIDAY_PROCESS_TENANCY_1);
		Assert.assertNotNull(processInstance1);
		holidayRequestId1 = processInstance1.getId();

		ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKeyAndTenantId(HOLIDAY_PROCESS_NAME,
				variables, HOLIDAY_PROCESS_TENANCY_2);
		Assert.assertNotNull(processInstance2);
		holidayRequestId2 = processInstance2.getId();

		// Check if a task is available for the given process
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId1).count());
		Assert.assertEquals(1, taskService.createTaskQuery().processInstanceId(holidayRequestId2).count());
	}

	@Test(dependsOnMethods = { "startTwoProcess" })
	@Rollback(value = false)
	public void checkTenancy() {
		TaskService taskService = processEngine.getTaskService();
		Assert.assertNotNull(taskService);

		RuntimeService runtimeService = processEngine.getRuntimeService();

		// Check get process by tenant id.
		Assert.assertEquals(1,
				runtimeService.createProcessInstanceQuery().processInstanceTenantId(HOLIDAY_PROCESS_TENANCY_1).list()
						.size());
		Assert.assertEquals(1,
				runtimeService.createProcessInstanceQuery().processInstanceTenantId(HOLIDAY_PROCESS_TENANCY_2).list()
						.size());

		Assert.assertEquals(holidayRequestId1,
				runtimeService.createProcessInstanceQuery().processInstanceTenantId(HOLIDAY_PROCESS_TENANCY_1)
						.singleResult().getProcessInstanceId());
		Assert.assertEquals(holidayRequestId2,
				runtimeService.createProcessInstanceQuery().processInstanceTenantId(HOLIDAY_PROCESS_TENANCY_2)
						.singleResult().getProcessInstanceId());

	}
}
