import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class BlogService {
    public static List<BlogPost> search(List<BlogPost> allPosts, String keyword, Integer maxReadingTime) {
        return allPosts.stream()
                .filter(p -> keyword == null ||
                        p.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        p.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .filter(p -> maxReadingTime == null || p.getReadingTime() <= maxReadingTime)
                .collect(Collectors.toList());
    }

    public static List<String> extractImageUrls(String markdown) {
        List<String> imageUrls = new ArrayList<>();
        Pattern pattern = Pattern.compile("!\\[.*?]\\((http.*?)\\)");
        Matcher matcher = pattern.matcher(markdown);
        while (matcher.find()) {
            imageUrls.add(matcher.group(1)); // Група 1 е URL адресът
        }
        return imageUrls;
    }

    public static void updateReadingTime(BlogPost post) {
        post.readingTime = post.estimateReadingTimeAdvanced(post.getContent());
    }

    private static final Set<String> STOP_WORDS = Set.of("и", "в", "на", "за", "с", "от", "по");
    
    public static List<String> generateTags(String content) {
        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] words = content.toLowerCase().replaceAll("[^a-zа-я]", " ").split("\\s+");

        for (String word : words) {
            if (!STOP_WORDS.contains(word) && word.length() > 3) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }

        return wordFrequency.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue()) // Сортиране по честота
                .limit(5) // Ограничаваме до 5 ключови тагове
                .map(Map.Entry::getKey)
                .toList();
    }
}
