## ADDED Requirements

### Requirement: Admin can view pending ordered items
The system SHALL allow admins to view ordered items waiting for approval.

#### Scenario: Pending ordered items load
- **WHEN** an admin opens the Orders tab
- **THEN** the app SHALL request pending ordered items and show table, food name, quantity, note, and creation time.

#### Scenario: No pending items exist
- **WHEN** the pending ordered items API returns an empty list
- **THEN** the app SHALL show an empty state instead of a blank screen.

### Requirement: Admin can approve ordered items
The system SHALL allow admins to approve a pending ordered item.

#### Scenario: Ordered item is approved
- **WHEN** an admin approves a pending ordered item
- **THEN** the app SHALL call the approve API and remove or update that item from the pending list after success.

### Requirement: Admin can reject ordered items
The system SHALL allow admins to reject a pending ordered item.

#### Scenario: Ordered item is rejected
- **WHEN** an admin rejects a pending ordered item
- **THEN** the app SHALL call the reject API and remove or update that item from the pending list after success.

### Requirement: Admin can mark ordered items served
The system SHALL allow admins to mark a processing ordered item as served when the item has been delivered.

#### Scenario: Ordered item is marked served
- **WHEN** an admin marks an ordered item as served
- **THEN** the app SHALL call the mark-served API and reflect the new served status after success.
