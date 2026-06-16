## ADDED Requirements

### Requirement: Menu item card uniform height in grid layout

The menu screen grid layout SHALL display all menu item cards with uniform heights regardless of text content length, ensuring visual consistency across the 2-column grid.

#### Scenario: Short food name single line display
- **WHEN** a menu item has a short name that fits on one line (e.g., "Bào ngư")
- **THEN** the card SHALL reserve space for two lines of text to match multi-line cards
- **THEN** the card height SHALL be identical to cards with longer names

#### Scenario: Long food name multi-line display
- **WHEN** a menu item has a long name that wraps to two lines (e.g., "Cá mú tẩm đặc xốt chấm")
- **THEN** the card SHALL display exactly two lines of text
- **THEN** the card height SHALL be identical to cards with shorter names

#### Scenario: Very long food name overflow
- **WHEN** a menu item has a very long name that exceeds two lines
- **THEN** the card SHALL truncate the text with ellipsis at the end of the second line
- **THEN** the card height SHALL remain identical to other cards

### Requirement: Compact quantity controls in menu grid

The menu screen item cards SHALL display compact quantity adjustment buttons that balance touch accessibility with space efficiency in the grid layout.

#### Scenario: Quantity control button size
- **WHEN** a menu item is added to cart and quantity controls appear
- **THEN** the increment and decrement buttons SHALL have a size of 36dp
- **THEN** the buttons SHALL remain large enough for comfortable touch interaction

#### Scenario: Quantity control text size
- **WHEN** quantity controls are displayed in a menu item card
- **THEN** the button labels ("-" and "+") SHALL use titleSmall typography
- **THEN** the quantity number SHALL use titleSmall typography for visual consistency
