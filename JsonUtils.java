package v20.Common;

import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.nio.file.Files;

public class JsonUtils {

    private static String jsonData;

    public static void loadJson(String filePath) {
        try {
            jsonData = new String(Files.readAllBytes(new File(filePath).toPath()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to load JSON file: " + filePath);
        }
    }

    public static String getString(String jsonPath) {
        try {
            return JsonPath.read(jsonData, jsonPath).toString();
        } catch (Exception e) {
            throw new RuntimeException("JSON Path not found: " + jsonPath);
        }
    }
}

