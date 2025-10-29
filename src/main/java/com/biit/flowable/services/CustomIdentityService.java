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
    public CustomIdentityService(ProcessEngineConfigurationImpl processEngineConfiguration, IAuthorizationService<Long, Long, Long> authorizationService,
                                 IAuthenticationService<Long, Long> authenticationService, IGroupToActivityRoleConverter groupToActivityConverter) {
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
