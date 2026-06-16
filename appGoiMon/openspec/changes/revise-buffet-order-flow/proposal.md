## Why

The current ordering flow is not coherent for a buffet restaurant: users wait for admin approval before using a table, payment is split from combo selection, and order item handling is tied to a pending-only admin queue. This change redesigns the flow around the actual business model: a user enters a free table, pays for a buffet combo immediately, receives a 100-minute dining session, and admin staff only manage food item status and table closure.

## What Changes

- Replace the admin-approved table opening flow with automatic session creation after a user enters a valid available table, selects a combo, and confirms simulated payment.
- Add a fixed 100-minute buffet session window that starts when payment is confirmed and the session is created.
- Add cash and simulated QR payment choices immediately after combo/guest selection.
- Remove the user checkout/payment bottom navigation flow because payment happens before menu access.
- Reduce the user active-session navigation to Menu, Cart, and Ordered Items.
- Preserve active table sessions across user logout; logout clears only local authentication and UI state, not the server-side buffet session.
- Block new order submissions after the 100-minute session expires while still allowing users to view already submitted orders.
- Redesign admin order handling around item statuses: pending, approved, served, and rejected.
- Let admins close occupied or expired tables, returning the table to available.
- **BREAKING**: Existing pending-payment/open-table semantics are replaced for the user buffet flow. The app should no longer require admin confirmation before a user can enter the menu after payment confirmation.

## Capabilities

### New Capabilities

- `buffet-session-order-flow`: Defines the end-to-end two-role buffet flow, including table entry, combo selection, simulated payment, 100-minute sessions, user logout behavior, order submission limits, admin item approval, and table closure.

### Modified Capabilities

- None. The root OpenSpec capability set is currently empty, so this change introduces a new formal capability instead of modifying an existing archived spec.

## Impact

- Android user flow:
  - `MainActivity`
  - `OrderViewModel`
  - user screens for table entry, combo/guest selection, payment confirmation, menu, cart, and order history
  - `UserSessionRepository`
  - `OrderRepository`
  - Retrofit DTOs and API declarations
- Android admin flow:
  - dashboard/table/order screens
  - admin table and order ViewModels/repositories
- PHP APIs:
  - `api/user/check_table.php`
  - `api/user/create_session.php`
  - `api/user/get_session_status.php`
  - `api/user/create_order.php`
  - `api/user/get_order_history.php`
  - admin table and order item endpoints
- Database/session data:
  - table session start and end time
  - 100-minute expiration logic
  - paid simulated payment state
  - simplified table/session/order item statuses
