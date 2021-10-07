package com.biit.flowable.security;

import java.util.HashSet;
import java.util.Set;

import com.biit.flowable.groups.GroupType;

public enum TestRole {

	/* empty role */
	NULL("", GroupType.ASSIGNMENT),

	RECEPTIONIST("usmo_receptionist", GroupType.ASSIGNMENT),

	PHYSIOTHERAPIST("usmo_physiotherapist", GroupType.ASSIGNMENT);

	private GroupType flowableGroup;
	private String liferayName;

	TestRole(String liferayName, GroupType flowableGroup) {
		this.liferayName = liferayName;
		this.flowableGroup = flowableGroup;
	}

	public String getLiferayName() {
		return liferayName;
	}

	public GroupType getFlowableGroup() {
		return flowableGroup;
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
			if (role.getFlowableGroup().equals(type)) {
				roles.add(role);
			}
		}
		return roles;
	}

}
