package com.biit.activiti.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.flowable.engine.identity.Picture;
import org.flowable.idm.api.PasswordEncoder;
import org.flowable.idm.api.PasswordSalt;
import org.flowable.idm.api.Picture;
import org.flowable.common.engine.impl.Page;
//import org.flowable.engine.impl.UserQueryImpl;
import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.UserQueryImpl;
//import org.flowable.engine.impl.persistence.entity.IdentityInfoEntity;
import org.flowable.idm.engine.impl.persistence.entity.*;
//import org.flowable.engine.impl.persistence.entity.UserEntity;
//import org.flowable.engine.impl.persistence.entity.UserEntityManager;
import org.flowable.idm.engine.impl.persistence.entity.data.UserDataManager;
import org.springframework.util.StringUtils;

import com.biit.activiti.groups.ActivitiGroupManager;
import com.biit.activiti.groups.IGroupToActivityRoleConverter;
import com.biit.activiti.logger.ActivitiUsersLogger;
import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.entity.IUser;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.biit.usermanager.security.exceptions.InvalidCredentialsException;
import com.biit.usermanager.security.exceptions.UserDoesNotExistException;
import com.biit.usermanager.security.exceptions.UserManagementException;
import com.liferay.portal.model.User;
import org.flowable.common.engine.impl.persistence.entity.EntityManager;
import org.flowable.idm.engine.impl.persistence.entity.data.UserDataManager;

/**
 * Allows the use of Liferay User in Activiti.
 */
public class ActivitiUserManager implements UserEntityManager {
	private IAuthorizationService<Long, Long, Long> authorizationService;
	private IAuthenticationService<Long, Long> authenticationService;
	private IGroupToActivityRoleConverter groupToActivityConverter;


	public ActivitiUserManager(IAuthorizationService<Long, Long, Long> authorizationService, IAuthenticationService<Long, Long> authenticationService,
			IGroupToActivityRoleConverter groupToActivityConverter) {
		this.authorizationService = authorizationService;
		this.authenticationService = authenticationService;
		this.groupToActivityConverter = groupToActivityConverter;
	}


	public static UserEntity getActivitiUser(IUser<Long> liferayUser) {
		if (liferayUser instanceof User) {
			return getActivitiUser((User) liferayUser);
		}
		return null;
	}

	public static UserEntity getActivitiUser(User liferayUser) {
		if (liferayUser == null) {
			return null;
		}
		UserEntityImpl activitiUser = new UserEntityImpl();
		activitiUser.setEmail(liferayUser.getEmailAddress());
		activitiUser.setFirstName(liferayUser.getFirstName());
		activitiUser.setId(liferayUser.getUniqueId() + "");
		activitiUser.setLastName(liferayUser.getLastName());
		activitiUser.setPassword(liferayUser.getPassword());
		activitiUser.setPicture(new Picture(null, null));
		activitiUser.setRevision(0);

		return activitiUser;
	}


