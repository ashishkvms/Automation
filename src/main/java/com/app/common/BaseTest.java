package com.app.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.ThreadContext;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.app.reports.ExtentReport;
import com.app.utils.TestUtils;
import com.aventstack.extentreports.Status;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;

public class BaseTest {
	protected static ThreadLocal<AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
	protected static ThreadLocal<Properties> props = new ThreadLocal<Properties>();
	protected static ThreadLocal<String> platform = new ThreadLocal<String>();
	protected static ThreadLocal<String> dateTime = new ThreadLocal<String>();
	protected static ThreadLocal<String> deviceName = new ThreadLocal<String>();
	private static AppiumDriverLocalService server;
	TestUtils utils = new TestUtils();

	public AppiumDriver getDriver() {
		return driver.get();
	}

	public void setDriver(AppiumDriver driver2) {
		driver.set(driver2);
	}

	public Properties getProps() {
		return props.get();
	}

	public void setProps(Properties props2) {
		props.set(props2);
	}

	public String getPlatform() {
		return platform.get();
	}

	public void setPlatform(String platform2) {
		platform.set(platform2);
	}

	public String getDateTime() {
		return dateTime.get();
	}

	public void setDateTime(String dateTime2) {
		dateTime.set(dateTime2);
	}

	public String getDeviceName() {
		return deviceName.get();
	}

	public void setDeviceName(String deviceName2) {
		deviceName.set(deviceName2);
	}

	public BaseTest() {
		PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
	}

	@BeforeMethod
	public void beforeMethod() {
		startExecutionRecording();
	}

	/*
	 * stop video capturing and create *.mp4 file
	 */
	@AfterMethod
	public synchronized void afterMethod(ITestResult result) throws Exception {
		storeExecutionRecording(result);
	}

	/*
	 * close running appium server and set appium server configuration
	 */

	@BeforeSuite
	public void beforeSuite() throws Exception, Exception {
		ThreadContext.put("ROUTINGKEY", "ServerLogs");
		closeAllAppiumNode();
	}

	/*
	 * stop running appium server
	 */
	@AfterSuite
	public void afterSuite() {
		server.stop();
		utils.log().info("Appium server stopped");
	}

	/*
	 * start android driver with the desired capabilities
	 */
	@Parameters({ "emulator", "platformName", "udid", "deviceName", "systemPort", "chromeDriverPort" })
	@BeforeTest
	public void beforeTest(@Optional("androidOnly") String emulator, String platformName, String udid,
			String deviceName, @Optional("androidOnly") String systemPort,
			@Optional("androidOnly") String chromeDriverPort) throws AppiumServerHasNotBeenStartedLocallyException, Exception {
		setDateTime(utils.dateTime());
		setPlatform(platformName);
		setDeviceName(deviceName);
		URL url;
		AppiumDriver driver;

		Properties props = new Properties();
		String propFileName = "config.properties";
		props = readpropertyFile(propFileName);
		setProps(props);
		String strFile = "logs" + File.separator + platformName + "_" + deviceName;
		File logFile = new File(strFile);
		if (!logFile.exists()) {
			logFile.mkdirs();
		}
		// route logs to separate file for each thread
		ThreadContext.put("ROUTINGKEY", strFile);
		utils.log().info("log path: " + strFile);

		server = getAppiumService();
		if (!checkIfAppiumServerIsRunnning(4723)) {
			server.start();
			server.clearOutPutStreams();
			utils.log().info("Appium server started");
		} else {
			utils.log().info("Appium server already running");
		}
		
		String automationName = getProps().getProperty("androidAutomationName");
		String appPackage = getProps().getProperty("androidAppPackage");
		String appActivity = getProps().getProperty("androidAppActivity");
		
		try {
			DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
			desiredCapabilities.setCapability("platformName", platformName);
			desiredCapabilities.setCapability("deviceName", deviceName);
			desiredCapabilities.setCapability("udid", udid);
			url = new URL(getProps().getProperty("appiumURL"));
			desiredCapabilities.setCapability("automationName", automationName);
			desiredCapabilities.setCapability("appPackage", appPackage);
			desiredCapabilities.setCapability("appActivity", appActivity);
			if (emulator.equalsIgnoreCase("true")) {
				desiredCapabilities.setCapability("avd", deviceName);
				desiredCapabilities.setCapability("avdLaunchTimeout", 120000);
			}
			desiredCapabilities.setCapability("systemPort", systemPort);
			driver = new AndroidDriver(url, desiredCapabilities);
			setDriver(driver);
			utils.log().info("driver initialized: " + driver);
		} catch (Exception e) {
			utils.log().fatal("driver initialization failure. ABORT!!!\n" + e.toString());
			throw e;
		}
	}

