package com.biit.activiti.tests;


import org.flowable.engine.IdentityService;
import org.flowable.engine.ProcessEngine;
//import org.flowable.engine.identity.User;
import org.flowable.idm.api.User;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
@Test(groups = "activitiUsers")
public class ActivitiUsersTest extends AbstractTransactionalTestNGSpringContextTests {
	private final static String USER_ID = "23376";
	private final static String USER_EMAIL = "sam@test.com";
	private final static String USER_FIRST_NAME = "Sam";
	private final static String USER_LAST_NAME = "Max";

	@Inject
	private ProcessEngine processEngine;

	@Test
	public void getLiferayUserAsActiviti() {
		Assert.assertNotNull(processEngine);
		IdentityService identityService = processEngine.getIdentityService();
		Assert.assertNotNull(identityService);

		User user = identityService.createUserQuery().userId(USER_ID).singleResult();
		Assert.assertNotNull(user);
		Assert.assertEquals(user.getFirstName(), USER_FIRST_NAME);
		Assert.assertEquals(user.getLastName(), USER_LAST_NAME);
	}

	@Test
	public void getLiferayUserAsActivitiByEmail() {
		Assert.assertNotNull(processEngine);
		IdentityService identityService = processEngine.getIdentityService();
		Assert.assertNotNull(identityService);

		User user = identityService.createUserQuery().userEmail(USER_EMAIL).singleResult();
		Assert.assertNotNull(user);
		Assert.assertEquals(user.getFirstName(), USER_FIRST_NAME);
		Assert.assertEquals(user.getLastName(), USER_LAST_NAME);
	}

}
