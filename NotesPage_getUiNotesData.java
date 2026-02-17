
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotesPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public NotesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public List<Map<String, String>> getUiNotesData() {
        List<Map<String, String>> uiDataList = new ArrayList<>();

        String listBaseXpath = "//div[@class='notes-bench__note-list']";
        By listLocator = By.xpath(listBaseXpath);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(listLocator));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(listBaseXpath + "//div[contains(@class,'notes-list-item')]")
            ));
        } catch (TimeoutException e) {
            System.out.println("No Notes found on page.");
            return uiDataList;
        }

        WebElement list = driver.findElement(listLocator);
        List<WebElement> items = list.findElements(By.xpath(".//div[contains(@class,'notes-list-item')]"));

        for (WebElement item : items) {

            String rawTitle = "";
            try {
                WebElement titleEl = item.findElement(By.xpath(".//dt[contains(@class,'notes-ellipsis')]"));
                rawTitle = Optional.ofNullable(titleEl.getAttribute("title"))
                        .filter(t -> !t.trim().isEmpty())
                        .orElseGet(() -> Optional.ofNullable(titleEl.getText()).orElse(""))
                        .trim();
            } catch (NoSuchElementException ignored) {}

            String normTitle = normalizeNoteTitleAfterComma(rawTitle);

            String createdBy = "";
            try {
                WebElement bodyEl = item.findElement(By.xpath(".//div[contains(@class,'notes-list-item__body-text')]"));
                String body = bodyEl.getText() == null ? "" : bodyEl.getText().trim();
                createdBy = extractLikelyCreatedBy(body);
            } catch (NoSuchElementException ignored) {}

            String normCreatedBy = normalizeCreatedBy(createdBy);

            if (!normTitle.isEmpty() || !normCreatedBy.isEmpty()) {
                Map<String, String> row = new HashMap<>();
                row.put("Title", normTitle);
                row.put("CreatedBy", normCreatedBy);
                uiDataList.add(row);
            }
        }

        return uiDataList;
    }

    private String normalizeNoteTitleAfterComma(String s) {
        if (s == null) return "";
        String v = s.trim();

        v = v.replaceFirst("^\\d{4}-\\d{2}-\\d{2}\\s+", "");
        v = v.replaceFirst("(?i)^PACS\\s*,\\s*", "");

        int idx = v.indexOf(',');
        if (idx >= 0 && idx + 1 < v.length()) {
            v = v.substring(idx + 1).trim();
        }

        return v.replaceAll("\\s+", " ").trim().toLowerCase(Locale.ROOT);
    }

    private String extractLikelyCreatedBy(String body) {
        if (body == null) return "";
        Matcher m = Pattern.compile("^([A-Za-z0-9]{3,})\\b").matcher(body.trim());
        if (m.find()) return m.group(1);
        return "";
    }

    private String normalizeCreatedBy(String s) {
        if (s == null) return "";
        String v = s.trim();
        if (v.matches("^[A-Za-z0-9]{3,}$")) {
            return v.toUpperCase(Locale.ROOT);
        }
        return v.replaceAll("\\s+", " ").trim().toLowerCase(Locale.ROOT);
    }
}
