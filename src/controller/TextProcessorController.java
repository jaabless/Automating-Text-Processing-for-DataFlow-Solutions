package controller;

import exceptions.FileProcessingException;
import exceptions.InvalidRegexPatternException;
import exceptions.NoPatternSelectedException;
import exceptions.SummarizationException;
import model.TextProcessor;
import processor.TextSummarizer;
import utils.FileHandlerUtil;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessorController {
    private final TextProcessor processor = new TextProcessor();
    private final FileHandlerUtil fileHandler = new FileHandlerUtil();

    // Predefined regex patterns
    private static final Map<String, String> REGEX_PATTERNS = Map.of(
            "Email", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            "URL", "https?://(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}(/[a-zA-Z0-9#?=&_.-]*)?",
            "Dates", "(0?[1-9]|1[0-2])[-/.](0?[1-9]|[12][0-9]|3[01])[-/.](\\d{4})"
    );

    public List<String> findMatches(String input, String selectedPattern, String customPattern) throws NoPatternSelectedException, InvalidRegexPatternException {
        String regex = (customPattern != null && !customPattern.isEmpty())
                ? customPattern
                : REGEX_PATTERNS.get(selectedPattern);

        if (regex == null || regex.trim().isEmpty()) {
            throw new NoPatternSelectedException("No regex pattern provided or selected.");
        }

        try {
            return processor.search(input, regex);
        } catch (Exception e) {
            throw new InvalidRegexPatternException("Invalid regex pattern.", e);
        }
    }

    public String replaceText(String input, String regex, String replacement) throws InvalidRegexPatternException {
        try {
            return processor.replace(input, regex, replacement);
        } catch (Exception e) {
            throw new InvalidRegexPatternException("Regex replacement failed.", e);
        }
    }

    public Map<String, Long> wordFrequency(String input) {
        return processor.analyzeWordFrequency(input);
    }

    public String readFile(String path) throws FileProcessingException {
        try {
            return fileHandler.readFile(path);
        } catch (IOException e) {
            throw new FileProcessingException("Failed to read file: " + path, e);
        }
    }

    public void writeFile(String path, String content) throws FileProcessingException {
        try {
            fileHandler.writeFile(path, content);
        } catch (IOException e) {
            throw new FileProcessingException("Failed to write file: " + path, e);
        }
    }

    public List<String> batchProcessFile(String path, Predicate<String> filter, Function<String, String> transformer) throws FileProcessingException {
        try {
            return fileHandler.batchProcessFile(path, filter, transformer);
        } catch (IOException e) {
            throw new FileProcessingException("Batch processing failed for file: " + path, e);
        }
    }

    public String replaceAndWriteToFile(String input, String regex, String replacement, String outputPath) throws FileProcessingException, InvalidRegexPatternException {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            String replaced = matcher.replaceAll(replacement);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(replaced);
            }

            return "File written successfully to: " + outputPath;
        } catch (IOException e) {
            throw new FileProcessingException("Failed to write to file: " + outputPath, e);
        } catch (Exception e) {
            throw new InvalidRegexPatternException("Regex processing failed.", e);
        }
    }

    public String summarizeText(String text, int numSentences) throws SummarizationException {
        try {
            return TextSummarizer.summarizeText(text, numSentences);
        } catch (Exception e) {
            throw new SummarizationException("Text summarization failed.", e);
        }
    }
}
