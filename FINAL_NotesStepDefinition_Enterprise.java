
package stepdefinitions;

import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

public class NotesStepDefinition {

    private TestContext context;

    public NotesStepDefinition(TestContext context) {
        this.context = context;
    }

    @Then("Validate Notes title and description with JSON")
    public void validateNotesTitleAndDescriptionWithJSON() {

        SoftAssert soft = new SoftAssert();

        // ===============================
        // STEP 1: Get all UI Notes
        // ===============================
        List<WebElement> uiNotes = new ArrayList<>(
                context.getDriver().findElements(
                        By.xpath("//div[contains(@class,'notes-list-item')]")
                )
        );

        final String jsonBasePath = "$.policies[0].notes";
        int jsonCount = JsonUtils.getCount(jsonBasePath);

        for (int i = 0; i < jsonCount; i++) {

            String base = jsonBasePath + "[" + i + "]";

            String jsonTitle = JsonUtils.safe(
                    JsonUtils.getString(base + ".title")).trim();

            String jsonText = JsonUtils.safe(
                    JsonUtils.getString(base + ".text")).trim();

            String jsonCreatedBy = JsonUtils.safe(
                    JsonUtils.getString(base + ".createdByID")).trim();

            boolean matchFound = false;

            // ===============================
            // Search Entire UI List (Order Independent)
            // ===============================
            for (WebElement note : uiNotes) {

                // 1️⃣ Extract Title After Comma
                String uiRawTitle = note.findElement(By.xpath(".//dt"))
                        .getText().trim();

                String uiTitle = "";

                if (uiRawTitle.contains(",")) {
                    uiTitle = uiRawTitle.substring(
                            uiRawTitle.indexOf(",") + 1).trim();
                }

                // 2️⃣ Click Note To Load Description
                note.click();

                WebElement descElement = context.getDriver().findElement(
                        By.xpath("//div[contains(@class,'notes-list-item__body-text')]")
                );

                String uiFullText = descElement.getText().trim();

                // 3️⃣ Remove CreatedBy Prefix
                if (uiFullText.startsWith(jsonCreatedBy)) {
                    uiFullText = uiFullText
                            .substring(jsonCreatedBy.length())
                            .trim();
                }

                // 4️⃣ Normalize White Spaces
                uiFullText = uiFullText.replaceAll("\\s+", " ").trim();
                jsonText = jsonText.replaceAll("\\s+", " ").trim();

                uiTitle = uiTitle.replaceAll("\\s+", " ").trim();
                jsonTitle = jsonTitle.replaceAll("\\s+", " ").trim();

                // 5️⃣ Compare Title + Description
                if (uiTitle.equalsIgnoreCase(jsonTitle)
                        && uiFullText.equalsIgnoreCase(jsonText)) {

                    matchFound = true;

                    // 🔥 Remove matched UI note (Duplicate Safe)
                    uiNotes.remove(note);

                    break;
                }
            }

            if (!matchFound) {
                soft.fail("JSON Note not matching in UI -> Title: "
                        + jsonTitle);
            }
        }

        soft.assertAll();
    }
}
