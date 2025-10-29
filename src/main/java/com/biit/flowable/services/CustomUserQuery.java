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


import com.biit.flowable.groups.IGroupToActivityRoleConverter;
import com.biit.flowable.users.LiferayUserManager;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.impl.UserQueryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomUserQuery extends UserQueryImpl {

    private LiferayUserManager liferayUserManager;

    @Autowired
    public CustomUserQuery(IAuthorizationService<Long, Long, Long> authorizationService, IAuthenticationService<Long, Long> authenticationService,
                           IGroupToActivityRoleConverter groupToActivityConverter) {
        liferayUserManager = new LiferayUserManager(authorizationService, authenticationService, groupToActivityConverter);
    }

    public LiferayUserManager getLiferayUserManager() {
        return liferayUserManager;
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
