# API Test-Scenario Catalogue  

**Feature:** Publish Blog (REST API)  

**Document role:** Enumerates test scenarios for the Publish Blog feature  

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

These endpoints let an authenticated author query or change the *published* state of an existing blog post.

### 2.2. Preconditions  

- The caller is authenticated with a valid JWT.  
- The blog post identified by `id` exists.  
- The caller is the owner of the blog post. 

### 2.3. Postconditions  

- When publishing, the blog’s `published` field becomes **true**.  
- When unpublishing, the field becomes **false**.  
- When toggling, the field inverts its current state.  
- *Publish state* is retrievable via the **GET** endpoint.

### 2.4. Details  

| Method | Path | Purpose | Success Response |
| --- | --- | --- | --- |
| `GET` | `/api/v1/blogs/{id}/publish` | Retrieve publish status | **200 OK**<br>`{"isPublished": true | false}` |
| `POST` | `/api/v1/blogs/{id}/publish` | Publish a draft | **204 No Content** |
| `PUT` | `/api/v1/blogs/{id}/publish` | Toggle publish status | **204 No Content** |
| `DELETE` | `/api/v1/blogs/{id}/publish` | Unpublish a blog | **204 No Content** |

Common headers  

```
Authorization: Bearer <JWT>
Accept: application/json
```


---

## 3. Scenarios  

### 3.1. Publish Blog – Happy Path  
**Scenario ID:** SCENARIO-001  

Step | Action | Expected Result
--- | --- | ---
1 | **GET** `/api/v1/blogs/42/publish` | 200 OK; `{"isPublished": false}`
2 | **POST** `/api/v1/blogs/42/publish` | 204 No Content
3 | **GET** `/api/v1/blogs/42/publish` | 200 OK; `{"isPublished": true}`
4 | **DELETE** `/api/v1/blogs/42/publish` | 204 No Content
5 | **GET** `/api/v1/blogs/42/publish` | 200 OK; `{"isPublished": false}`
6 | **PUT** `/api/v1/blogs/42/publish` | 204 No Content
7 | **GET** `/api/v1/blogs/42/publish` | 200 OK; `{"isPublished": true}`
8 | Verify database row for blog 42 shows `published = false` at the end of the flow.

### 3.2. Publish Blog – Negative Paths  

Scenario ID | Condition | Example Request | Expected Result
--- | --- | --- | ---
SCENARIO-002 | Blog **id** does not exist | `GET /api/v1/blogs/999/publish` | 404 Not Found; error `BLOG_NOT_FOUND`
SCENARIO-003 | Caller not owner of blog | `POST /api/v1/blogs/41/publish` (owned by another user) | 403 Forbidden; error `ACCESS_DENIED`
SCENARIO-004 | No authentication token | Any method without `Authorization` header | 401 Unauthorized; error `AUTH_REQUIRED`
SCENARIO-005 | Invalid / expired token | `Authorization: Bearer bad.jwt` | 403 Forbidden; error `AUTH_INVALID`
SCENARIO-006 | Publish already-published blog | `POST /api/v1/blogs/42/publish` (state = true) | 409 Conflict; error `ALREADY_PUBLISHED`
SCENARIO-007 | Unpublish already-unpublished blog | `DELETE /api/v1/blogs/42/publish` (state = false) | 409 Conflict; error `ALREADY_UNPUBLISHED`
SCENARIO-008 | Toggle on non-existent blog | `PUT /api/v1/blogs/999/publish` | 404 Not Found; error `BLOG_NOT_FOUND`
SCENARIO-009 | Path parameter not an integer | `GET /api/v1/blogs/abc/publish` | 400 Bad Request; error `INVALID_ID_FORMAT`
SCENARIO-010 | Database unavailable | Simulate DB outage, then `POST /api/v1/blogs/42/publish` | 503 Service Unavailable; error `BACKEND_DOWN`
<!-- SCENARIO-011 | Blog in *deleted* state | `POST /api/v1/blogs/43/publish` (soft-deleted) | 409 Conflict; error `BLOG_DELETED` -->
<!-- SCENARIO-012 | Excessive rate (DoS protection) | 101 rapid `POST` requests within a minute | 429 Too Many Requests; error `RATE_LIMIT` -->

---

## 4. Exit Criteria  

- All scenarios SCENARIO-001 – SCENARIO-010 are implemented and executed.  
- Every scenario passes with expected results.  
- No critical or high-severity defects remain open.  
