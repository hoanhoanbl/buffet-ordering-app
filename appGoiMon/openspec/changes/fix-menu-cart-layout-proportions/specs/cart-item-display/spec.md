## ADDED Requirements

### Requirement: Compact quantity controls in cart item layout

The cart screen SHALL display compact quantity adjustment buttons that maximize space efficiency while maintaining touch accessibility in the list layout.

#### Scenario: Quantity control button size
- **WHEN** cart items are displayed with quantity controls
- **THEN** the increment and decrement buttons SHALL have a size of 36dp
- **THEN** the buttons SHALL be smaller than the default 48dp to reduce horizontal space usage
- **THEN** the buttons SHALL remain above the 32dp minimum for reasonable touch interaction

#### Scenario: Quantity control text size
- **WHEN** quantity controls are displayed in a cart item
- **THEN** the button labels ("-" and "+") SHALL use titleSmall typography instead of titleMedium
- **THEN** the quantity number SHALL use titleSmall typography for visual consistency
- **THEN** the text SHALL be legible at the smaller size

#### Scenario: Quantity control spacing
- **WHEN** quantity controls are displayed in a horizontal row
- **THEN** the spacing between buttons and quantity number SHALL be 4dp
- **THEN** the tighter spacing SHALL create a more compact control group
- **THEN** the controls SHALL still be visually distinct and easy to target

### Requirement: Balanced cart item proportions

The cart screen SHALL display item cards with balanced proportions between image, text content, and controls to prevent layout expansion when text wraps.

#### Scenario: Cart item with wrapped text
- **WHEN** a cart item name or details cause text to wrap to multiple lines
- **THEN** the compact quantity controls SHALL prevent excessive horizontal space usage
- **THEN** the overall item card SHALL maintain reasonable height
- **THEN** the layout SHALL remain visually balanced with other items
