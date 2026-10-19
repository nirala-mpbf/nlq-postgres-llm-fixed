# Step 2 — Profile Details
---
referenceId: R2
---
## Purpose
Handle the profile-details workflow for the user identified by `referenceId`.

## Workflow API
- Method: `POST`
- Path: `/workflows/submit-profile`
- Request body:
  ```json
  {
    "referenceId": "<referenceId>"
  }
  ```

## Workflow rules
1. Use this SOP when the user's request is about entering/submitting profile details or completing onboarding Step 2.
2. `referenceId` identifies the user. Its source will be supplied by the application; do not invent it.
3. Before calling the workflow API, obtain the current workflow state and allow/block decision from Brain through the application's Brain integration.
4. If Brain says `BLOCK`, do not call the workflow API. Explain Brain's supplied current state and reason. Do not independently decide that the step is allowed.
5. If Brain says `ALLOW`, call `POST /workflows/submit-profile` with the `referenceId` in the request body.
6. Treat the actual API response as authoritative. Never claim success unless the API returns success.

## Success response contract
All workflow endpoints use the same success structure. The `state` value is supplied by the workflow service and may change after a successful transition.

```json
{
  "referenceId": "R2",
  "state": "<state returned by workflow service>"
}
```

On success, use the confirmed response state to prepare a concise, user-friendly reply with Ollama. Do not invent a state or claim any unreturned operation occurred.

## Failure response contract
The workflow service may return this problem-details structure, including HTTP 409 for an invalid transition:

```json
{
  "type": "about:blank",
  "title": "Invalid Workflow Transition",
  "status": 409,
  "detail": "<failure detail returned by workflow service>",
  "instance": "/workflows/submit-profile",
  "allowedEvents": []
}
```

On failure, do not generate a success message. Preserve the actual status and explain the provided `detail`/current-state information without inventing a retry or transition. Do not call subsequent-step APIs.

## Step order
1. OTP verification
2. Profile details
3. Account statement
4. Udyam document

This file describes the SOP and API contract. It does not perform HTTP calls itself; Java workflow tools/client must implement the call and enforce Brain's decision.
