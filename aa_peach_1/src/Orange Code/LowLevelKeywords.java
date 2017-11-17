package com.AnyAUT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.log4testng.Logger;

public class LowLevelKeywords {

	// Global Variables
	public WebDriver driver;
	Alert alert;
	int linkcount;
	String orig_win_handle;
	static Logger logger = Logger.getLogger(LowLevelKeywords.class);

	public LowLevelKeywords(WebDriver driver) {
		
		PropertyConfigurator.configure("log4j.properties");
		
		try {
			this.driver = driver;
		}catch (Exception ex) {
			logger.error("Error occured while passing the webdriver  " + ex);
		}
	}

	/// **********************Reusable function library*****************\\\
	public void openBrowser(String browserType) {
		// Purpose: Open a Browser, Timeout
		// I/P : which Browser
		// o/p : N/A
		/*
		 * Updated By:Aparna Date: 11/16/2016 Purpose:updated the .exe filepaths
		 */
		
		int browserTimeout = 0;
		String strTimeout = new UICode().returnValues("BrowserTimeOut");
		
		
		if (strTimeout.length() > 0) {
			browserTimeout = Integer.valueOf(strTimeout);
		}else {
			browserTimeout = 10;
		}
		
		try {
			switch (browserType) {
			case "Firefox":
				if (System.getProperty("os.name").contains("Windows")) {
					System.setProperty("webdriver.gecko.driver", Utilities.fileAbsolutePath() + "Browsers/Win/geckodriver.exe");
					//Thread.sleep(10000);
				}else {
					System.setProperty("webdriver.gecko.driver", Utilities.fileAbsolutePath() + "Browsers/Mac/geckodriver");
				}
				driver = new FirefoxDriver();
				break;
				
			case "Chrome":
					System.out.println("Entered Chrome browser case");
					if (System.getProperty("os.name").contains("Windows")) {
						System.setProperty("webdriver.chrome.driver", Utilities.fileAbsolutePath() + "Browsers/Win/chromedriver.exe");
						Thread.sleep(10000);
					}else {
				    	System.setProperty("webdriver.chrome.driver", Utilities.fileAbsolutePath() + "Browsers/Mac/chromedriver");
				    }
					driver = new ChromeDriver();
					System.out.println(driver);
					break;
			case "IE":
				System.setProperty("webdriver.ie.driver", Utilities.fileAbsolutePath() + "Browsers/Win/IEDriverServer.exe");
				driver = new InternetExplorerDriver();
				break;
			case "Safari":
				driver = new SafariDriver();
				break;
			case "PhantomJS":
				   System.setProperty("phantomjs.binary.path", Utilities.fileAbsolutePath() + "Browsers/Win/phantomjs.exe");  
				   driver = new PhantomJSDriver();
				break;
			default:
				driver = new ChromeDriver();
	
			}
		}catch(Exception ex) {
			logger.error("Error occured while initializing the browser drivers " + ex);
		}
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(browserTimeout, TimeUnit.SECONDS);

	}

	//3/14/2017 by Aparna- added parameters  EleType, EleLocator in the lowlevelkeyword methods
	
	public 	By getByType(String ElementType,String ElementLocator)
	{   /*Purpose: Get the elementType (id, className,cssSelector, name, Xpath, linkText and partial linkText )
		 * I/P : ElementType and ElementLocator
		 * o/p : N/A
		 * Created By:Aparna 
		 * Date: 03/14/2016 
		 */
		By byType;
		switch (ElementType) {
		case "id":
			byType=By.id(ElementLocator);
			break;
		case "className":
			byType=By.className(ElementLocator);
			break;
		case "cssSelector":
			byType = By.cssSelector(ElementLocator);
			break;
		case "name":
			byType = By.name(ElementLocator);
			break;
		case "linkText":
			byType = By.linkText(ElementLocator);
			break;
		case "xpath":
			byType = By.xpath(ElementLocator);
			break;
		case "PartialLinkText":
			byType = By.partialLinkText(ElementLocator);
			break;
		case "tagname":
			byType =By.tagName(ElementLocator);
			break;
			default:
				byType = By.xpath(ElementLocator);
		}
		return byType;
	}
	public void navigateBrowser(String URL) {
		// Purpose: Navigates a browser
		// IP: URL
		// OP: N/A
		driver.navigate().to(URL);
	}

	public void maximizeBrowser() {
		driver.manage().window().maximize();
	}

