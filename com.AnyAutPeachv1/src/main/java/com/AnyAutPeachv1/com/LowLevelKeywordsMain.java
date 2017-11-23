package com.AnyAutPeachv1.com;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LowLevelKeywordsMain {
	
	 WebDriver driver;

	 //Quit browsers
	  public void quitBrowser(){
		  driver=new FirefoxDriver();
		  driver.quit();
		  
	  }

}
