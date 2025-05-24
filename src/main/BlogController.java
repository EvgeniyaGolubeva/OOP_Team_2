@GetMapping("/blog")
public String showBlogForm() { ... }

@PostMapping("/blog")
public String submitPost(...) { ... }

@GetMapping("/search")
public String searchPosts(@RequestParam String keyword, ...) { ... }
