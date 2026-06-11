## ADDED Requirements

### Requirement: Admin can view all tables
The system SHALL allow admins to view all restaurant tables with current status and active or pending session summary.

#### Scenario: Table list loads
- **WHEN** an admin opens the Tables tab
- **THEN** the app SHALL load tables from the admin tables API and show table code, table name, status, and session summary when present.

#### Scenario: Table list request fails
- **WHEN** the admin tables API request fails
- **THEN** the app SHALL show a readable error state and allow retry.

### Requirement: Admin can inspect a table session
The system SHALL allow admins to open a table and inspect its current session, payment information, and ordered items.

#### Scenario: Table detail opens
- **WHEN** an admin selects a table that has an active or pending payment session
- **THEN** the app SHALL show session details and ordered items for that table.

### Requirement: Admin can confirm payment
The system SHALL allow admins to confirm payment for a pending payment session.

#### Scenario: Payment is confirmed
- **WHEN** an admin confirms payment for a pending payment session
- **THEN** the app SHALL call the confirm payment API and refresh the table session after success.

### Requirement: Admin can close an active table
The system SHALL allow admins to close an active table session and return the table to available status.

#### Scenario: Active table is closed
- **WHEN** an admin closes an active table session
- **THEN** the app SHALL call the close table API and refresh the table list or detail after success.
