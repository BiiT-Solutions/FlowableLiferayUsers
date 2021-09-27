package com.biit.activiti.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.biit.usermanager.entity.IRole;
import com.biit.usermanager.security.IActivity;
import com.biit.usermanager.security.IRoleActivities;

@Service
public class RoleActivities implements IRoleActivities {

	@Override
	public Set<IActivity> getRoleActivities(IRole<Long> arg0) {
		return new HashSet<>();
	}

}
