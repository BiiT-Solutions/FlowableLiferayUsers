package com.biit.flowable.services;


import com.biit.flowable.users.LiferayUserManager;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.impl.UserQueryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.flowable.groups.IGroupToActivityRoleConverter;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;

@Service
public class CustomUserQuery extends UserQueryImpl {

    protected LiferayUserManager liferayUserManager;

    @Autowired
    public CustomUserQuery(IAuthorizationService<Long, Long, Long> authorizationService, IAuthenticationService<Long, Long> authenticationService, IGroupToActivityRoleConverter groupToActivityConverter) {
        liferayUserManager = new LiferayUserManager(authorizationService, authenticationService, groupToActivityConverter);
    }

    @Override
    public UserQuery userId(String id) {
        super.id = id;
        return this;
    }

    @Override
    public UserQuery userEmail(String email) {
        super.email = email;
        return this;
    }

    @Override
    public User singleResult() {
        return liferayUserManager.findUserByQueryCriteria(this).stream().findAny().orElse(null);
    }
}