	/*
	 * quit the open driver
	 */
	@AfterTest
	public void afterTest() {
		getDriver().quit();
	}

	/*
	 * check if appium server is running on a particular port
	 */
	public boolean checkIfAppiumServerIsRunnning(int port) throws Exception {
		boolean isAppiumServerRunning = false;
		ServerSocket socket;
		try {
			socket = new ServerSocket(port);
			socket.close();
		} catch (IOException e) {
			System.out.println("1");
			isAppiumServerRunning = true;
		} finally {
			socket = null;
		}
		return isAppiumServerRunning;
	}

	/*
	 * set appium driver local service configuration
	 */
	public AppiumDriverLocalService getAppiumService() throws IOException, InterruptedException {
		HashMap<String, String> environment = new HashMap<String, String>();
		environment.put("PATH", getProps().getProperty("path")+ System.getenv("PATH"));
		environment.put("ANDROID_HOME", getProps().getProperty("androidHome"));
		environment.put("JAVA_HOME", getProps().getProperty("javaHome"));
		String driverLoc = getProps().getProperty("driverLocation");
		String jsFileLoc = getProps().getProperty("jsFileLocation"); 
		String serverLogsFileLoc = getProps().getProperty("logFileLocation");
		return AppiumDriverLocalService
				.buildService(new AppiumServiceBuilder().usingDriverExecutable(new File(driverLoc))
						.withAppiumJS(new File(jsFileLoc)).usingPort(4723)
						.withArgument(GeneralServerFlag.SESSION_OVERRIDE).withEnvironment(environment)
						.withLogFile(new File(serverLogsFileLoc)));
	}

	/*
	 * start test execution screen recording
	 */
	public void startExecutionRecording() {
		((CanRecordScreen) getDriver()).startRecordingScreen();
	}

