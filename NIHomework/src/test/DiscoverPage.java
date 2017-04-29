package test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * 
 * @author Melinda, Gáborné Darvasi
 *
 */
public class DiscoverPage {

	private WebDriver driver;

	@Before
	public void init() {
		File pathBinary = new File(Keys.BROWSER_URL);
		FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		driver = new FirefoxDriver(firefoxBinary, firefoxProfile);

		Properties prop = new Properties();

		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(Keys.CONFIG_FILE)) {
			prop.load(is);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void finish() {
		driver.quit();
	}

	@Test
	public void collectLinks() {

		List<WebElement> linkList = driver.findElements(By.tagName("a"));
		Iterator it = linkList.iterator();

		System.out.println(linkList.size());
		WebElement e;
		while (it.hasNext()) {
			e = (WebElement) it.next();
			System.out.println(e.getAttribute("outerHTML"));
		}
	}
}
