import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

package com.example.blog;

public class BlogService {
    private static final Set<String> STOP_WORDS = Set.of("и", "в", "на", "с", "от", "по");

    public static int estimateReadingTimeAdvanced(String content) {
        int wordsPerMinute = 200;
        String[] paragraphs = content.split("\n\n");
        int totalTime = 0;

        for (String paragraph : paragraphs) {
            int wordCount = paragraph.split("\\s+").length;
            int sentenceCount = Math.max(paragraph.split("[.!?]").length, 1);
            double complexityFactor = (double) wordCount / sentenceCount;
            int adjustedTime = (int) Math.ceil((wordCount / (double) wordsPerMinute) * (1 + complexityFactor * 0.1));
            totalTime += adjustedTime;
        }
        return totalTime;
    }

    public static List<String> generateTags(String content) {
        Map<String, Integer> freq = new HashMap<>();
        String[] words = content.toLowerCase().replaceAll("[^a-zа-я]", " ").split("\\s+");

        for (String word : words) {
            if (!STOP_WORDS.contains(word) && word.length() > 3) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }

        return freq.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static List<BlogPost> search(List<BlogPost> posts, String keyword, Integer maxTime) {
        return posts.stream()
                .filter(p -> keyword == null || 
                            p.getTitle().toLowerCase().contains(keyword.toLowerCase()) || 
                            p.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .filter(p -> maxTime == null || p.getReadingTime() <= maxTime)
                .toList();
    }

    public static List<String> extractImageUrls(String markdown) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = Pattern.compile("!\\[.*?]\\((http.*?)\\)").matcher(markdown);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }
}
