package com.AnyAUT;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.AnyAUT.Utilities;
import com.AnyAUT.arrayStructure;

import org.testng.annotations.BeforeTest;

public class PrepareRun extends Utilities {
	/* Updated By: Sindura Ravela
	 * Date: 11/4/2016
	 * Updates: 
	 * 			1. Removed vLLK, vHLK_TD, and vHLK_EID variables since they are not used
	 */
	
	String xlInputPath, xlResultPath, xlFileLocation;
	static int modRowCount, modColumnCount, testCaseRowCount, testCaseColumnCount, testStepRowCount, testStepColumnCount, 
	xRows_HLK, HLKColumnCount, EIDRowCount, EIDColumnCount,testDataRowCount, testDataColumnCount, tempTSRowCount, tempTSColumnCount, executableTCRowCount,
	executableTCColumnCount;
	static String[] testDataSetID, strElementDetails;;
	static String[][] xlmainFileData, xlModuleData, xlTestCaseData, xlTestStepData, xlHighLevelKeysData, xlElementIDData, xlTestData, xlTempTestCases, xlExecutableTCs;
	static String strStepDetail, strOutput, strResult, strError, strScreenShot, strTimeTaken ;
	String strTestStepID, strKeyWord, strKeyWordType;
	static String strElementID, strTestData, strElementType, strTestDataType;
	String strTestStepResult, strTestCaseResult, strTestCaseModuleID, strTestCaseID, strTestCaseIExecute, strModuleID, strModuleExecute;
	static int runnerTSRowCount;
	arrayStructure testStepArray = new arrayStructure();
	
	
	
	/* Update by Aparna 
	 * Oct 26 2016
	 * To get only the required rows from tcsheet added arraystructure testCaseArray
	 */
	
	arrayStructure testCaseArray = new arrayStructure();
	
	static Logger logger = Logger.getLogger(PrepareRun.class);
	
	/* Updated By: Sindura Ravela
	 * Date: 11/11/2016
	 * Purpose: 1. Removed the file path definitions that had static path
	 * 			2. Calling a method from Utilities class to obtain the absolute path of the current workspace
	 * 			3. Updated @BeforeTest method name
	 * Date: 3/23/2017
	 * Purpose: Updated code to create Results and testSuite folders
	 * 			
	 */
	
