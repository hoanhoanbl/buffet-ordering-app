## Context

The Android user flow currently stores only `selectedUserTableCode` in `MainActivity`. `SelectTableScreen` accepts a table code and moves directly to `TableOrderScreen`, which is still a placeholder. Several user ViewModels and screens exist as empty shells, while the PHP API already supports the required session flow through `check_table.php`, `get_combos.php`, `create_session.php`, `get_session_status.php`, and `get_menu_by_combo.php`.

This change should build the user ordering session flow without creating new backend endpoints. The combined combo and guest-count screen must be optimized for phone use and should happen immediately after a valid empty table is selected.

## Goals / Non-Goals

**Goals:**
- Validate table codes before advancing from the table entry screen.
- Load active buffet combos from the PHP API.
- Present combo selection, paid guest count, free child count, payment method, and total preview in one mobile screen.
- Create a pending-payment table session from the combined screen.
- Show a waiting-payment state until admin confirms payment.
- Load combo-scoped menu items when the session is active.

**Non-Goals:**
- No new PHP endpoints or database schema changes.
- No redesign of admin screens.
- No full cart/order submission workflow beyond loading the menu for the active combo.
- No QR payment integration beyond passing `payment_method = 'qr'`.

## Decisions

### Use a User Session ViewModel

Introduce a dedicated user session/order ViewModel to own table validation, combo loading, guest-count form state, session creation, waiting-payment refresh, and combo menu loading.

Rationale: Keeping the full session state in `MainActivity` would make routing brittle as the flow grows. A ViewModel matches the existing architecture used by admin screens.

Alternative considered: Add more `remember` state to `MainActivity`. This is simpler initially but scales poorly once waiting-payment and menu loading state are added.

### Combine Combo and Guest Input

Use one `ComboAndGuestScreen` after table validation. The screen should show combo cards, guest count controls, payment method controls, calculated total, and the create-session button.

Rationale: The user explicitly chose a combined screen. It reduces mobile steps and keeps pricing context visible while guest count is entered.

Alternative considered: Separate combo selection and guest count screens. This is clearer structurally but adds another step for a simple buffet flow.

### Reuse Existing PHP APIs

Use the current user endpoints:
- `check_table.php` to validate table codes and discover existing sessions.
- `get_combos.php` to list active combos.
- `create_session.php` to create pending-payment sessions.
- `get_session_status.php` to refresh waiting-payment state.
- `get_menu_by_combo.php` to load available menu items for the selected combo.

Rationale: The backend already has the necessary behavior. This change should primarily complete Android integration.

### Handle Existing Sessions

If `check_table.php` returns an existing session:
- If the session is `pending_payment`, show the waiting-payment state.
- If the session is `active`, go to the table order/menu screen and load menu by the session's `combo_id`.

Rationale: A user returning to a table should not be forced to create a new session.

## Risks / Trade-offs

- Existing user screens and ViewModels are mostly stubs -> Implementation must add DTOs, repository methods, ViewModel state, and screens together to keep compile safety.
- Session status may change while the user waits -> Waiting screen must expose refresh and not assume automatic updates unless polling is implemented.
- Combo price values are returned as strings/decimals from PHP -> Android should parse defensively for total preview and display currency safely.
- Existing PHP messages contain mojibake in older files -> Android should provide local readable fallback messages.

## Migration Plan

1. Add Android DTOs and Retrofit declarations for existing user APIs.
2. Add a user repository and ViewModel for table/session/combo/menu operations.
3. Add `ComboAndGuestScreen` and a waiting-payment screen/state.
4. Update `SelectTableScreen`, `TableOrderScreen`, and `MainActivity` user routing.
5. Compile and manually verify table entry, combo selection, session creation, waiting payment refresh, and menu loading.

Rollback is file-level: return `MainActivity` to the previous `selectedUserTableCode` routing and remove the new user session screens/data wiring.

## Open Questions

- Should the waiting-payment screen poll automatically or only refresh when the user taps a button? Initial implementation should provide a manual refresh button.
- Should free child count have a maximum rule? Initial implementation should require non-negative values only.
