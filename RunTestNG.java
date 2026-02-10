package v20.Driver;

import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class RunTestNG {
    public static void main(String[] args) {
        // Create TestNG object
        TestNG testng = new TestNG();

        // Create a list of String to store XML file paths
        List<String> suiteFiles = new ArrayList<String>();
        suiteFiles.add("./testng.xml");

        testng.setTestSuites(suiteFiles);
        testng.run();
    }
}