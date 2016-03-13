package eds.component.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.webapp.filter.FileUploadFilter;

import eds.component.DBService;
import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.HibernateHelper;
import eds.entity.audit.AuditedObject;
import eds.entity.audit.AuditedObjectListener;
import eds.entity.audit.AuditedObject_;
import eds.entity.data.EnterpriseData;
import eds.entity.data.EnterpriseDataListener;
import eds.entity.data.EnterpriseData_;
import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseObjectListener;
import eds.entity.data.EnterpriseObject_;
import eds.entity.data.EnterpriseRelationship;
import eds.entity.data.EnterpriseRelationshipListener;
import eds.entity.user.APIAccount;
import eds.entity.user.User;
import eds.entity.user.UserAccount;
import eds.entity.user.UserAccount_;
import eds.entity.user.UserPreferenceSet;
import eds.entity.user.UserType;
import eds.entity.user.UserType_;

@RunWith(Arquillian.class)
public class UserServiceTest {

	@EJB
	private UserService userService;

	@Deployment
	public static WebArchive createTestArchive() {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("joda-time:joda-time", "org.slf4j:slf4j-simple").withTransitivity().as(File.class);
		File[] libsHibernate = Maven.resolver().resolve("org.hibernate:hibernate-entitymanager:5.0.1.Final",
				"org.hibernate:hibernate-core:5.0.1.Final").withTransitivity().as(File.class);
		WebArchive webArchive = ShrinkWrap.create(WebArchive.class)
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/ejb-jar.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
				.setWebXML(new File("src/main/webapp/WEB-INF/web.xml")).addClasses(UserService.class,
						GenericObjectService.class, UserType.class, User.class, UserAccount.class, DBService.class,
						DBConnectionException.class, UserTypeException.class, EnterpriseObject.class,
						UserCreationException.class, UserRegistrationException.class, AuditedObject.class,
						EnterpriseRelationship.class, UserAccountLockedException.class, UserLoginException.class,
						UserNotFoundException.class, EnterpriseData.class, FileUploadFilter.class, APIAccount.class,
						UserPreferenceSet.class, AuditedObjectListener.class, EnterpriseObjectListener.class,
						EnterpriseRelationshipListener.class, EnterpriseDataListener.class, EnterpriseObject_.class,
						UserAccount_.class, EnterpriseData_.class, AuditedObject_.class, UserType_.class,
						HibernateHelper.class);

		webArchive.addAsLibraries(libs);
		webArchive.addAsLibraries(libsHibernate);
		return webArchive;
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
		String userTypeNameExpected = "userTypeNameTest";
		String userTypeDescriptionExpected = "userTypeDescrTest";
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
	@Transactional(value = TransactionMode.DISABLED)
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
