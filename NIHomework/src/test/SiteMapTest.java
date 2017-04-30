package test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
public class SiteMapTest {

	/**
	 * 
	 */
	private WebDriver driver;

	/**
	 * 
	 */
	private int depth;

	/**
	 * 
	 */
	private String url;

	/**
	 * 
	 */
	private Output out;

	/**
	 * 
	 */
	@Before
	public void init() {
		// init Selenium Webdriver
		File pathBinary = new File(Keys.BROWSER_URL);
		FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		driver = new FirefoxDriver(firefoxBinary, firefoxProfile);

		// load config.properties file
		Properties prop = new Properties();
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(Keys.CONFIG_FILE)) {
			prop.load(is);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// init output file
		out = new Output(Keys.OUTPUT_FILE);

		// load config params from properties file
		depth = Integer.parseInt(prop.getProperty(Keys.DEPTH));
		url = prop.getProperty(Keys.URL);
	}

	@After
	public void finish() {
		// close output file
		out.close();
		// close Selenium Webdriver
		driver.quit();
	}

	/**
	 * If depth is 0, then just the first page is loaded, the links of this page
	 * will be print out to the output file.
	 * 
	 */
	@Test
	public void siteMapTest() {
		collectLinks(url, 0);
	}

	/**
	 * This method load the page of the param url and collect links from this
	 * page.
	 * 
	 * @param url
	 * @param level
	 */
	public void collectLinks(final String url, final int level) {

		if (level == depth) {
			// leaf node - print out without size then end the recursion
			// print out link and linkList size
			out.write(",,," + level + url + ",,\n");
			return;
		}

		// branch node, gotta visit

		driver.get(url);
		List<WebElement> linkList = driver.findElements(By.tagName("a"));

		// print out link and linkList size
		out.write(",,," + level + url + linkList.size() + ",,\n");

		List<String> urlList = new ArrayList<>();
		// linkList.stream().map(e ->
		// e.getAttribute("href")).forEach(urlList::add);

		for (WebElement e : linkList) {
			urlList.add(e.getAttribute("href"));
		}

		// List<String> urlList2 = linkList.stream().map(e ->
		// e.getAttribute("href")).collect(Collectors.toList());

		for (String childUrl : urlList) {
			collectLinks(childUrl, level + 1);
		}
	}
}
