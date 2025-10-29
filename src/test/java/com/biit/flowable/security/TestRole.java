package com.biit.flowable.security;

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
