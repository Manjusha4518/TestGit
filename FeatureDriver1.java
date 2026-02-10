
package v20.Driver;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.util.Arrays;

public class FeatureDriver extends AbstractTestNGCucumberTests {

    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext context) {

        String runJson =
                context.getCurrentXmlTest().getParameter("run.json");

        if (runJson != null) {
            System.setProperty("run.json", runJson);
        }
    }

    @Override
    public Object[][] scenarios() {

        boolean isJsonRun =
                System.getProperty("run.json", "false")
                        .equalsIgnoreCase("true");

        if (!isJsonRun) {
            return super.scenarios();
        }

        File folder = new File(
                System.getProperty("user.dir") + "/EIS/testData");

        File[] jsonFiles = folder.listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".json"));

        if (jsonFiles == null || jsonFiles.length == 0) {
            throw new RuntimeException("No JSON files found in testData folder");
        }

        Arrays.sort(jsonFiles);

        Object[][] cucumberScenarios = super.scenarios();
        Object[][] finalData =
                new Object[cucumberScenarios.length * jsonFiles.length][1];

        int index = 0;

        for (File json : jsonFiles) {
            for (Object[] scenario : cucumberScenarios) {
                System.setProperty("current.json.file", json.getName());
                finalData[index++] = scenario;
            }
        }

        return finalData;
    }
}
