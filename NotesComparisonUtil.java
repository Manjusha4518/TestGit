
import java.util.*;

public class NotesComparisonUtil {

    public static void compareNotes(
            List<Map<String, String>> uiData,
            List<Map<String, String>> jsonData) {

        Map<String, Integer> uiCounts = buildMultiset(uiData);
        Map<String, Integer> jsonCounts = buildMultiset(jsonData);

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(uiCounts.keySet());
        allKeys.addAll(jsonCounts.keySet());

        for (String key : allKeys) {
            int uiCount = uiCounts.getOrDefault(key, 0);
            int jsonCount = jsonCounts.getOrDefault(key, 0);

            if (uiCount != jsonCount) {
                throw new AssertionError(
                        "Mismatch for Note -> " + key +
                        " | UI Count: " + uiCount +
                        " | JSON Count: " + jsonCount);
            }
        }

        System.out.println("All Notes matched successfully (Order Independent)");
    }

    private static Map<String, Integer> buildMultiset(List<Map<String, String>> data) {
        Map<String, Integer> counts = new HashMap<>();

        for (Map<String, String> row : data) {
            String key = row.getOrDefault("CreatedBy", "") + "||" +
                         row.getOrDefault("Title", "");

            counts.merge(key, 1, Integer::sum);
        }
        return counts;
    }
}
