# API Test-Scenario Catalogue

**Feature:** Notification Utility

**Document role:** Enumerates test scenarios for `NotificationUtil.notify_users` / `fake_notify_users`

## 1. Document Control

Parameter | Value
--- | ---
Version | 1.0
Author | QA
Date | 2/06/2025
Status | Waiting for Review
Last Updated | 3/06/2025

## 2. Documentation

### 2.1. Overview

`NotificationUtil.notify_users(List<String>)` calls an external REST service to send e‑mail / push notifications.  
For unit tests, `fake_notify_users` logs the intended recipients instead of performing the HTTP call.

### 2.2. Preconditions

- Method receives a non‑null `List<String>` of recipient identifiers.
- Network configuration.

### 2.3. Postconditions

- For `fake_notify_users` the method completes without throwing.
- For the real method, HTTP 2xx indicates success; non‑2xx is handled gracefully (retry or log).

### 2.4. Details

Operation | Behaviour
--- | ---
`fake_notify_users` | Prints `Sending notifications to [ids]`; never throws
`notify_users` | POSTs JSON `{recipients:[...]}` to configured URI; retries 3× on 5xx; logs errors

---

## 3. Scenarios

### 3.1. Happy Paths

Scenario ID | Description | Expected Result
--- | --- | ---
SCENARIO-001 | Single e‑mail | `fake_notify_users` returns void
SCENARIO-002 | Multiple recipients | no exception thrown

### 3.2. Edge / Negative

Scenario ID | Condition | Example | Expected Result
--- | --- | --- | ---
SCENARIO-003 | Empty list | `[]` | no‑op, no exception
SCENARIO-004 | Invalid URI configured | override URI | method handles, logs, no exception
SCENARIO-005 | Simulated server 5xx (fake) | fake call | no exception

> **Note:** Real HTTP error handling depends on the external service's reliability and is not tested here.

---

## 4. Exit Criteria

- All scenarios SCENARIO‑001 – SCENARIO‑005 implemented and pass.  
- No critical issues remain.
