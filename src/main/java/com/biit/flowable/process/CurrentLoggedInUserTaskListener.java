package com.biit.flowable.process;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class CurrentLoggedInUserTaskListener implements TaskListener {
    private static final long serialVersionUID = 814372315504639168L;
    private Expression variableName;

    @Override
    public void notify(DelegateTask delegateTask) {
        String currentLoggedInUser = Authentication.getAuthenticatedUserId();
        String targetVariable = (String) variableName.getValue(delegateTask);

        delegateTask.setVariable(targetVariable, currentLoggedInUser);
    }

    public void setVariableName(Expression variableName) {
        this.variableName = variableName;
    }
}
