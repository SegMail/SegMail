package eds.component.user;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@RunWith(Arquillian.class)
public class UserServiceIntegrationGUITest {

	private static final By USERNAME_FIELD = By.id("form-user-login:login-username");
	private static final By PASSWORD_FIELD = By.id("form-user-login:login-password");

	private static final By LOGIN_BUTTON = By.id("form-user-login:login-button");

	private static final By USER_TYPE_NAME = By.id("createUsertypeForm:usertypeName");
	private static final By USER_TYPE_BUTTON_CREATE = By.id("createUsertypeForm:createUserTypeButton");

	private static final By USER_TYPE_SELECT_LIST = By.xpath("//button[@id='createUserForm:userType']");
	private static final By USER_TYPE_SELECT_ELEMENT_TEST = By
			.id("(//ul[@role='menu']/li[a[span[text()='Usertype test']]])[1]");

	private static final By USER_CREATE_NAME_INPUT = By.id("createUserForm:username");
	private static final By USER_CREATE_PASSWORD = By.id("createUserForm:password");
	private static final By USER_CREATE_BUTTON = By.id("createUserForm:createUserButton");

	private static final By USER_LOGIN_NAME_INPUT = By.id("loginUserForm:username");
	private static final By USER_LOGIN_PASSWORD_INPUT = By.id("loginUserForm:password");
	private static final By USER_LOGIN_BUTTON = By.id("loginUserForm:loginUserButton");

	private static final By LOGGED_IN = By.xpath("//*[@id='loginUserForm:j_idt121']/div/strong");

