package utils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileHandlerUtil {
    public String readFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            LoggerUtil.logInfo("File successfully read: " + path);
        } catch (IOException e) {
            LoggerUtil.logError("Failed to read file: " + path, e);
            throw e;
        }
        return content.toString();
    }

    public void writeFile(String path, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content);
            LoggerUtil.logInfo("File successfully written: " + path);
        } catch (IOException e) {
            LoggerUtil.logError("Failed to write file: " + path, e);
            throw e;
        }
    }

    public List<String> batchProcessFile(String path, Predicate<String> filter, Function<String, String> transformer) throws IOException {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (filter.test(line)) {
                    result.add(transformer.apply(line));
                }
            }
        }
        return result;
    }


}