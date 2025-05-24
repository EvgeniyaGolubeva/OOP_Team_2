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

    public void calculateReadingTime() {
        int wordsPerMinute = 200; // Средна скорост на четене
        int wordCount = content.split("\\s+").length;
        this.readingTime = (int) Math.ceil((double) wordCount / wordsPerMinute);
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
