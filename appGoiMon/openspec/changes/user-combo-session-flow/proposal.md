## Why

The current user flow only asks for a table code and then shows a placeholder order screen, so users cannot select a buffet combo, enter guest counts, create a table session, or reach a real combo menu. The app already has PHP user APIs for checking tables, listing combos, creating sessions, and loading combo menus; this change connects those APIs into a usable mobile ordering flow.

## What Changes

- Update the user table entry flow to validate the table code with the existing `check_table.php` API before continuing.
- Add a combined combo and guest-count screen shown after a valid empty table is selected.
- Let users select an active combo, enter paid guest count, enter free child count, choose cash or QR payment, and preview the total before creating a session.
- Create a pending-payment table session through the existing `create_session.php` API.
- Add a waiting-payment state while the session is pending admin confirmation.
- Load the available menu items for the selected/active combo through the existing `get_menu_by_combo.php` API once the session is active.
- Preserve the admin interface change as a separate active change and avoid modifying admin requirements.

## Capabilities

### New Capabilities
- `user-combo-session-flow`: User table validation, combo selection, guest-count input, session creation, waiting-payment state, and combo menu loading.

### Modified Capabilities

None.

## Impact

- Android user flow in `MainActivity`, `SelectTableScreen`, `TableOrderScreen`, and new user-facing Compose screens.
- Android data layer in `ApiService`, user repositories, DTOs, and ViewModels.
- Existing PHP API endpoints under `C:\xampp\htdocs\appOrder\appGoiMon_API\api\user` are used as-is: `check_table.php`, `get_combos.php`, `create_session.php`, `get_session_status.php`, and `get_menu_by_combo.php`.
- No database schema change is expected.
- Existing admin flow should remain unchanged.
