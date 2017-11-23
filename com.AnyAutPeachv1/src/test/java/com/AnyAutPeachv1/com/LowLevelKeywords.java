package com.AnyAutPeachv1.com;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;

public class LowLevelKeywords {

  WebDriver driver;
  
  @Test
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
  @BeforeClass
  public void beforeClass() {
  }

  @AfterClass
  public void afterClass() {
  }

}