	@BeforeTest
	public void readSetUpData() throws Exception {
		
		/* Updated by: Sindura Ravela
		 * Date: 12/2/2016
		 * Purpose: 1. Removed Demo from the file names
		 * 			2. Updated file paths to include ExcelFiles folder to syncup with TestRun
		 */
		
		File resultsDirectory, testSuiteDirectory;
		String TestRunName, TestRunBy, TestRunLocation;
		
		TestRunName = new UICode().returnValues("TestRunName");
		TestRunBy = new UICode().returnValues("TestRunBy");
		TestRunLocation = new UICode().returnValues("TestRunLocation");
		
		PropertyConfigurator.configure("log4j.properties");
		
		xlInputPath = TestRunLocation;
		resultsDirectory = new File(fileAbsolutePath() + "/Results");
		
		if (!resultsDirectory.isDirectory()) {
			resultsDirectory.mkdir();
		}
		
		testSuiteDirectory = new File (resultsDirectory.getAbsolutePath() + "/" + "TestRun_" + TestRunName.trim());
		testSuiteDirectory.mkdir();
		//returns the absolute file path of the current workspace directory
		xlResultPath = testSuiteDirectory.getAbsolutePath() + "/" + "TestRun_" + TestRunName.trim() + ".xls";
		//xlResultPath = fileAbsolutePath() + "ExcelFiles/RunSetup.xls";
		
		logger.info("Input FIle Path: " + xlInputPath);
		logger.info("Output file path  :"+ xlResultPath);
		
		runnerTSRowCount = 0;
		
		try {
			
			xlModuleData = readXL(xlInputPath, "Modules");
			xlTestCaseData = readXL(xlInputPath, "TestCases");
			xlTestStepData = readXL(xlInputPath, "TestSteps");
			xlHighLevelKeysData = readXL(xlInputPath, "HighLevelKeywords");
			xlElementIDData = readXL(xlInputPath, "EID");
			xlTestData = readXL(xlInputPath, "TestData");
			modRowCount = xlModuleData.length;
			modColumnCount = xlModuleData[0].length;
			testCaseRowCount = xlTestCaseData.length;
			testCaseColumnCount = xlTestCaseData[0].length;
			testStepRowCount = xlTestStepData.length;
			testStepColumnCount = xlTestStepData[0].length;
			xRows_HLK = xlHighLevelKeysData.length;
			HLKColumnCount = xlHighLevelKeysData[0].length;
			EIDRowCount = xlElementIDData.length;
			EIDColumnCount = xlElementIDData[0].length;
			testDataRowCount = xlTestData.length;
			testDataColumnCount = xlTestData[0].length;
		}catch(Exception ex) {
			logger.error("Error message:  "+ ex);
		}
		
	}
	/* Updated By: Sindura Ravela
	 * Data: 11/11/2016
	 * Purpose: Updated the method name from mainTest to createRunSetupData
	 */
	/* Updated By: Sindura Ravela
	 * Data: 11/4/2016
	 * Purpose: 1. Added a method to identify the test cases to be executed per each module. 
	 * 			Current code is missing the logic when the option is All in Module sheet
	 * 			2. Updated code to implement multiple sets of Test Data for All/Specific test case 
	 */
	@Test
	public void createRunSetUpData() throws Exception {
		
		String vTSTCID, vTSModID, vTDSets;
		int a;
		
		logger.info("Entered Main Test");
		//this method is used to identify the executable test cases
		getExecutableTestCases();
		
		//sets the header row of the test step output sheet
		setTopRow();
		
		a = 1;
		
		try{
			//loop through each of the executable test cases
			for (int i = 0; i < executableTCRowCount; i++) {
				strTestCaseID = xlExecutableTCs[i][1];
				strModuleID = xlExecutableTCs[i][0];
				vTDSets = xlExecutableTCs[i][5];
				//call fetchTestDataSets method to fetch the Test Data set IDs for each test case
				testDataSetID = fetchTestDataSets(vTDSets);
				//steps are repeated for the number of Test Data sets for each test case
				for (int r = 0; r < testDataSetID.length; r++) {
					//if a null value is in Test Data set then break out of the for loop
					if (testDataSetID[r] == null) { 
						break; 
					}
					runnerTSRowCount = 1;
					//steps are repeated for the number of Test Steps for each test case
					for (int j = 1;j < testStepRowCount;j++) {
						strElementDetails = new String[2];
						vTSTCID = xlTestStepData[j][1];
						vTSModID = xlTestStepData[j][0];
						//verify if the Module ID and Test Case at the test case level are same as test step
						if ((strModuleID.equalsIgnoreCase(vTSModID)==true) &&(strTestCaseID.equals(vTSTCID)==true)) {
							//capture elementID, Keyword type, Test Data from Test Data sheet
							strElementType = xlTestStepData[j][6];
							strElementID = xlTestStepData[j][7];
							strTestDataType = xlTestStepData[j][8];
							strTestData = xlTestStepData[j][9];
							strKeyWordType = xlTestStepData[j][4];
							//verify if the Keyword is HLK
							if (strKeyWordType.equalsIgnoreCase("HLK")==true) {
								//repeat steps for the number of number in Highlevel Keywords sheet
								for (int l = 1; l < xRows_HLK; l++) {
									//verify if the step description of a test step matches the step description in highlevel keywords sheet
									if (xlTestStepData[j][5].equals(xlHighLevelKeysData[l][0])==true) {
										strStepDetail = xlHighLevelKeysData[l][3];
										strKeyWord = xlHighLevelKeysData[l][4];	
										strElementType = xlHighLevelKeysData[l][5];
										strElementID = xlHighLevelKeysData[l][6];
										strTestDataType = xlHighLevelKeysData[l][7];
										strTestData = xlHighLevelKeysData[l][8];
										//call this method to assign data in Output, Result, Error, 
										//ScreenShot and TimeTaken columns
										assignRemainingData(j);
										
										//call this method to fetch the value of the element
										if (strElementType.trim().equalsIgnoreCase("Reusable Element")) {//SR
											strElementDetails = getEID (strElementID);
										} else if (strElementType.trim().length() == 0){
											strElementDetails[0] = "-";
											strElementDetails[1] = "-";
										}else if ((strElementType.trim().length() > 0) || (strElementType.equals("-")) ){
											strElementDetails[0] = strElementType;
											strElementDetails[1] = strElementID;
										}
										System.out.println("Test Data type : " + strTestDataType);
										//fetch test data
										if ((testDataSetID[r].trim().equals("-")) || (testDataSetID[r].trim().length() == 0)) {//SR
											strTestData = "-";
										}else if (strTestDataType.trim().equalsIgnoreCase("Reusable TestData")) {//SR
											//call this method to fetch the test data value
											strTestData = getTD(strTestData, testDataSetID[r].trim());
										}
										//call this method to add the o/p data to an array
										updateTestRunnerArray(a, j, testDataSetID[r].trim());
										a++;
									}//end of if	
								}//end of l for loop
							} else if (strKeyWordType.equalsIgnoreCase("LLK")) {
								strStepDetail = xlTestStepData[j][3];
								strKeyWord = xlTestStepData[j][5];
								//call this method to assign data in Output, Result, Error, 
								//ScreenShot and TimeTaken columns
								assignRemainingData(j);
								//call this method to fetch the value of the element
								if (strElementType.equalsIgnoreCase("Reusable Element")) {//SR
									strElementDetails = getEID (strElementID);
								} else if (strElementType.trim().length() == 0){
									strElementDetails[0] = "-";
									strElementDetails[1] = "-";
								}else if ((strElementType.trim().length() > 0) || (strElementType.trim().equals("-")) ){
									strElementDetails[0] = strElementType;
									strElementDetails[1] = strElementID;
								}
								if ((testDataSetID[r].trim().equals("-")) || (testDataSetID[r].trim().length() == 0)) {
									strTestData = "-";
								}else if (strTestDataType.equalsIgnoreCase("Reusable TestData")) {//SR
									//call this method to fetch the test data value
									strTestData = getTD(strTestData, testDataSetID[r].trim());
								}
								//call this method to add the o/p data to an array
								updateTestRunnerArray(a, j, testDataSetID[r].trim());
								a++;
								}//end of if
						}//end if 
					}//end j loop
				}//end of r loop
			}//end i for loop
		}catch (Exception e) {
			logger.error("Exception occured in main test");
			e.printStackTrace();
		}
		
		logger.info("Main test execution has completed");
	
	}

