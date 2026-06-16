## ADDED Requirements

### Requirement: Users can submit cart as an order
The system SHALL allow users to submit their cart contents as an order to the kitchen.

#### Scenario: Submit order with items
- **WHEN** a user taps the submit order button and the cart contains at least one item
- **THEN** the system SHALL send the cart contents to the create_order.php API with the active session_id
- **THEN** the system SHALL include all cart items with their food_id, quantity, and notes

#### Scenario: Cannot submit empty cart
- **WHEN** the cart is empty
- **THEN** the submit order button SHALL be disabled

#### Scenario: Loading state during submission
- **WHEN** the order submission API request is in progress
- **THEN** the system SHALL display a loading indicator
- **THEN** the submit button SHALL be disabled to prevent duplicate submissions

### Requirement: System provides feedback on order submission success
The system SHALL inform users when their order has been successfully submitted.

#### Scenario: Successful order submission
- **WHEN** the create_order.php API returns success
- **THEN** the system SHALL display a success message to the user
- **THEN** the system SHALL clear the cart
- **THEN** the cart bottom sheet SHALL close automatically

#### Scenario: Order confirmation shows order details
- **WHEN** an order is successfully submitted
- **THEN** the success message SHALL indicate the number of items ordered

### Requirement: System provides feedback on order submission failure
The system SHALL inform users when their order submission fails and allow recovery.

#### Scenario: Network error during submission
- **WHEN** the create_order.php API request fails due to network error
- **THEN** the system SHALL display an error message indicating connection failure
- **THEN** the cart contents SHALL remain unchanged
- **THEN** the user SHALL be able to retry submission

#### Scenario: API validation error
- **WHEN** the create_order.php API returns a validation error (e.g., invalid menu item or session expired)
- **THEN** the system SHALL display the error message from the API
- **THEN** the cart SHALL remain accessible for user review

#### Scenario: Session expired during order
- **WHEN** the create_order.php API returns a session error
- **THEN** the system SHALL display an error message indicating the session is no longer active
- **THEN** the system SHALL return the user to the table entry step

### Requirement: Order submission includes all required data
The system SHALL format order data correctly for the create_order.php API.

#### Scenario: Order request includes session ID
- **WHEN** submitting an order
- **THEN** the API request SHALL include the session_id from the active user session

#### Scenario: Order request includes item details
- **WHEN** submitting an order
- **THEN** each item in the API request SHALL include food_id (from menu item), quantity (from cart), and note (if provided)

#### Scenario: Empty notes are not sent
- **WHEN** a cart item has no note
- **THEN** the API request SHALL send an empty string or omit the note field for that item