	/*
	 * stop screen recording and convert video file to .mp4 format
	 */
	public void storeExecutionRecording(ITestResult result) throws IOException {
		String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();

		Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();
		String dirPath = "videos" + File.separator + params.get("platformName") + "_" + params.get("deviceName")
				+ File.separator + getDateTime() + File.separator
				+ result.getTestClass().getRealClass().getSimpleName();

		File videoDir = new File(dirPath);

		synchronized (videoDir) {
			if (!videoDir.exists()) {
				videoDir.mkdirs();
			}
		}
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4");
			stream.write(Base64.decodeBase64(media));
			stream.close();
			utils.log().info("video path: " + videoDir + File.separator + result.getName() + ".mp4");
		} catch (Exception e) {
			utils.log().error("error during video capture" + e.toString());
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/*
	 * read data of .properties file
	 */
	public Properties readpropertyFile(String propFileName) {
		InputStream is = null;
		Properties prop = new Properties();
		utils.log("reading property file at path " + propFileName);
		try {
			is = getClass().getClassLoader().getResourceAsStream(propFileName);
			prop.load(is);
			is.close();
		} catch (IOException e) {
			utils.log("error in reading property file at path " + propFileName + " " + e);
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;

	}

	/*
	 * close all running appium nodes
	 */
	public void closeAllAppiumNode() throws IOException {
		Runtime rt = Runtime.getRuntime();
		try {
			Process proc = rt.exec("killall -9 node");
		} catch (Exception e) {
			utils.log("Running appium node is not stopped");
		}
	}
	
	public LinkedHashSet<String> exceuteOnTerminal(String command) {
		LinkedHashSet<String> resultSet = new LinkedHashSet<>();
		String userDir = System.getProperty("user.home");
		Runtime rt = Runtime.getRuntime();
		try {
			Process proc = rt.exec(userDir+"/"+command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String s;
			while ((s = reader.readLine()) != null) {
				System.out.println(s);
				resultSet.add(s);
			}
		} catch (IOException e) {
			utils.log("command execution on the terminal is failed >:< " + e.toString());
		}
		return resultSet;
	}

	/*
	 * execute adb commands on terminal
	 */
	public void executeADBCommand(String command, String deviceName) throws IOException, InterruptedException {
		String ADB = System.getProperty("user.home") + "/Library/Android/sdk";
		// String ADB=System.getenv("ANDROID_HOME");
		String commandToBeExecuted;
		if (deviceName.equals("")) {
			commandToBeExecuted = "/platform-tools/adb " + command;
		} else {
			commandToBeExecuted = "/platform-tools/adb -s " + deviceName + " " + command;
		}
		utils.log("command " + ADB + commandToBeExecuted);
		Process process = Runtime.getRuntime().exec(ADB + commandToBeExecuted);

		BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			utils.log(line);
		}
		process.waitFor();

	}

	/*
	 * scroll to the end of a page
	 */
	public void scrollToBottomOfPage(List<MobileElement> ele) {
		int size = 0;
		HashSet<String> title = new HashSet<>();
		size = ele.size();
		int index = size-1;
		String temp = "";
		try {
			conditionForScrolling(title, ele, index);
		}catch(Exception e) {
			scrollByPointer(getDriver(), 0.06);
			conditionForScrolling(title, ele, index);
		}
	}
	
	public void conditionForScrolling(HashSet<String> title,List<MobileElement> ele, int index) {
		while (!title.contains(ele.get(index).getText())) {
			for (MobileElement e : ele) {
				title.add(e.getText());
			}
			scrollByPointer(getDriver(), 0.06);
		}
	}

	/*
	 * drag from element1 to element2
	 */
	public void dragElement(MobileElement element1, MobileElement element2) {
		TouchAction swipe = new TouchAction(getDriver()).press(ElementOption.element(element1))
				.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2))).moveTo(ElementOption.element(element2))
				.release().perform();
	}

	/*
	 * scroll page using percentage
	 */
	public void scrollByPointer(AppiumDriver appiumDriver, double value) {
		Dimension size = appiumDriver.manage().window().getSize();
		int startx = size.width / 2;
		int starty = (int) (size.height * 0.80);
		int endy = (int) (size.height * value);
		TouchAction action = new TouchAction(appiumDriver);
		action.press(PointOption.point(startx, starty)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
				.moveTo(PointOption.point(startx, endy)).release().perform();
	}

	/*
	 * check if MobileElement is displayed on the page
	 */
	public boolean isMobileElementDisplayed(MobileElement ele) {
		boolean isDisplay;
		try {
			isDisplay = ele.isDisplayed();
			return isDisplay;
		} catch (NoSuchElementException e) {
			isDisplay = false;
			return isDisplay;
		}

	}

	/*
	 * wait for visibility of a MobileElement
	 */
	public void waitForVisibility(MobileElement e) {
		WebDriverWait wait = new WebDriverWait(getDriver(), TestUtils.WAIT);
		wait.until(ExpectedConditions.visibilityOf(e));
	}

	/*
	 * wait for visibility of a MobilElement
	 */
	public void waitForElementToBeVisible(MobileElement e) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriver()).withTimeout(Duration.ofSeconds(30))
				.pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

		wait.until(ExpectedConditions.visibilityOf(e));
	}

	/*
	 * clear the textbox
	 */
	public void clear(MobileElement e) {
		waitForVisibility(e);
		e.clear();
	}

	/*
	 * click on a MobileElement if it is visible
	 */
	public void click(MobileElement e) {
		waitForVisibility(e);
		e.click();
	}

	/*
	 * click on a MobileElement if it is visible and log the msg
	 */
	public void click(MobileElement e, String msg) {
		waitForVisibility(e);
		utils.log().info(msg);
		ExtentReport.getTest().log(Status.INFO, msg);
		e.click();
	}

	/*
	 * enter string into a textbox
	 */
	public void sendKeys(MobileElement e, String txt) {
		waitForVisibility(e);
		e.sendKeys(txt);
	}

	/*
	 * enter string into a textbox and log the msg
	 */
	public void sendKeys(MobileElement e, String txt, String msg) {
		waitForVisibility(e);
		utils.log().info(msg);
		ExtentReport.getTest().log(Status.INFO, msg);
		e.sendKeys(txt);
	}

	/*
	 * get attributes of a MobileElement
	 */
	public String getAttribute(MobileElement e, String attribute) {
		waitForVisibility(e);
		return e.getAttribute(attribute);
	}

	/*
	 * get text attribute value of a MobileElement
	 */
	public String getText(MobileElement e, String msg) {
		String txt = null;
		txt = getAttribute(e, "text");
		utils.log().info(msg + txt);
		ExtentReport.getTest().log(Status.INFO, msg+txt);
		return txt;
	}

	/*
	 * read .json test data file
	 */
	public JSONObject readTestData() throws IOException {
		JSONObject data;
		InputStream datails = null;
		try {
			String dataFileName = "data/testData.json";
			datails = getClass().getClassLoader().getResourceAsStream(dataFileName);
			JSONTokener tokener = new JSONTokener(datails);
			data = new JSONObject(tokener);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (datails != null) {
				datails.close();
			}
		}
		return data;
	}
	
	/*
	 * close the android app
	 */
	public void closeApp() {
		((InteractsWithApps) getDriver()).closeApp();
	}

	/*
	 * launch the android app
	 */
	public void launchApp() {
		((InteractsWithApps) getDriver()).launchApp();
	}

	/*
	 * reset the android app
	 */
	public void resetApp() {
		((InteractsWithApps) getDriver()).resetApp();
	}
}