	public void closeBrowser() {
		// Purpose: Closes a Browser.
		// IP: N/A
		// OP: N/A
		// driver.close();
		driver.quit();
	}

	public void quitBrowser() {
		// Purpose: Closes all webdriver Browsers.
		// IP: N/A
		// OP: N/A
		driver.quit();
	}

	public void selectList(String EleType,String EleLocator, String text) {
		// Purpose: Select an item from a DDLB
		// IP: Xpath of the DDLB, Item to select
		// OP: N/A
		Select selectByXpath = new Select(driver.findElement(getByType(EleType,EleLocator)));
		selectByXpath.selectByVisibleText(text);
	}

	public void clickLink(String EleType,String EleLocator) {
		// Purpose: Click on a specified Link
		// IP: Link Text
		// OP: N/A
		driver.findElement(getByType(EleType,EleLocator)).click();
		//driver.findElement(By.linkText(linkText)).click();
	}

	public void typeText(String EleType,String EleLocator, String text) {
		// Purpose: Type a text into edit field
		// IP: Xpath of the element and the text to enter
		// OP: N/A
		driver.findElement(getByType(EleType,EleLocator)).clear();
		driver.findElement(getByType(EleType,EleLocator)).sendKeys(text);
	}

	public boolean verifyTitle(String title) {
		// Purpose: Verify the title of the page
		// IP: Title
		// OP: Return True or False
		if (title.equals(driver.getTitle())) {
			return true;
		} else {
			return false;
		}
	}

	public String verifyText(String EleType,String EleLocator, String text) {
		// Purpose: Verifies if a text is present
		// IP: xpath of the element and the text to verify
		// OP: true or false
		if (text.equalsIgnoreCase(driver.findElement(getByType(EleType,EleLocator)).getText())) {
			return "Pass";
		} else {
			return "Fail";
		}
	}

	public void clickButton(String EleType,String EleLocator) {
		// Purpose: Clicks a button
		// IP: Xpath of the button
		// OP: N/A
		driver.findElement(getByType(EleType,EleLocator)).click();
	}

	public void clickElement(String EleType,String EleLocator) {
		// Purpose: Clicks an element
		// IP: Xpath of the button
		// OP: N/A
		driver.findElement(getByType(EleType,EleLocator)).click();
	}
	/*
	 * Updated: AParna Date Nov 17 2016 Purpose: Commented this as we have
	 * isDisplayed method - this method is not related to click Image Pointed
	 * clickImage keyword in driver script to click Element.
	 */
	/*
	 * public boolean ImageURL(String vURL){ //Purpose: Verifies if an image is
	 * present or not using the Image's xpath
	 * if(driver.findElement(By.xpath(vURL)).isDisplayed()){ return true; }else{
	 * return false; } }
	 */

	public boolean verifyLink(String EleType,String EleLocator) {
		// Purpose: Verifies if an image is present or not using the Image's
		// xpath
		if (driver.findElement(getByType(EleType,EleLocator)).isDisplayed()) {
			return true;
		} else {
			return false;
		}

	}

	public void enterKeyboard(String EleType,String EleLocator) {
		// Purpose: Clicks enter using keyboard
		// IP: Xpath of the element
		// OP: N/A
		driver.findElement(getByType(EleType,EleLocator)).sendKeys(Keys.ENTER);
	}

	public String readText(String EleType,String EleLocator) {
		// Purpose: Reads a text from an edit field
		// IP: xpath of the element
		// OP: Text of type String
		String vOutput = driver.findElement(getByType(EleType,EleLocator)).getAttribute("value");
		return vOutput;
	}
	/// **********************Newly added Reusable function
	/// library*****************\\\

	/*
	 * Updated By: Vijaysankari Date: Nov 8 2016 Reason: More keywords Added
	 */
	public void clearText(String EleType,String EleLocator) {
		// Clears the text value of text entry element identified by locator.
		driver.findElement(getByType(EleType,EleLocator)).clear();
	}

	public void dragAndDrop(String xPath1, String xPath2) {
		// For e.g. Drag And Drop elem1,elem2 # Move elem1 over elem2.
		WebElement element = driver.findElement(By.xpath(xPath1));
		WebElement target = driver.findElement(By.xpath(xPath2));

		(new Actions(driver)).dragAndDrop(element, target).perform();
	}

	public String getTitle() {
		// get the title of Page
		String title = driver.getTitle();
		return title;
	}

