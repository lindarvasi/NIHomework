package test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * 
 * The <code>SiteMapTest</code> creates a list of pages of a website accessible to users. The
 * entry point and the max level of fetching come from the config.properties file. The
 * config file also contains a regex pattern to filter which links have to be visited by the program.
 * This can be used to limit the scope of sitemap to a certain domain or even to a subset of pages.  
 * 
 * As a Unit Test, a next step could be comparing the fetched sitemap to an existing, expected output sitemap.
 * 
 * In the output file there will be a list of urls with id and parent id and the
 * number of links on each page.
 * When max level is reached, the link is not fetched, hence the number of links will not be reported.
 * 
 * If a page is accessible from multiple urls,it will be listed again,but with different parent id.
 * Such pages are only fetched at most once, i.e. fetched once if they are reachable within the max level.
 * 
 * This program is only looking for a href... javascript, css script img src - no,
 * 
 * 
 * @author Melinda, Gáborné Darvasi
 *
 */
public class SiteMapTest {

	/**
	 * Log4j Logger for this class
	 */
	private static Logger logger = LogManager.getLogger(SiteMapTest.class);

	/**
	 * Selenium webdriver
	 */
	private WebDriver driver;

	/**
	 * Max visited level
	 */
	private int depth;

	/**
	 * The entry point of the sitemap
	 */
	private String startUrl;

	/**
	 * The output file
	 */
	private SiteMapOutput out;

	/**
	 * Id for the urls will be generated from this field
	 */
	private int nextId;

	/**
	 * Regex pattern for urls
	 */
	private Pattern followIncludePattern;

	/**
	 * Map of urls with their id. Used for avoiding duplication
	 */
	private Map<String, Integer> idByUrl;

	/**
	 * Set of actually visited, fetched urls
	 */
	private Set<String> visitedUrls;

	/**
	 * Init fields
	 */
	@Before
	public void init() {
		logger.info("init started");

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
			logger.error(e.getMessage());			
		}
		
		// init output file
		out = new CsvOutput(Keys.OUTPUT_FILE);

		// init nextId field
		nextId = 0;

		// load config parameters from properties file
		depth = Integer.parseInt(prop.getProperty(Keys.FOLLOW_DEPTH));
		startUrl = prop.getProperty(Keys.URL);
		followIncludePattern = Pattern.compile(prop.getProperty(Keys.FOLLOW_INCLUDE));

		// init collections
		idByUrl = new HashMap<>();
		visitedUrls = new HashSet<>();
		logger.info("init finished");
	}

	/**
	 * Tear down, close and release resources: outputstream and webdriver
	 */
	@After
	public void tearDown() {
		logger.info("tearDown started");
		// close output file
		out.close();
		// close Selenium Webdriver
		driver.quit();
		logger.info("tearDown finished");
	}

	/**
	 * Create the sitemap recursively.
	 * 
	 * If depth value is 1, then just the first page is loaded and the links of
	 * this page will be print out to the output file.
	 * 
	 */
	@Test
	public void siteMapTest() {
		logger.info("siteMapTest started");
		// start with level = 0, parent id = -1;
		collectLinks(startUrl, 0, -1);
		// assert if sitemap is as expected
		logger.info("siteMapTest finished");
	}

	/**
	 * This method load the page of the given url and collect links from this
	 * page.
	 * 
	 * @param url
	 * @param level
	 * @param parentId	
	 */
	public void collectLinks(final String url, final int level, int parentId) {		
		logger.info("collectLinks started");
		logger.info("url:" + url);

		// check the url in the visited urls map
		Integer id = idByUrl.get(url);
		if (id == null) {
			id = nextId++;
			idByUrl.put(url, id);
		}

		// stopping condition value is false by default
		boolean stopProcessing = false;

		// stopping condition value will be true if the visited level is greater
		// than (if depth is negative int) or equal to depth's value
		stopProcessing = level >= depth;
		if (!stopProcessing) {
			// if stopProcessing condition is false then check the url is
			// visited earlier
			stopProcessing = visitedUrls.contains(url);
		}
		if (!stopProcessing) {
			// if stopProcessing condition is still false then check the url is
			// matching
			// the regex pattern from the config file
			stopProcessing = !followIncludePattern.matcher(url).matches();
		}

		if (stopProcessing) {
			// leaf node - print out without size then end the recursion
			// print out link and linkList size
			out.write(id, parentId, level, url);
			logger.info("collectLinks finished");
			return;
		}

		// branch node, gotta visit
		// add url to visitedUrls Set
		visitedUrls.add(url);

		// load the url using Selenium webdriver
		driver.get(url);

		// get all the links from the loaded page
		List<WebElement> linkList = driver.findElements(By.tagName("a"));

		// collect urls
		List<String> urlList = new ArrayList<>();
		String href;

		for (WebElement element : linkList) {
			try {
				href = element.getAttribute("href");
				if (href == null) {
					continue;
				}
				// ignoring the fragment part of the links (part after the hashtag)
				// because they refer to the same page, should not affect the sitemap.
				href = href.replaceAll("#.*$", "");

			} catch (StaleElementReferenceException e) {
				logger.error("Skipping over stale element" + element);
				continue;
			}
			// only register a link once and ignore self-links 
			if (urlList.contains(href) || url.equals(href)) {
				continue;
			}

			// add new URL to urlList List
			urlList.add(href);
		}

		// print out link and linkList size
		out.write(id,parentId,level,url,urlList.size());

		// build sitemap recursively
		for (String childUrl : urlList) {
			collectLinks(childUrl, level + 1, id);
		}
		logger.info("collectLinks finished");
	}

}
