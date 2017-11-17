package com.AnyAUT;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
//import org.testng.log4testng.Logger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExecuteTest extends Utilities {
	
	/* Updated by: Sindura Ravela
	 * Date: 11/28/2016
	 * Purpose: Updated all the variable and some method names in this class to more meaningful names 
	 * 			Removed duplicate instances of date and dateFormat variables from this method since they are declared 
	 * 			--in executeTestSteps
	 * 
	 */

 	WebDriver webDriver;
	String fileInputPath, fileResultPath;
	int xlTestStepsRowCount, xlTestStepsColumnCount,xlTestCasesRowCount,xlTestCasesColumnCount;
	String[][] xlTestSteps,xlTestCases, xlmainFileData;	
	String testCase_Result, screenShotDirectoryName = null;
	static Logger logger = Logger.getLogger(ExecuteTest.class);
	
	/*
	 * Updated by :Aparna
	 * Date:10/26/2016 ,11/17/2016
	 * Purpose1: Added global variables for ExtentReport 
	 * Purpose2: Added object and boolean flag to test one test step output as next step input 
	 */
	ExtentReports report;
	ExtentTest testCase_Report,testSteps_Report;
	Object TestStep_Output ;
	boolean set_Input_Flag=false;
	
	//@Parameters ({"TestRunName", "TestRunBy", "TestRunLocation"})
	@BeforeTest
	public void readTestData()
	{   
		/* Updated by: Sindura Ravela
		 * Date: 11/21/2016
		 * Purpose: 1. Updated the file name fileResultPath from RunSetUp_output to RunResult
		 * Date: 11/28/2016
		 * Purpose: Commented variable scrnshtpath since it's no more required. Also, deleted it's definition
		 * 
		 */
		
		/*
		 * Updated by :Aparna
		 * Date:10/26/2016 ,11/17/2016
		 * Purpose1: Reading TestCases and TestSteps sheet instead of output
		 * Purpose2:set the path for data,html and screenshots using Utilities.fileAbsolutePath()
		 * Purpose3:Added try/catch blocks ,updated the method name myBefore to readTestData
		 */
		
		String TestRunName, TestRunBy, tempPath;
		TestRunName = new UICode().returnValues("TestRunName");
		TestRunBy = new UICode().returnValues("TestRunBy");
	
		tempPath = Utilities.fileAbsolutePath()+"Results/TestRun_" + TestRunName + "/";
	
		fileInputPath = tempPath + "TestRun_" + TestRunName + ".xls";
		fileResultPath = tempPath + "RunResult.xls";
		
		PropertyConfigurator.configure("log4j.properties");
		
		report = new ExtentReports(tempPath + "Report.html",true);
		logger.info(fileInputPath);
		logger.info(fileResultPath);
		
		screenShotDirectoryName = tempPath + "Screenshots/";
		
		//returns the absolute file path of the current workspace directory
		//xlInputPath = fileAbsolutePath() + "ExcelFiles/TestSetup.xls";
		
		
		try{
		    xlTestCases=readXL(fileInputPath,"TestCases");
		    xlTestSteps = readXL(fileInputPath, "TestSteps");
		}
		catch(Exception e)
		{
			logger.error("Exception in readTestData method"+e.getClass().getSimpleName());
		}
	    xlTestStepsRowCount = xlTestSteps.length;
	    xlTestStepsColumnCount = xlTestSteps[0].length;
	    xlTestCasesRowCount=xlTestCases.length;
	    xlTestCasesColumnCount= xlTestCases[0].length;
		logger.info("Rows are " + xlTestStepsRowCount);
		logger.info("Cols are " + xlTestStepsColumnCount);	
	}//end of readTestData	
	
	@Test
	public void executeTestSteps()  {
		/* Updated by: Sindura Ravela
		 * Date: 11/21/2016
		 * Purpose: 1. Added a folder to store screenshots based on date and time stamp of execution
		 * Date: 11/28/2016
		 * Purpose: 1. Added Test Data set as additional information to the report
		 * 			2. Updated xlTestSteps[i][10] value from "Look at Screenshot: "+ scrnshtpath to "Look at Screenshot: "+ screenShotFilePath in all occurrences
		 * 
		 */
		LowLevelKeywords lowLevelKeywords = new LowLevelKeywords(webDriver); 
		DateFormat dateFormat;
		File screenShotDirectory;
		long vStartTime = 0;
		String screenShotFilePath = null, dateConversion;
		Date screenShotDate, stepDate; 
		String keyWord, elementID, testData,elementType;
		screenShotDate = new Date();
		
		dateFormat = new SimpleDateFormat("MMddyyyy hh.mm.ss a") ;
			
		//converts screenShotDate in to the defined dateFormat
		dateConversion = dateFormat.format(screenShotDate);
		
		//fetch the absolute path of the workspace and append Screenshots folder
		screenShotDirectory = new File( screenShotDirectoryName + dateConversion);
		
		//create a directory with the file path. This ensures that there is a folder created for each execution
		screenShotDirectory.mkdir();
		screenShotFilePath = screenShotDirectory.getAbsolutePath();

		/* Updated by :Aparna 
		 * Date:10/26/2016 
		 * Purpose: Loop through TCSheet to get the Extent Report at TC level.Added if block to 
		 * pick all TestSteps for each TestCase.updated few xlTestSteps fields which are pointing to wrong column 
		 * updated jpg to png to display the screenshots in the extentreport.
		 * Date: 11/15/2016
		 * Purpose:one test step output acts as input for next step.Tested this for MultipleWindows.
		 * Date:11/17/2016
		 * Purpose:updated the method name mainTest to executeTestSteps Added testSteps_Report
		 * (TestStep info - testdata,error,scrnshot)and appended it to testCase_Report(TestCase info)
		 * Added a new method takePageScreenshot in lowlevelkeywords.Added date,dateformat 
		 * to append the file path with current date,time in 12 hour format.Updated Error column xlTestSteps[i][10]
		 * Date:03/14/2017
		 * Added new column Element type functionality in the executeKW switchcase and in the for loop 
		 * For now ElementType is  pointing to column 13
		 */
			for ( int j=1; j < xlTestCasesRowCount; j++)
			{
				testCase_Report = report.startTest(xlTestCases[j][1]);		
				for (int i = 1; i < xlTestStepsRowCount; i++)
				{  
				if((xlTestCases[j][1]).equals(xlTestSteps[i][1]))
				{
					testSteps_Report=report.startTest(xlTestSteps[i][3]);
					testCase_Result = "Pass";
					
					keyWord =	xlTestSteps[i][4];
					elementType = xlTestSteps[i][5];
					elementID =	xlTestSteps[i][6];
					testData =	xlTestSteps[i][7];
					/* 3/14/2017 - For now we are passing the ElementType in the 13 th column later we need to change this 
					depending on the webUI and SetUpTestRun output.*/
					
					logger.info("KW: " + keyWord +" ,Element Type: " +elementType+" ,Element ID: " +elementID+" ,Test Data: " + testData);
					try{
						/***********code begin for one TestStep output act as next step input********************************/
						if(set_Input_Flag)
						{
							logger.info("inside set input flag");
							logger.info(TestStep_Output);
							String prevTSoutput = setInput(TestStep_Output,testData);
							testData=prevTSoutput;
							set_Input_Flag=false;
							logger.info("new testData value"+testData);
							logger.info("inside set input flag clean flag"+set_Input_Flag);
						}
						/*******************************************/
						vStartTime = System.currentTimeMillis();
						executeKW(lowLevelKeywords, keyWord,elementType, elementID, testData);
//						logger.info("Teststep status"+testCase_Result);
						long vStopTime = System.currentTimeMillis();
					    long vElapsedTime = vStopTime - vStartTime;
					    vElapsedTime = vElapsedTime/1000;
					    String vExecutionTime = Long.toString(vElapsedTime); 
					    xlTestSteps[i][12] = vExecutionTime;
					    if(testCase_Result.equalsIgnoreCase("Pass"))
			    		{
						    testSteps_Report.log(LogStatus.PASS,xlTestSteps[i][7]);
						    testSteps_Report.log(LogStatus.INFO, "Test Data Set: "+ xlTestSteps[i][13]); 
				    		testCase_Report.appendChild(testSteps_Report);
			    		} 
					    else if(!testCase_Result.equals("Pass")) 
					    {
							logger.info("test failed");
							xlTestSteps[i][10]  = "Verification Failed";
							stepDate = new Date();
							dateFormat = new SimpleDateFormat("yyyy-MM-dd hh.mm.ss a") ;
							String testStepScreenShot = lowLevelKeywords.takePageScreenshot(screenShotFilePath+"/"+xlTestSteps[i][1]+"_"+xlTestSteps[i][3]+"_"+dateFormat.format(stepDate)+".png");
							xlTestSteps[i][11]="Look at Screenshot: "+screenShotFilePath; 
							testSteps_Report.log(LogStatus.FAIL,xlTestSteps[i][7]);
							testSteps_Report.log(LogStatus.INFO,testCase_Result);
							testSteps_Report.log(LogStatus.INFO, "Test Data Set: "+ xlTestSteps[i][13]); 
							testSteps_Report.log(LogStatus.INFO, "Error Snapshot:" +testCase_Report.addScreenCapture(testStepScreenShot));
					    	testCase_Report.appendChild(testSteps_Report);                       
					    }
						}
						catch(Exception ex)
						{
							long vStopTime = System.currentTimeMillis();
						    long vElapsedTime = vStopTime - vStartTime;
						    vElapsedTime = vElapsedTime/1000;
						    String vExecutionTime = Long.toString(vElapsedTime);
							logger.error("Error : " + ex.getClass().getSimpleName());
							testCase_Result = "Fail";
							xlTestSteps[i][10] = "Error : " + ex;
							stepDate = new Date();
							String testStepScreenShot=lowLevelKeywords.takePageScreenshot(screenShotFilePath+"/"+xlTestSteps[i][1]+"_"+xlTestSteps[i][3]+"_"+dateFormat.format(stepDate)+".png");	
							xlTestSteps[i][11]="Look at Screenshot: " + screenShotFilePath; 
							testSteps_Report.log(LogStatus.FAIL,xlTestSteps[i][7]);
							testSteps_Report.log(LogStatus.INFO,testCase_Result);
							testSteps_Report.log(LogStatus.INFO, "Test Data Set: "+ xlTestSteps[i][13]); 
							testSteps_Report.log(LogStatus.INFO, "Error Snapshot:" +testCase_Report.addScreenCapture(testStepScreenShot));
					    	testCase_Report.appendChild(testSteps_Report);
						}//end of try-catch block
					xlTestSteps[i][9] = testCase_Result;
					//for a failed test step, assigning 
					//if (xlTestSteps[i][12].equals("-")) {
					//	xlTestSteps[i][12] = "0";
					//}
				}// end of if end 
		
			}// end xlTestSteps for 
			report.endTest(testCase_Report);
			report.flush();
		} // end xlTestCases for
	}//end of executeTestSteps		
	
	@AfterTest
	public void getResults() {		
		/*
		 * Updated by :Aparna
		 * Date:10/26/2016
		 * Purpose: publish results using writeXLSheets method wee need to add TCSheet in future 
		 * currently this code is not capturing TC pass or fail .
		 * Date:11/17/2016
		 * Purpose: Added try catch blocks , updated the method name myAfterTest to getResults.
		 */
		try
		{
			writeXLSheets(fileResultPath, "Output",0, xlTestSteps);
		}
		catch(Exception e)
		{
			logger.error("Exception in getResults method"+e.getClass().getSimpleName());
		}
	}//end of getResults
	
 /* Update: Aparna
  * Date: Nov 17 2016
  * 	Purpose:commented this method
  *  Using the takePageScreenshot method from low level keywords
  */
	/*public String  takeScreenshot(WebDriver fdriver,String fPath) throws Exception{
		File scrFile = ((TakesScreenshot)fdriver).getScreenshotAs(OutputType.FILE);
		// Now you can do whatever you need to do with it, for example copy somewhere
		File destFile=new File(fPath);
		FileUtils.copyFile(scrFile, new File(fPath));
	String 	VTS_ScrnSht=destFile.getAbsolutePath();
	return VTS_ScrnSht;
	}*/
	// 3/14/2017 added ElementType parameter in all the cases
	public void executeKW(LowLevelKeywords lowLevelKeywords, String strKeyWord, String ElementType,String strElementID, String strTestData) throws Exception
	{
		
		switch(strKeyWord){
		case "openBrowser":
			logger.info("Keyword is: "+ strKeyWord);
			lowLevelKeywords.openBrowser(strTestData);
			break;
		case "closeBrowser":
			lowLevelKeywords.closeBrowser();
			break;
		case "navigateBrowser":
			lowLevelKeywords.navigateBrowser(strTestData);
			break;
		case "typeText":
			lowLevelKeywords.typeText(ElementType,strElementID, strTestData);
			break;
		case "selectList":
			lowLevelKeywords.selectList(ElementType,strElementID, strTestData);
			break;
		case "clickButton":
			lowLevelKeywords.clickElement(ElementType,strElementID);
			break;
		case "verifyText":
			testCase_Result = lowLevelKeywords.verifyText(ElementType,strElementID, strTestData);
			break;
		case "enterKeyboard":
			lowLevelKeywords.enterKeyboard(ElementType,strElementID);
			break;
		case "sendKeys":
			lowLevelKeywords.selectList(ElementType,strElementID, strTestData);
			break;
		case "clickImage":
			lowLevelKeywords.clickElement(ElementType,strElementID);
			break;			
		case "readText":
			testCase_Result = lowLevelKeywords.readText(ElementType,strElementID);
			break;
		case "clickLink":
			lowLevelKeywords.clickLink(ElementType,strElementID);
			break;
			/* Updated:Aparna
			 * Date:Nov 15 2016
			 * Reason:New cases added to accommodate new keywords
			 */
		case "getAllLinks":
			TestStep_Output=lowLevelKeywords.getAllLinks(ElementType,strElementID);
			logger.info(TestStep_Output);
			break;		
		case "verify":
			lowLevelKeywords.scrollByElement(ElementType,strElementID);
			break;
		case "selectLinkToNewWindow":
			lowLevelKeywords.selectLinkToNewWindow(ElementType,strElementID);
			break;
		case "getWindowNames":
			TestStep_Output = lowLevelKeywords.getWindowNames();
			set_Input_Flag=true;
			logger.info("in switch case getwindownames:"+set_Input_Flag);
			break;
		case "switchToWindow":
			if(strTestData.equalsIgnoreCase("original")){strTestData=lowLevelKeywords.orig_win_handle;}
			logger.info("original window url:"+strTestData);
			lowLevelKeywords.switchToWindow(strTestData);
			break;
		case "closepopupWindow":	
			lowLevelKeywords.closepopupWindow();
			break;
		case "verifyTitle":	
			lowLevelKeywords.verifyTitle(strTestData);
			break;
		case "scrollByElement":	
			lowLevelKeywords.scrollByElement(ElementType,strElementID);
			break;	
		case "selectByValue":	
			lowLevelKeywords.selectByValue(ElementType,strElementID,strTestData);
			break;
		case "selectByIndex":	
			lowLevelKeywords.selectByIndex(ElementType,strElementID,strTestData);
			break;
		case "checkBoxclick":	
			lowLevelKeywords.checkBoxclick(ElementType,strElementID,strTestData);
			break;
		case "chkBoxRadioSelected":
			lowLevelKeywords.chkBoxRadioSelected(ElementType,strElementID);
			break;
		case "scrollDown":	
			lowLevelKeywords.scrollDown();
			break;
		case "refresh":	
			lowLevelKeywords.refresh();
			break;	
		case "goBack":	
			lowLevelKeywords.goBack();
			break;
		case "goForward":	
			lowLevelKeywords.goForward();
			break;
		case "clearText":	
			lowLevelKeywords.clearText(ElementType,strElementID);
			break;
		case "switchToFrame":
			lowLevelKeywords.switchToFrame(ElementType,strElementID);
			break;
		case "defaultContent":
			lowLevelKeywords.defaultContent();
			break;
		case "getTitle":
			lowLevelKeywords.getTitle();
			break;
		case "waitTilElementEnable":
			lowLevelKeywords.waitTilElementEnable(ElementType,strElementID,strTestData);
			break;
		case "waitTilElementIsVisible":
			lowLevelKeywords.waitTilElementIsVisible(ElementType,strElementID,strTestData);
			break;
		case "waitTilElementTextPresent":
			lowLevelKeywords.waitTilElementTextPresent(ElementType,strElementID,strTestData);
			break;
		case "waitTilElementNotVisible":
			lowLevelKeywords.waitTilElementNotVisible(ElementType,strElementID,strTestData);
			break;
		case "fluentWait":
			lowLevelKeywords.fluentWait();
			break;
		case "isEnabled":
			lowLevelKeywords.isEnabled(ElementType,strElementID);
			logger.info(lowLevelKeywords.isEnabled(ElementType,strElementID));
			break;
		case "isDisplayed":
			lowLevelKeywords.isDisplayed(ElementType,strElementID);
			logger.info("is displayed"+lowLevelKeywords.isDisplayed(ElementType,strElementID));
			break;
		case "selectRadio":
			lowLevelKeywords.selectRadio(ElementType,strElementID,strTestData);
			break;
		case "getText":	
			lowLevelKeywords.getText(ElementType,strElementID);
			break;
		case "isAlertPresent":
		testCase_Result =	lowLevelKeywords.isAlertPresent();
		break;		
		case "switchToAlert":
			lowLevelKeywords.switchToAlert();
			break;			
		case "dismissAlert":
			lowLevelKeywords.dismissAlert();
			break;			
		case "confirmAlert":
			lowLevelKeywords.confirmAlert();
			break;			
		case "getAlertText":
			testCase_Result =	lowLevelKeywords.getAlertText(strTestData);
			break;			
		case "sendKeys_promptAlert":
			lowLevelKeywords.sendKeys_promptAlert(strTestData);
			break;			
		case "getAttribute":
			lowLevelKeywords.getAttribute(ElementType,strElementID,strTestData);
			break;
		case "getCssValue":
			lowLevelKeywords.getCssValue(ElementType,strElementID,strTestData);
			break;
			
		case "mouseHover":
			lowLevelKeywords.mouseHover(ElementType,strElementID);
			break;
			
		case "rightClick":
			lowLevelKeywords.contextClick(ElementType,strElementID);
			break;
			/*
			 * Updated by :Vijaysankari
			 * Date: Dec 9 2016
			 * Reason : Added keyword verifyLink
			 */	
		case "verifyLink":
			lowLevelKeywords.verifyLink(ElementType,strTestData);
			break;
		
			/**********************************************************************************/
		default : 
			
			/*
			 * Updated by :Aparna
			 * Date: Oct 26 2016
			 * Reason : keyword missing then make it as fail.
			 */
			testCase_Result="Fail";
			logger.info("Keyword is missing : " + strKeyWord);

		}
	}

	public String setInput(Object TSout , String strTestData)
	{
		/* Updated by: Aparna
		 * Date: Nov 11 2016
		 * Purpose:
		 * One test step output acts as input to another step .As we are executing 
		 * all the test steps in a loop created set_Input_Flag - flag cleaned after execution.
		 */
		logger.info("inside setInput method");
		String output="";
		// perform something 
		if(TSout instanceof String)
		{ //need to implement when this scenario comes up
			logger.info("string");
		}
		else if(TSout instanceof HashMap)
		{
			HashMap<String, String> hmap = (HashMap<String, String>) TSout;
			
			logger.info("inside hashmap if"+TSout);
			//loop through each entry in the map using Map.Entry
			for (Map.Entry<String, String> entry : hmap.entrySet())
			{
				String key=entry.getKey();
				String value=entry.getValue();
				logger.info("Key : " +key + " Value : " +value);
				if(value.equalsIgnoreCase(strTestData)) {output=key;}
			}
			logger.info("output is hashmap"+output);
		}
		else if(TSout instanceof List)
		{
			//need to implement when this scenario comes up
			logger.info("list");
		}
		return output;
	}
	
}

