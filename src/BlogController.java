import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")

public class BlogController {
    private List<BlogPost> posts = new ArrayList<>();

    @PostMapping
    public BlogPost createPost(@RequestBody BlogPost post) {
        post.generateReadingTime();
        post.generateTags();
        post.setHtmlContent(MarkdownUtil.markdownToHtml(post.getContent()));
        posts.add(post);
        return post;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getPost(@PathVariable int id) {
        if (id < 0 || id >= posts.size()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(posts.get(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getPost(@PathVariable int id) {
        if (id < 0 || id >= posts.size()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(posts.get(id));
    }

    @PostMapping("/extract-images")
    public List<String> extractImages(@RequestBody String markdown) {
        return BlogService.extractImageUrls(markdown);
    }
}
