package com.biit.flowable.users;

import com.biit.flowable.groups.IGroupToActivityRoleConverter;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.Session;
import org.flowable.common.engine.impl.interceptor.SessionFactory;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowableUserManagerFactory implements SessionFactory {

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
        return new LiferayUserManager(authorizationService, authenticationService, groupToActivityConverter);
    }
}