package eds.component.user;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptAfter;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.experimental.categories.Category;
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
public class UserServiceIntegrationTest {

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

	@Test
	@ApplyScriptAfter("scripts/IC1CleanUP.sql")
	@Cleanup(phase = TestExecutionPhase.NONE)
	@Transactional(value = TransactionMode.DISABLED)
	public void testIC1() throws DBConnectionException, UserTypeException, UserRegistrationException,
			UserAccountLockedException, UserLoginException {
		String expectedUserTypeName = "expectedUserType";
		String expectedUserTypeDescription = "expectedUserTypeDescription";
		String expectedUserName = "expectedUserName";
		String expectedUserPassword = "expectedPassword";
		userService.createUserType(expectedUserTypeName, expectedUserTypeDescription);
		UserType userType = userService.getSingleUserTypeByName(expectedUserTypeName);
		long expectedUserTypeId = userType.getOBJECTID();
		userService.registerUserByUserTypeId(expectedUserTypeId, expectedUserName, expectedUserPassword);
		User user = userService.login(expectedUserName, expectedUserPassword);
		assertNotNull(user);

	}
}
