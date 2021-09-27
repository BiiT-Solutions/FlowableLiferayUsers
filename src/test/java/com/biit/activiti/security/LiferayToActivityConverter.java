package com.biit.activiti.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biit.activiti.groups.GroupType;
import com.biit.activiti.groups.IGroupToActivityRoleConverter;
import com.biit.activiti.logger.ActivitiUsersLogger;
import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.security.IAuthorizationService;
import com.biit.usermanager.security.exceptions.RoleDoesNotExistsException;
import com.biit.usermanager.security.exceptions.UserManagementException;

@Service
public class LiferayToActivityConverter implements IGroupToActivityRoleConverter {

	@Autowired
	private IAuthorizationService<Long, Long, Long> authorizationService;

	@Override
	public GroupType getActivitiGroup(IRole<Long> liferayRole) {
		return TestRole.get(liferayRole.getUniqueName()).getActivitiGroup();
	}

	@Override
	public Set<IRole<Long>> getRoles(GroupType type) {
		Set<IRole<Long>> roles = new HashSet<>();
		for (TestRole testRole : TestRole.get(type)) {
			try {
				roles.add(authorizationService.getRole(testRole.getLiferayName()));
			} catch (UserManagementException | RoleDoesNotExistsException e) {
				ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
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
			} catch (UserManagementException | RoleDoesNotExistsException e) {
				ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
			}
		}
		return roles;
	}

	@Override
	public String getGroupName(IRole<Long> liferayRole) {
		return liferayRole.getUniqueName();
	}

	@Override
	public String getRoleName(String activitiGroupName) {
		return activitiGroupName;
	}

}
