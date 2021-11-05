package com.biit.flowable.process;

//import org.flowable.engine.delegate.DelegateTask;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.engine.common.api.delegate.Expression;
//import org.flowable.engine.delegate.Expression;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.common.impl.identity.Authentication;
//import org.flowable.engine.impl.identity.Authentication;

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
