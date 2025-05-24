# API Test-Scenario Catalogue

**Feature:** Create Blog (REST API)

**Document role:** Enumerates test scenarios for the Create Blog feature


## 1. Document Control

Parameter | Value
--- | ---
Version | 1.0
Author | QA 
Date | 24/05/2025
Status | Draft
Last Updated | 24/05/2025

## 2. Documentation

### 2.1. Overview

This functionality allows users to create a new blog post via the REST API. The API endpoint accepts various parameters to define the blog post's content, title, and other metadata.

### 2.2. Preconditions

- The user must be authenticated.
- The API endpoint must be accessible.
- The user must provide all required fields in the request body.

### 2.3. Postconditions

- A new blog post is created in the system.
- The response contains the details of the created blog post.
- The blog post is retrievable via the API.

### 2.4. Details

```POST /api/v1/blogs``` - Create a new blog post

Field (JSON) | Type | Required | Description | Validation Rules
--- | --- | --- | --- | ---
```title``` | ```string``` | Yes | The title of the blog post | 1 – 120 UTF-8 chars, unique per author
```thumbnailUrl``` | ```string``` | No | URL of the thumbnail image for the blog post | Absolute URL, image MIME, ≤ 2 kB
```content``` | ```string``` | Yes | The content of the blog post in Markdown format | 10 – 50 000 UTF-8 chars
```tags``` | ```array[string]``` | No | Tags associated with the blog post | 0 – 10 elements, each 1 – 30 chars, alphanum + hyphen

**Success Response:**

```HTTP
HTTP/1.1 201 Created
{
  "id": "12345",
  "title": "Sample Blog Post",
  "thumbnailUrl": "https://example.com/image.jpg",
  "content": "This is the content of the blog post.",
  "tags": ["tag1", "tag2"],
  "createdAt": "2025-05-24T12:00:00Z"
}
```

```Location```: ```/api/v1/blogs/{id}```

## 3. Scenarios

### 3.1. Create Blog - Happy Path
**Scenario ID:** SCENARIO-001

**Description:** Successfully create a new blog post with all required fields provided.

Step | Action | Expected Result
--- | --- | ---
1 | Send a POST request to `/api/v1/blogs` with valid JSON body containing `title`, `content`, and optional `thumbnailUrl` and `tags`. | HTTP Status 201 Created; body matches contract; ```id``` present
2 | Verify the response body contains the created blog post details. | Response body matches the expected structure; `id`, `title`, `content`, and `createdAt` are present.
3 | Verify the blog post is retrievable via GET request to `/api/v1/blogs/{id}`. | HTTP Status 200 OK; body matches the created blog post details.
4 | Verify the blog post is stored in the database. | Database contains the new blog post with matching `id`, `title`, and `content`.

### 3.2. Create Blog - Negative Paths

Scenario ID | Condition | Example Fragment | Expected Result
--- | --- | --- | ---
SCENARIO-002 | Missing required field `title` | ```{"content": "Blog content"}``` | HTTP Status 400 Bad Request; error message indicating `title` is required.
SCENARIO-003 | Field `title` exceeds maximum length | ```{"title": "A very long title that exceeds the maximum allowed length of 120 characters... (more text)"}``` | HTTP Status 400 Bad Request; error message indicating `title` is too long.
SCENARIO-004 | Duplicate `title` for the same author | ```{"title": "Existing Blog Title", "content": "New content"}``` | HTTP Status 409 Conflict; error message indicating `title` must be unique per author.
SCENARIO-005 | XSS attack in `title` | ```{"title": "<script>alert('XSS')</script>", "content": "Content with XSS"}``` | HTTP Status 400 Bad Request; error message indicating invalid characters in `title`.
SCENARIO-006 | Missing required field `content` | ```{"title": "Blog Title"}``` | HTTP Status 400 Bad Request; error message indicating `content` is required.
SCENARIO-007 | Field `content` exceeds maximum length | ```{"title": "Blog Title", "content": "A very long content that exceeds the maximum allowed length of 50,000 characters... (more text)"}``` | HTTP Status 400 Bad Request; error message indicating `content` is too long.
SCENARIO-008 | Field `content` is too short | ```{"title": "Blog Title", "content": "Short"}``` | HTTP Status 400 Bad Request; error message indicating `content` must be at least 10 characters.
SCENARIO-009 | Invalid `thumbnailUrl` format | ```{"title": "Blog Title", "content": "Content", "thumbnailUrl": "invalid-url"}``` | HTTP Status 400 Bad Request; error message indicating invalid URL format.
SCENARIO-010 | `thumbnailUrl` exceeds size limit | ```{"title": "Blog Title", "content": "Content", "thumbnailUrl": "https://example.com/large-image.jpg"}``` | HTTP Status 400 Bad Request; error message indicating image size exceeds 2 kB.
SCENARIO-011 | Invalid `tags` format (not an array) | ```{"title": "Blog Title", "content": "Content", "tags": "tag1, tag2"}``` | HTTP Status 400 Bad Request; error message indicating `tags` must be an array.
SCENARIO-012 | `tags` array exceeds maximum length | ```{"title": "Blog Title", "content": "Content", "tags": ["tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tag10", "tag11"]}``` | HTTP Status 400 Bad Request; error message indicating `tags` array must have 0-10 elements.
SCENARIO-013 | `tags` contains too long tag | ```{"title": "Blog Title", "content": "Content", "tags": ["tag1", "tag2", "a-very-long-tag-that-exceeds-thirty-characters"]}``` | HTTP Status 400 Bad Request; error message indicating tags must be 1-30 characters.
SCENARIO-014 | `tags` contains invalid characters | ```{"title": "Blog Title", "content": "Content", "tags": ["tag1", "tag2!", "tag3"]}``` | HTTP Status 400 Bad Request; error message indicating tags must be alphanumeric or hyphen.
SCENARIO-015 | Invalid JSON format in request body | ```{"title": "Blog Title", "content": "Content", "tags": ["tag1", "tag2"}``` | HTTP Status 400 Bad Request; error message indicating invalid JSON format.
SCENARIO-016 | Unauthorized user attempts to create a blog post | No authentication token provided in request header | HTTP Status 401 Unauthorized; error message indicating authentication is required.
SCENARIO-017 | User attempts to create a blog post with invalid authentication token | Invalid or expired token provided in request header | HTTP Status 403 Forbidden; error message indicating access denied.
ScENARIO-018 | Server error during blog post creation | Simulate server error (e.g., database down) | HTTP Status 503 Service Unavailable; error message indicating server error.

## 4. Exit Criteria
- All scenarios are implemented and tested.
- All scenarios pass successfully.
- No critical bugs are found.



