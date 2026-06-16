## ADDED Requirements

### Requirement: Display logo in menu header row
The system SHALL display the logo in a horizontal Row layout alongside the search field and search button.

#### Scenario: Logo appears in header row
- **WHEN** user views the menu screen
- **THEN** the logo is displayed in a Row at the top alongside search elements

### Requirement: Logo is 48dp and positioned on the left
The system SHALL display the logo at 48dp size on the left side of the header row with circular clipping.

#### Scenario: Logo size is 48dp
- **WHEN** logo is displayed in menu header
- **THEN** the logo is rendered at 48dp height and width

#### Scenario: Logo is first element in row
- **WHEN** header row is rendered
- **THEN** the logo is the leftmost element

#### Scenario: Logo has circular shape
- **WHEN** logo is displayed
- **THEN** the logo is clipped to a circular shape

### Requirement: Search field is positioned in center with flexible width
The system SHALL position the search TextField in the center of the row with weight(1f) to fill available space.

#### Scenario: Search field fills available space
- **WHEN** header row is rendered
- **THEN** the search TextField expands to fill space between logo and search button

### Requirement: Search button is positioned on the right
The system SHALL display a search IconButton on the right side of the header row at 48dp size.

#### Scenario: Search button is rightmost element
- **WHEN** header row is rendered
- **THEN** the search IconButton is the rightmost element at 48dp size

#### Scenario: Search icon uses standard search icon
- **WHEN** search button is displayed
- **THEN** it shows the standard search icon (Icons.Default.Search)

### Requirement: Row has 8dp spacing between elements
The system SHALL space elements in the header row with 8dp horizontal spacing.

#### Scenario: Elements have consistent spacing
- **WHEN** header row is rendered
- **THEN** there is 8dp space between logo and search field, and between search field and search button

### Requirement: Logo loads from server URL
The system SHALL load the logo image from BASE_URL + "uploads/foods/logo.jpg".

#### Scenario: Logo URL is correctly constructed
- **WHEN** system needs to display logo
- **THEN** the image URL is constructed as BASE_URL concatenated with "uploads/foods/logo.jpg"

### Requirement: Display loading indicator while logo loads
The system SHALL display a small circular loading indicator while the logo image is being fetched.

#### Scenario: Loading indicator shown during fetch
- **WHEN** logo image is being downloaded
- **THEN** a small circular progress indicator is displayed in the logo placeholder

### Requirement: Handle logo loading errors gracefully
The system SHALL display a fallback when logo fails to load without breaking the header layout.

#### Scenario: Logo fails to load
- **WHEN** logo image fails to load due to network error or invalid URL
- **THEN** the header layout remains intact without crashing

#### Scenario: Fallback preserves horizontal spacing
- **WHEN** logo fails to load
- **THEN** the horizontal spacing is preserved as if logo were present

### Requirement: Search functionality remains unchanged
The system SHALL maintain existing search functionality with the new layout.

#### Scenario: Search query updates on text change
- **WHEN** user types in search field
- **THEN** the search query is updated and menu items are filtered

#### Scenario: Search button focuses search field
- **WHEN** user taps search IconButton
- **THEN** focus is set to the search TextField
