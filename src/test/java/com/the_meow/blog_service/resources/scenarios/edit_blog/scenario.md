# API Test-Scenario Catalogue

**Feature:** Edit/Delete Blog (REST API)

**Document role:** Enumerates test scenarios for the Edit and Delete Blog features


## 1. Document Control

Parameter | Value
--- | ---
Version | 1.0
Author | QA
Date | 25/05/2025
Status | Waiting for Review
Last Updated | 27/05/2025

## 2. Documentation

### 2.1. Overview

These endpoints allow an authenticated author to update the content of an existing blog post or to delete the post entirely.

### 2.2. Preconditions

- The caller must be authenticated.
- The target blog post must exist.
- The caller must own the blog post.

### 2.3. Postconditions

- **Update:** The blog post is modified with the provided fields; the latest state is returned in the response.
- **Delete:** The blog post is removed (soft‑deleted) and no longer retrievable via the API.

### 2.4. Details

#### PATCH `/api/v1/blogs/{id}` – Update a blog post

Field (JSON) | Type | Required | Description | Validation Rules
--- | --- | --- | --- | ---
`title` | string | Yes | Updated title | 1 – 120 UTF‑8 chars, unique per author
`thumbnailUrl` | string | No | Updated thumbnail URL | Absolute URL, image MIME, ≤ 2 kB
`content` | string | Yes | Updated Markdown content | 10 – 50 000 UTF‑8 chars
`tags` | array[string] | No | Updated tags | 0 – 10 elements, each 1 – 30 chars, alphanum + hyphen

**Success – update**

```http
HTTP/1.1 200 OK
{
  "id": 42,
  "title": "Updated Title",
  "thumbnailUrl": "https://cdn.example/new.png",
  "content": "## Updated content …",
  "tags": ["tag1","tag2"],
  "updatedAt": "2025-05-24T13:00:00Z"
}
```

#### DELETE `/api/v1/blogs/{id}` – Delete a blog post

**Success – delete**

```http
HTTP/1.1 204 No Content
```

---

## 3. Scenarios

### 3.1. Update Blog – Happy Path  
**Scenario ID:** SCENARIO‑001

Step | Action | Expected Result
--- | --- | ---
1 | **PATCH** `/api/v1/blogs/42` with valid JSON body containing new `title`, `content`, optional `thumbnailUrl`, `tags`. | 200 OK; body reflects updated fields, `updatedAt` present
2 | Verify response values. | Fields match request; `id` unchanged
3 | **GET** `/api/v1/blogs/42` with owner JWT. | 200 OK; body contains updated data
4 | Verify database record. | Row shows new values; audit log entry created

### 3.2. Delete Blog – Happy Path  
**Scenario ID:** SCENARIO‑002

Step | Action | Expected Result
--- | --- | ---
1 | **DELETE** `/api/v1/blogs/42` with owner JWT. | 204 No Content
2 | **GET** `/api/v1/blogs/42` | 404 Not Found (or 410 Gone)  
3 | Database check | Row flagged as deleted (soft delete)

### 3.3. Update Blog – Negative Paths

Scenario ID | Condition | Example Fragment | Expected Result
--- | --- | --- | ---
SCENARIO‑003 | Missing `title` | `{"content":"New content"}` | 400 Bad Request; `TITLE_REQUIRED`
SCENARIO‑004 | `title` exceeds 120 chars | long string | 422 Unprocessable; `TITLE_LENGTH`
SCENARIO‑005 | Duplicate `title` for same author | existing title | 409 Conflict; `TITLE_DUPLICATE`
SCENARIO‑006 | XSS in `title` | `<script>`… | 422 Unprocessable; `INVALID_TITLE_CHARS`
SCENARIO‑007 | Missing `content` | `{"title":"Valid"}` | 400 Bad Request; `CONTENT_REQUIRED`
SCENARIO‑008 | `content` too long (50 001+) | long body | 413 Payload Too Large
SCENARIO‑009 | `thumbnailUrl` invalid format | `http:/bad` | 422 Unprocessable; `THUMBNAIL_URL_INVALID`
SCENARIO‑010 | `thumbnailUrl` > 2 kB | very long URL | 413 Payload Too Large; `THUMBNAIL_URL_TOO_LONG`
SCENARIO‑011 | `tags` not an array | `"tags":"tag1,tag2"` | 400 Bad Request; `TAGS_TYPE`
SCENARIO‑012 | `tags` length > 10 | 11 items | 422 Unprocessable; `TAGS_COUNT`
SCENARIO‑013 | Tag > 30 chars | `"averylongtagexceedinglimit"` | 422; `TAG_LENGTH`
SCENARIO‑014 | Tag invalid chars | `"c++"` | 422; `TAG_FORMAT`
SCENARIO‑015 | Unauthorized update | no token | 401 Unauthorized; `AUTH_REQUIRED`
SCENARIO‑016 | Update by non‑owner | other user token | 403 Forbidden; `ACCESS_DENIED`
SCENARIO‑017 | Blog id not numeric | `PATCH /abc` | 400 Bad Request; `INVALID_ID_FORMAT`
SCENARIO‑018 | Blog id does not exist | `/999` | 404 Not Found; `BLOG_NOT_FOUND`
SCENARIO‑019 | Blog deleted already | updating deleted blog | 409 Conflict; `BLOG_DELETED`
SCENARIO‑020 | Database outage | simulate down | 503 Service Unavailable; `BACKEND_DOWN`

### 3.4. Delete Blog – Negative Paths

Scenario ID | Condition | Example Request | Expected Result
--- | --- | --- | ---
SCENARIO‑021 | Unauthorized delete | no token | 401 Unauthorized; `AUTH_REQUIRED`
SCENARIO‑022 | Delete by non‑owner | other user token | 403 Forbidden; `ACCESS_DENIED`
SCENARIO‑023 | Blog already deleted | repeat delete | 404 Not Found; `BLOG_NOT_FOUND`
SCENARIO‑024 | Blog id not numeric | `DELETE /abc` | 400 Bad Request; `INVALID_ID_FORMAT`
SCENARIO‑025 | Blog id does not exist | `/999` | 404 Not Found; `BLOG_NOT_FOUND`
SCENARIO‑026 | Database outage | simulate down | 503 Service Unavailable; `BACKEND_DOWN`

---

## 4. Exit Criteria
- All scenarios SCENARIO‑001 – SCENARIO‑026 are implemented and tested.
- All scenarios pass successfully.
- No critical bugs remain.
