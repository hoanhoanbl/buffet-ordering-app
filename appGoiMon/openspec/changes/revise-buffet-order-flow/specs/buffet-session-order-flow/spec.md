## ADDED Requirements

### Requirement: Users can enter only valid available tables
The system SHALL allow a user to proceed from table entry only when the submitted table code exists and the table is available.

#### Scenario: Valid available table proceeds to combo selection
- **WHEN** a logged-in user submits an existing table code whose table has no open session
- **THEN** the system SHALL show the combo and guest selection flow for that table

#### Scenario: Invalid table code is rejected
- **WHEN** a logged-in user submits a table code that does not exist
- **THEN** the system SHALL show an error that the table code is incorrect
- **THEN** the system SHALL keep the user on the table entry screen

#### Scenario: Occupied table is rejected for new setup
- **WHEN** a logged-in user submits a table code whose table has an active or expired unclosed session
- **THEN** the system SHALL not create a new session for that table
- **THEN** the system SHALL either resume the existing session when it is active and not expired or show that the table is already in use

### Requirement: Users pay before menu access
The system SHALL require combo selection, guest counts, and simulated payment confirmation before creating an active buffet session.

#### Scenario: Cash payment confirmation creates active paid session
- **WHEN** a user selects a combo, enters valid guest counts, selects cash, and confirms payment
- **THEN** the system SHALL create a table session with payment status paid
- **THEN** the system SHALL mark the session active and the table occupied
- **THEN** the system SHALL navigate the user to the active menu flow

#### Scenario: QR payment confirmation creates active paid session
- **WHEN** a user selects a combo, enters valid guest counts, selects QR, views the simulated QR payment details, and confirms payment
- **THEN** the system SHALL create a table session with payment method qr and payment status paid
- **THEN** the system SHALL mark the session active and the table occupied
- **THEN** the system SHALL navigate the user to the active menu flow

#### Scenario: Invalid guest counts block payment confirmation
- **WHEN** a user attempts to confirm payment without a combo or with paid guest count less than one
- **THEN** the system SHALL not create a session
- **THEN** the system SHALL show a validation message

### Requirement: Buffet sessions last 100 minutes
The system SHALL assign every new active buffet session a 100-minute dining window.

#### Scenario: Session creation stores dining window
- **WHEN** the system creates a paid active session
- **THEN** the system SHALL store a start time
- **THEN** the system SHALL define an end time exactly 100 minutes after the start time

#### Scenario: Active session status includes remaining time
- **WHEN** the app requests the status of an active session before its end time
- **THEN** the system SHALL return that the session is active
- **THEN** the system SHALL return the remaining time for display

#### Scenario: Expired session status blocks further ordering
- **WHEN** the app requests the status of a session after its end time
- **THEN** the system SHALL identify the session as expired or expired-by-time
- **THEN** the user interface SHALL prevent new order submission

### Requirement: Server enforces session expiration for ordering
The system MUST reject new order submissions after the 100-minute session window has expired.

#### Scenario: Order within time limit succeeds
- **WHEN** a user submits a non-empty cart for an active paid session before the end time
- **THEN** the system SHALL create an order and pending order items

#### Scenario: Order after time limit is rejected
- **WHEN** a user submits a cart for a session after the end time
- **THEN** the system SHALL reject the order request
- **THEN** the system SHALL return a message that the dining time has expired
- **THEN** the cart contents SHALL remain available locally for the user to review until they leave or refresh

### Requirement: Logout does not end buffet sessions
The system SHALL treat logout as a local authentication action and not as a table-session lifecycle action.

#### Scenario: User logout preserves server session
- **WHEN** a user logs out during an active buffet session
- **THEN** the system SHALL clear local user authentication state
- **THEN** the system SHALL not close the table session
- **THEN** the system SHALL not reset the session start time or end time

#### Scenario: User returns to active session after login
- **WHEN** a user logs in again and enters a table code for an active unexpired session
- **THEN** the system SHALL resume the active menu flow for that session
- **THEN** the remaining time SHALL be calculated from the original session end time

#### Scenario: User returns after session expiration
- **WHEN** a user logs in again and enters a table code for a session whose end time has passed
- **THEN** the system SHALL not allow new order submission
- **THEN** the system SHALL allow viewing already submitted orders for that session

### Requirement: Active user navigation contains only order workflow tabs
The system SHALL show active buffet users only the core ordering tabs: Menu, Cart, and Ordered Items.

#### Scenario: Active session shows simplified bottom navigation
- **WHEN** a user is inside an active buffet session
- **THEN** the bottom navigation SHALL include Menu, Cart, and Ordered Items
- **THEN** the bottom navigation SHALL not include a checkout or payment tab

### Requirement: Users can review submitted order status
The system SHALL allow users to view submitted orders and item statuses for the current session.

#### Scenario: User views ordered items
- **WHEN** a user opens the Ordered Items tab during or after a session
- **THEN** the system SHALL show submitted order items grouped by order or submission time
- **THEN** each item SHALL display its current status as pending, approved, served, or rejected

### Requirement: Admin manages order items by status
The system SHALL allow admins to process order items through pending, approved, served, and rejected statuses.

#### Scenario: Admin approves pending item
- **WHEN** an admin approves a pending order item
- **THEN** the system SHALL change that item status to approved
- **THEN** the item SHALL appear in the approved or in-service admin queue

#### Scenario: Admin rejects pending item
- **WHEN** an admin rejects a pending order item
- **THEN** the system SHALL change that item status to rejected
- **THEN** the item SHALL no longer appear in the pending queue

#### Scenario: Admin marks approved item served
- **WHEN** an admin marks an approved order item as served
- **THEN** the system SHALL change that item status to served
- **THEN** the item SHALL no longer appear in the approved queue

#### Scenario: Admin order queues are filterable by status
- **WHEN** an admin opens order management
- **THEN** the system SHALL allow viewing order items by pending, approved, served, and rejected status groups

### Requirement: Admin can close occupied or expired tables
The system SHALL allow admins to close a table session and return the table to available.

#### Scenario: Admin closes active table
- **WHEN** an admin closes an occupied table session
- **THEN** the system SHALL mark the session closed
- **THEN** the system SHALL mark the table available

#### Scenario: Admin closes expired table
- **WHEN** an admin closes a table session whose 100-minute window has expired
- **THEN** the system SHALL mark the session closed
- **THEN** the system SHALL mark the table available

#### Scenario: Admin sees warning for unfinished items
- **WHEN** an admin attempts to close a table with pending or approved order items
- **THEN** the system SHALL warn that some items are not served or rejected
- **THEN** the system SHALL still allow the admin to confirm closure
