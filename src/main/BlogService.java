import java.util.regex.*;
import java.util.*;
import java.util.stream.Collectors;

public static List<BlogPost> search(List<BlogPost> allPosts, String keyword, Integer maxReadingTime) {
    return allPosts.stream()
            .filter(p -> keyword == null || p.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .filter(p -> maxReadingTime == null || p.getReadingTime() <= maxReadingTime)
            .collect(Collectors.toList());

public int estimateReadingTime(String markdownText) { ... }



public static List<String> extractImageUrls(String markdown) {
    List<String> imageUrls = new ArrayList<>();
    Pattern pattern = Pattern.compile("!\\[.*?]\\((.*?)\\)");
    Matcher matcher = pattern.matcher(markdown);
    while (matcher.find()) {
        imageUrls.add(matcher.group(1)); // група 1 е самият URL
    }
    return imageUrls;
}
