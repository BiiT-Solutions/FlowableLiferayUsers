package com.biit.activiti.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import org.flowable.engine.identity.GroupQuery;
import org.flowable.idm.api.GroupQuery;
//import org.flowable.engine.impl.GroupQueryImpl;
import org.flowable.idm.engine.impl.GroupQueryImpl;
import org.flowable.common.engine.impl.Page;
//import otg.flowable.engine.impl.Page
//import org.flowable.engine.impl.persistence.entity.GroupEntity;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
//import org.flowable.engine.impl.persistence.entity.GroupEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityManagerImpl;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Map;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.api.event.FlowableIdmEventType;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.delegate.event.impl.FlowableIdmEventBuilder;
import org.flowable.idm.engine.impl.GroupQueryImpl;
import org.flowable.idm.engine.impl.persistence.entity.data.GroupDataManager;

import com.biit.activiti.logger.ActivitiUsersLogger;
import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.entity.IUser;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import com.biit.usermanager.security.exceptions.RoleDoesNotExistsException;
import com.biit.usermanager.security.exceptions.UserManagementException;

/**
 * Allows the use of Liferay Roles as Activiti groups.
 */
public class ActivitiGroupManager implements GroupEntityManager {

	private IAuthorizationService<Long, Long, Long> authorizationService;
	private IAuthenticationService<Long, Long> authenticationService;
	private IGroupToActivityRoleConverter groupToActivityConverter;

	public ActivitiGroupManager(IAuthorizationService<Long, Long, Long> authorizationService, IAuthenticationService<Long, Long> authenticationService,
			IGroupToActivityRoleConverter groupToActivityConverter) {
		this.authorizationService = authorizationService;
		this.authenticationService = authenticationService;
		this.groupToActivityConverter = groupToActivityConverter;
	}

	public static GroupEntityImpl getActivitiGroup(IRole<Long> liferayRole, IGroupToActivityRoleConverter liferayToActivity) {
		GroupEntityImpl activitiGroup = new GroupEntityImpl();
		activitiGroup.setName(liferayToActivity.getGroupName(liferayRole));
		activitiGroup.setType(liferayToActivity.getActivitiGroup(liferayRole).getType());
		activitiGroup.setId(liferayRole.getUniqueId() + "");
		activitiGroup.setRevision(0);

		return activitiGroup;
	}

	public GroupEntityImpl findGroupById(String roleId) {
		try {
			IRole<Long> liferayUser = authorizationService.getRole(Long.parseLong(roleId));
			return getActivitiGroup(liferayUser, groupToActivityConverter);
		} catch (NumberFormatException | UserManagementException | RoleDoesNotExistsException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}

	@Override
	public List<Group> findGroupsByUser(String userId) {
		List<Group> activitiGroups = new ArrayList<>();

		IUser<Long> liferayUser;
		try {
			liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			Set<IRole<Long>> liferayRoles = authorizationService.getUserRoles(liferayUser);
			for (IRole<Long> liferayRole : liferayRoles) {
				activitiGroups.add(ActivitiGroupManager.getActivitiGroup(liferayRole, groupToActivityConverter));
			}
		} catch (NumberFormatException | UserManagementException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return activitiGroups;
	}

	@Override
	public List<Group> findGroupsByNativeQuery(Map<String, Object> map) {
		return null;
	}

	@Override
	public long findGroupCountByNativeQuery(Map<String, Object> map) {
		return 0;
	}

	@Override
	public Group createNewGroup(String groupId) {
		throw new UnsupportedOperationException();
	}


	public void insertGroup(Group group) {
		throw new UnsupportedOperationException();
	}


	public void updateGroup(Group updatedGroup) {
		throw new UnsupportedOperationException();
	}


	public void deleteGroup(String groupId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public GroupQuery createNewGroupQuery() {
		//return super.createNewGroupQuery();
		return null;
	}

	@Override
	public List<Group> findGroupByQueryCriteria(GroupQueryImpl groupQuery) {
		return null;
	}


	public List<org.flowable.idm.api.Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
		List<org.flowable.idm.api.Group> groupList = new ArrayList<org.flowable.idm.api.Group>();
		GroupQueryImpl groupQuery = (GroupQueryImpl) query;
		if (!StringUtils.isEmpty(groupQuery.getId())) {
			groupList.add(findGroupById(groupQuery.getId()));
			return groupList;
		}
		if (!StringUtils.isEmpty(groupQuery.getName())) {
			try {
				groupList.add(getActivitiGroup(authorizationService.getRole(groupToActivityConverter.getRoleName(groupQuery.getName())),
						groupToActivityConverter));
			} catch (UserManagementException | RoleDoesNotExistsException e) {
				ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
			}
			return groupList;
		} else if (!StringUtils.isEmpty(groupQuery.getUserId())) {
			groupList.addAll(findGroupsByUser(groupQuery.getUserId()));
			return groupList;
		} else if (!StringUtils.isEmpty(groupQuery.getType())) {
			Set<IRole<Long>> roles = groupToActivityConverter.getRoles(GroupType.getGroupType(groupQuery.getType()));
			for (IRole<Long> role : roles) {
				groupList.add(getActivitiGroup(role, groupToActivityConverter));
			}
			return groupList;
		} else {
			Set<IRole<Long>> liferayRoles = groupToActivityConverter.getAllRoles();
			for (IRole<Long> liferayRole : liferayRoles) {
				groupList.add(getActivitiGroup(liferayRole, groupToActivityConverter));
			}
			return groupList;
		}
	}

	@Override
	public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
		return findGroupByQueryCriteria(query, null).size();
	}

	@Override
	public boolean isNewGroup(org.flowable.idm.api.Group activitiGroup) {
		return false;
	}

	@Override
	public List<Group> findGroupsByPrivilegeId(String s) {
		return null;
	}

	public IAuthorizationService<Long, Long, Long> getAuthorizationService() {
		return authorizationService;
	}

	public void setAuthorizationService(IAuthorizationService<Long, Long, Long> authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public GroupEntity create() {
		return null;
	}

	@Override
	public GroupEntity findById(String s) {
		return null;
	}

	@Override
	public void insert(GroupEntity entity) {

	}

	@Override
	public void insert(GroupEntity entity, boolean b) {

	}

	@Override
	public GroupEntity update(GroupEntity entity) {
		return null;
	}

	@Override
	public GroupEntity update(GroupEntity entity, boolean b) {
		return null;
	}

	@Override
	public void delete(String s) {

	}

	@Override
	public void delete(GroupEntity entity) {

	}

	@Override
	public void delete(GroupEntity entity, boolean b) {

	}
}