	/* Created By: Sindura Ravela
	 * Date: 11/4/2016
	 * Purpose: This method will identify the executable test cases based on the selection made in the data sheet
	 */
	private void getExecutableTestCases() {
		// TODO Auto-generated method stub	
		/* Updated by: Sin
		 * 
		 */
		int vMod_Rows, p;
		
		p = 0;
		vMod_Rows = xlModuleData.length;
		xlExecutableTCs = new String[xlTestCaseData.length][xlTestCaseData[0].length];
		
		//loops the number modules in TestSetup
		for (int i = 1; i< vMod_Rows; i++) {
			//verify if Execute column has All
			if (xlModuleData[i][3].equalsIgnoreCase("All")) {
				for (int j = 1; j < xlTestCaseData.length; j++) {
					if (xlModuleData[i][1].equals(xlTestCaseData[j][0])==true) {
						xlExecutableTCs[p][0] = xlTestCaseData[j][0];
						xlExecutableTCs[p][1] = xlTestCaseData[j][1];
						xlExecutableTCs[p][2] = xlTestCaseData[j][2];
						xlExecutableTCs[p][3] = xlTestCaseData[j][3];
						xlExecutableTCs[p][4] = xlTestCaseData[j][4];
						xlExecutableTCs[p][5] = xlTestCaseData[j][5];
						p++;
						executableTCRowCount = p;
					}
				}
				//verify if Execute column has N or Y
			} else if (xlModuleData[i][3].equalsIgnoreCase("Y")) {
				//loop for the number of test cases in TestSetup
				for (int j = 1; j < xlTestCaseData.length; j++) {
					//verify if module ID from Modules sheet matches with the one in Test Cases sheet
					if (xlModuleData[i][1].equals(xlTestCaseData[j][0])==true) {	
						//verify if execute is Y in test cases sheet
						if (xlTestCaseData[j][3].equalsIgnoreCase("Y")) {
							//loop for the number test steps in TestSteps sheet
							for (int m = 1; m < testStepRowCount; m++) {
								//verify if there is atleast one Test Step for the test case
								if (xlTestCaseData[j][1].equalsIgnoreCase(xlTestStepData[m][1]) == true)  
								{
									xlExecutableTCs[p][0] = xlTestCaseData[j][0];
									xlExecutableTCs[p][1] = xlTestCaseData[j][1];
									xlExecutableTCs[p][2] = xlTestCaseData[j][2];
									xlExecutableTCs[p][3] = xlTestCaseData[j][3];
									xlExecutableTCs[p][4] = xlTestCaseData[j][4];
									xlExecutableTCs[p][5] = xlTestCaseData[j][5];
									p++;
									executableTCRowCount = p;
									break;
								}//end if
							}//end for loop
						}//end of if
					}//end of if
				}//end of j for loop
			}//end of if
		}//end of i for loop	
		logger.info("Completed fetching the test cases ready for execution");
	}

