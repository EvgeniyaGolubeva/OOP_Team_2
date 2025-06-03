import java.time.LocalDateTime;

public class BlogPost {
    private String title;
    private String content;
    private int readingTime;
    private LocalDateTime createdAt;

    public BlogPost(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        calculateReadingTime();
    }

     public int estimateReadingTimeAdvanced(String content) {
        int wordsPerMinute = 200;
        String[] paragraphs = content.split("\n\n"); 
        int totalTime = 0;

        for (String paragraph : paragraphs) {
            int wordCount = paragraph.split("\\s+").length;
            int sentenceCount = paragraph.split("[.!?]").length;
            double complexityFactor = (double) wordCount / sentenceCount;

            int adjustedTime = (int) Math.ceil((wordCount / wordsPerMinute) * (1 + complexityFactor * 0.1));
            totalTime += adjustedTime;
        }

        return totalTime;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public int getReadingTime() {
        return readingTime;
    }
    public void setReadingTime(int readingTime) {
        this.readingTime = readingTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
