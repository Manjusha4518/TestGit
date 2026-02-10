
package v20.StepDefinition;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import org.testng.Reporter;
import v20.Common.JsonUtils;
import v20.Common.Config;
import v20.Driver.FeatureDriver;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Hooks {

    public static Scenario sc;
    public static String message = "";

    public Date beforeScExeTimeDate;
    public Date afterScExeTimeDate;

    @Before
    public void initializeTest(Scenario scenario) throws Exception {

        message = "";

        boolean isJsonRun =
                System.getProperty("run.json", "false")
                        .equalsIgnoreCase("true");

        if (isJsonRun) {

            String jsonFileName =
                    System.getProperty("current.json.file");

            if (jsonFileName == null || jsonFileName.isEmpty()) {
                throw new RuntimeException("JSON file not set for execution");
            }

            String jsonPath = System.getProperty("user.dir")
                    + "/EIS/testData/"
                    + jsonFileName;

            File jsonFile = new File(jsonPath);

            if (!jsonFile.exists()) {
                throw new RuntimeException("JSON file not found: " + jsonPath);
            }

            System.out.println("Executing with JSON -> " + jsonFileName);
            JsonUtils.loadJson(jsonFile.getAbsolutePath());
        }

        sc = scenario;
        beforeScExeTimeDate = new Date();
    }

    @After
    public void postTestProcess(Scenario scenario) throws IOException {
        afterScExeTimeDate = new Date();
        Reporter.log("Scenario completed: " + scenario.getName());
    }
}
