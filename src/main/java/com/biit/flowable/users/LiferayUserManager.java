package com.biit.flowable.users;


import com.biit.flowable.groups.FlowableGroupManager;
import com.biit.flowable.groups.IGroupToActivityRoleConverter;
import com.biit.flowable.logger.FlowableUsersLogger;
import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.entity.IUser;
import com.biit.usermanager.security.IActivityManager;
import com.biit.usermanager.security.IAuthenticationService;
import com.biit.usermanager.security.IAuthorizationService;
import com.biit.usermanager.security.exceptions.AuthenticationRequired;
import com.biit.usermanager.security.exceptions.InvalidCredentialsException;
import com.biit.usermanager.security.exceptions.UserDoesNotExistException;
import com.biit.usermanager.security.exceptions.UserManagementException;
import com.liferay.portal.model.User;
import org.flowable.common.engine.impl.Page;
import org.flowable.common.engine.impl.interceptor.Session;
import org.flowable.idm.api.PasswordEncoder;
import org.flowable.idm.api.PasswordSalt;
import org.flowable.idm.api.Picture;
import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.impl.UserQueryImpl;
import org.flowable.idm.engine.impl.persistence.entity.IdentityInfoEntity;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityManager;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Allows the use of Liferay User in Flowable.
 */
public class LiferayUserManager implements UserEntityManager, Session {
    private IAuthorizationService<Long, Long, Long> authorizationService;
    private final IAuthenticationService<Long, Long> authenticationService;
    private final IGroupToActivityRoleConverter groupToActivityConverter;


    public LiferayUserManager(IAuthorizationService<Long, Long, Long> authorizationService, IAuthenticationService<Long, Long> authenticationService,
                              IGroupToActivityRoleConverter groupToActivityConverter) {
        this.authorizationService = authorizationService;
        this.authenticationService = authenticationService;
        this.groupToActivityConverter = groupToActivityConverter;
    }


    public static UserEntity getFlowableUser(IUser<Long> liferayUser) {
        if (liferayUser instanceof User) {
            return getFlowableUser((User) liferayUser);
        }
        return null;
    }

    public static UserEntity getFlowableUser(User liferayUser) {
        if (liferayUser == null) {
            return null;
        }
        UserEntityImpl flowableUser = new UserEntityImpl();
        flowableUser.setEmail(liferayUser.getEmailAddress());
        flowableUser.setFirstName(liferayUser.getFirstName());
        flowableUser.setId(liferayUser.getUniqueId() + "");
        flowableUser.setLastName(liferayUser.getLastName());
        flowableUser.setPassword(liferayUser.getPassword());
        flowableUser.setPicture(new Picture(null, null));
        flowableUser.setRevision(0);

        return flowableUser;
    }


    public UserEntity findUserById(String userId) {
        try {
            IUser<Long> liferayUser = authenticationService.getUserById(Long.parseLong(userId));
            return getFlowableUser(liferayUser);
        } catch (NumberFormatException | UserManagementException | UserDoesNotExistException |
                 InvalidCredentialsException e) {
            FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
        }
        return null;
    }

    private UserEntity findUserByEmail(String userEmail) {
        try {
            IUser<Long> liferayUser = authenticationService.getUserByEmail(userEmail);
            return getFlowableUser(liferayUser);
        } catch (NumberFormatException | UserManagementException | UserDoesNotExistException |
                 InvalidCredentialsException e) {
            FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void deletePicture(org.flowable.idm.api.User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<org.flowable.idm.api.User> findUsersByPrivilegeId(String s) {
        throw new UnsupportedOperationException();
    }


    public List<org.flowable.idm.api.Group> findGroupsByUser(String userId) {
        List<org.flowable.idm.api.Group> flowableGroups = new ArrayList<>();

        IUser<Long> liferayUser;
        try {
            liferayUser = authenticationService.getUserById(Long.parseLong(userId));
            Set<IRole<Long>> liferayRoles = authorizationService.getUserRoles(liferayUser);
            for (IRole<Long> liferayRole : liferayRoles) {
                flowableGroups.add(FlowableGroupManager.getFlowableGroup(liferayRole, groupToActivityConverter));
            }
        } catch (NumberFormatException | UserManagementException | UserDoesNotExistException |
                 InvalidCredentialsException e) {
            FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
        }
        return flowableGroups;
    }


    public Boolean checkPassword(String userId, String password) {
        IUser<Long> liferayUser;
        try {
            liferayUser = authenticationService.getUserById(Long.parseLong(userId));
            return authenticationService.authenticate(liferayUser.getEmailAddress(), password) != null;
        } catch (NumberFormatException | InvalidCredentialsException | UserManagementException | AuthenticationRequired | UserDoesNotExistException e) {
            FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
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
    public List<org.flowable.idm.api.User> findUserByQueryCriteria(UserQueryImpl query) {
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
                    userList.add(getFlowableUser(liferayUser));
                }
                return userList;
            } catch (UserManagementException | InvalidCredentialsException e) {
                FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
            }
        }
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
                    userList.add(getFlowableUser(liferayUser));
                }
                return userList;
            } catch (UserManagementException | InvalidCredentialsException e) {
                FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean checkPassword(String userId, String password, PasswordEncoder passwordEncoder, PasswordSalt passwordSalt) {
        IUser<Long> liferayUser;
        try {
            liferayUser = authenticationService.getUserById(Long.parseLong(userId));
            return authenticationService.authenticate(liferayUser.getEmailAddress(), password) != null;
        } catch (NumberFormatException | InvalidCredentialsException | UserManagementException | AuthenticationRequired | UserDoesNotExistException e) {
            FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
        }
        return false;
    }

    @Override
    public List<org.flowable.idm.api.User> findUsersByNativeQuery(Map<String, Object> map) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public UserEntity findById(String userId) {
        try {
            IUser<Long> liferayUser = authenticationService.getUserById(Long.parseLong(userId));
            return getFlowableUser(liferayUser);
        } catch (NumberFormatException | UserManagementException | UserDoesNotExistException |
                 InvalidCredentialsException e) {
            e.printStackTrace();
            FlowableUsersLogger.errorMessage(this.getClass().getName(), e);
        }
        return null;
    }

    @Override
    public void insert(UserEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(UserEntity entity, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserEntity update(UserEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserEntity update(UserEntity entity, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(UserEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(UserEntity entity, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
