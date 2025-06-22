# API Test-Scenario Catalogue

**Feature:** Person Tag Utility

**Document role:** Enumerates test scenarios for the `PersonTagUtil.parse_blog_and_notify` feature


## 1. Document Control

Parameter | Value
--- | ---
Version | 1.0
Author | QA
Date | 15/06/2025
Status | Waiting for Review
Last Updated | 18/06/2025

## 2. Documentation

### 2.1. Overview

`PersonTagUtil.parse_blog_and_notify(text)` scans the blog **markdown/plain‑text** for e‑mail‑style mentions of the form `@user@example.com`.  
It collects syntactically valid e‑mail strings and forwards them to `NotificationUtil.fake_notify_users(validEmails)`.

### 2.2. Preconditions

- `NotificationUtil.fake_notify_users` is available and does not throw.  
- The input parameter `text` is a non‑null `String`.

### 2.3. Postconditions

- All valid, **unique** e‑mail tags are passed to the notification util in the order encountered.  
- Invalid or duplicate addresses are ignored.  
- The method never throws; it silently skips malformed tags.

### 2.4. Details

Validation Rule | Description
--- | ---
Tag prefix | Must start with `@`
Terminator | `space`, `tab`, `newline`, or another `@` ends the tag
Email syntax | Checked via `jakarta.mail.internet.InternetAddress.validate()`
Deduplication | Same address appearing multiple times is added once
Notification call | Exactly one call to `fake_notify_users(List<String>)`

---

## 3. Scenarios

### 3.1. Happy Paths

Scenario ID | Description | Input | Expected Emails Passed
--- | --- | --- | ---
SCENARIO-001 | Single valid tag | `"Hello @alice@example.com!"` | `["alice@example.com"]`
SCENARIO-002 | Multiple valid tags separated by spaces | `"@bob@test.io thanks @carol@corp.org"` | `["bob@test.io","carol@corp.org"]`
SCENARIO-003 | Tags separated by newlines and tabs | `"Hi\n@dave@mail.com\t welcome"` | `["dave@mail.com"]`
SCENARIO-004 | Duplicate tags | `"@eve@site.com and again @eve@site.com"` | `["eve@site.com"]`

### 3.2. Negative / Edge Paths

Scenario ID | Condition | Example Input | Expected Emails Passed
--- | --- | --- | ---
SCENARIO-005 | Invalid email format | `"Hello @not-an-email"` | `[]`
SCENARIO-006 | Consecutive `@` without address | `"Look here @@bad.com"` | `[]`
SCENARIO-007 | Mixed valid and invalid | `"@good@ok.com @@ bad @no@"` | `["good@ok.com"]`
SCENARIO-008 | Email followed by punctuation | `"Thanks, @foo@bar.com,"` | `["foo@bar.com"]`
SCENARIO-009 | Empty input string | `""` | `[]`
SCENARIO-010 | Very long text with many tags | 1 MB doc with 100 addresses | list of 100 unique addresses
SCENARIO-011 | Address immediately followed by another `@` | `"@one@mail.com@two@site.net"` | `["one@mail.com","two@site.net"]`

---

## 4. Exit Criteria

- All scenarios SCENARIO-001 – SCENARIO-011 implemented and tested.  
- All pass successfully.  
- No critical bugs remain.
