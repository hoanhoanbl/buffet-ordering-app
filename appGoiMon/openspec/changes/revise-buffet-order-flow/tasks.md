## 1. Backend Session Model

- [x] 1.1 Inspect the current `table_sessions`, `restaurant_tables`, `orders`, and `order_items` schema for required timing and payment fields.
- [x] 1.2 Add or document the required session fields for `start_time`, `end_time`, `payment_status`, `payment_method`, and optional `paid_at`.
- [x] 1.3 Normalize session statuses around `active`, `expired`, and `closed` for the revised buffet flow.
- [x] 1.4 Normalize table statuses around `available` and `occupied` for the revised buffet flow.
- [x] 1.5 Add a shared PHP helper for calculating session expiration and remaining minutes from server time.

## 2. Backend User APIs

- [x] 2.1 Update `api/user/check_table.php` to reject unknown table codes with a clear incorrect-table message.
- [x] 2.2 Update `api/user/check_table.php` to allow setup only for available tables and to return resumable active session details when applicable.
- [x] 2.3 Update `api/user/create_session.php` to create an active paid session immediately after simulated payment confirmation.
- [x] 2.4 Update `api/user/create_session.php` to set the buffet end time to 100 minutes after session start.
- [x] 2.5 Update `api/user/get_session_status.php` to return session status, expiration state, and remaining minutes.
- [x] 2.6 Update `api/user/create_order.php` to reject order creation when the session is not active, not paid, closed, or past the 100-minute end time.
- [x] 2.7 Ensure `api/user/get_order_history.php` returns item statuses compatible with pending, approved, served, and rejected.
- [x] 2.8 Stop using the old user checkout request flow in the revised user order path.

## 3. Backend Admin APIs

- [x] 3.1 Update table list/detail admin APIs to include active session timing and remaining minutes.
- [x] 3.2 Replace or extend the pending-only order endpoint with a status-filterable order item endpoint.
- [x] 3.3 Update order item status mutation APIs to support pending to approved, pending to rejected, and approved to served.
- [x] 3.4 Update parent order status refresh logic to account for approved, served, rejected, and pending item combinations.
- [x] 3.5 Update close-table behavior to close active or expired sessions and return the table to available.
- [x] 3.6 Add warning data for table detail when pending or approved items still exist before table closure.

## 4. Android Data Layer

- [x] 4.1 Update Retrofit DTOs for session timing, remaining minutes, expiration state, and paid session fields.
- [x] 4.2 Update Retrofit declarations for revised user session APIs and status-filtered admin order item APIs.
- [x] 4.3 Update `UserSessionRepository` for paid active session creation and resumable session status handling.
- [x] 4.4 Update `OrderRepository` for revised item statuses and status-filtered admin queues.
- [x] 4.5 Update `TableRepository` for active/expired session timing and close-table behavior.

## 5. Android User Flow

- [x] 5.1 Replace the waiting-payment step with a payment confirmation step after combo and guest selection.
- [x] 5.2 Add cash simulated payment confirmation UI with total amount and confirm action.
- [x] 5.3 Add QR simulated payment UI with amount, transfer content, mock QR display, and confirm action.
- [x] 5.4 Update `OrderViewModel` to create an active paid session after simulated payment confirmation.
- [x] 5.5 Update `OrderViewModel` to resume an active unexpired session after logout/login and table re-entry.
- [x] 5.6 Update `OrderViewModel` to represent expired sessions and prevent new order submission after expiration.
- [x] 5.7 Show the session countdown and remaining minutes in the active menu flow.
- [x] 5.8 Show a warning when the remaining session time is 10 minutes or less.
- [x] 5.9 Reduce active user bottom navigation to Menu, Cart, and Ordered Items.
- [x] 5.10 Ensure submitted orders remain visible after logout/login while unsent local cart data may be cleared.
- [x] 5.11 Clean Vietnamese UI copy touched by the revised user flow.

## 6. Android Admin Flow

- [x] 6.1 Update admin table cards to show table availability, occupied sessions, expired sessions, combo, guest counts, and remaining minutes.
- [x] 6.2 Update admin table detail to show session timing, payment method, paid status, and order item summary.
- [x] 6.3 Add or revise admin close-table confirmation with a warning for pending or approved items.
- [x] 6.4 Replace pending-only order management with queues for pending, approved, served, and rejected items.
- [x] 6.5 Show approve and reject actions only for pending items.
- [x] 6.6 Show the served action only for approved items.
- [x] 6.7 Clean Vietnamese UI copy touched by the revised admin flow.

## 7. Verification

- [x] 7.1 Verify a user can enter a valid available table, select combo/guests, confirm cash payment, and reach the menu.
- [x] 7.2 Verify a user can enter a valid available table, select combo/guests, confirm QR payment, and reach the menu.
- [x] 7.3 Verify an invalid table code shows an incorrect-table error.
- [x] 7.4 Verify an occupied table cannot create a second active session.
- [x] 7.5 Verify logout does not close the session or reset the 100-minute timer.
- [x] 7.6 Verify login again with the same active table resumes the original session and remaining time.
- [x] 7.7 Verify order submission succeeds before session expiration and creates pending items.
- [x] 7.8 Verify order submission is rejected after the 100-minute end time.
- [x] 7.9 Verify admin can move an item from pending to approved, pending to rejected, and approved to served.
- [x] 7.10 Verify admin can close an active or expired table and the table becomes available.
- [x] 7.11 Run the Android build or compile check available for the project.
- [x] 7.12 Manually smoke test the PHP endpoints against local XAMPP/MySQL data.
