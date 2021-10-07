package com.biit.flowable.groups;

import java.util.Set;

import com.biit.usermanager.entity.IRole;

/**
 * This class must translate from Liferay Roles to Activiti groups.
 */
public interface IGroupToActivityRoleConverter {

	/**
	 * Gets the Activiti GroupType equivalence from a Liferay role.
	 * 
	 * @param liferayRole liferayRole
	 * @return GroupType
	 */
	GroupType getFlowableGroup(IRole<Long> liferayRole);

	/**
	 * Creates a unique group name from a Liferay role. This can be the role name, or in the case of multitenancy, a
	 * composition of the role name and the organization name.
	 * 
	 * @param liferayRole role from liferay
	 * @return unique group name
	 */
	String getGroupName(IRole<Long> liferayRole);

	/**
	 * Gets the Liferay equivalence role from an Activiti group. Must exactly do the inverse process of
	 * {@link #getGroupName(IRole<Long>), getGroupName}.
	 * 
	 * @param flowableGroupName name of the activiti group
	 * @return string that represents the equivalence role from an activity group
	 */
	String getRoleName(String flowableGroupName);

	/**
	 * Gets all available roles from Liferay. The list only must include all roles that can be use with the application.
	 * 
	 * @return set of all roles from liferay.
	 */
	Set<IRole<Long>> getAllRoles();

	/**
	 * Gets all available roles from Liferay that are related to an Activiti GroupType. The list only must include all
	 * roles that can be use with the application.
	 * 
	 * @param type GroupType 
	 * @return Set of all roles from liferay related to a GroupType
	 */
	Set<IRole<Long>> getRoles(GroupType type);

}
