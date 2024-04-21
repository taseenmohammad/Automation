package Base;

import Pages.DashboardPage;
import Pages.LoginPage;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class TestBase {
    //  Logger log = LogManager.getLogger(TestBase.class.getName());
    WebDriver driver;

    //protected modifer is used so that any class that extends this class can use the object references.
    protected LoginPage loginPage;
    protected DashboardPage dashboardPage;
    public Properties prop;
    TestBase testBase;

    @BeforeTest
    @Parameters({"useCloudEnv", "envName", "os", "osVersion", "browserName", "browserVersion", "url"})

    public void setUp(@Optional("false") String useCloudEnv,
                      @Optional("browserstack") String envName,
                      @Optional("windows") String os,
                      @Optional("10") String osVersion,
                      @Optional("chrome") String browserName,
                      @Optional("110") String browserVersion,
                      @Optional("https://opensource-demo.orangehrmlive.com") String url) throws MalformedURLException, InterruptedException {


        if (useCloudEnv.equalsIgnoreCase("true")) {
            driver = getCloudDriver(envName, os, osVersion, browserName, browserVersion, "browserstackUsername", "browserstackPassword");
        } else if (useCloudEnv.equalsIgnoreCase("false")) {
            driver = getLocalDriver(browserName);
        } else {
            throw new IllegalArgumentException("Invalid useCloudEnv value: " + useCloudEnv);
        }

        testBase = new TestBase();
        prop = testBase.initProperties();

        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.get(url);
        Thread.sleep(5000);
        loginPage = new LoginPage(driver);

    }


    //Tear Down

    @AfterTest
    public void tearDown() {
        //close browser
        driver.quit();
        //log.info("browser close success");
    }


    //Local Driver Initialization
    public WebDriver getLocalDriver(String browserName) {
        if (browserName.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
            //log.info("chrome browser opened successfully");
        } else if (browserName.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
            //log.info("firefox browser opened successfully");
        } else if (browserName.equalsIgnoreCase("edge")) {
            driver = new EdgeDriver();
            //log.info("edge browser opened successfully");
        }
        return driver;
    }


    // Cloud Driver Initialization
    public WebDriver getCloudDriver(String envName, String os, String osVersion, String browserName, String browserVersion, String username, String password) throws MalformedURLException {
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("os", os);
        cap.setCapability("os_Version", osVersion);
        cap.setCapability("browser", browserName);
        cap.setCapability("browser_Version", browserVersion);

        if (envName.equalsIgnoreCase("browserstack")) {
            cap.setCapability("resolution", "1024x768");
            driver = new RemoteWebDriver(new URL("http://" + username + ":" + password + "@hub-cloud.browserstack.com:80/wd/hub"), cap);
        } else if (envName.equalsIgnoreCase("saucelabs")) {
            driver = new RemoteWebDriver(new URL("http://" + username + ":" + password + "@ondemand.saucelabs.com:80/wd.hub"), cap);
        }
        return driver;
    }


    /**
     * Establishes connection with the config file through FileInputStream
     * Supplies the config data to the property class object
     *
     * @return Prop;
     */
    public Properties initProperties() {
        prop = new Properties();
        try {


            FileInputStream ip = new FileInputStream("./src/test/resources/config/config.properties");

            //Supply the source to the Property object
            prop.load(ip);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop;
    }


}
