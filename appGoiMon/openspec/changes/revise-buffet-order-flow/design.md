## Context

The application has two roles: `user` and `admin`. The business model is buffet dining, so users should not need admin approval to open a table once they have entered a valid available table code and completed simulated upfront payment. The current implementation contains useful building blocks, including table validation, combo selection, cart submission, admin table management, and order item status endpoints, but the flow still carries pending-payment semantics that do not match the desired buffet experience.

The new flow treats the server-side table session as the source of truth. A session starts only after combo selection and simulated payment confirmation, lasts 100 minutes, and remains active even if the user logs out or closes the app. Android timers are display-only; PHP/API validation must enforce expiration.

## Goals / Non-Goals

**Goals:**

- Let users enter an available table by table code without admin approval.
- Let users select a buffet combo, guest counts, and either cash or simulated QR payment before menu access.
- Create an active paid table session immediately after simulated payment confirmation.
- Enforce a 100-minute dining window from session creation.
- Preserve server-side sessions across user logout.
- Let users call menu items only while the session is active and not expired.
- Let admins approve, reject, and mark submitted order items as served.
- Let admins close active or expired tables and return them to available.
- Simplify user bottom navigation to Menu, Cart, and Ordered Items.

**Non-Goals:**

- No real payment gateway, bank transfer confirmation, or QR provider integration.
- No new kitchen role; admin remains responsible for item approval and served status.
- No table reservation, table transfer, or multi-table merge workflow.
- No cart persistence across logout unless it has already been submitted as an order.
- No automatic server job is required to close expired tables; expiration may be calculated lazily by API responses and enforced during order submission.

## Decisions

### Use automatic paid session creation after simulated payment

`create_session.php` should create an `active` session with `payment_status = 'paid'` after the app confirms cash or QR simulated payment. The table should move to `occupied` immediately.

Rationale: Buffet customers normally pay for the selected package before ordering. Admin approval before menu access adds friction and does not match the user's desired operational model.

Alternative considered: Keep `pending_payment` and require admin confirmation. This was rejected because payment is simulated and the user explicitly wants no admin approval when the table code is valid and available.

### Keep table state simple and session state authoritative

Tables should primarily use `available` and `occupied`. Session status should carry dining state: `active`, `expired`, and `closed`.

Rationale: A table is either free for a new party or not. The nuanced state belongs to the session because session timing, combo, guests, payment, and order history are session-scoped.

Alternative considered: Add many table statuses such as `waiting_open`, `waiting_checkout`, and `expired`. This was rejected for the revised flow because there is no admin-open or checkout request phase.

### Store and enforce a 100-minute session window on the server

Each active session should have `start_time` and `end_time`, where `end_time = start_time + 100 minutes`. APIs should return remaining time data and reject new orders once the server time exceeds `end_time`.

Rationale: Local Android timers are unreliable across logout, app restarts, clock changes, or multiple devices.

Alternative considered: Track remaining time only in Android. This was rejected because logout must not reset the session timer.

### Simulate cash and QR payment in the Android flow

The payment screen should support `cash` and `qr`. Cash shows the total and a confirmation button. QR shows a generated/mock QR visual or placeholder, amount, and transfer content, plus a confirmation button.

Rationale: This meets the product requirement without adding external payment complexity.

Alternative considered: Store unpaid sessions and let admin confirm payment. Rejected because it reintroduces the old approval bottleneck.

### Reduce user navigation to order-related tabs

The active user shell should contain Menu, Cart, and Ordered Items. The previous payment/checkout tab should be removed from the active session flow.

Rationale: Payment is already complete before menu access. The user needs only ordering, cart review, and status/history while dining.

Alternative considered: Keep Feedback/Contact placeholders or a checkout tab. Rejected because they distract from the core order flow and do not serve the revised buffet session model.

### Replace pending-only admin orders with status-based queues

Admin order management should load order items by status groups: pending, approved, served, and rejected. Admin can move items from pending to approved or rejected, then from approved to served.

Rationale: The current pending-only queue cannot support a realistic "mark served" step after approval because approved items disappear from the list.

Alternative considered: Keep `processing`. Rejected for this two-role app because `approved` is clearer and avoids implying a separate kitchen role.

### Logout clears only local authentication state

User and admin logout should return to login and clear local UI/ViewModel state. It must not close table sessions, reset the 100-minute timer, delete submitted orders, or change table status.

Rationale: Logout is an account/session action, not a restaurant table lifecycle action.

Alternative considered: Close the table on logout. Rejected because it would accidentally end an active dining session.

## Risks / Trade-offs

- Existing PHP code and Android text contain older pending-payment assumptions -> Update API behavior, user copy, and admin copy together to avoid mixed semantics.
- Database schema may not have `end_time` or `paid_at` fields suitable for this flow -> Add fields if needed, or compute `end_time` from `start_time` until a migration is available.
- Active sessions across logout can let another logged-in user enter the same occupied table code -> The chosen flow permits re-entry by table code; this matches simple buffet operation but is not identity-secure.
- Expired sessions may remain `active` in storage until an API checks them -> APIs must calculate expiration consistently and block order creation after expiry even if the stored status has not yet been updated.
- Cart contents can be lost on logout -> Acceptable because only submitted orders are authoritative server data.
- Admin may close a table while items are still pending or approved -> Show a warning in the table detail view, but allow closure because staff need operational override.

## Migration Plan

1. Update API response and mutation semantics for table check, session creation, session status, and order creation.
2. Add or normalize session timing fields and server-side remaining-time calculation.
3. Update Android DTOs, repositories, and ViewModel state for paid active sessions and expiration handling.
4. Replace the user waiting-payment/checkout flow with payment confirmation before session creation and active-session re-entry after logout.
5. Simplify active user bottom navigation to Menu, Cart, and Ordered Items.
6. Update admin table lists/details to show occupied and expired sessions with remaining time and close-table actions.
7. Replace pending-only admin order loading with status-filtered item queues.
8. Clean user-facing Vietnamese text touched by the flow.

Rollback is file-level: restore previous `pending_payment` session creation, waiting-payment screen routing, and pending-only admin orders if the revised buffet flow must be backed out.

## Open Questions

- Should expired sessions be physically updated to `expired` in the database on every status check, or should expiration remain computed until admin closes the table?
- Should cash and QR simulated payment confirmation happen on a dedicated payment screen, or as a final confirmation section inside the combo/guest screen?
- Should the admin be allowed to manually extend a session beyond 100 minutes in a later change?
