package v20.Driver;

import base.Common;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import net.masterthought.cucumber.Reportable;
import org.apache.commons.io.FileUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;
import v20.Common.Config;
import v20.Common.LegacyDBFunctions;
import v20.StepDefinition.Hooks;
import v20.StepDefinition.Steps;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FeatureDriver {
//    static final Logger log = LogManager.getLogger(FeatureDriver.class);
    public static WebDriver driver = null;
    private static String cucumberFeature = Config.CUCUMBER_OPTION_FEATURES;
    private static final String cucumberGlue = Config.CUCUMBER_OPTION_GLUE;
    private static final String runFromTP = Config.RUN_FROM_TESTPLAN;
    private TestNGCucumberRunner testNGCucumberRunner;
    public static String env;
    public static String browser;
    public static String testPlan;
    private static final String testPlanRootdir = base.Config.TESTPLAN_ROOT_PATH;
    private static final String testPlanPath = base.Config.TESTPLAN_FILE_PATH;
    public static String startExeTimeDate;
    public static String startExeTimeStr;
    public static String endExeTimeDate;
    public static String featureName;
    public static String tcTags;
    private static final String excelReportPath = base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH;
    private static final String sheetNameTP = base.Config.RUNSHEET_FROM_TESTPLAN;
    static base.Common cn = new base.Common();
    private static String quitbrowser = "";
    public static ArrayList<String[]> resultData = new ArrayList<>();
    public static boolean openNewDriver = true;
    public static boolean requiredToLogin = true;
    public static String featureFilePath;
    public static HashMap<String, String> tcSheetMapsFromTD = new HashMap<>();
    DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static Config config;
    //public static int loginloop=0;
    public static LegacyDBFunctions legacyDB = new LegacyDBFunctions();
    String prod = "";
    public static String isMinimize;
    public static String browserSize;
    public static int browserWidth;
    public static int browserHeight;
    protected final static String COLOR_RESET = "\u001B[0m";
    // Regular Colors
    protected static final String BLACK = "\033[0;30m";   // BLACK
    protected static final String RED = "\033[0;31m";     // RED
    protected static final String GREEN = "\033[0;32m";   // GREEN
    protected static final String YELLOW = "\033[0;33m";  // YELLOW
    protected static final String BLUE = "\033[0;34m";    // BLUE
    protected static final String PURPLE = "\033[0;35m";  // PURPLE
    protected static final String CYAN = "\033[0;36m";    // CYAN
    protected static final String WHITE = "\033[0;37m";   // WHITE
    protected static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    protected static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    protected static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    protected static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    protected static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    // Bold High Intensity
    protected static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    protected static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    protected static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    protected static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    protected static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    protected static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    protected static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    protected static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    @Parameters({"env", "testPlanName", "browserName", "quitbrowserperfeature", "tags", "minimize", "headless", "browserSize", "feature"})
    @BeforeSuite
    public void parentSetUp(@Optional("") String environment, @Optional("") String testPlanName, String browserName, @Optional("yes") String quitbrowserperfeature, @Optional("") String tagsFromUser, @Optional("") String minimize,
                            @Optional("no") String isDriverHeadless, @Optional("") String browserSize, @Optional("") String feature) throws Exception {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ Before SUITE TestNG ]--------------------");
        System.out.println("    ---->User input: Execution Environment from POM parameter: " + environment);
        System.out.println("    ---->User input: Test Plan excel Name from POM parameter: " + testPlanName);
        System.out.println("    ---->User input: Browser Name from POM parameter: " + browserName);
        System.out.println("    ---->User input: Quit Browser Selection from POM parameter: " + quitbrowserperfeature);
        System.out.println("    ---->User input: Tags from POM parameter: " + tagsFromUser);
        System.out.println("    ---->User input: Browser Minimize Selection from POM parameter: " + minimize);
        System.out.println("    ---->User input: Headless browser selection from POM parameter: " + isDriverHeadless);
        System.out.println("    ---->User input: Browser Size from POM parameter: " + browserSize);
        System.out.println("    ---->User input: feature detail from POM parameter: " + feature);
        System.out.println("---------------------------------------------------------------------" + COLOR_RESET);
        env = environment;
        config = new Config(env);
        //legacyDB=new LegacyDBFunctions();
        Config.ENV = env;
        if (Config.userSleepMS != null && !Config.userSleepMS.equalsIgnoreCase(""))
            Common.userSleepMS = Config.userSleepMS;
        if (isDriverHeadless != null && isDriverHeadless.contains("y")) {
            base.Config.isDriverHeadless = isDriverHeadless;
        } else if (isDriverHeadless != null && isDriverHeadless.contains("n")) {
            base.Config.isDriverHeadless = "no";
        } else {
            if (Config.isDriverHeadless != null)
                base.Config.isDriverHeadless = Config.isDriverHeadless;
        }
        //For ajax load for EIS check
        if (Config.SUITE_APPNAME != null && !Config.SUITE_APPNAME.isEmpty())
            Common.appName = Config.SUITE_APPNAME;
//        Common.appName= "eis";
        System.out.println("URL: " + Config.URL);
        System.out.println("UN: " + Config.userName);
        System.out.println("PW: " + Config.passWord);
        //System.out.println("username1: "+config.URL);
        browser = browserName;
        testPlan = testPlanName;
        quitbrowser = quitbrowserperfeature;
        isMinimize = minimize;
        this.browserSize = browserSize;
        base.Common.closeExcel();
        base.Common.closeOpenBrowsers(browserName);
        cn.mkdir(base.Config.REPORT_DIR);
        cn.removedir(base.Config.SCREENSHOTSDIR);
        cn.mkdir(base.Config.SCREENSHOTSDIR);
        cn.moveToArchiveExecutionSummary();
        //specific to EIS
        cn.removedir(Config.defaultdownloaddirEIS);
        cn.mkdir(Config.defaultdownloaddirEIS);
        if (!testPlan.equalsIgnoreCase("smoke") && !testPlan.toLowerCase().contains("employer"))
            legacyDB.LegacyDBConnections();
        System.out.println("----> Report CSV path: " + base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);
        try {
            cn.createCSVResultTemplate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("----> Report CSV path: " + base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);
        try {
            cn.createCSVResultTemplate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-------- Get TAGS based on options---------------");
        if (tagsFromUser != null && !tagsFromUser.trim().equalsIgnoreCase("")) {
            tcTags = base.Common.getTagsFormatted(tagsFromUser);
            System.out.println(YELLOW + "--> User TAGS there, so user TAGS considered as first priority(Test Plan and Feature are skipped) : [" + tcTags + "]" + COLOR_RESET);
        } else {
            if (feature != null && !feature.trim().equalsIgnoreCase("")) {
                feature = feature.replaceAll(";", ":").replaceAll(",", ":");
                if (!feature.contains("\\.")) {
                    if (feature.contains(":")) {
                        String fe[] = feature.replaceFirst(":", "@").split("@");
                        cucumberFeature = cucumberFeature + "/" + fe[0] + ".feature" + ":" + fe[1];
                        System.out.println(YELLOW + "--> User TAGS Not provided, Feature has LINE to execute : [" + feature + "]" + COLOR_RESET);
                    } else {
                        feature = feature.trim() + ".feature";
                        cucumberFeature = cucumberFeature + "/" + feature.trim();
                        System.out.println(YELLOW + "--> User TAGS Not provided, Feature has NO LINE so Whole feature to execute : [" + feature + "]" + COLOR_RESET);
                    }
                } else {
                    cucumberFeature = cucumberFeature + "/" + feature.trim();
                    System.out.println(YELLOW + "--> User TAGS Not provided, Feature value taken to execute : [" + feature + "]" + COLOR_RESET);
                }
                System.out.println(BLUE + "Feature detail to run: " + cucumberFeature + COLOR_RESET);
            } else {
                tcTags = base.Common.getTags(runFromTP, tagsFromUser, testPlanName, Config.CUCUMBER_OPTION_TAGS);
                System.out.println(YELLOW + "--> User TAGS and Feature Not provided, So this will read Test Plan to take TAGS to execute : [" + tcTags + "]" + COLOR_RESET);
            }
        }
        System.out.println(BLUE_BOLD + "-------- SET CUCUMBER OPTIONS ---------------");
        System.out.println("Tags--> [" + tcTags + "]");
        System.out.println("Feature--> [" + cucumberFeature + "]");
        System.out.println("Testplan/Profile --> [" + testPlanName + "]");
        System.out.println("Glue--> [" + cucumberGlue + "]");
        System.out.println("---------------------------------------------" + COLOR_RESET);
        String tagOption = (tcTags != null && !tcTags.equalsIgnoreCase("")) ? "--tags " + tcTags + " " : "";
        System.setProperty("cucumber.options",
                cucumberFeature + " " +
                        "-m " +
                        "-s " +
                        "--glue " + cucumberGlue + " " +
                        tagOption +
                        "--plugin pretty:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/cucumber-pretty.txt " +
                        //"--plugin html:"+Config.REPORT_CUCUMBERHTML_PATH+"/html " +
                        "--plugin json:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/Cucumber.json " +
                        "--plugin junit:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/cucumber-junit-results.xml " +
                        "--plugin base.CustomFormatter " +
                        "--plugin rerun:" + base.Config.REPORT_CUCUMBERHTML_PATH + "/failed_features.txt"
        );
        System.out.println("-----------------------------------");
        Common.driverSetting(browser);
        //System.out.println("-------------- [ END ]--------------------");
        //  "-monochrome true "+
        //          "-strict true "+
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass(ITestContext context) throws Exception {
        //context.getCurrentXmlTest().getSuite().setDataProviderThreadCount(2);
        //context.getCurrentXmlTest().getSuite().setPreserveOrder(false);
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ Before Class Test NG ]--------------------" + COLOR_RESET);
        //browser=browserName.trim().toUpperCase();
        System.out.println("---> TestPlan name  from TestNG: " + testPlan);
        System.out.println("---> Browser Parameter from TestNG: " + browser);
       /* tcSheetMapsFromTD = cn.getTCSheetsMapFromTP(tcTags.replaceAll("@", "").split(","));
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());*/
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        if (tcTags != null && !tcTags.equalsIgnoreCase("")) {
            System.out.println("Tags provided so Get the TC sheets map for the tags from test data sheet");
            tcSheetMapsFromTD = cn.getTCSheetsMapFromTP(tcTags.replaceAll("@", "").split(","));
        } else {
            System.out.println("No tags provided so Get the whole TC sheets map from test data sheet");
            tcSheetMapsFromTD = cn.getTCSheetsMap();
        }
        Date Date = new Date();
        startExeTimeDate = sdf.format(Date);
        startExeTimeStr = String.valueOf((Date).getTime());
        try {
            if (!new File(Config.dynamicdatafolderpath).exists())
                (new File(Config.dynamicdatafolderpath)).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (testPlan.contains("VHI")) {
                prod = "VHI";
            } else if (testPlan.contains("VAI")) {
                prod = "VAI";
            } else if (testPlan.contains("VCI")) {
                prod = "VCI";
            } else if (testPlan.contains("LTD")) {
                prod = "LTD_LTD";
            }
            File from = null;
            if (testPlan.contains("e2eAfter")) {
//                if (testPlan.contains("e2eAfter")||testPlan.contains("Renewal")) {
                System.out.println("ThisisE2EAfterRenewal");
                if (testPlan.contains("VHI")
                        ||tcTags.contains("VHI")) {
                    System.out.println("ThissiAfterVHIRenewal");
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_HI);
                } else if (testPlan.contains("VAI") ||tcTags.contains("VAI")) {
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_AC);
                } else if (testPlan.contains("VCI")||tcTags.contains("VCI")) {
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_CI);
                } else if (testPlan.contains("STD")||tcTags.contains("STD")) {
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_STD_STD);
                } else if (testPlan.contains("LTD")||tcTags.contains("LTD")) {
                    System.out.println("ThisisLTDfILEnOTeXIST");
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_LTD_LTD);
                } else if (testPlan.contains("ORL")) {
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_SMP_ORL);
                } else if (testPlan.contains("VPL")||tcTags.contains("VPL")) {
                    System.out.println("Thisisvpl");
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_LTD_VPL);
                } else if (testPlan.contains("ASL")||tcTags.contains("ASL")) {
                    System.out.println("ThisisASL");
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_LTD_ASL);
                } else if (testPlan.contains("VAR")||tcTags.contains("VAR")) {
                    System.out.println("ThisisVAR");
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_GTL_VAR);
                } else if (testPlan.contains("VG")||tcTags.contains("VG")) {
                    System.out.println("ThisisVG");
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_GTL_VG);
                } else if (testPlan.contains("GL")||tcTags.contains("GL")) {
                    System.out.println("ThisisGL");
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_GTL_GL);

                } else if (testPlan.contains("COL")) {
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_SMP_COL);
                } else if (testPlan.contains("MEL")) {
                    from = new File(Config.dynamicdatafoldersharedpathQS + "/" + Config.CustomerDatafileName_SMP_MEL);
                }
                File to = new File(Config.dynamicdatafolderpath);
                FileUtils.copyFileToDirectory(from, to);
                System.out.println("Downloaded dynamic data file shared to local File Name: " + from.getName() + " For Product: " + prod);
                System.out.println("Dynamicdata property files moved from: " + from + " To: " + to);

            } else if (testPlan.contains("e2eS2BAfter")) {
                if (testPlan.contains("VHI")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_HI);
                } else if (testPlan.contains("VAI")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_AC);
                } else if (testPlan.contains("VCI")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_CI);
                } else if (testPlan.contains("STD")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_STD_STD);
                } else if (testPlan.contains("LTD")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_LTD_LTD);
                } else if (testPlan.contains("ORL")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_SMP_ORL);
                } else if (testPlan.contains("VPL")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_LTD_VPL);
                } else if (testPlan.contains("ASL")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_LTD_ASL);
                } else if (testPlan.contains("VAR")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_GTL_VAR);
                } else if (testPlan.contains("VG")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_GTL_VG);
                } else if (testPlan.contains("GL")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_GTL_GL);

                } else if (testPlan.contains("COL")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_SMP_COL);
                } else if (testPlan.contains("MEL")) {
                    from = new File(Config.dynamicdatafoldersharedpathSB + "/" + Config.CustomerDatafileName_SMP_MEL);
                }
                File to = new File(Config.dynamicdatafolderpath);
                FileUtils.copyFileToDirectory(from, to);
                System.out.println("Downloaded dynamic data file shared to local File Name: " + from.getName() + " For Product: " + prod);
                System.out.println("Dynamicdata property files moved from: " + from + " To: " + to);
            }
        } catch (Exception e) {
            System.out.println("Exceptionwhilemovefilesfromlocaltosharedfolder: " + e.getCause().toString());
        }
        System.out.println("Start time of Before Class: " + startExeTimeDate);
        //System.out.println("-------------- [ END ]--------------------");
    }

    @BeforeMethod(alwaysRun = true)
    public void setupTest() throws Exception {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ Before Method Test NG ]--------------------" + COLOR_RESET);
        WebDriver wd;
        Hooks.loginloop = 0;
        Hooks.quoteloop = 0;
        Hooks.steploop = 0;
        //openNewDriver=true;
        if (Config.driverPerFeature.trim().equalsIgnoreCase("yes") ||
                Config.driverPerFeature.trim().equalsIgnoreCase("y")) {
            System.out.println(BLUE + "---->  Driver open per Feature/userstory ----" + COLOR_RESET);
            base.Common.defaultFileDownloadiDir = Config.defaultdownloaddirEIS;
            System.out.println("Taking WebManager");
//            Thread.sleep(2000);
            wd = cn.getDriver(browser);
            wd.manage().window().maximize();
            if (isMinimize != null && (isMinimize.trim().equalsIgnoreCase("yes") || isMinimize.trim().equalsIgnoreCase("y")))
                wd.manage().window().minimize();
           /* if (browserSize != null) {
                browserSize = browserSize.replace(",", "*").replace("_", "*");
                browserWidth = Integer.parseInt(browserSize.split("/*")[0]);
                browserHeight = Integer.parseInt(browserSize.split("/*")[1]);
                driver.manage().window().setSize(new Dimension(browserWidth, browserHeight));
            }*/
            System.out.println("Browser Size --> " + browserSize);
            if (browserSize != null && !browserSize.trim().equalsIgnoreCase("")) {
                browserSize = browserSize.replace("*", "_").replace(",", "_");
                System.out.println("Browser Size After format --> " + browserSize);
                browserWidth = Integer.parseInt(browserSize.split("_")[0].trim());
                browserHeight = Integer.parseInt(browserSize.split("_")[1].trim());
                System.out.println("Browser width: " + browserWidth + "  Browser Height: " + browserHeight);
                wd.manage().window().setSize(new Dimension(browserWidth, browserHeight));
            }
            Steps.driver = wd;
        }
        // System.out.println("-------------- [ END ]--------------------");
    }

    @Test(groups = "EIS", description = "EIS Features", dataProvider = "features")
    public void feature(CucumberFeatureWrapper cucumberFeature) {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ TEST TestNG ] -------------------------------" + COLOR_RESET);
        featureName = cucumberFeature.getCucumberFeature().getGherkinFeature().getName();
        featureFilePath = cucumberFeature.getCucumberFeature().getPath();
        System.out.println("----- [Start Feature: " + featureName + "  - Filepath: " + featureFilePath + " ]---------");
        testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
        System.out.println("-------------- [ END TEST ]--------------------");
    }

    //@Parameters("browserName")
    @AfterMethod(alwaysRun = true)
    public void tearDownTest() {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ After Method Test NG ]--------------------" + COLOR_RESET);
        System.out.println(YELLOW + "Driver close required in feature level: " + quitbrowser + COLOR_RESET);
        try {
            //Close driver per feature
            if (quitbrowser.trim().contains("y")) {
                System.out.println(YELLOW_BOLD + "---->  Driver closed per Feature ----" + COLOR_RESET);
                //driver.quit();
                Steps.driver.quit();
                base.Common.sleep(3000);
            } else
                System.out.println("Driver is not closed as per parameter");
        } catch (Exception e) {
            System.out.println(RED + "---->  !!! EXCEPTION: Some problem in quiting browser ----" + COLOR_RESET);
        }
        try {
            //Close driver per feature
            if (quitbrowser.trim().contains("y")) {
                System.out.println(YELLOW_BOLD + "---->  Driver killed per Feature ----" + COLOR_RESET);
                base.Common.closeOpenBrowsers(browser);
                base.Common.sleep(2000);
            } else
                System.out.println("Driver is not killed as per parameter");
        } catch (Exception e) {
            System.out.println(RED + "---->  !!! EXCEPTION: Some problem in closing browser from kill task ----" + COLOR_RESET);
        }
        //System.out.println("--------------[ END ]--------------------");
    }

    //@DataProvider(parallel = true)
    @DataProvider(name = "features")
    public Object[][] features() {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ DataProvider Test NG ]--------------------" + COLOR_RESET);
        //System.out.println("This is TestNG Data provider reading Feature file to get looped based on number of feature file");
        System.out.println(BLUE + "---->  This is @DataProvider start, execute each feature files as iterations" + COLOR_RESET);
        return testNGCucumberRunner.provideFeatures();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ After Class Test NG ]--------------------" + COLOR_RESET);
        //Date Date = new Date();
        endExeTimeDate = sdf.format(new Date());
        System.out.println("Execution completed time: " + endExeTimeDate);
        String endTimestr = String.valueOf((new Date()).getTime());
        try {
            double duration = Common.getTotalExecutionTime(startExeTimeStr, endTimestr, "min");
            Common.writeToFile(base.Config.exedurationfilepath, String.valueOf(duration), false);
        } catch (Exception e) {
            System.out.println("Some issue on calculating duration or issue on writing value to file: " + e.getMessage());
        }
        //base.Common.closeOpenBrowsers(browser);
        //Close driver at the end of all features executed
      /*  try {
//            Hooks.driver.quit();
//            base.Common.closeOpenBrowsers(browser);
        } catch (Exception e) {
            System.out.println("---->  !!! EXCEPTION: Some problem in closing browser from killtask ----");
        }*/
        try {
            System.out.println("Start file upload");
            File from = null;
            String sharedExistingFilePath = "";
            File to;
            if (testPlan.contains("e2eBefore") || testPlan.contains("e2eS2BBefore")) {
                System.out.println("This is Renewal policies Before");
                System.out.println(BLUE + "--- E2E related dynamic datafile handling configuration---------" + COLOR_RESET);
                if (testPlan.contains("CVcheck")) {
                    to = new File(Config.dynamicdatafoldersharedpathCV);
                    if (testPlan.contains("VHI")) {
                        from = new File(Config.CustomerDatafilePath_HI);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_HI;
                    } else if (testPlan.contains("VAI")) {
                        System.out.println("This CV Validation EIS Policies Creation");
                        from = new File(Config.CustomerDatafilePath_AC);
                        System.out.println("frompathCV-->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_AC;
                        System.out.println("sharedExistingFilePathCV-->" + sharedExistingFilePath);
                    } else if (testPlan.contains("VCI")) {
                        System.out.println("CV file Taken");
                        from = new File(Config.CustomerDatafilePath_CI);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_CI;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("STD_STD")) {
                        System.out.println("CV file Taken");
                        from = new File(Config.CustomerDatafilePath_STD_STD);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_STD_STD;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("LTD_LTD")) {
                        System.out.println("THISISBEFOREBATCHRUNANDLTDPLAN");
                        from = new File(Config.CustomerDatafilePath_LTD_LTD);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_LTD;
                        System.out.println("TOpath -->" + to);
                    } else if (testPlan.contains("SMP_ORL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_ORL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_SMP_ORL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("LTD_VPL")) {
                        System.out.println("ThisisVPLFile");
                        from = new File(Config.CustomerDatafilePath_LTD_VPL);
                        System.out.println("frompath -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_VPL;
                        System.out.println("TOpath -->" + to);
                    } else if (testPlan.contains("LTD_ASL")) {
                        System.out.println("ThisisALLFile");
                        from = new File(Config.CustomerDatafilePath_LTD_ASL);
                        System.out.println("frompath -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_ASL;
                        System.out.println("TOpath -->" + to);
                    } else if (testPlan.contains("GTL_VAR")) {
                        System.out.println("ThisisVARFile");
                        from = new File(Config.CustomerDatafilePath_GTL_VAR);
                        System.out.println("frompath -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_VAR;
                        System.out.println("TOpath -->" + to);
                    } else if (testPlan.contains("GTL_VG")) {
                        System.out.println("ThisisVGFile");
                        from = new File(Config.CustomerDatafilePath_GTL_VG);
                        System.out.println("frompath -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_VG;
                        System.out.println("TOpath -->" + to);
                    } else if (testPlan.contains("GTL_GL")) {
                        System.out.println("ThisisGLFile");
                        from = new File(Config.CustomerDatafilePath_GTL_GL);
                        System.out.println("frompath -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_GL;
                        System.out.println("TOpath -->" + to);

                    } else if (testPlan.contains("SMP_COL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_COL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_SMP_COL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("SMP_MEL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_MEL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_SMP_MEL;
                        System.out.println("TO path -->" + to);
                    }
                } else if (testPlan.contains("e2eS2BBefore")) {
                    to = new File(Config.dynamicdatafoldersharedpathSB);
                    System.out.println("to file path -- >" + to);
                    if (testPlan.contains("VHI")) {
                        from = new File(Config.CustomerDatafilePath_HI);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_HI;
                    } else if (testPlan.contains("VAI")) {
                        from = new File(Config.CustomerDatafilePath_AC);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_AC;
                    } else if (testPlan.contains("VCI")) {
                        System.out.println("CV file Taken");
                        from = new File(Config.CustomerDatafilePath_CI);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_CI;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("STD")) {
                        System.out.println("CV file Taken");
                        from = new File(Config.CustomerDatafilePath_STD_STD);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_STD_STD;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("LTD")) {
                        System.out.println("CV file Taken");
                        from = new File(Config.CustomerDatafilePath_LTD_LTD);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_LTD;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("VPL")) {
                        from = new File(Config.CustomerDatafilePath_LTD_VPL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_VPL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("ASL")) {
                        from = new File(Config.CustomerDatafilePath_LTD_ASL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_ASL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("VAR")) {
                        from = new File(Config.CustomerDatafilePath_GTL_VAR);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_VAR;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("VG")) {
                        from = new File(Config.CustomerDatafilePath_GTL_VG);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_VG;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("GL")) {
                        from = new File(Config.CustomerDatafilePath_GTL_GL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_GL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("CTL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_CTL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_CTL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("ORL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_ORL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_ORL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("COL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_COL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_COL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("MEL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_MEL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_MEL;
                        System.out.println("TO path -->" + to);
                    }
                } else if (testPlan.contains("Renewal")) {
                    System.out.println("Added03021015s");
                    System.out.println("This Renewal Policies");
//                    to = new File(Config.dynamicdatafoldersharedpathSB);
                    to = new File(Config.dynamicdatafoldersharedpathQS);
                    System.out.println("to file path -- >" + to);
                    if (testPlan.contains("VHI") || tcTags.contains("VHI")) {
                        System.out.println("ThisisVHI");
                        from = new File(Config.CustomerDatafilePath_HI);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_HI;
                        System.out.println("sharedExistingFilePath-->" + sharedExistingFilePath);
                    } else if (testPlan.contains("VAI")|| tcTags.contains("VAI")) {
                        System.out.println("ThisisVAI");
                        from = new File(Config.CustomerDatafilePath_AC);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_AC;
                    } else if (testPlan.contains("VCI")|| tcTags.contains("VCI")) {
                        System.out.println("ThisisVCI");
                        from = new File(Config.CustomerDatafilePath_CI);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_CI;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("STD")|| tcTags.contains("STD")) {
                        System.out.println("This is STD");
                        from = new File(Config.CustomerDatafilePath_STD_STD);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_STD_STD;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("VPS")|| tcTags.contains("VPS")) {
                        System.out.println("ThisisVPS");
                        from = new File(Config.CustomerDatafilePath_STD_VPS);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_STD_VPS;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("LTD")|| tcTags.contains("LTD")) {
                        System.out.println("CV file Taken");
                        from = new File(Config.CustomerDatafilePath_LTD_LTD);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_LTD;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("VPL")|| tcTags.contains("VPL")) {
                        from = new File(Config.CustomerDatafilePath_LTD_VPL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_VPL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("ASL")|| tcTags.contains("ASL")) {
                        from = new File(Config.CustomerDatafilePath_LTD_ASL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_ASL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("VAR")|| tcTags.contains("VAR")) {
                        from = new File(Config.CustomerDatafilePath_GTL_VAR);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_VAR;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("VG")|| tcTags.contains("VG")) {
                        from = new File(Config.CustomerDatafilePath_GTL_VG);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_VG;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("GL")|| tcTags.contains("GL")) {
                        from = new File(Config.CustomerDatafilePath_GTL_GL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_GL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("CTL")|| tcTags.contains("CTL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_CTL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_CTL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("DBL")|| tcTags.contains("DBL")) {
                        System.out.println("ThisisDBL");
                        from = new File(Config.CustomerDatafilePath_SMP_DBL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_DBL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("MEL")|| tcTags.contains("MEL")) {
                        System.out.println("ThisisMEL");
                        from = new File(Config.CustomerDatafilePath_SMP_MEL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_MEL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("MAL")|| tcTags.contains("MAL")) {
                        System.out.println("ThisisMAL");
                        from = new File(Config.CustomerDatafilePath_SMP_MAL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_MAL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("ORL")|| tcTags.contains("ORL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_ORL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_ORL;
                        System.out.println("TO path -->" + to);
                    } else if (testPlan.contains("COL")|| tcTags.contains("COL")) {
                        from = new File(Config.CustomerDatafilePath_SMP_COL);
                        System.out.println("from path -->" + from);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafilePath_SMP_COL;
                        System.out.println("TO path -->" + to);

                    }
                } else {
                    to = new File(Config.dynamicdatafoldersharedpathQS);
                    if (testPlan.contains("VHI")) {
                        //prod="VHI";
                        from = new File(Config.CustomerDatafilePath_HI);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_HI;
                        System.out.println("sharedExistingFilePathVHI-->" + sharedExistingFilePath);
                    } else if (testPlan.contains("VAI")) {
                        //prod="VAI";
                        from = new File(Config.CustomerDatafilePath_AC);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_AC;
                        System.out.println("sharedExistingFilePathOther-->" + sharedExistingFilePath);
                    } else if (testPlan.contains("VCI")) {
                        //prod="VCI";
                        System.out.println("CV EIS file Taken");
                        from = new File(Config.CustomerDatafilePath_CI);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_CI;
                    } else if (testPlan.contains("STD")) {
                        System.out.println("CV EIS file Taken");
                        from = new File(Config.CustomerDatafilePath_STD_STD);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_STD_STD;
                    } else if (testPlan.contains("LTD")) {
                        from = new File(Config.CustomerDatafilePath_LTD_LTD);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_LTD;
                    } else if (testPlan.contains("VPL")) {
                        from = new File(Config.CustomerDatafilePath_LTD_VPL);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_VPL;
                    } else if (testPlan.contains("ASL")) {
                        from = new File(Config.CustomerDatafilePath_LTD_ASL);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_LTD_ASL;
                    } else if (testPlan.contains("VAR")) {
                        from = new File(Config.CustomerDatafilePath_GTL_VAR);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_VAR;
                    } else if (testPlan.contains("VG")) {
                        from = new File(Config.CustomerDatafilePath_GTL_VG);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_VG;
                    } else if (testPlan.contains("GL")) {
                        from = new File(Config.CustomerDatafilePath_GTL_GL);
                        sharedExistingFilePath = to.getPath() + "/" + Config.CustomerDatafileName_GTL_GL;
                    }
                }
                DateFormat dateformat = new SimpleDateFormat("YYYYMMddHHmmss");
                File backupFile = new File(sharedExistingFilePath + "_" + dateformat.format(new Date()));
                File orginalInShared = new File(sharedExistingFilePath);
                //if(backupFile.exists()) {
                if (orginalInShared.exists()) {
                    System.out.println("Same property file in shared folder, so taking backup to crate in shared folder");
                    FileUtils.copyFile(new File(sharedExistingFilePath), backupFile);
                    base.Common.sleep(2000);
                    FileUtils.copyFileToDirectory(from, to);
                    System.out.println("Dynamic data property files moved from: " + from + " To: " + to);
                } else {
                    System.out.println("No property file in shared folder, so directly uploading to shared folder");
                    FileUtils.copyFileToDirectory(from, to);
                    System.out.println("Dynamicdata property files moved from:2 " + from + " To: " + to);
                }
            }
            System.out.println("--------------");
        } catch (Exception e) {
            System.out.println(RED + "Exception while move files from local to shared folder: " + e.getMessage() + COLOR_RESET);
        }
        testNGCucumberRunner.finish();
        // System.out.println("-------------- END --------------------");
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() throws Exception {
        System.out.println(CYAN_BOLD_BRIGHT + "-------------- [ After SUITE TestNG ]--------------------" + COLOR_RESET);
        System.out.println("----> Updating result in SUMMARY REPORT EXCEL");
        cn.csvWriterFromArrayList(resultData, base.Config.REPORT_EXECUTIONSUMMARYREPORT_PATH);
        try {
            String outputdatapath;
            if (prod.trim().equals(""))
                outputdatapath = Config.dynamicdatafolderpath + "/OutputData.csv";
            else
                outputdatapath = Config.dynamicdatafolderpath + "/OutputData_" + prod + ".csv";
            System.out.println(BLUE + "Create the Output data csv -> " + outputdatapath + COLOR_RESET);
            v20.Common.Common.readResultArrayListAndConvertToDataCSV(resultData, outputdatapath, prod);
        } catch (Exception e) {
            System.out.println(YELLOW + "Unable to Create the Output data CSV -> " + e.getMessage() + COLOR_RESET);
        }
        System.out.println(BLUE + "Add result data into Cucumber report and Generate Report" + COLOR_RESET);
        Reportable results = cn.cucumberReportsHtml(base.Config.REPORT_CUCUMBERHTML_PATH, base.Config.REPORT_CUCUMBERHTML_PATH + "/Cucumber.json", "", Config.SUITE_APPNAME, "Sprint 46", base.Config.OS_NAME, base.Config.testbrowserAndVersion, "Shinka P2");
        try {
            System.out.println(
                    "Execution duration: " + results.getDuration() + "\n" +
                            "Total features: " + results.getFeatures() + "\n" +
                            "Passed Features: " + results.getPassedFeatures() + "\n" +
                            "Failed features: " + results.getFailedFeatures() + "\n" +
                            "Passed Scenarios: " + results.getPassedScenarios() + "\n" +
                            "Failed Scenarios: " + results.getFailedScenarios()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String cucumberHTLFilepath = (((new File(base.Config.REPORT_CUCUMBERHTML_PATH)).getAbsolutePath()) + "/cucumber-html-reports/overview-features.html").replaceAll("\\\\", "/");
            System.out.println(CYAN_BOLD_BRIGHT + "\n============< Cucumber Report >=============== \n" + COLOR_RESET + "file:///" + cucumberHTLFilepath + CYAN_BOLD_BRIGHT + "\n==============================================\n" + COLOR_RESET);
        } catch (Exception e) {
        }
        Thread.sleep(1000);
        //cn.detailCucumberReports(base.Config.REPORT_CUCUMBERHTML_PATH, "testResult/cucumberReport/Cucumber.json", "testResult/cucumberReport/cucumber-html-reports/embeddings/", "50");
        //System.out.println("Close all the DB connection");
        System.out.println("-------------- [ END ]--------------------");
    }
}