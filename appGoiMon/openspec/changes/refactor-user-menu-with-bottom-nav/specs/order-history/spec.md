## ADDED Requirements

### Requirement: Display list of past orders for current session
The system SHALL retrieve and display all orders placed during the current table session.

#### Scenario: Orders displayed in reverse chronological order
- **WHEN** user views the order history screen
- **THEN** orders are displayed with the most recent order at the top

#### Scenario: Each order shows order number and timestamp
- **WHEN** order is displayed
- **THEN** the order number and creation timestamp are shown in the header

### Requirement: Order items with details
The system SHALL display all items within each order including name, quantity, note, and status.

#### Scenario: Order items show food name and quantity
- **WHEN** order is expanded or displayed
- **THEN** each item shows the food name and quantity (e.g., "Sườn xào chua ngọt x2")

#### Scenario: Order items show preparation status
- **WHEN** order item is displayed
- **THEN** the status is shown (e.g., "Đã phục vụ", "Đang chế biến", "Chờ duyệt")

#### Scenario: Order items show customer notes
- **WHEN** order item has a note
- **THEN** the note is displayed below or alongside the item name

### Requirement: Backend API for order history
The system SHALL call GET `/api/user/get_order_history.php?session_id={session_id}` to retrieve order history data.

#### Scenario: Successful API response returns order list
- **WHEN** API call succeeds
- **THEN** the response contains an array of orders with items and statuses

#### Scenario: API error shows error message
- **WHEN** API call fails
- **THEN** an error message is displayed with a retry button

### Requirement: Empty state when no orders exist
The system SHALL display an empty state when the session has no orders yet.

#### Scenario: No orders placed shows empty message
- **WHEN** user views order history and no orders have been placed
- **THEN** a message "Chưa có đơn hàng nào" is displayed

### Requirement: Refresh order history
The system SHALL provide a way to refresh the order history to see status updates.

#### Scenario: Pull to refresh updates order list
- **WHEN** user pulls down to refresh
- **THEN** the order history is reloaded from the API