	public void defaultContent() {
		// Getting back to Default Content from Iframe
		driver.switchTo().defaultContent();
	}

	public void switchToFrame(String EleType,String EleLocator) {
		// switch to frame by select frame
		WebElement frame = driver.findElement(getByType(EleType,EleLocator));

		driver.switchTo().frame(frame);
	}

	public void selectByValue(String EleType,String EleLocator, String value) {
		// Select the given values of multi select list.
		WebElement combobox = driver.findElement(getByType(EleType,EleLocator));
		Select sList = new Select(combobox);
		sList.selectByValue(value);

	}

	public void selectByIndex(String EleType,String EleLocator, String index) {
		// Select the given index of multi select list.
		int parseIndex = Integer.parseInt(index);
		WebElement mye = driver.findElement(getByType(EleType,EleLocator));
		// Pointing to a WE object which is of Type Select
		Select selectList = new Select(mye); 
		// By index is pointing to order in the drop-down list
		selectList.selectByIndex(parseIndex); 
	}

	/*
	 * Updated By: Suyash Date: Nov 16 2016 Reason: removed try/catch block from
	 * verifyalertispresent and SwitchtoAlertMethod
	 */
	public String isAlertPresent() {
		String foundAlert = "Fail";
		WebDriverWait wait = new WebDriverWait(driver,
				15 /* timeout in seconds */);
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			foundAlert = "Pass";
		} catch (TimeoutException eTO) {
			foundAlert = "Fail";
		}
		return foundAlert;

	}

	public void switchToAlert() {

		// Switch to SimpleAlert/Confirm Alert box/Prompt Alert box
		alert = driver.switchTo().alert();

	}
	
	
	/*
	 * Updated By: Suyash Date: Nov 14 2016 Reason: added parameter Data under
	 * getAlertText
	 */
	public String getAlertText(String data) {
		// This method should be used only if SwitchtoAlertMethod return true ;
		if (alert.getText().equalsIgnoreCase(data))
			return "Pass";
		// This returns the text displayed on Simple Alert/Confirm Alert box/Prompt Alert box
		else
			return "Fail";
	}

	public void dismissAlert() {
		// This function will dismiss the Simple Alert/Confirm Alert box/Prompt Alert and user can continue 
		// operations with main window
		alert.dismiss(); 
	}

	public void confirmAlert() {
		// This function will Confirm the Simple Alert/Confirm Alert box/Prompt Alert and user can continue
		// operations with main window
		alert.accept();
	}

	public void sendKeys_promptAlert(String message) {
		// This function is created to sendKeys on PromptAlert Box
		alert.sendKeys(message);
	}

	public void mouseHover(String EleType,String EleLocator) {
		WebElement menu =driver.findElement(getByType(EleType,EleLocator));
		Actions action = new Actions(driver);
		action.moveToElement(menu).build().perform();
	}

	public void contextClick(String EleType,String EleLocator) {
		// You can use this function for doing right click on any Webelement
		WebElement rightClick = driver.findElement(getByType(EleType,EleLocator));
		Actions action = new Actions(driver);
		Action a1 = action.contextClick(rightClick).build();
		a1.perform();
	}

	/*
	 * Updated By: Aparna Date: Nov 8 2016 Reason: More keywords Added
	 */
	public void dragndropbyoffset(String EleType,String EleLocator, String xoffset, String yoffset) {
		WebElement dragElementFrom = driver.findElement(getByType(EleType,EleLocator));
		int x = Integer.parseInt(xoffset);
		int y = Integer.parseInt(yoffset);
		new Actions(driver).dragAndDropBy(dragElementFrom, x, y).build().perform();
	}

	public HashMap<String, String> getWindowNames() {
		Set<String> windowHandles = driver.getWindowHandles();
		HashMap<String, String> hmap = new HashMap<String, String>();
		orig_win_handle = driver.getWindowHandle();
		// window is a string looping thru all the window handles
		for (String window : windowHandles) {
			// when window is not original then switch to the new window and get
			// the title of the new window
			if (!window.equals(orig_win_handle)) {
				driver.switchTo().window(window);
				// hmap.put(window, driver.getTitle());
				hmap.put(window, driver.getCurrentUrl());
			}
		}
		// go back to original window get the title
		driver.switchTo().window(orig_win_handle);
		// hmap.put(orig_win_handle, driver.getTitle());
		hmap.put(orig_win_handle, driver.getCurrentUrl());
		System.out.println(hmap);
		return hmap;
	}

	public void closepopupWindow() {
		driver.close();
	}

	public void switchToWindow(String url) {
		driver.switchTo().window(url);
	}

	public void waitTilElementEnable(String EleType,String EleLocator, String seconds) {
		/*
		 * Explicit wait - wait for vTDsecs until the element is clickable. If
		 * the element is not visible till the seconds specified then it throws
		 * TimeoutException occurred while waiting for element.
		 */
		WebDriverWait wait = new WebDriverWait(driver, Integer.parseInt(seconds));
		wait.until(ExpectedConditions.elementToBeClickable(getByType( EleType, EleLocator)));
	}

	public void waitTilElementNotVisible(String EleType,String EleLocator, String seconds) {
		/*
		 * Wait until any element is not visible or hidden- example modal dialog
		 * disappears so perform operations in the form
		 */
		WebDriverWait wait = new WebDriverWait(driver, Integer.parseInt(seconds));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(getByType( EleType, EleLocator)));
	}

	public void waitTilElementIsVisible(String EleType,String EleLocator, String seconds) {
		WebDriverWait wait = new WebDriverWait(driver, Integer.parseInt(seconds));
		wait.until(ExpectedConditions.visibilityOfElementLocated(getByType( EleType, EleLocator)));
	}

	public void waitTilElementTextPresent(String EleType,String EleLocator, String text) {
		// Wait till text is present - we know the text displayed but don't know
		// xpath/locator
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.textToBePresentInElementLocated(getByType( EleType, EleLocator), text));
	}

	public void fluentWait() {
		(((new FluentWait<WebDriver>(driver)).withTimeout(20, TimeUnit.SECONDS)).pollingEvery(10, TimeUnit.SECONDS))
				.ignoring(NoSuchElementException.class);
	}

	public void selectLinkToNewWindow(String EleType,String EleLocator) {
		WebElement Webelement =driver.findElement(getByType(EleType,EleLocator));
		Actions action = new Actions(driver);
		action.keyDown(Keys.SHIFT).click(Webelement).keyUp(Keys.SHIFT).build().perform();
	}

	/********** Extra keywords begin here *********************/
	/*
	 * Updated By: Seshi Date: Nov 8 2016 Reason: More keywords Added
	 */
	public void checkBoxclick(String EleType,String EleLocator, String text) {
		// Purpose: Verifies if the checkbox identified by locator is
		// selected/checked.
		// IP:
		// OP:
		boolean chkboxstate =driver.findElement(getByType(EleType,EleLocator)).isSelected();
		if ((text.equalsIgnoreCase("check")) && (chkboxstate == false)) {
			driver.findElement(getByType(EleType,EleLocator)).click();
		} else if ((text.equalsIgnoreCase("uncheck")) && (chkboxstate == true)) {
			driver.findElement(getByType(EleType,EleLocator)).click();
		}
	}

	public void tabKeyboard(String EleType,String EleLocator) {
		WebElement webElement = driver.findElement(getByType(EleType,EleLocator));
		webElement.sendKeys(Keys.TAB);
	}

	public void capsLockKeyword(String EleType,String EleLocator, String text) {
		// Purpose:
		// IP:
		// OP:
		Actions builder = new Actions(driver);
		WebElement e = driver.findElement(getByType(EleType,EleLocator));
		Action writeCapital = builder.keyDown(Keys.SHIFT).sendKeys(e, text).keyUp(Keys.SHIFT).build();
		writeCapital.perform();
	}

	public void implictWait(int seconds) {
		driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.MILLISECONDS);
	}

	// Updated By Roopali
	public void doubleClick(String EleType,String EleLocator) {
		// Purpose: douublicks the element
		// IP: xpath of the element
		Actions action = new Actions(driver);
		WebElement element = driver.findElement(getByType(EleType,EleLocator));
		action.doubleClick(element).perform();
	}

	public String isEnabled(String EleType,String EleLocator) {
		// Purpose: Verifies if an image is present or not using the Image's
		// xpath
		// driver.findElement(By.xpath(xPath)).isEnabled();
		if (driver.findElement(getByType(EleType,EleLocator)).isEnabled()) {
			return "Pass";
		} else {
			return "Fail";
		}
	}

	public String isDisabled(String EleType,String EleLocator) {
		// Purpose: Verifies if an image is present or not using the Image's
		// xpath
		if (!(driver.findElement(getByType(EleType,EleLocator))).isEnabled()) {
			return "Pass";
		} else {
			return "Fail";
		}
	}

	/*
	 * Updated: AParna Date 11/17/2016 Purpose: In this method else part will
	 * throw an exception so added throws Exception
	 */
	public String isDisplayed(String EleType,String EleLocator) throws Exception {
		// Purpose: Checks if element is displayed or not
		if (driver.findElement(getByType(EleType,EleLocator)).isDisplayed())
			return "Pass";
		else
			return "Fail";
	}

	public void pageLoadTimeout(String seconds) {
		driver.manage().timeouts().pageLoadTimeout(Integer.parseInt(seconds), TimeUnit.SECONDS);
	}

	/*
	 * Updated By: Suyash Date: Nov 9 2016 Reason: More keywords Added
	 */
	public String getAttribute(String EleType,String EleLocator, String data)
	// Return value of element attribute.foir e.g. if you want to know some
	// attribute of element like name', 'id', 'class' and 'aria-label'
	{
		String attribute;
		String[] output = data.split("|");
		attribute = output[0];
		data = output[1];

		WebElement webElementAttribute = driver.findElement(getByType(EleType,EleLocator));
		if (webElementAttribute.getAttribute(attribute).equalsIgnoreCase(data))

			return "Pass";

		else

			return "Fail";

	}
	
	public String getCssValue(String EleType,String EleLocator, String data)
	// This function will Get Css value of ELement for eg. Colour
	{
		String attribute;
		String[] output = data.split("|");
		attribute = output[0];
		data = output[1];

		WebElement webElementAttribute = driver.findElement(getByType(EleType,EleLocator));
		if (webElementAttribute.getAttribute(attribute).equalsIgnoreCase(data))

			return "Pass";

		else

			return "Fail";
	}
	

	/*
	 * Updated By: Aparna Date: Nov 9 2016 Reason: More keywords Added
	 */
	public List<String> getAllLinks(String EleType,String EleLocator) {
		List<WebElement> links = new ArrayList<WebElement>();
		List<String> linkText = new ArrayList<String>();
		
		
			links = driver.findElements(getByType(EleType,EleLocator));
		
		linkcount = links.size();
		for (int i = 0; i < links.size(); i++) {
			linkText.add((links.get(i)).getText());
		}
		System.out.println("inside llk" + linkText);
		return linkText;

	}

	public void goBack() {
		driver.navigate().back();

	}

	public void goForward() {
		// Go forward
		driver.navigate().forward();
	}

	public void refresh() {
		// Refresh browser
		driver.navigate().refresh();

	}

	public void scrollDown() {
		((JavascriptExecutor) driver).executeScript("window.scrollBy(0, document.body.scrollHeight)");

	}

	public void scrollByElement(String EleType,String EleLocator) {

		WebElement element = driver.findElement(getByType(EleType,EleLocator));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);

	}

	public void selectRadio(String EleType,String EleLocator, String value) {
		List<WebElement> radiobtn = new ArrayList<WebElement>();
		radiobtn=driver.findElements(getByType(EleType,EleLocator));
		for (int i = 0; i < radiobtn.size(); i++) {
			String rdvalue = radiobtn.get(i).getAttribute("value");
			if (rdvalue.equalsIgnoreCase(value)) {
				radiobtn.get(i).click();
				break;
			}
		}
	}

	public String getText(String EleType,String EleLocator) {
		return driver.findElement(getByType(EleType,EleLocator)).getText();
	}

	public boolean chkBoxRadioSelected(String EleType,String EleLocator) {
		return driver.findElement(getByType(EleType,EleLocator)).isSelected();
	}

	public List<WebElement> getWebElements(String EleType,String EleLocator) {
		List<WebElement> webelements = new ArrayList<WebElement>();
		webelements = driver.findElements(getByType(EleType,EleLocator));
		return webelements;
	}

	public String takePageScreenshot(String filePath) {
		String scrnSht = "";
		try {
			File srcfile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			File destFile = new File(filePath);
			FileUtils.copyFile(srcfile, new File(filePath));
			scrnSht = destFile.getAbsolutePath();
		} catch (Exception e) {
			System.out.println("Exception in Lowlevelkeywords takepagescreenshot method" + e.getClass().getSimpleName());
		}
		return scrnSht;
	}

}
