## ADDED Requirements

### Requirement: Full-screen cart accessible via tab navigation
The system SHALL display the cart as a dedicated full-screen page accessible from the "Giỏ hàng" tab in the bottom navigation.

#### Scenario: Cart screen shows all cart items
- **WHEN** user navigates to the "Giỏ hàng" tab
- **THEN** all items in the cart are displayed in a scrollable list

#### Scenario: Empty cart shows empty state
- **WHEN** user navigates to the cart tab and cart is empty
- **THEN** an empty state message "Giỏ hàng trống" is displayed

### Requirement: Cart screen maintains existing cart functionality
The system SHALL provide the same cart management features as the previous bottom sheet implementation.

#### Scenario: User can adjust item quantity
- **WHEN** user taps [-] or [+] buttons on a cart item
- **THEN** the quantity is decremented or incremented accordingly

#### Scenario: User can add notes to cart items
- **WHEN** user types in the note field for a cart item
- **THEN** the note is saved to that cart item

#### Scenario: Removing item when quantity reaches zero
- **WHEN** user decrements quantity to 0
- **THEN** the item is removed from the cart

### Requirement: Submit order button
The system SHALL display a "Gọi món" button at the bottom of the cart screen.

#### Scenario: Submit button disabled when cart is empty
- **WHEN** cart is empty
- **THEN** the "Gọi món" button is disabled

#### Scenario: Submit button enabled when cart has items
- **WHEN** cart has at least one item
- **THEN** the "Gọi món" button is enabled

#### Scenario: Submit button shows loading state
- **WHEN** order submission is in progress
- **THEN** the button shows a loading indicator and is disabled

### Requirement: Cart header shows item count
The system SHALL display a header showing the total number of items in the cart.

#### Scenario: Header shows total quantity
- **WHEN** cart has items
- **THEN** the header displays "Giỏ hàng (N món)" where N is the sum of all item quantities
