package com.biit.flowable.services;

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

import org.flowable.engine.IdentityService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;

    protected Task findTaskByDefinitionKey(ProcessInstance processInstance, String definitionKey) {
        return taskService.createTaskQuery().processInstanceId(processInstance.getId())
                .taskDefinitionKey(definitionKey).singleResult();
    }

    protected String getFullNameOfUser(String userId) {
        final User user = identityService.createUserQuery().userId(userId).singleResult();
        if (user == null) {
            return null;
        } else {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        }
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }
}
