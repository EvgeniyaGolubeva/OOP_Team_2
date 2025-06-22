# API Test-Scenario Catalogue

**Feature:** Compression Utility

**Document role:** Enumerates test scenarios for `CompressionUtil.compress` and `CompressionUtil.decompress`

## 1. Document Control

Parameter | Value
--- | ---
Version | 1.0
Author | QA
Date | 02/06/2025
Status | Waiting for Review
Last Updated | 03/06/2025

## 2. Documentation

### 2.1. Overview

`CompressionUtil.compress(String)`  
‑ If the input length > 70 chars, return Base64‑encoded **GZIP** data.  
‑ Otherwise return the original string unchanged.  

`CompressionUtil.decompress(String)`  
‑ Accepts a Base64 string produced by `compress` and returns the original text.

### 2.2. Preconditions

- Both methods receive non‑null `String` arguments.

### 2.3. Postconditions

- Compression followed by decompression yields the original value.
- Short strings (< 71 chars) are not altered by `compress`.

### 2.4. Details

Condition | compress behaviour | decompress behaviour
--- | --- | ---
`len(input) ≤ 70` | returns input | returns input (identity)
`len(input) > 70` | returns Base64(GZIP(input)) | returns original text
Non‑Base64 input to decompress | — | throws `IllegalArgumentException`
Base64 but not GZIP | — | throws `IOException`

---

## 3. Scenarios

### 3.1. Happy Paths

Scenario ID | Description | Input Length | Expected Result
--- | --- | --- | ---
SCENARIO-001 | Compress/decompress long text | > 70 | compressed ≠ input, decompressed == input
SCENARIO-002 | Short text bypass | ≤ 70 | compress == input (no change)

### 3.2. Negative Paths

Scenario ID | Condition | Input | Expected Result
--- | --- | --- | ---
SCENARIO-003 | Decompress invalid Base64 | `"not_base64!"` | `IllegalArgumentException`
SCENARIO-004 | Decompress Base64 non‑GZIP | Base64("plain") | `IOException`

---

## 4. Exit Criteria

- All scenarios SCENARIO‑001 – SCENARIO‑004 implemented and tested.  
- All scenarios pass.  
- No critical defects remain.

