# API Test-Scenario Catalogue

**Feature:** Blog Quoting Utility

**Document role:** Enumerates test scenarios for the `BlogQuotingUtil.validateQuotedBlogs` feature


## 1. Document Control

Parameter | Value
--- | ---
Version | 1.0
Author | QA
Date | 20/06/2025
Status | Waiting for Review
Last Updated | 22/06/2025

## 2. Documentation

### 2.1. Overview

`BlogQuotingUtil.validateQuotedBlogs(input)` parses a body of **Markdown‑like** text and validates “inline quote” tags of the form `[[(_blogId_) _quoted text_]]`.  
For every valid quote it returns **three** `Long` values: `blogId`, `startIndex`, `endIndex` (1‑based, inclusive).  
If any quote fails validation, the method returns `null`.

### 2.2. Preconditions

- A `BlogRepository` instance is available and returns the requested blog by ID.  
- The target `Blog` entity’s `content` is non‑null.  
- Method is invoked with a non‑null `String` input.  

### 2.3. Postconditions

- On success: a `List<Long>` with `3 × number_of_valid_quotes` elements.  
- On partial failure: if **any** quote is invalid, **null** is returned.  
- Repository is queried once per distinct `blogId`.

### 2.4. Details

Validation Rule | Description
--- | ---
Quote syntax | Must exactly match `[[({id}) {space}{quotedText}]]`
`id` | Positive integer, no leading zeros
Blog existence | `blogRepository.findById(id)` must return a blog
Blog content | `blog.getContent()` must be non‑null and contain `quotedText`
Quoted text | Must appear **verbatim** in blog content; search is case‑sensitive
Overlap | Overlapping quoted ranges are **allowed**
Whitespace | Leading/trailing spaces around whole input are ignored
Return indices | Calculated against the original blog content (1‑based)

---

## 3. Scenarios

### 3.1. Happy Paths

Scenario ID | Description | Input | Expected Result (example)
--- | --- | --- | ---
SCENARIO-001 | Single valid quote | `"[[(1) quoted part]]"` | `[1, 11, 22]`
SCENARIO-002 | Multiple quotes, same blog | `"[[ (1) first]] …"` | Index triples for each quote
SCENARIO-003 | Multiple quotes, different blogs | `"[[ (1) apple]] [[(2) dog]]"` | Mixed blogId/indices
SCENARIO-004 | Quote at start | `"[[ (3) Start]]"` | `[3, 1, 6]`
SCENARIO-005 | Extra spaces around input | `"  [[(4) quote is]]  "` | `[4, 11, 19]`
SCENARIO-006 | Overlapping quotes | `"[[one two]] [[two three]]"` | Triples for both

### 3.2. Negative Paths

Scenario ID | Condition | Example Input | Expected Result
--- | --- | --- | ---
SCENARIO-007 | Blog ID not found | `"[[(999) anything]]"` | `null`
SCENARIO-008 | Blog content null | blog 5 content = null | `null`
SCENARIO-009 | Quoted text absent | `"[[(6) not found]]"` | `null`
SCENARIO-010 | Missing opening parenthesis | `"[[6) text]]"` | `null`
SCENARIO-011 | No ID between parentheses | `"[[( ) text]]"` | `null`
SCENARIO-012 | Empty input | `""` | `null`
SCENARIO-013 | No space after ID | `"[[(7)text]]"` | `null`
SCENARIO-014 | Extra bracket | `"[[[(8) text]]]"` | `null`
SCENARIO-015 | Non‑numeric ID | `"[[(abc) text]]"` | `null`
SCENARIO-016 | Blog exists but empty content | blog 9 | `null`
SCENARIO-017 | One quote valid, one invalid | Mixed input | Only first triple returned
SCENARIO-018 | All quotes invalid | All fail | `null`
SCENARIO-019 | Quoted text only whitespace | `"[[(12)     ]]"` | `null`

---

## 4. Exit Criteria

- All scenarios SCENARIO‑001 – SCENARIO‑019 are implemented and tested.  
- All scenarios pass successfully.  
- No critical bugs remain.
