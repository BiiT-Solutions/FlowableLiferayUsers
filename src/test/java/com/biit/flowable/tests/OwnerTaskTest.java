package com.biit.flowable.tests;

/*-
 * #%L
 * Liferay users in Flowable
 * %%
 * Copyright (C) 2021 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
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
@Test(groups = "ownerTasks")
public class OwnerTaskTest extends AbstractTransactionalTestNGSpringContextTests {
    private static final String HOLIDAY_PROCESS_NAME = "holidayRequest";
    private static final String HOLIDAY_PROCESS_APPROVE_TASK_USER = "sam@test.com";
    private static final String HOLIDAY_PROCESS_APPROVE_TASK_OWNER = "orbis";

    @Autowired
    private ProcessEngine processEngine;

    private String holidayRequestId1;
    private String holidayRequestId2;
    private String holidayRequestId3;

    @Test
    @Deployment(resources = {"process/holidayRequest.bpmn20.xml"})
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
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId1).count(), 1);
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId2).count(), 1);
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId3).count(), 1);
    }

    /**
     * One process instances would have an owner, the other not. The owner is assigned to a task, therefore next tasks
     * of a process will lose the task.
     */
    @Test(dependsOnMethods = {"startThreeProcess"})
    @Rollback(value = false)
    public void assignOwnerToTask() {
        TaskService taskService = processEngine.getTaskService();
        Assert.assertNotNull(taskService);

        // Check if a task is available for the given process
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId1).count(), 1);
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId2).count(), 1);
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId3).count(), 1);

        // No tasks with this owner.
        Assert.assertEquals(taskService.createTaskQuery().taskOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER).list()
                .size(), 0);

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

        Assert.assertEquals(taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).list()
                .size(), 3);

        // Assign owner only to first and second task
        requestApproval1 = taskService.createTaskQuery().processInstanceId(holidayRequestId1).list().get(0);
        requestApproval1.setOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER);
        taskService.saveTask(requestApproval1);

        requestApproval2 = taskService.createTaskQuery().processInstanceId(holidayRequestId2).list().get(0);
        requestApproval2.setOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER);
        taskService.saveTask(requestApproval2);

        Assert.assertEquals(taskService.createTaskQuery().taskOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER).list()
                .size(), 2);

        // Get all tasks for a user from selected owner.
        Assert.assertEquals(
                taskService.createTaskQuery().taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER)
                        .taskOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER).list().size(), 2);

        // Different way.
        Assert.assertEquals(taskService.createTaskQuery().taskOwner(HOLIDAY_PROCESS_APPROVE_TASK_OWNER)
                .taskAssignee(HOLIDAY_PROCESS_APPROVE_TASK_USER).list().size(), 2);
    }

}