	/* Created By: Sindura Ravela
	 * Date: 11/4/2016
	 * Purpose: This method is used to assign data in Output, Result, Error, ScreenShot and TimeTaken columns
	 */
	
	private static void assignRemainingData(int j) 
	{		
		strOutput = xlTestStepData[j][10];
		strResult = xlTestStepData[j][11];
		strError = xlTestStepData[j][12];
		strScreenShot = xlTestStepData[j][13];
		strTimeTaken = xlTestStepData[j][14];
		
	}//end of assignRemainingData
	
	/* Created By: Sindura Ravela
	 * Date: 11/8/2016
	 * Purpose: Updated this method to handle N/A as an option for test data set
	 */
	
	/* Created By: Sindura Ravela
	 * Date: 11/4/2016
	 * Purpose: This method will fetch the different test data sets used for each test case
	 */
	@SuppressWarnings("null")
	private static String[] fetchTestDataSets(String vTDSets) {
		
		String[] vTDSetIDs = null;

		try {
			if (vTDSets.equalsIgnoreCase("All")) {
				int m = 0;
				vTDSetIDs = new String[testDataColumnCount];
				for (int x = 1; x < testDataColumnCount; x++) {
					vTDSetIDs[m] = xlTestData[0][x];
					m++;				
				}
			} else if ((vTDSets != "-") || (!vTDSets.equals("N/A"))) {
				vTDSetIDs = vTDSets.split(",");
			} else {
				vTDSetIDs[0] = "N/A";
			}
		}catch (Exception ex) {
			logger.error("Error occured in fetchTestDataSets method:   "+ ex);
		}
		logger.info("Completed fetching test data sets for test case");
		return vTDSetIDs;
	}//fetchTestDataSets
	
	/* Updated By: Sindura Ravela
	 * Data: 11/4/2016
	 * Purpose: Updated the method name from mainTest to createRunSetupData
	 */
	@AfterTest
	public void writeRunSetupData() throws Exception {
		// 8 Publish results back to an Excel
		xlTempTestCases = testStepArray.toArray();
		writeXLSheets(xlResultPath, "TestCases", 0, TCSheet());
		writeXLSheets(xlResultPath, "TestSteps",1, xlTempTestCases);
	}

	/* Updated By: Sindura Ravela
	 * Date: 11/4/2016
	 * Purpose: 1. Updated with variable names from row 7 onwards
	 * 			2. Updated the Step name to add Test Data ID when there is one present
	 * 			3. Added an extra column to display the test data sets used for each test step
	 * Date: 3/21/2017
	 * Purpose: Added Element Type to the array and moved the positions of the rest of the columns
	 */ 
	public void updateTestRunnerArray(int fA, int fJ, String vTDID) 
	{
		/* Updated by: Sindura Ravela
		 * Date: 12/2/2016
		 * Purpose: Added N/A as the other options in the if statement
		 */
		
		testStepArray.add(fA, 0, strModuleID);
		testStepArray.add(fA, 1, strTestCaseID);
		if (vTDID.trim().equals("-")|| vTDID.trim().equals("N/A")) {
			testStepArray.add(fA, 2, xlTestStepData[fJ][1]+"_"+runnerTSRowCount);
		} else {
			testStepArray.add(fA, 2, xlTestStepData[fJ][1]+"_"+vTDID+"_"+runnerTSRowCount);
		}
		testStepArray.add(fA, 3, strStepDetail);
		testStepArray.add(fA, 4, strKeyWord);
		testStepArray.add(fA, 5, strElementDetails[0]);
		testStepArray.add(fA, 6, strElementDetails[1]);
		testStepArray.add(fA, 7, strTestData);
		testStepArray.add(fA, 8, strOutput);
		testStepArray.add(fA, 9, strResult);
		testStepArray.add(fA, 10, strError);
		testStepArray.add(fA, 11, strScreenShot);
		testStepArray.add(fA, 12, strTimeTaken);
		testStepArray.add(fA, 13, vTDID);
		runnerTSRowCount++;
		
	}
	
	/* Created By: Sindura Ravela
	 * Date: 11/8/2016
	 * Purpose:Updated this method to add Test Data Set as the last column header
	 */
	
	/* Updated By: Sindura Ravela
	 * Date: 11/4/2016
	 * Purpose: Fixed the issues of not displayed all the row headers, and Keyword Type is still being displayed 
	 */ 
	
