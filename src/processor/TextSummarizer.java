package processor;

import java.util.*;
import java.util.stream.Collectors;

public class TextSummarizer {

    public static String summarizeText(String text, int numSentences) {
        // 1. Split into sentences
        String[] sentences = text.split("(?<=[.!?])\\s+");
        if (sentences.length <= numSentences) return text;

        // 2. Map Phase – Word Frequencies
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String sentence : sentences) {
            for (String word : sentence.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+")) {
                if (!word.isBlank()) {
                    wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                }
            }
        }

        // 3. Reduce Phase – Score sentences based on word significance
        Map<String, Double> sentenceScores = new LinkedHashMap<>();
        for (String sentence : sentences) {
            double score = 0.0;
            for (String word : sentence.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+")) {
                score += wordFreq.getOrDefault(word, 0);
            }
            sentenceScores.put(sentence, score);
        }

        // 4. Select top N sentences
        return sentenceScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(numSentences)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(" "));
    }
}
