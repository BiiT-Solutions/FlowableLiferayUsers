package com.biit.flowable.groups;

/*-
 * #%L
 * Liferay users in Flowable
 * %%
 * Copyright (C) 2021 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.usermanager.entity.IRole;

import java.util.Set;

/**
 * This class must translate from Liferay Roles to Flowable groups.
 */
public interface IGroupToActivityRoleConverter {

    /**
     * Gets the Flowable GroupType equivalence from a Liferay role.
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
     * Gets the Liferay equivalence role from a Flowable group. Must exactly do the inverse process of
     *
     * @param flowableGroupName name of the flowable group
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
     * Gets all available roles from Liferay that are related to an Flowable GroupType. The list only must include all
     * roles that can be use with the application.
     *
     * @param type GroupType
     * @return Set of all roles from liferay related to a GroupType
     */
    Set<IRole<Long>> getRoles(GroupType type);

}
