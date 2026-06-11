## ADDED Requirements

### Requirement: User table code is validated before session setup
The system SHALL validate a user-entered table code with the existing user table-check API before showing combo and guest-count setup.

#### Scenario: Empty table is selected
- **WHEN** a user enters a valid table code for a table without an active or pending session
- **THEN** the app SHALL proceed to the combined combo and guest-count setup screen for that table.

#### Scenario: Table does not exist
- **WHEN** a user enters a table code that the API does not recognize
- **THEN** the app SHALL show a readable error and remain on the table entry screen.

#### Scenario: Table already has pending payment session
- **WHEN** the table-check API returns a session with `status = 'pending_payment'`
- **THEN** the app SHALL show the waiting-payment state for that session.

#### Scenario: Table already has active session
- **WHEN** the table-check API returns a session with `status = 'active'`
- **THEN** the app SHALL open the table order screen and load menu items for the session combo.

### Requirement: User can select combo and enter guests in one screen
The system SHALL present active combo selection, paid guest count, free child count, payment method, and total preview in one phone-optimized screen.

#### Scenario: Combo setup screen loads
- **WHEN** a user reaches combo setup for an empty table
- **THEN** the app SHALL load active combos from the existing combos API and display selectable combo cards.

#### Scenario: User changes paid guest count
- **WHEN** the user changes the paid guest count
- **THEN** the total preview SHALL update using `paid_guest_count * price_per_person`.

#### Scenario: User enters invalid guest count
- **WHEN** the user attempts to create a session with paid guest count less than 1 or free child count less than 0
- **THEN** the app SHALL show validation feedback and SHALL NOT call the create-session API.

### Requirement: User can create a pending payment session
The system SHALL create a table session with selected combo, paid guest count, free child count, and payment method through the existing create-session API.

#### Scenario: Session is created successfully
- **WHEN** the user submits valid combo and guest data
- **THEN** the app SHALL call the create-session API and show a waiting-payment state with the returned session and total.

#### Scenario: Session creation fails
- **WHEN** the create-session API returns an error
- **THEN** the app SHALL show the error and keep the user's selected combo and guest-count inputs available for correction.

### Requirement: User can wait for admin payment confirmation
The system SHALL show a waiting-payment state for sessions that are not active yet.

#### Scenario: User refreshes waiting status
- **WHEN** the user taps refresh on the waiting-payment screen
- **THEN** the app SHALL call the session-status API and update the displayed session status.

#### Scenario: Admin confirms payment
- **WHEN** the refreshed session status is `active`
- **THEN** the app SHALL open the table order screen for that active session.

### Requirement: User sees menu for active combo
The system SHALL load available menu items for the active session combo on the table order screen.

#### Scenario: Active combo menu loads
- **WHEN** the table order screen opens with an active session combo
- **THEN** the app SHALL request menu items with `get_menu_by_combo.php` and display the returned available foods.

#### Scenario: Combo menu is empty
- **WHEN** the combo menu API returns no items
- **THEN** the app SHALL show an empty state instead of a blank screen.
