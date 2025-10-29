package com.biit.flowable.tests;

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


import org.flowable.engine.IdentityService;
import org.flowable.engine.ProcessEngine;
import org.flowable.idm.api.User;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
@Test(groups = "flowableUsers")
public class FlowableUsersTest extends AbstractTransactionalTestNGSpringContextTests {
	private final static String USER_ID = "20735";
	private final static String USER_EMAIL = "sam@test.com";
	private final static String USER_FIRST_NAME = "Sam";
	private final static String USER_LAST_NAME = "Max";

	@Autowired
	private ProcessEngine processEngine;

	@Test
	public void getLiferayUserAsFlowable() {
		Assert.assertNotNull(processEngine);
		IdentityService identityService = processEngine.getIdentityService();
		Assert.assertNotNull(identityService);

		User user = identityService.createUserQuery().userId(USER_ID).singleResult();
		Assert.assertNotNull(user);
		Assert.assertEquals(user.getFirstName(), USER_FIRST_NAME);
		Assert.assertEquals(user.getLastName(), USER_LAST_NAME);
	}

	@Test
	public void getLiferayUserAsFlowableByEmail() {
		Assert.assertNotNull(processEngine);
		IdentityService identityService =  processEngine.getIdentityService();
		Assert.assertNotNull(identityService);


		User user = identityService.createUserQuery().userEmail(USER_EMAIL).singleResult();
		Assert.assertNotNull(user);
		Assert.assertEquals(user.getFirstName(), USER_FIRST_NAME);
		Assert.assertEquals(user.getLastName(), USER_LAST_NAME);
	}

}
