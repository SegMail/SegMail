package eds.component.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
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
import eds.entity.user.UserType;

@RunWith(Arquillian.class)
public class UserServiceTest {

	@EJB
	private UserService userService;

	// static EntityManager entityManager;
	//
	// @BeforeClass
	// public static void initEntityManger() {
	//// EntityManagerFactory emf =
	// Persistence.createEntityManagerFactory("HIBERNATE");
	////
	//// entityManager = emf.createEntityManager();
	//// emf.close();
	// }
	//
	// @AfterClass
	// public static void closeEntityManager() {
	// entityManager.close();
	// }

	@Deployment
	public static JavaArchive createTestArchive() {
		return ShrinkWrap.create(JavaArchive.class, "UserServiceEJB.jar")
				.addAsManifestResource("test-persistence.xml", "persistence.xml")
				.addAsManifestResource("glassfish-resources.xml")
				.addClasses(UserService.class, GenericObjectService.class, UserType.class);
	}

	@Test(expected = UserTypeException.class)
	public void testCreateUserTypeUserTypeException() throws DBConnectionException, UserTypeException {
		userService.createUserType(null, "test descr");
	}

	@Test
	@ApplyScriptBefore("scripts/UserTypeCleanUP.sql")
	@Cleanup(phase = TestExecutionPhase.NONE)
	@Transactional(value = TransactionMode.DISABLED)
	public void testDBCon() throws DBConnectionException, UserTypeException, NotSupportedException, SystemException {
		String userTypeNameExpected = "user type name test";
		String userTypeDescriptionExpected = "user type descr test";
		UserType userTypeAbsent = userService.getSingleUserTypeByName(userTypeNameExpected);
		assertNull(userTypeAbsent);
		userService.createUserType(userTypeNameExpected, userTypeDescriptionExpected);
		EntityManager em = userService.getEm();
		Query query = em.createQuery("SELECT ut FROM UserType ut WHERE ut.USERTYPENAME =:arg1 ");
		query.setParameter("arg1", userTypeNameExpected);
		assertNotNull(query);
		List<UserType> resultList = query.getResultList();
		assertEquals(resultList.size(), 1);
		UserType userTypeActual = resultList.get(0);
		assertNotNull(userTypeActual);
		assertEquals(userTypeNameExpected, userTypeActual.getUSERTYPENAME());
		assertEquals(userTypeDescriptionExpected, userTypeActual.getDESCRIPTION());
	}

	@Test
	@Cleanup(phase = TestExecutionPhase.NONE)
	@Transactional(value=TransactionMode.DISABLED)
	public void testGetSingleUserTypeByName() {

		String expectedDescription = null;
		boolean expectedPortalAcces = false;
		String expectedUserTypeName = "Segmail Administrator";
		boolean expectedWSAccess = false;
		
		UserType userTypeActual = userService.getSingleUserTypeByName(expectedUserTypeName);
		assertNotNull(userTypeActual);
		assertEquals(expectedDescription, userTypeActual.getDESCRIPTION());
		assertEquals(expectedPortalAcces, userTypeActual.isPORTAL_ACCESS());
		assertEquals(expectedUserTypeName, userTypeActual.getUSERTYPENAME());
		assertEquals(expectedWSAccess, userTypeActual.isWS_ACCESS());

	}
	

}
