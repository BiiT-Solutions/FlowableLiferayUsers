package com.biit.activiti.security;

import java.util.HashSet;
import java.util.Set;

import com.biit.activiti.groups.GroupType;

public enum TestRole {

	/* empty role */
	NULL("", GroupType.ASSIGNMENT),

	RECEPTIONIST("usmo_receptionist", GroupType.ASSIGNMENT),

	PHYSIOTHERAPIST("usmo_physiotherapist", GroupType.ASSIGNMENT);

	private GroupType activitiGroup;
	private String liferayName;

	TestRole(String liferayName, GroupType activitiGroup) {
		this.liferayName = liferayName;
		this.activitiGroup = activitiGroup;
	}

	public String getLiferayName() {
		return liferayName;
	}

	public GroupType getActivitiGroup() {
		return activitiGroup;
	}

	public static TestRole get(String liferayName) {
		for (TestRole role : values()) {
			if (liferayName.toLowerCase().equals(role.getLiferayName())) {
				return role;
			}
		}
		return NULL;
	}

	public static Set<TestRole> get(GroupType type) {
		Set<TestRole> roles = new HashSet<>();
		for (TestRole role : values()) {
			if (role.getActivitiGroup().equals(type)) {
				roles.add(role);
			}
		}
		return roles;
	}

}
