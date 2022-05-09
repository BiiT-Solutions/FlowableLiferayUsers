package com.biit.flowable.groups;

import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.Session;
import org.flowable.common.engine.impl.interceptor.SessionFactory;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowableGroupManagerFactory implements SessionFactory {

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
        return (Session) new FlowableGroupManager(authorizationService, authenticationService, groupToActivityConverter);
    }

}
