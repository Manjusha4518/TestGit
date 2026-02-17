
package stepdefinitions;

import io.cucumber.java.en.Then;
import org.testng.asserts.SoftAssert;
import pages.NotesPage;
import utils.JsonUtils;

import java.util.*;

public class NotesStepDefinition {

    private NotesPage notesPage;
    private SoftAssert softAssert;

    public NotesStepDefinition(TestContext context) {
        this.notesPage = context.getPageObjectManager().getNotesPage();
        this.softAssert = context.getSoftAssert();
    }

    @Then("Validate Notes tab data with JSON")
    public void validate_notes_tab_data_with_json() {

        // ===== UI DATA =====
        List<Map<String, String>> uiData = notesPage.getUiNotesData();

        // ===== JSON DATA =====
        final String jsonBasePath = "$.policies[0].notes";
        int jsonCount = JsonUtils.getCount(jsonBasePath);

        List<Map<String, String>> jsonData = new ArrayList<>();

        for (int i = 0; i < jsonCount; i++) {

            String base = jsonBasePath + "[" + i + "]";

            String title = JsonUtils.safe(JsonUtils.getString(base + ".title"));
            String id = JsonUtils.safe(JsonUtils.getString(base + ".createdByID"));
            String firstName = JsonUtils.safe(JsonUtils.getString(base + ".createdByFirstName"));
            String lastName = JsonUtils.safe(JsonUtils.getString(base + ".createdByLastName"));

            String expectedCreatedBy = JsonUtils.computeExpectedCreatedBy(id, firstName, lastName);
            String expectedTitle = JsonUtils.normalizeJsonTitleAfterComma(title);

            Map<String, String> row = new HashMap<>();
            row.put("Title", expectedTitle);
            row.put("CreatedBy", expectedCreatedBy);

            jsonData.add(row);
        }

        // ===== ORDER INDEPENDENT COMPARISON =====
        Map<String, Integer> uiCounts = buildMultiset(uiData);
        Map<String, Integer> jsonCounts = buildMultiset(jsonData);

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(uiCounts.keySet());
        allKeys.addAll(jsonCounts.keySet());

        for (String key : allKeys) {

            int uiCount = uiCounts.getOrDefault(key, 0);
            int jsonCountValue = jsonCounts.getOrDefault(key, 0);

            if (uiCount != jsonCountValue) {
                softAssert.fail(
                        "Mismatch for Note -> " + key +
                        " | UI Count: " + uiCount +
                        " | JSON Count: " + jsonCountValue
                );
            }
        }

        softAssert.assertAll();
    }

    private Map<String, Integer> buildMultiset(List<Map<String, String>> data) {

        Map<String, Integer> counts = new HashMap<>();

        for (Map<String, String> row : data) {

            String key = row.getOrDefault("CreatedBy", "") + "||" +
                         row.getOrDefault("Title", "");

            counts.merge(key, 1, Integer::sum);
        }

        return counts;
    }
}
