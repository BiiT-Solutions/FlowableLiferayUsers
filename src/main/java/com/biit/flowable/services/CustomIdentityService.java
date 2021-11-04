package com.biit.flowable.services;

import com.biit.flowable.groups.IGroupToActivityRoleConverter;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import org.flowable.engine.impl.IdentityServiceImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.idm.api.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;


public class CustomIdentityService extends IdentityServiceImpl {

    private final IAuthorizationService<Long, Long, Long> authorizationService;
    private final IAuthenticationService<Long, Long> authenticationService;
    private final IGroupToActivityRoleConverter groupToActivityConverter;


    @Autowired
    public CustomIdentityService(ProcessEngineConfigurationImpl processEngineConfiguration, IAuthorizationService<Long, Long, Long> authorizationService, IAuthenticationService<Long, Long> authenticationService, IGroupToActivityRoleConverter groupToActivityConverter) {
        super(processEngineConfiguration);
        this.authorizationService = authorizationService;
        this.authenticationService = authenticationService;
        this.groupToActivityConverter = groupToActivityConverter;
    }


    @Override
    public UserQuery createUserQuery() {
        return new CustomUserQuery(authorizationService, authenticationService, groupToActivityConverter);
    }
}
