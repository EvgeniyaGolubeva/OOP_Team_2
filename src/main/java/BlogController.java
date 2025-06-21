import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.List;

@RestController
@RequestMapping("/blog")

public class BlogController {
    private List<BlogPost> posts = new ArrayList<>();

    @GetMapping
    public String showBlogForm() {
        return "blog_form";
    }

    @PostMapping
    public String submitPost(@RequestParam String title, @RequestParam String content) {
        BlogPost post = new BlogPost(title, content);
        posts.add(post);
        return "redirect:/blog/" + posts.size();
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer maxReadingTime,
                              Model model) {
        List<BlogPost> results = BlogService.search(posts, keyword, maxReadingTime);
        model.addAttribute("results", results);
        return "search";
    }
}
