
package stepdefinitions;

import io.cucumber.java.en.Then;
import org.testng.asserts.SoftAssert;
import java.util.*;

public class NotesStepDefinition {

    private NotesPage std;

    public NotesStepDefinition(TestContext context) {
        this.std = context.getPageObjectManager().getNotesPage();
    }

    @Then("Validate Notes tab data with JSON")
    public void validateNotesTabDataWithJSON() {

        SoftAssert soft = new SoftAssert();

        // ===============================
        // STEP 1: Get UI Data
        // ===============================
        List<Map<String, String>> uiRows = std.getUiNotesData();

        Map<String, Integer> uiCounts = new LinkedHashMap<>();
        for (Map<String, String> row : uiRows) {
            String key = row.getOrDefault("CreatedBy", "") + "||" +
                         row.getOrDefault("Title", "");
            uiCounts.merge(key, 1, Integer::sum);
            System.out.println("UI -> " + key);
        }

        // ===============================
        // STEP 2: Get JSON Data
        // ===============================
        final String jsonBasePath = "$.policies[0].notes";
        int jsonCount = JsonUtils.getCount(jsonBasePath);

        Map<String, Integer> jsonCounts = new LinkedHashMap<>();

        for (int i = 0; i < jsonCount; i++) {

            String base = jsonBasePath + "[" + i + "]";

            String jTitle = JsonUtils.safe(JsonUtils.getString(base + ".title"));
            String jId = JsonUtils.safe(JsonUtils.getString(base + ".createdByID"));
            String jFn = JsonUtils.safe(JsonUtils.getString(base + ".createdByFirstName"));
            String jLn = JsonUtils.safe(JsonUtils.getString(base + ".createdByLastName"));

            String expectedCreatedBy;
            if (jId != null && !jId.trim().isEmpty()) {
                expectedCreatedBy = jId.trim().toUpperCase();
            } else {
                expectedCreatedBy = (jFn + " " + jLn).trim().toLowerCase();
            }

            String expectedTitle = jTitle == null ? "" : jTitle.trim().toLowerCase();

            String key = expectedCreatedBy + "||" + expectedTitle;

            jsonCounts.merge(key, 1, Integer::sum);

            System.out.println("JSON -> " + key);
        }

        // ===============================
        // STEP 3: Order Independent Compare
        // ===============================
        Set<String> allKeys = new LinkedHashSet<>();
        allKeys.addAll(uiCounts.keySet());
        allKeys.addAll(jsonCounts.keySet());

        for (String key : allKeys) {

            int uiCount = uiCounts.getOrDefault(key, 0);
            int jsonCountValue = jsonCounts.getOrDefault(key, 0);

            if (uiCount != jsonCountValue) {
                soft.fail("Mismatch for Note -> " + key +
                        " | UI Count: " + uiCount +
                        " | JSON Count: " + jsonCountValue);
            }
        }

        soft.assertAll();
    }
}
