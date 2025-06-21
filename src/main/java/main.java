public class Main {
    public static void main(String[] args) {
        String markdownContent = """
                # Welcome in the blog
                This is a sample article with several paragraphs.
                ![cat](https://example.com/cat.jpg)
                Winds on Neptune can blow faster than 1,200 miles an hour.
                """;

        BlogPost post = new BlogPost("Моят първи пост", markdownContent);
        post.generateReadingTime();
        post.generateTags();
        post.setHtmlContent(MarkdownUtil.markdownToHtml(markdownContent));

        System.out.println("Заглавие: " + post.getTitle());
        System.out.println("Време за четене: " + post.getReadingTime() + " минути");
        System.out.println("Генерирани тагове: " + post.getTags());
        System.out.println("Изображения: " + BlogService.extractImageUrls(markdownContent));
        System.out.println("HTML съдържание:\n" + post.getHtmlContent());
    }
}
