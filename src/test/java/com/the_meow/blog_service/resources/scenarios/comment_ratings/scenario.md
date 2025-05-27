# API Test-Scenario Catalogue  

**Feature:** Manage Comment Ratings (REST API)  

**Document role:** Enumerates test scenarios for the Comment‑Rating features  

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

These endpoints let authenticated users rate an existing **comment** on a blog post.  
*Each user may leave one rating per comment.* Ratings range from **0.0** to **5.0** (inclusive) and support one decimal place.  

### 2.2. Preconditions  

- Caller is authenticated with a valid JWT.  
- Target blog and comment (`blogId`, `commentId`) exist.  
- Caller has not already rated the same comment when creating a rating.  

### 2.3. Postconditions  

- **Create:** A new rating record is stored linking `authorId` → `commentId` with `value`.  
- **Update:** The rating value is replaced; `updatedAt` timestamp set.  
- **Delete:** The rating is soft‑deleted; averages recalculated.  
- **Average:** Returns the mean of all non‑deleted ratings for the comment.  

### 2.4. Details  

| Method | Path | Purpose | Success Response | Notes |  
| --- | --- | --- | --- | --- |  
| `POST` | `/api/v1/blogs/{blogId}/comments/{commentId}/ratings` | Create a rating | **201 Created** – body RatingInfo | JSON `{ "value": 4.5 }` |  
| `GET` | `/api/v1/blogs/{blogId}/comments/{commentId}/ratings` | List ratings | **200 OK** – Page\<RatingInfo\> | Supports `page` & `size` |  
| `GET` | `/api/v1/blogs/{blogId}/comments/{commentId}/ratings/{ratingId}` | Retrieve rating | **200 OK** – RatingInfo |  |  
| `PATCH` | `/api/v1/blogs/{blogId}/comments/{commentId}/ratings/{ratingId}` | Update rating | **200 OK** – updated RatingInfo | Only `value` mutable |  
| `DELETE` | `/api/v1/blogs/{blogId}/comments/{commentId}/ratings/{ratingId}` | Delete rating | **204 No Content** | Soft delete |  
| `GET` | `/api/v1/blogs/{blogId}/comments/{commentId}/ratings/average` | Average rating | **200 OK** – `{ "average": 4.25, "count": 8 }` |  |  

**RatingCreate / Update JSON**  

Field | Type | Required | Validation Rules  
--- | --- | --- | ---  
`value` | number (float) | Yes | 0.0 – 5.0 inclusive, max 1 decimal place  

**Sample success – create**  

```http  
POST /api/v1/blogs/42/comments/7/ratings  
Authorization: Bearer <token>  
Content-Type: application/json  

{ "value": 4.5 }  
```  

```http  
HTTP/1.1 201 Created  
{  
  "id": 3,  
  "commentId": 7,  
  "authorId": 12,  
  "value": 4.5,  
  "createdAt": "2025-05-24T15:00:00Z",  
  "updatedAt": null  
}  
Location: /api/v1/blogs/42/comments/7/ratings/3  
```  

---  

## 3. Scenarios  

### 3.1. Create Rating – Happy Path  
**Scenario ID:** SCENARIO-001  

Step | Action | Expected Result  
--- | --- | ---  
1 | **POST** `/api/v1/blogs/42/comments/7/ratings` with `{ "value": 4.0 }` | 201 Created; response body contains `id`, `value` = 4.0  
2 | **GET** `/api/v1/blogs/42/comments/7/ratings/{newId}` | 200 OK; body identical  
3 | **GET** `/api/v1/blogs/42/comments/7/ratings/average` | 200 OK; `count` incremented by 1; `average` updated  
4 | DB check | Row exists with correct value & author link  

### 3.2. Update Rating – Happy Path  
**Scenario ID:** SCENARIO-002  

Step | Action | Expected Result  
--- | --- | ---  
1 | **PATCH** `/api/v1/blogs/42/comments/7/ratings/3` with `{ "value": 5.0 }` | 200 OK; `value` = 5.0; `updatedAt` non‑null  
2 | **GET average** | New average reflects updated value  

### 3.3. Delete Rating – Happy Path  
**Scenario ID:** SCENARIO-003  

Step | Action | Expected Result  
--- | --- | ---  
1 | **DELETE** `/api/v1/blogs/42/comments/7/ratings/3` | 204 No Content  
2 | **GET** same rating | 404 Not Found  
3 | **GET average** | `count` decreased, average recalculated  

### 3.4. Negative Paths – Create  

Scenario ID | Condition | Example Body | Expected Result  
--- | --- | --- | ---  
SCENARIO-004 | Missing `value` | `{}` | 400 Bad Request; `VALUE_REQUIRED`  
SCENARIO-005 | `value` < 0 | `{ "value": -1 }` | 422 Unprocessable; `VALUE_RANGE`  
SCENARIO-006 | `value` > 5 | `{ "value": 5.5 }` | 422 Unprocessable; `VALUE_RANGE`  
SCENARIO-007 | More than 1 decimal | `{ "value": 4.55 }` | 422; `VALUE_PRECISION`  
SCENARIO-008 | Non‑numeric value | `{ "value": "good" }` | 400 Bad Request; `VALUE_TYPE`  
SCENARIO-009 | Duplicate rating by same user | second POST | 409 Conflict; `RATING_EXISTS`  
SCENARIO-010 | Blog not found | `/blogs/999/comments/7/ratings` | 404 Not Found; `BLOG_NOT_FOUND`  
SCENARIO-011 | Comment not found | `/comments/999/ratings` | 404 Not Found; `COMMENT_NOT_FOUND`  
SCENARIO-012 | Unauthorized | no token | 401 Unauthorized; `AUTH_REQUIRED`  
SCENARIO-013 | Invalid token | bad JWT | 403 Forbidden; `AUTH_INVALID`  
SCENARIO-014 | DB outage | simulate | 503 Service Unavailable; `BACKEND_DOWN`  

### 3.5. Negative Paths – Update  

Scenario ID | Condition | Example | Expected Result  
--- | --- | --- | ---  
SCENARIO-015 | Rating not owned | PATCH with other user token | 403 Forbidden; `ACCESS_DENIED`  
SCENARIO-016 | Rating not found | `/ratings/999` | 404 Not Found; `RATING_NOT_FOUND`  
SCENARIO-017 | Value out of range | `{ "value": 6 }` | 422; `VALUE_RANGE`  
SCENARIO-018 | Duplicate precision | `{ "value": 3.333 }` | 422; `VALUE_PRECISION`  
SCENARIO-019 | Rating deleted | update after delete | 409 Conflict; `RATING_DELETED`  

### 3.6. Negative Paths – Delete / Get / Average  

Scenario ID | Condition | Path | Expected Result  
--- | --- | --- | ---  
SCENARIO-020 | Non‑owner delete | other user JWT | 403 Forbidden; `ACCESS_DENIED`  
SCENARIO-021 | Rating already deleted | DELETE again | 404 Not Found; `RATING_NOT_FOUND`  
SCENARIO-022 | Ids not numeric | `/ratings/abc` | 400 Bad Request; `INVALID_ID_FORMAT`  
SCENARIO-023 | Blog/comment mismatch | blog 42 but rating belongs to comment 8 | 404 Not Found; `RATING_NOT_FOUND`  
SCENARIO-024 | Average when no ratings | `/average` after all deleted | 200 OK; `"average": 0.0`, `"count": 0`  

---  

## 4. Exit Criteria  

- All scenarios SCENARIO-001 – SCENARIO-024 are implemented and tested.  
- All scenarios pass with expected results.  
- No critical or high‑severity defects remain open.
