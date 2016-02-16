package eds.component.user;

import static org.junit.Assert.assertNotNull;

import javax.ejb.EJB;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.entity.user.User;
import eds.entity.user.UserAccount;
import eds.entity.user.UserType;
@RunWith(Arquillian.class)
public class UserServiceTestIntegration {
	
	@EJB
	private UserService userService;
	
	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap.create(JavaArchive.class, "UserServiceEJB.jar")
				.addAsManifestResource("test-persistence.xml", "persistence.xml")
				.addAsManifestResource("glassfish-resources.xml")
				.addClasses(UserService.class, GenericObjectService.class, UserType.class,User.class,UserAccount.class);
	}
	
	@Test
	@ApplyScriptAfter("scripts/IC1CleanUP.sql")
	@Cleanup(phase=TestExecutionPhase.NONE)
	@Transactional(value=TransactionMode.DISABLED)
	public void testIC1() throws DBConnectionException, UserTypeException, UserRegistrationException, UserAccountLockedException, UserLoginException{
		String expectedUserTypeName="expectedUserType";
		String expectedUserTypeDescription="expectedUserTypeDescription";
		String expectedUserName="expectedUserName";
		String expectedUserPassword="expectedPassword";
		userService.createUserType(expectedUserTypeName, expectedUserTypeDescription);
		UserType userType=userService.getSingleUserTypeByName(expectedUserTypeName);
		long expectedUserTypeId=userType.getOBJECTID();
		userService.registerUserByUserTypeId(expectedUserTypeId, expectedUserName, expectedUserPassword);
		User user=userService.login(expectedUserName, expectedUserPassword);
		assertNotNull(user);
		EntityManager em=userService.getEm();
		
	}
}
