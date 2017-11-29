package com.AnyAutPeachv1.com;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterClass;

public class LowLevelKeywords {

  WebDriver driver;
  
  @Test
  
  //Read text from a text box
  
  public void readText() throws Exception {
	  driver=new FirefoxDriver();
	  driver.manage().window().maximize();
	  driver.get("https://www.facebook.com/");
	  Thread.sleep(2000);
	  driver.findElement(By.id("email")).sendKeys("archana@test.com");
	  String readEmail=driver.findElement(By.id("email")).getAttribute("value");
	  System.out.println("Email===>"+ readEmail);
	  driver.close();
	  
  }
  
  @Test
  //Refresh browser
  
  public void refresh() throws Exception {
	  driver=new FirefoxDriver();
	  driver.manage().window().maximize();
	  driver.get("https://www.facebook.com/");
	  Thread.sleep(2000);
	  driver.findElement(By.id("email")).sendKeys("archana@test.com");
      driver.navigate().refresh();
      Thread.sleep(2000);
      driver.close();
}

  @Test
  //Scroll the page for a specific element
  
  public void scrollForElement() throws Exception {
	  driver=new FirefoxDriver();
	  driver.manage().window().maximize();
	  driver.get("http://manos.malihu.gr/repository/custom-scrollbar/demo/examples/complete_examples.html");
	  Thread.sleep(2000);
	  
	// Create an instance of JavaScript executor
	  JavascriptExecutor je = (JavascriptExecutor) driver;
	  
	//Identify the WebElement which will appear after scrolling down

	  WebElement element = driver.findElement(By.xpath(".//*[@id='mCSB_3_container']/p[3]"));
	  
	// now execute query which actually will scroll until that element is not appeared on page.

	  je.executeScript("arguments[0].scrollIntoView(true);",element);
	  
	// Wait for 5 second
		 Thread.sleep(4000);
	  
	// Extract the text and verify

	  System.out.println(element.getText());
	  driver.close();
  }
  @Test
//Scrolls the web page vertically
  public void scrollPage() throws Exception{
	//load browser
	 WebDriver driver=new FirefoxDriver();
	
	// maximize browser
	 driver.manage().window().maximize();
	
	 // Open Application
	 driver.get("http://jqueryui.com");
	 
	 // Wait for 5 second
	 Thread.sleep(5000);
	
	// This  will scroll page 400 pixel vertical
	 ((JavascriptExecutor)driver).executeScript("scroll(0,400)");
	 Thread.sleep(4000);
	 driver.close();
  }
  
  @Test
  
//Select dropdown option by its index
  public void selectByIndex() throws Exception{
	  
	//load browser
	 WebDriver driver=new FirefoxDriver();
		
	// maximize browser
	 driver.manage().window().maximize();
		
	 // Open Application
	 driver.get("https://www.facebook.com/");
		 
	 // Wait for 5 second
	 Thread.sleep(5000);
	 
	WebElement month_dropdown= driver.findElement(By.id("month"));
	Select month=new Select(month_dropdown);
	month.selectByIndex(3);
	driver.close();
  }
  
  @Test
  //Select dropdown option by its value
  public void selectByValue() throws Exception{
	  
	//load browser
	 WebDriver driver=new FirefoxDriver();
		
	// maximize browser
	 driver.manage().window().maximize();
		
	 // Open Application
	 driver.get("https://www.facebook.com/");
		 
	 // Wait for 5 second
	 Thread.sleep(5000);
	 
	 WebElement month_dropdown= driver.findElement(By.id("month"));
	 Select month=new Select(month_dropdown);
	 month.selectByValue("5");
	 driver.close();
  }
  
  @Test
  //Select an option from drop down list
  public void selectByVisibleText() throws Exception{
	  
	//load browser
	 WebDriver driver=new FirefoxDriver();
		
	// maximize browser
	 driver.manage().window().maximize();
		
	 // Open Application
	 driver.get("https://www.facebook.com/");
		 
	WebElement month_dropdown= driver.findElement(By.id("month"));
	Select month=new Select(month_dropdown);
	month.selectByVisibleText("Jul");
	
	 // Wait for 2 second
	 Thread.sleep(2000);
	 driver.close();
  }
  @Test
  
  //Open link in new window
  public void openLinkInNewWindow() throws Exception{
	  
	//load browser
	  WebDriver driver=new FirefoxDriver();
			
	// maximize browser
	  driver.manage().window().maximize();
			
	 // Open Application
	  driver.get("https://www.facebook.com/");
	  
	  WebElement Webelement =driver.findElement(By.id("terms-link"));
      Actions action = new Actions(driver);
      action.keyDown(Keys.SHIFT).click(Webelement).keyUp(Keys.SHIFT).build().perform();
      Thread.sleep(3000);
      driver.quit();
  }
			 
  @Test
  //Select Radio button 
  public void selectRadioByText(){
	  
	//load browser
	  WebDriver driver=new FirefoxDriver();
			
	// maximize browser
	  driver.manage().window().maximize();
			
	 // Open Application
	  driver.get("http://seleniumpractise.blogspot.in/2016/08/how-to-automate-radio-button-in.html");
	  List<WebElement> radio=driver.findElements(By.xpath("//input[@name='lang' and @type='radio']"));
	  for(int i=0;i<radio.size();i++)
	  {
		  WebElement local_radio=radio.get(i);
		 String value= local_radio.getAttribute("value");
		 System.out.println("Values from radio button====>"+ value);
				 
	  }
	  driver.close();
	  
  }
  
  @BeforeClass
  public void beforeClass() {
  }

  @AfterClass
  public void afterClass() {
  }

}
