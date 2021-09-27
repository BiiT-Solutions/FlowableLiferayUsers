package com.biit.activiti.groups;

public enum GroupType {

	SECURITY_ROLE("security-role"),

	ASSIGNMENT("assignment");

	private String type;

	GroupType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static GroupType getGroupType(String type) {
		for (GroupType groupType : GroupType.values()) {
			if (groupType.getType().equals(type)) {
				return groupType;
			}
		}
		return null;
	}

}
