package TestNGPackage;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

public class Contextclick {
 
	  
	  static WebDriver driver;
		Alert alert;
		
		
		@Test(priority=1,description="navigating to Browser")
		public void setUp() 
		{
			
			System.setProperty("webdriver.chrome.driver", "C:\\Users\\MeghnaMahesh\\Desktop\\meghna\\Driver files\\chromedriver.exe\\");
		  	driver = new ChromeDriver();
		  	
		  // driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		  // driver.manage().window().maximize();
		  // had to comment the timeout and maximize as the application was not opening
		  	driver.get("http://www.anyaut.com/orange");
	}
		
		@Test(priority=2,description="Login with user Credentials")
		public static void executeKW()
		{
			// sendKeys
			//login
			sendKeys("id","username","aaa.bbb@mail.com");
			sendKeys("id","password","AbcDef!23");
			driver.findElement(By.id("singlebutton")).click();
		}
		
		// Sendkeys Method
		  @Test(enabled=false)
		  public static void sendKeys(String EleType,String EleLocator, String text) {
			    // Purpose: Type a text into edit field
			    // IP: Xpath of the element and the text to enter
			    // OP: N/A
			    driver.findElement(getByType(EleType,EleLocator)).clear();
			    driver.findElement(getByType(EleType,EleLocator)).sendKeys(text);
			} 
		  // Click on Module icon 
		  @Test(priority=3,description="Click On Modules")
		  public static void module() throws InterruptedException
		  {
			  Thread.sleep(2000);
				driver.findElement(By.xpath("html/body/div[1]/div/div[3]/div[1]/div[1]/a/div/div[1]")).click();
				
		  }
  
		  
		  @Test(priority=4)
		  public void editModule()
		  {
			 
			 driver.findElement(By.xpath(".//*[@id='module_name']")).click();
			  contextClick("id","module_name");
			 
		  }

		 @Test 
		  // context link
		  public void contextClick(String EleType,String EleLocator) {
		             //Purpose: You can use this function for doing right click on any Webelement
		             WebElement rightClick = driver.findElement(getByType(EleType,EleLocator));
		             Actions action = new Actions(driver);
		             Action a1 = action.contextClick(rightClick).build();
		             a1.perform();
		     }
		  
		  @Test(enabled=false)
		  public 	static By getByType(String ElementType,String ElementLocator)
		  {   
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

}




