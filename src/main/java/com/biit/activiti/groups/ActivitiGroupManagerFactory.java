package com.biit.activiti.groups;

import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.Session;
import org.flowable.common.engine.impl.interceptor.SessionFactory;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;

@Service
public class ActivitiGroupManagerFactory implements SessionFactory {

	@Autowired
	private IAuthorizationService<Long, Long, Long> authorizationService;
	
	@Autowired
	private IAuthenticationService<Long, Long> authenticationService;
	
	@Autowired
	private IGroupToActivityRoleConverter groupToActivityConverter;

	@Override
	public Class<?> getSessionType() {
		return GroupEntityManager.class;
	}

	@Override
	public Session openSession(CommandContext commandContext) {
		return null;
	}

	public Session openSession() {
		return (Session) new ActivitiGroupManager(authorizationService, authenticationService, groupToActivityConverter);
	}

}