	/* Updated by : Aparna
	 * Date: Oct 26 2016
	 * Reason : To display top row - skip keyword type column
	 */
	/* Updated by : Sindura Ravela
	 * Date: 3/21/2017
	 * Purpose : Added Element Type as an additional column before Element ID
	 */
	public void setTopRow()
	{
		int x = 0;
		for (x = 0; x < 4; x++)
		{  
			testStepArray.add(0, x, xlTestStepData[0][x]);  
		}
		for (x = 4; x < 7; x++)
		{  
			System.out.println("Value in the test step  " + xlTestStepData[0][x+1]);
			testStepArray.add(0, x, xlTestStepData[0][x+1]);  
		}
		for (x = 7 ; x < testStepColumnCount-2; x++) {
			System.out.println("Value in the test step  " + xlTestStepData[0][x+2]);
			testStepArray.add(0, x, xlTestStepData[0][x+2]);
		}
		testStepArray.add(0, x, "Test Data Set");
		logger.info("Completed assigning the top row");
	}

	/* Updated by: Sindura Ravela
	 * Date: 3/21/2017
	 * Purpose: Updated the method to read and capture Element Type as well from EID sheet. Also returns an array now instead
	 * 			of just one string
	 * 
	 */
	public static String[] getEID(String fEID) {
		// Go to each row in PageObjects
		String[] elementDetails = null;
		elementDetails = new String[2];
		try{
			if ((fEID.equalsIgnoreCase("-")) || (fEID.equals(null))) {
				//return fEID;
				elementDetails[0] = "-";
				elementDetails[1] = "-";
				return elementDetails;
			}else {
				for (int m = 1; m < EIDRowCount; m++) {
					// Check if the EID's match
					if (fEID.equals(xlElementIDData[m][1])) {
						// Return the xPath
						elementDetails[0] = xlElementIDData[m][2];
						elementDetails[1] = xlElementIDData[m][3];
						return elementDetails;
					}
				}
			}//end of if
		}catch(Exception e) {
			logger.error("Exception occured while fetching elements  "+ e);
		}
		elementDetails[0] = "-";
		elementDetails[1] = "-";
		return elementDetails;
	}

	/* Created By: Sindura Ravela
	 * Date: 11/4/2016
	 * Purpose: Added an extra "x" for loop to run through the number of columns in the test data sheet.
	 * 			Also compare if the the test data set ID from the test case matches the one in the test data
	 * 			sheet
	 */
	public static String getTD(String fTD, String vTDSID) {
		// Go to each row in TestData
		System.out.println(fTD + "  " + vTDSID );
		for (int n = 2; n < testDataRowCount; n++) {
			// Check if the TDID's match
			if (fTD.equalsIgnoreCase(xlTestData[n][0])) {
				for (int x = 0; x < testDataColumnCount; x++) {
					if (xlTestData[0][x].equalsIgnoreCase(vTDSID)) {
						return xlTestData[n][x];
					}
				}	
			}
		}
		// If TDID's do not match any one, then return TDID's as is
		return fTD;
	}
	
	/* Updated By: Sindura Ravela
	 * Date: 11/4/2016
	 * Purpose: 1. Updated the 2D string being used from xlTestCaseData to xlExecutableTCs since xlExecutableTCs has only the executable TCs.
	 * 			So also removed the if statement that verifies if Execute is "Y"
	 * 			2. Added fifth column to be captured that has test data sets
	 */
	
	/*Updated by : Aparna
	 * Date: Oct 26 2016
	 * Reason : Test case sheet requirement
	 */
	public String [][] TCSheet()
	{
		int nrow = 1;
		executableTCColumnCount = xlExecutableTCs[0].length;
		
		for (int tcrow = 0 ; tcrow< executableTCRowCount; tcrow++)
		{	
			testCaseArray.add(nrow, 0, xlExecutableTCs[tcrow][0]);
			testCaseArray.add(nrow, 1, xlExecutableTCs[tcrow][1]);
			testCaseArray.add(nrow, 2, xlExecutableTCs[tcrow][2]);
			testCaseArray.add(nrow, 3, xlExecutableTCs[tcrow][3]);
			testCaseArray.add(nrow, 4, "-");
			testCaseArray.add(nrow, 5, xlExecutableTCs[tcrow][5]);
			nrow++;	
		}
		//assign the header for the Test Cases output sheet
		for (int tccol=0 ; tccol< executableTCColumnCount; tccol++) 
		{
			testCaseArray.add(0, tccol, xlTestCaseData[0][tccol]);
		}
		
		String xltc_updated[][] =testCaseArray.toArray();
		logger.info("Test Cases are added to the array");
		return xltc_updated;
	}
}

