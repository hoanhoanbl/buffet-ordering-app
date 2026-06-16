## ADDED Requirements

### Requirement: Users can add menu items to cart
The system SHALL allow users to add menu items from the active session menu to a shopping cart.

#### Scenario: Add item to empty cart
- **WHEN** a user taps the add button on a menu item
- **THEN** the system SHALL add that item to the cart with quantity 1
- **THEN** the cart item count badge SHALL display "1"

#### Scenario: Add same item increases quantity
- **WHEN** a user adds a menu item that is already in the cart
- **THEN** the system SHALL increment the quantity of that existing cart item by 1
- **THEN** the cart SHALL NOT contain duplicate entries for the same menu item

#### Scenario: Add item updates cart count
- **WHEN** a user adds any menu item to the cart
- **THEN** the cart item count badge SHALL reflect the total number of items (sum of all quantities)

### Requirement: Users can view cart contents
The system SHALL provide a way for users to review all items currently in their cart.

#### Scenario: Open cart with items
- **WHEN** a user taps the cart button and the cart contains items
- **THEN** the system SHALL display a bottom sheet showing all cart items with their names, quantities, and individual notes

#### Scenario: Cart shows item details
- **WHEN** the cart bottom sheet is displayed
- **THEN** each cart item SHALL show the menu item name, current quantity, and any note the user has added

#### Scenario: Empty cart shows message
- **WHEN** a user taps the cart button and the cart is empty
- **THEN** the system SHALL display a message indicating the cart is empty

### Requirement: Users can adjust item quantities in cart
The system SHALL allow users to increase or decrease the quantity of items in the cart.

#### Scenario: Increase quantity
- **WHEN** a user taps the plus button on a cart item
- **THEN** the system SHALL increment that item's quantity by 1

#### Scenario: Decrease quantity
- **WHEN** a user taps the minus button on a cart item with quantity greater than 1
- **THEN** the system SHALL decrement that item's quantity by 1

#### Scenario: Remove item when quantity reaches zero
- **WHEN** a user taps the minus button on a cart item with quantity 1
- **THEN** the system SHALL remove that item from the cart entirely

### Requirement: Users can add notes to cart items
The system SHALL allow users to add optional text notes to individual cart items.

#### Scenario: Add note to cart item
- **WHEN** a user enters text in the note field for a cart item
- **THEN** the system SHALL store that note with the cart item
- **THEN** the note SHALL be included when submitting the order

#### Scenario: Notes are optional
- **WHEN** a user does not enter a note for a cart item
- **THEN** the system SHALL accept the cart item without a note

### Requirement: Users can remove items from cart
The system SHALL allow users to remove items from the cart before submitting the order.

#### Scenario: Remove item from cart
- **WHEN** a user decrements a cart item quantity to zero
- **THEN** the system SHALL remove that item from the cart
- **THEN** the cart item count SHALL update to reflect the removal

### Requirement: Cart state persists during active session
The system SHALL maintain cart contents while the user remains in the active menu step.

#### Scenario: Cart survives screen orientation changes
- **WHEN** the device orientation changes while viewing the menu
- **THEN** the cart contents SHALL remain unchanged

#### Scenario: Cart cleared on logout
- **WHEN** a user logs out
- **THEN** the system SHALL clear the cart contents
