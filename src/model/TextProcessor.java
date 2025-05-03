package model;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class TextProcessor {
    public List<String> search(String text, String regex) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    public String replace(String text, String regex, String replacement) {
        return text.replaceAll(regex, replacement);
    }

    public Map<String, Long> analyzeWordFrequency(String text) {
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(word -> word.matches("[a-zA-Z]+"))  // Removed non-alphabetic values
                .parallel()  // Enables parallel processing for large files
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
    }
}