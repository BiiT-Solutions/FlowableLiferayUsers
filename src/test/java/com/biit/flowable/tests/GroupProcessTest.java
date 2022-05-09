package com.biit.flowable.tests;

import org.flowable.engine.IdentityService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

@ContextConfiguration({"classpath:applicationContext.xml"})
@Test(groups = "flowableGroupTasks")
public class GroupProcessTest extends AbstractTransactionalTestNGSpringContextTests {
    private static final String HOLIDAY_PROCESS_NAME = "holidayRequest";
    private static final String HOLIDAY_PROCESS_APPROVE_TASK_USER = "sam@test.com";
    private static final String HOLIDAY_PROCESS_APPROVE_TASK_GROUP = "usmo_physiotherapist";

    @Autowired
    private ProcessEngine processEngine;

    private String holidayRequestId;

    /**
     * Create user.
     */
    @Test(enabled = false)
    @Rollback(value = false)
    private void createUser() {
        IdentityService identityService = processEngine.getIdentityService();

        User user = identityService.newUser(HOLIDAY_PROCESS_APPROVE_TASK_USER);
        user.setEmail("kermit@sessam.com");
        user.setFirstName(HOLIDAY_PROCESS_APPROVE_TASK_USER);
        user.setLastName("The Frog");
        user.setPassword("123");
        identityService.saveUser(user);
    }

    /**
     * Assign user to group.
     */
    @Test(enabled = false, dependsOnMethods = "createUser")
    @Rollback(value = false)
    public void addUserToGroup() {
        IdentityService identityService = processEngine.getIdentityService();
        Group group = identityService.newGroup(HOLIDAY_PROCESS_APPROVE_TASK_GROUP);
        group.setName(HOLIDAY_PROCESS_APPROVE_TASK_GROUP);

        identityService.saveGroup(group);

        identityService.createMembership(HOLIDAY_PROCESS_APPROVE_TASK_USER, HOLIDAY_PROCESS_APPROVE_TASK_GROUP);
    }

    /**
     * Holidays with more than 2 days, needs human approval.
     */
    @Test
    @Deployment(resources = {"process/holidayRequest.bpmn20.xml"})
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
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId).count(), 1);
    }

    /**
     * This task has two user actions. Assign and resolve both of them.
     */
    @Test(dependsOnMethods = {"holidayRequestsManualTask"})
    @Rollback(value = false)
    public void assignFirstTaskToGroup() {
        TaskService taskService = processEngine.getTaskService();
        Assert.assertNotNull(taskService);

        // Check if a task is available for the given process
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId).count(), 1);

        Task requestApproval = taskService.createTaskQuery().processInstanceId(holidayRequestId).list().get(0);
        Assert.assertEquals(taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).count(), 0);

        // Assign task to group
        taskService.addCandidateGroup(requestApproval.getId(), HOLIDAY_PROCESS_APPROVE_TASK_GROUP);
        Assert.assertEquals(taskService.createTaskQuery().taskCandidateGroup(HOLIDAY_PROCESS_APPROVE_TASK_GROUP)
                .count(), 1);
    }
}
