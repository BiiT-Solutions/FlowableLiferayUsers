package com.biit.flowable.security;

import java.util.List;
import java.util.Set;

//import org.flowable.engine.identity.User;
import org.flowable.idm.api.User;

import com.liferay.portal.model.Role;

public interface FlowableLiferayAuthorizationService {

	Set<Role> AgetLiferayUserRoles(User liferayUser);

	List<User> getAllLiferayUsers();

}
