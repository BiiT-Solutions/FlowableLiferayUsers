package com.biit.flowable.services;

import org.flowable.engine.IdentityService;
import org.flowable.engine.TaskService;
import org.flowable.idm.api.User;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService {

	@Autowired
	protected TaskService taskService;
	@Autowired
	protected IdentityService identityService;

	protected Task findTaskByDefinitionKey(ProcessInstance processInstance, String definitionKey) {
		return taskService.createTaskQuery().processInstanceId(processInstance.getId())
				.taskDefinitionKey(definitionKey).singleResult();
	}

	protected String getFullNameOfUser(String userId) {
		User user = identityService.createUserQuery().userId(userId).singleResult();
		if (user == null) {
			return null;
		} else {
			return String.format("%s %s", user.getFirstName(), user.getLastName());
		}
	}

}
