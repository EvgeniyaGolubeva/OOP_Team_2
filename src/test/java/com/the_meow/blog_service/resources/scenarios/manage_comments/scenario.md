# API Test-Scenario Catalogue  

**Feature:** Manage Comments (REST API)  

**Document role:** Enumerates test scenarios for the Comment management features  

---

## 1. Document Control  

Parameter | Value  
--- | ---  
Version | 1.0  
Author | QA  
Date | 25/05/2025  
Status | Waiting for Review  
Last Updated | 27/05/2025  

---

## 2. Documentation  

### 2.1. Overview  

These endpoints let authenticated users add, list, read, update, or delete comments that belong to a blog post. Comments are always tied to a parent post and cannot exist independently.  

### 2.2. Preconditions  

- The caller is authenticated with a valid JWT.  
- The target blog post (`blogId`) exists.  
- For update/delete operations the caller must be the comment owner or have moderation rights.  

### 2.3. Postconditions  

- **Create:** a new comment record is stored with a reference to the blog and author.  
- **Update:** the comment’s `content` field is changed; `updatedAt` timestamp is set.  
- **Delete:** the comment is soft‑deleted and excluded from subsequent list calls.  
- **List / Get:** only non‑deleted comments are returned, ordered by `createdAt` ascending.  

### 2.4. Details  

| Method | Path | Purpose | Success Response | Notes |  
| --- | --- | --- | --- | --- |  
| `POST` | `/api/v1/blogs/{blogId}/comments` | Create a comment | **201 Created** + body of CommentInfo | Requires JSON body `{ "content": "text…" }` |  
| `GET` | `/api/v1/blogs/{blogId}/comments` | List comments for blog | **200 OK** – Page\<CommentInfo\> | Supports `page` & `size` query params |  
| `GET` | `/api/v1/blogs/{blogId}/comments/{commentId}` | Retrieve one comment | **200 OK** – CommentInfo |  |  
| `PATCH` | `/api/v1/blogs/{blogId}/comments/{commentId}` | Update comment | **200 OK** – updated CommentInfo | Only `content` mutable |  
| `DELETE` | `/api/v1/blogs/{blogId}/comments/{commentId}` | Delete comment | **204 No Content** | Soft delete |  

**CommentCreate / Update JSON**  

Field | Type | Required | Validation Rules  
--- | --- | --- | ---  
`content` | string | Yes | 1 – 5 000 UTF‑8 chars, no HTML tags, no scripts  

**Sample success – create**  

```http  
POST /api/v1/blogs/42/comments  
Authorization: Bearer <token>  
Content-Type: application/json  

{  
  "content": "Great article, thanks!"  
}  
```  

```http  
HTTP/1.1 201 Created  
{  
  "id": 7,  
  "blogId": 42,  
  "authorId": 12,  
  "content": "Great article, thanks!",  
  "createdAt": "2025-05-24T14:30:00Z",  
  "updatedAt": null  ,
  "commentRatings": []
}  
Location: /api/v1/blogs/42/comments/7  
```  

---

## 3. Scenarios  

### 3.1. Create Comment – Happy Path  
**Scenario ID:** SCENARIO-001  

Step | Action | Expected Result  
--- | --- | ---  
1 | **POST** `/api/v1/blogs/42/comments` with JSON `{ "content": "Nice post!" }` | 201 Created; body matches contract; `id` present  
2 | **GET** `/api/v1/blogs/42/comments/{newId}` | 200 OK; body identical to create response  
3 | Verify list call `/api/v1/blogs/42/comments` | Comment appears in list  
4 | DB check | Row exists, linked to blog 42 and author  

### 3.2. Update Comment – Happy Path  
**Scenario ID:** SCENARIO-002  

Step | Action | Expected Result  
--- | --- | ---  
1 | **PATCH** `/api/v1/blogs/42/comments/7` with `{ "content": "Edited text" }` | 200 OK; body shows updated content, `updatedAt` non‑null  
2 | **GET** same path | 200 OK; `content` equals `"Edited text"`  

### 3.3. Delete Comment – Happy Path  
**Scenario ID:** SCENARIO-003  

Step | Action | Expected Result  
--- | --- | ---  
1 | **DELETE** `/api/v1/blogs/42/comments/7` | 204 No Content  
2 | **GET** `/api/v1/blogs/42/comments/7` | 404 Not Found (or 410 Gone)  
3 | List call shows comment absent | `content` array no longer contains id 7  

### 3.4. Negative Paths – Create  

Scenario ID | Condition | Example Fragment | Expected Result  
--- | --- | --- | ---  
SCENARIO-004 | Missing `content` | `{}` | 400 Bad Request; `CONTENT_REQUIRED`  
SCENARIO-005 | `content` too short | `""` | 422 Unprocessable; `CONTENT_LENGTH`  
SCENARIO-006 | `content` > 5 000 chars | long string | 413 Payload Too Large  
SCENARIO-007 | HTML / script injection | `<script>alert(1)</script>` | 422; `CONTENT_INVALID_CHARS`  
SCENARIO-008 | Blog does not exist | `/api/v1/blogs/999/comments` | 404 Not Found; `BLOG_NOT_FOUND`  
SCENARIO-009 | Unauthorized (no token) | — | 401 Unauthorized; `AUTH_REQUIRED`  
SCENARIO-010 | Invalid token | bad JWT | 403 Forbidden; `AUTH_INVALID`  
SCENARIO-011 | Database outage | simulate | 503 Service Unavailable; `BACKEND_DOWN`  

### 3.5. Negative Paths – Update  

Scenario ID | Condition | Example Request | Expected Result  
--- | --- | --- | ---  
SCENARIO-012 | Comment not owned | owner mismatch token | 403 Forbidden; `ACCESS_DENIED`  
SCENARIO-013 | Comment not found | `/comments/999` | 404 Not Found; `COMMENT_NOT_FOUND`  
SCENARIO-014 | Missing content | `{}` | 400 Bad Request; `CONTENT_REQUIRED`  
SCENARIO-015 | Content too long | 5 001+ chars | 413 Payload Too Large  
SCENARIO-016 | Content XSS | `<img onerror>` | 422; `CONTENT_INVALID_CHARS`  
SCENARIO-017 | Comment deleted | update after delete | 409 Conflict; `COMMENT_DELETED`  

### 3.6. Negative Paths – Delete / Get  

Scenario ID | Condition | Path | Expected Result  
--- | --- | --- | ---  
SCENARIO-018 | Non‑owner delete | DELETE … | 403 Forbidden; `ACCESS_DENIED`  
SCENARIO-019 | Comment already deleted | DELETE again | 404 Not Found; `COMMENT_NOT_FOUND`  
SCENARIO-020 | Id not numeric | `/comments/abc` | 400 Bad Request; `INVALID_ID_FORMAT`  
SCENARIO-021 | Blog & comment mismatch | `/blogs/42/comments/9` (comment blogId=43) | 404 Not Found; `COMMENT_NOT_FOUND`  

---

## 4. Exit Criteria  

- All scenarios SCENARIO‑001 – SCENARIO‑021 are implemented and tested.  
- All scenarios pass with expected results.  
- No critical or high‑severity defects remain open.  
