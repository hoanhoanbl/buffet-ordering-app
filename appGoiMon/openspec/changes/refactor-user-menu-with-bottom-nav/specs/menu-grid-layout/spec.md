## ADDED Requirements

### Requirement: Two-column grid layout for menu items
The system SHALL display menu items in a two-column grid layout using LazyVerticalGrid.

#### Scenario: Menu items arranged in two columns
- **WHEN** user views the menu screen
- **THEN** menu items are displayed in a grid with two columns of equal width

#### Scenario: Grid scrolls vertically
- **WHEN** user scrolls the menu
- **THEN** the grid scrolls vertically and both columns scroll together

### Requirement: Compact menu item cards
The system SHALL display each menu item as a compact card with image, name, category, and action button.

#### Scenario: Card shows item image
- **WHEN** menu item has an image URL
- **THEN** the image is displayed at the top of the card with appropriate aspect ratio

#### Scenario: Card shows item name and category
- **WHEN** card is rendered
- **THEN** the item name is displayed below the image and the category name is displayed below the title

#### Scenario: Card shows add button for items not in cart
- **WHEN** menu item is not in the cart
- **THEN** a [+] button is displayed in the bottom-right corner of the card

#### Scenario: Card shows quantity controls for items in cart
- **WHEN** menu item is already in the cart
- **THEN** [-] [quantity] [+] controls are displayed instead of the single [+] button

### Requirement: No price display on menu cards
The system SHALL NOT display price information on menu item cards (buffet model).

#### Scenario: Card does not show price
- **WHEN** card is rendered
- **THEN** no price information is displayed

### Requirement: No status text on menu cards
The system SHALL NOT display status text (e.g., "Trạng thái: available") on menu item cards.

#### Scenario: Card does not show status
- **WHEN** card is rendered
- **THEN** no status text is displayed