	public UserEntity findUserById(String userId) {
		try {
			IUser<Long> liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			return getActivitiUser(liferayUser);
		} catch (NumberFormatException | UserManagementException e) {
			e.printStackTrace();
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}

	private UserEntity findUserByEmail(String userEmail) {
		try {
			IUser<Long> liferayUser = authenticationService.getUserByEmail(userEmail);
			return getActivitiUser(liferayUser);
		} catch (NumberFormatException | UserManagementException | UserDoesNotExistException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return null;
	}

	@Override
	public boolean isNewUser(org.flowable.idm.api.User user) {
		return false;
	}

	@Override
	public Picture getUserPicture(org.flowable.idm.api.User user) {
		return null;
	}

	@Override
	public void setUserPicture(org.flowable.idm.api.User user, Picture picture) {

	}

	@Override
	public void deletePicture(org.flowable.idm.api.User user) {

	}

	@Override
	public List<org.flowable.idm.api.User> findUsersByPrivilegeId(String s) {
		return null;
	}


	public List<org.flowable.idm.api.Group> findGroupsByUser(String userId) {
		List<org.flowable.idm.api.Group> activitiGroups = new ArrayList<>();

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


	public Boolean checkPassword(String userId, String password) {
		IUser<Long> liferayUser;
		try {
			liferayUser = authenticationService.getUserById(Long.parseLong(userId));
			return authenticationService.authenticate(liferayUser.getEmailAddress(), password) != null;
		} catch (NumberFormatException | InvalidCredentialsException | UserManagementException | AuthenticationRequired | UserDoesNotExistException e) {
			ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
		}
		return false;
	}


	public void insertUser(org.flowable.idm.api.User user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateUser(org.flowable.idm.api.User updatedUser) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<org.flowable.idm.api.User> findUserByQueryCriteria(UserQueryImpl userQuery) {
		return null;
	}

	@Override
	public UserEntity createNewUser(String userId) {
		throw new UnsupportedOperationException();
	}


	public void deleteUser(String userId) {
		throw new UnsupportedOperationException();
	}


	public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
		throw new UnsupportedOperationException();
	}


	public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
		throw new UnsupportedOperationException();
	}


	public List<org.flowable.idm.api.User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		List<org.flowable.idm.api.User> userList = new ArrayList<org.flowable.idm.api.User>();
		UserQueryImpl userQuery = (UserQueryImpl) query;
		if (!StringUtils.isEmpty(userQuery.getId())) {
			userList.add(findUserById(userQuery.getId()));
			return userList;
		} else if (!StringUtils.isEmpty(userQuery.getEmail())) {
			userList.add(findUserByEmail(userQuery.getEmail()));
			return userList;
		} else {
			Set<IUser<Long>> liferayUsers;
			try {
				liferayUsers = authorizationService.getAllUsers();
				for (IUser<Long> liferayUser : liferayUsers) {
					userList.add(getActivitiUser(liferayUser));
				}
				return userList;
			} catch (UserManagementException e) {
				ActivitiUsersLogger.errorMessage(this.getClass().getName(), e);
			}
		}
		return null;
	}

	@Override
	public long findUserCountByQueryCriteria(UserQueryImpl query) {
		return findUserByQueryCriteria(query, null).size();
	}

	@Override
	public UserQuery createNewUserQuery() {
		return null;
	}

	@Override
	public Boolean checkPassword(String s, String s1, PasswordEncoder passwordEncoder, PasswordSalt passwordSalt) {
		return null;
	}

	@Override
	public List<org.flowable.idm.api.User> findUsersByNativeQuery(Map<String, Object> map) {
		return null;
	}


	public List<org.flowable.idm.api.User> findPotentialStarterUsers(String proceDefId) {
		throw new UnsupportedOperationException();
	}


	public List<org.flowable.idm.api.User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
		throw new UnsupportedOperationException();
	}


	public Picture getUserPicture(String userId) {
		return null;

	}

	public void setUserPicture(String userId, Picture picture) {
		throw new UnsupportedOperationException();
	}

	public IAuthorizationService<Long, Long, Long> getAuthorizationService() {
		return authorizationService;
	}

	public void setAuthorizationService(IAuthorizationService<Long, Long, Long> authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public UserEntity create() {
		return null;
	}

	@Override
	public UserEntity findById(String s) {
		return null;
	}

	@Override
	public void insert(UserEntity entity) {

	}

	@Override
	public void insert(UserEntity entity, boolean b) {

	}

	@Override
	public UserEntity update(UserEntity entity) {
		return null;
	}

	@Override
	public UserEntity update(UserEntity entity, boolean b) {
		return null;
	}

	@Override
	public void delete(String s) {

	}

	@Override
	public void delete(UserEntity entity) {

	}

	@Override
	public void delete(UserEntity entity, boolean b) {

	}
}
