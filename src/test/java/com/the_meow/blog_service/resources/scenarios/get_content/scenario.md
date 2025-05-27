# API Test-Scenario Catalogue  

**Feature:** Get Blog Content (REST API)  

**Document role:** Enumerates test scenarios for retrieving blog posts  

---

## 1. Document Control  

Parameter | Value
--- | ---
Version | 1.0
Author | QA
Date | 24/05/2025
Status | Waiting for Review
Last Updated | 27/05/2025

---

## 2. Documentation  

### 2.1. Overview  

These endpoints let consumers list published blogs and retrieve individual blog posts.  
- **Public users** can see only blogs whose `published` flag is **true**.  
- **Authenticated authors** may also see their own unpublished drafts.  

### 2.2. Preconditions  

- For public access (`GET /api/v1/blogs`) no authentication is required.  
- For private drafts (`GET /api/v1/blogs/{id}` when the post is not published) the caller must be the owner.  
- The service and database are available.  

### 2.3. Postconditions  

- A JSON Page object is returned for list calls.  
- A JSON BlogInfo object is returned for detail calls.  
- No data outside the caller’s permission scope is exposed.  

### 2.4. Details  

| Method | Path | Purpose | Success Response |
| --- | --- | --- | --- |
| `GET` | `/api/v1/blogs` | List published blogs with optional filters | **200 OK** – Page<BlogInfoPublic> |
| `GET` | `/api/v1/blogs/{id}` | Retrieve a single blog | **200 OK** – BlogInfoPublic or BlogInfoPrivate |

**Query parameters (BlogFilterRequest)**  

Parameter | Type | Required | Validation Rules | Description
--- | --- | --- | --- | ---
`page` | int | No | ≥ 0 | Index of result page (default 0)
`size` | int | No | 1 – 100 | Items per page (default 20)
`tags` | array[string] | No | 0 – 10 tags, 1 – 30 chars | Filter by tag(s)
`authorId` | int | No | positive | Filter by author
`search` | string | No | ≤ 120 UTF-8 chars | Free-text search

**Sample success – list**  

```http
HTTP/1.1 200 OK
{
  "content": [
    {
      "id": 42,
      "title": "Learning Bitcoin",
      "thumbnailUrl": "https://cdn.example/img/bitcoin.png",
      "excerpt": "Bitcoin basics …",
      "tags": ["bitcoin","trading"],
      "authorId": 7,
      "publishedAt": "2025-05-10T10:00:00Z"
    }
  ],
  "pageable": { "page":0,"size":20 },
  "totalElements": 1,
  "totalPages": 1
}
```

**Sample success – detail**  

```http
HTTP/1.1 200 OK
{
  "id": 42,
  "title": "Learning Bitcoin",
  "thumbnailUrl": "https://cdn.example/img/bitcoin.png",
  "content": "### Intro …",
  "tags": ["bitcoin","trading"],
  "authorId": 7,
  "published": true,
  "publishedAt": "2025-05-10T10:00:00Z"
}
```

---

## 3. Scenarios  

### 3.1. List Blogs – Happy Path  
**Scenario ID:** SCENARIO-001  

Step | Action | Expected Result
--- | --- | ---
1 | **GET** `/api/v1/blogs?page=0&size=10` | 200 OK; JSON Page with ≤ 10 items
2 | Validate each item’s `published` field is **true**. | All returned records are published
3 | Database cross-check (integration) | Result count ≤ database count of published blogs

### 3.2. Get Blog Detail – Happy Path (Published)  
**Scenario ID:** SCENARIO-002  

Step | Action | Expected Result
--- | --- | ---
1 | **GET** `/api/v1/blogs/42` (blog is published) | 200 OK; full blog JSON
2 | Ensure `published` == **true** | Field matches
3 | Validate returned `id`, `title` etc. against DB | Values match database row

### 3.3. Get Blog Detail – Happy Path (Owner Draft)  
**Scenario ID:** SCENARIO-003  

Step | Action | Expected Result
--- | --- | ---
1 | **GET** `/api/v1/blogs/43` with owner’s JWT (blog unpublished) | 200 OK; blog JSON with `published` == **false**
2 | Verify caller id matches `authorId` | True
3 | Validate returned `id`, `title` etc. against DB | Values match database row

### 3.4. Negative Paths  

Scenario ID | Condition | Example Request | Expected Result
--- | --- | --- | ---
SCENARIO-004 | Invalid `page` parameter (−1) | `GET /api/v1/blogs?page=-1` | 400 Bad Request; `INVALID_PAGE`
SCENARIO-005 | `size` greater than 100 | `GET /api/v1/blogs?size=200` | 400 Bad Request; `SIZE_LIMIT`
SCENARIO-006 | Tag filter array too long | `GET /api/v1/blogs?tags=tag1,tag2,…tag11` | 400 Bad Request; `TAGS_COUNT`
SCENARIO-007 | Search string > 120 chars | `GET /api/v1/blogs?search=<121 chars>` | 400 Bad Request; `SEARCH_LENGTH`
SCENARIO-008 | Blog **id** not numeric | `GET /api/v1/blogs/abc` | 400 Bad Request; `INVALID_ID_FORMAT`
SCENARIO-009 | Blog **id** does not exist | `GET /api/v1/blogs/9999` | 404 Not Found; `BLOG_NOT_FOUND`
SCENARIO-010 | Unpublished blog by non-owner | `GET /api/v1/blogs/43` (no JWT) | 404 Not Found (or 403); `BLOG_NOT_FOUND`
SCENARIO-011 | Corrupted JWT | `Authorization: Bearer bad.jwt` with draft request | 403 Forbidden; `AUTH_INVALID`
SCENARIO-012 | Database outage | Simulate outage, then `GET /api/v1/blogs` | 503 Service Unavailable; `BACKEND_DOWN`
SCENARIO-013 | Page number exceeds total | `GET /api/v1/blogs?page=99` (only 1 page) | 200 OK; `content` empty

---

## 4. Exit Criteria  

- All scenarios SCENARIO-001 – SCENARIO-013 are implemented and tested.  
- All scenarios pass with expected results.  
- No critical defects remain open.  