	@ArquillianResource
	URL contextPath;

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws Exception {

		File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importCompileAndRuntimeDependencies().resolve()
				.withMavenCentralRepo(false).withTransitivity().asFile();
		File[] libs2 = Maven.resolver().loadPomFromFile("pom.xml").resolve("junit:junit").withTransitivity().as(File.class);
		List<File> libsList = new ArrayList(Arrays.asList(libs));

		List<String> exclusionList = Arrays.asList(
		// "joda-time"
		// , "commons-fileupload"
		);
		excludeFiles(libsList, exclusionList);
		WebArchive war = ShrinkWrap.create(WebArchive.class)
				// add classes
				.addPackages(true, "eds.component.client", "eds.component.layout", "eds.component.link",
						"eds.component.mail", "eds.component.navigation", "eds.component.program", "eds.component.user",
						"eds.entity.client", "eds.entity.layout", "eds.entity.mail", "eds.entity.navigation",
						"eds.entity.program", "eds.entity.resource", "eds.entity.user", "seca2.bootstrap", "seca2.jsf",
						"seca2.program", "seca2.template", "segmail.bootstrap.module.subscription",
						"segmail.component.subscription", "segmail.entity.subscription", "segmail.program",
						"talent.component", "talent.entity", "talent.program")
				.addClasses(WebArchive.class)

		// add configuration
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/ejb-jar.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/glassfish-web.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/seca2-s2.taglib.xml"))
				// add pages
				.addAsResource(new File("src/main/resources/seca2/selectOneMenuBootstrap.xhtml"),
						"seca2/selectOneMenuBootstrap.xhtml")
				.addAsWebResource(new File("src/main/webapp/index.xhtml"))
				.addAsWebResource(new File("src/main/webapp/fix-ajax-loader.css"))
				.addAsWebResource(new File("src/main/webapp/fix_f_ajax.js"))
				.setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));

		for (File file : libsList.toArray(new File[libsList.size()])) {
			war.addAsLibrary(file);
		}
		war.addAsLibraries(libs2);

		addFiles(war, new File("src/main/webapp/programs/autoemail"));
		addFiles(war, new File("src/main/webapp/programs/chartjs"));
		addFiles(war, new File("src/main/webapp/programs/error"));
		addFiles(war, new File("src/main/webapp/programs/file"));
		addFiles(war, new File("src/main/webapp/programs/list"));
		addFiles(war, new File("src/main/webapp/programs/menu"));
		addFiles(war, new File("src/main/webapp/programs/mysettings"));
		addFiles(war, new File("src/main/webapp/programs/orgchart"));
		addFiles(war, new File("src/main/webapp/programs/profile"));
		addFiles(war, new File("src/main/webapp/programs/program"));
		addFiles(war, new File("src/main/webapp/programs/sendmail"));
		addFiles(war, new File("src/main/webapp/programs/signup"));
		addFiles(war, new File("src/main/webapp/programs/talentattributes"));
		addFiles(war, new File("src/main/webapp/programs/test"));
		addFiles(war, new File("src/main/webapp/programs/user"));
		addFiles(war, new File("src/main/webapp/templates"));
		return war;
	}

	@Drone
	WebDriver driver;

	@Test
	@ApplyScriptBefore("IntegraionGUIUserCleanUp.sql")
	public void login() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 5);

		String userName = "sadmin";
		String password = "sadmin";
		String userNameNew = "userTest";
		String passwordNew = "password";
		String userType = "test";
		String jqueryScript = "$('#createUserForm > div:nth-child(2) > div > div > ul > li.selected').removeAttr('class'); optionTest = $('span').filter(function () { return $(this).html() == 'Usertype test'; })[1].parentElement.parentElement;  $(optionTest).attr('class', 'selected');";

		driver.manage().timeouts().setScriptTimeout(Integer.valueOf(10), TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(Integer.valueOf(10), TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(Integer.valueOf(10), TimeUnit.SECONDS);

		driver.get(contextPath.toString());
		driver.findElement(USERNAME_FIELD).sendKeys(userName);
		driver.findElement(PASSWORD_FIELD).sendKeys(password);
		driver.findElement(LOGIN_BUTTON).click();
		wait.until(ExpectedConditions.elementToBeClickable(USER_TYPE_BUTTON_CREATE));
		driver.findElement(USER_TYPE_NAME).sendKeys(userType);
		wait.until(ExpectedConditions.elementToBeClickable(USER_TYPE_BUTTON_CREATE));
		driver.findElement(USER_TYPE_BUTTON_CREATE).click();

		wait.until(ExpectedConditions.elementToBeClickable(USER_CREATE_BUTTON));
		Thread.sleep(700);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(jqueryScript);
		driver.findElement(USER_CREATE_NAME_INPUT).sendKeys(userNameNew);
		driver.findElement(USER_CREATE_PASSWORD).sendKeys(passwordNew);
		wait.until(ExpectedConditions.elementToBeClickable(USER_CREATE_BUTTON));
		driver.findElement(USER_CREATE_BUTTON).click();
		wait.until(ExpectedConditions.elementToBeClickable(USER_LOGIN_BUTTON));
		driver.findElement(USER_LOGIN_NAME_INPUT).sendKeys(userNameNew);
		Thread.sleep(700);
		driver.findElement(USER_LOGIN_PASSWORD_INPUT).sendKeys(passwordNew);
		Thread.sleep(700);
		driver.findElement(USER_LOGIN_BUTTON).click();
		waitGui().withTimeout(15, TimeUnit.SECONDS).until().element(LOGGED_IN).is().visible();
		assertEquals("Login successful!", driver.findElement(LOGGED_IN).getText());
	}

	private static void addFiles(WebArchive war, File dir) throws Exception {
		if (!dir.isDirectory()) {
			throw new Exception("not a directory");
		}
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				war.addAsWebResource(f, f.getPath().replace("\\", "/").substring("src/main/webapp/".length()));
			} else {
				addFiles(war, f);
			}
		}
	}

	private static void excludeFiles(List<File> deps, List<String> excl) {
		List<File> exclToBeDeleted = new ArrayList();
		for (String exclusion : excl) {
			for (File file : deps) {
				if (file.toPath().toString().contains(exclusion)) {
					exclToBeDeleted.add(file);
				}
			}
		}
		for (File file : exclToBeDeleted) {
			deps.remove(file);

		}
	}

}
