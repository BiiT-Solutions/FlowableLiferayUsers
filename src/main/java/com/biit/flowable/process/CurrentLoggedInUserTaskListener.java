package com.biit.flowable.process;

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

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class CurrentLoggedInUserTaskListener implements TaskListener {
    private static final long serialVersionUID = 814372315504639168L;
    private Expression variableName;

    @Override
    public void notify(DelegateTask delegateTask) {
        final String currentLoggedInUser = Authentication.getAuthenticatedUserId();
        final String targetVariable = (String) variableName.getValue(delegateTask);

        delegateTask.setVariable(targetVariable, currentLoggedInUser);
    }

    public void setVariableName(Expression variableName) {
        this.variableName = variableName;
    }
}
