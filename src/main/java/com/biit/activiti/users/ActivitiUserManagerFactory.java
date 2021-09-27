package com.biit.activiti.users;

// import org.flowable.engine.impl.interceptor.Session;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.Session;
//import org.flowable.engine.impl.interceptor.SessionFactory;
import org.flowable.common.engine.impl.interceptor.SessionFactory;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityManager;
//import org.flowable.idm.engine.impl.persistence.entity.UserIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.activiti.groups.IGroupToActivityRoleConverter;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;

@Service
public class ActivitiUserManagerFactory implements SessionFactory {

	@Autowired
	private IAuthorizationService<Long, Long, Long> authorizationService;
	@Autowired
	private IAuthenticationService<Long, Long> authenticationService;
	@Autowired
	private IGroupToActivityRoleConverter groupToActivityConverter;

	@Override
	public Class<?> getSessionType() {
		return UserEntityManager.class;
	}

	@Override
	public Session openSession(CommandContext commandContext) {
		return null;
	}


	public Session openSession() {
		return (Session) new ActivitiUserManager(authorizationService, authenticationService, groupToActivityConverter);
	}
}