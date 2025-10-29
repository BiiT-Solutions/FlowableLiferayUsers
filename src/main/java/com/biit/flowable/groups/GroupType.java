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
