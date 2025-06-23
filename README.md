
Scenario ID | Condition | Example Fragment | Expected Result
--- | --- | --- | ---
SCENARIO-002 | Missing required field `title` | ```{"content": "Blog content"}``` | HTTP Status 400 Bad Request; error message indicating `title` is required.
SCENARIO-003 | Field `title` exceeds maximum length | ```{"title": "A very long title that exceeds the maximum allowed length of 120 characters... (more text)"}``` | HTTP Status 400 Bad Request; error message indicating `title` is too long.
SCENARIO-004 | Duplicate `title` for the same author | ```{"title": "Existing Blog Title", "content": "New content"}``` | HTTP Status 409 Conflict; error message indicating `title` must be unique per author.
SCENARIO-005 | XSS attack in `title` | ```{"title": "<script>alert('XSS')</script>", "content": "Content with XSS"}``` | HTTP Status 400 Bad Request; error message indicating invalid characters in `title`.
SCENARIO-007 | Field `content` exceeds maximum length | ```{"title": "Blog Title", "content": "A very long content that exceeds the maximum allowed length of 50,000 characters... (more text)"}``` | HTTP Status 400 Bad Request; error message indicating `content` is too long.
SCENARIO-008 | Field `content` is too short if not null | ```{"title": "Blog Title", "content": "Short"}``` | HTTP Status 400 Bad Request; error message indicating `content` must be at least 10 characters.
SCENARIO-010 | `thumbnailUrl` exceeds size limit | ```{"title": "Blog Title", "content": "Content", "thumbnailUrl": "https://example.com/large-image.jpg"}``` | HTTP Status 400 Bad Request; error message indicating image size exceeds 2 kB.
SCENARIO-011 | Invalid `tags` format (not an array) | ```{"title": "Blog Title", "content": "Content", "tags": "tag1, tag2"}``` | HTTP Status 400 Bad Request; error message indicating `tags` must be an array.
SCENARIO-012 | `tags` array exceeds maximum length | ```{"title": "Blog Title", "content": "Content", "tags": ["tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10", "tag11"]}``` | HTTP Status 400 Bad Request; error message indicating `tags` array must have 0-20 elements.
SCENARIO-013 | `tags` contains too long tag | ```{"title": "Blog Title", "content": "Content", "tags": ["tag1", "tag2", "a-very-long-tag-that-exceeds-thirty-characters"]}``` | HTTP Status 400 Bad Request; error message indicating tags must be 1-30 characters.
SCENARIO-014 | `tags` contains invalid characters | ```{"title": "Blog Title", "content": "Content", "tags": ["tag1", "tag2!", "tag3"]}``` | HTTP Status 400 Bad Request; error message indicating tags must be alphanumeric or hyphen.
SCENARIO-015 | Invalid JSON format in request body | ```{"title": "Blog Title", "content": "Content", "tags": ["tag1", "tag2"}``` | HTTP Status 400 Bad Request; error message indicating invalid JSON format.
