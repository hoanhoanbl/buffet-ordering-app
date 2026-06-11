## 1. Android User API Contracts

- [x] 1.1 Add Retrofit DTOs and request models for table check, combo list, session creation, session status, and combo menu responses.
- [x] 1.2 Add Retrofit API declarations for `check_table.php`, `get_combos.php`, `create_session.php`, `get_session_status.php`, and `get_menu_by_combo.php`.
- [x] 1.3 Implement a user session repository that wraps table check, combo loading, session creation, session refresh, and combo menu loading with `Result<T>` handling.

## 2. User Session State

- [x] 2.1 Implement a user session/order ViewModel with table-code validation state, selected table/session state, combo list state, guest-count form state, payment method state, and menu item state.
- [x] 2.2 Add validation for paid guest count greater than zero and free child count greater than or equal to zero before session creation.
- [x] 2.3 Add total preview calculation from selected combo price and paid guest count.
- [x] 2.4 Add handling for existing pending-payment and active sessions returned by table check.

## 3. User Screens

- [x] 3.1 Update `SelectTableScreen` to submit table code through ViewModel-driven validation and show loading/error feedback.
- [x] 3.2 Add `ComboAndGuestScreen` with combo cards, paid guest count, free child count, payment method controls, total preview, and create-session action.
- [x] 3.3 Add a waiting-payment screen/state with returned session summary and manual refresh action.
- [x] 3.4 Update `TableOrderScreen` to load and display combo menu items for the active session combo.
- [x] 3.5 Add empty, loading, and error states for combo loading, waiting-payment refresh, and combo menu loading.

## 4. Navigation Integration

- [x] 4.1 Update `MainActivity` user routing to use explicit user flow state instead of only `selectedUserTableCode`.
- [x] 4.2 Preserve logout and back behavior across table entry, combo setup, waiting payment, and table order screens.
- [x] 4.3 Ensure admin routing from the existing `admin-interface` change remains unchanged.

## 5. Verification

- [x] 5.1 Run Android compilation checks with the project Gradle wrapper.
- [ ] 5.2 Manually verify user table entry with invalid table, empty valid table, pending-payment table, and active table.
- [ ] 5.3 Manually verify combo selection, paid/free guest count validation, payment method selection, total preview, and session creation.
- [ ] 5.4 Manually verify waiting-payment refresh transitions to active session after admin confirmation.
- [ ] 5.5 Manually verify active session menu loads items from the selected combo.
