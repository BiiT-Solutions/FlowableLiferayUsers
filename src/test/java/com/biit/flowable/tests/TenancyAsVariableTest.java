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
@Test(groups = "tenancyAsVariable")
public class TenancyAsVariableTest extends AbstractTransactionalTestNGSpringContextTests {

    private static final String HOLIDAY_PROCESS_NAME = "holidayRequest";
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
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId1).count(), 1);
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId2).count(), 1);
    }

    @Test(dependsOnMethods = {"defineTwoTenancyVariables"})
    @Rollback(value = false)
    public void checkTenancy() {
        TaskService taskService = processEngine.getTaskService();
        Assert.assertNotNull(taskService);

        // Check get process by tenant using a variable as separator.
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId1).count(), 1);
        Assert.assertEquals(taskService.createTaskQuery().processInstanceId(holidayRequestId2).count(), 1);

        Assert.assertEquals(
                taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_1).list()
                        .size(), 1);
        Assert.assertEquals(
                taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_2).list()
                        .size(), 1);
    }

    @Test(dependsOnMethods = {"checkTenancy"})
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

        Assert.assertEquals(
                taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_1).list()
                        .size(), 0);
        Assert.assertEquals(
                taskService.createTaskQuery().processVariableValueEquals("tenant", HOLIDAY_PROCESS_TENANCY_2).list()
                        .size(), 1);
    }
}
