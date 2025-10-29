package com.biit.flowable.security;

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

import java.util.HashSet;
import java.util.Set;

import com.biit.usermanager.security.exceptions.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.flowable.groups.GroupType;
import com.biit.flowable.groups.IGroupToActivityRoleConverter;
import com.biit.flowable.logger.FlowableUsersLogger;
import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.security.IAuthorizationService;
import com.biit.usermanager.security.exceptions.RoleDoesNotExistsException;
import com.biit.usermanager.security.exceptions.UserManagementException;

@Service
public class LiferayToActivityConverter implements IGroupToActivityRoleConverter {

	@Autowired
	private IAuthorizationService<Long, Long, Long> authorizationService;

	@Override
	public GroupType getFlowableGroup(IRole<Long> liferayRole) {
		return TestRole.get(liferayRole.getUniqueName()).getFlowableGroup();
	}

	@Override
	public Set<IRole<Long>> getRoles(GroupType type) {
		Set<IRole<Long>> roles = new HashSet<>();
		for (TestRole testRole : TestRole.get(type)) {
			try {
				roles.add(authorizationService.getRole(testRole.getLiferayName()));
			} catch (UserManagementException | RoleDoesNotExistsException | InvalidCredentialsException e) {
				FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
			}
		}
		return roles;
	}

	@Override
	public Set<IRole<Long>> getAllRoles() {
		Set<IRole<Long>> roles = new HashSet<>();
		for (TestRole role : TestRole.values()) {
			try {
				roles.add(authorizationService.getRole(role.getLiferayName()));
			} catch (UserManagementException | RoleDoesNotExistsException | InvalidCredentialsException e) {
				FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
			}
		}
		return roles;
	}

	@Override
	public String getGroupName(IRole<Long> liferayRole) {
		return liferayRole.getUniqueName();
	}

	@Override
	public String getRoleName(String flowableGroupName) {
		return flowableGroupName;
	}

}
