## ADDED Requirements

### Requirement: Display horizontal row of category filter chips
The system SHALL display a horizontally scrolling row of FilterChip components above the menu grid, showing all unique categories derived from menu items.

#### Scenario: Category chips displayed on screen load
- **WHEN** user views the menu screen with loaded menu items
- **THEN** a horizontal row of category chips is displayed between the search bar and menu grid

#### Scenario: Chips scroll horizontally when many categories exist
- **WHEN** there are more categories than fit on screen width
- **THEN** the user can scroll horizontally to view all category chips

### Requirement: Include "Tất cả" chip to show all items
The system SHALL display a "Tất cả" (All) chip as the first item in the category row.

#### Scenario: "Tất cả" chip is first in the list
- **WHEN** category chips are rendered
- **THEN** the "Tất cả" chip appears as the leftmost chip before any category-specific chips

#### Scenario: "Tất cả" selected by default
- **WHEN** user first views the menu screen
- **THEN** the "Tất cả" chip is selected and all menu items are displayed

### Requirement: Visual indication of selected category
The system SHALL visually highlight the currently selected category chip.

#### Scenario: Selected chip has distinct appearance
- **WHEN** a category chip is selected
- **THEN** that chip is visually highlighted to indicate selection

#### Scenario: Only one chip selected at a time
- **WHEN** user taps a different category chip
- **THEN** the previously selected chip is deselected and the newly tapped chip is selected

### Requirement: Category selection filters menu items
The system SHALL filter the displayed menu items when a category chip is tapped.

#### Scenario: Selecting category shows only items in that category
- **WHEN** user taps a category chip (not "Tất cả")
- **THEN** only menu items matching that category are displayed in the grid

#### Scenario: Selecting "Tất cả" shows all items
- **WHEN** user taps the "Tất cả" chip
- **THEN** all menu items are displayed regardless of category

### Requirement: Derive categories from menu items
The system SHALL derive the list of categories from the category_name field of loaded menu items.

#### Scenario: Categories extracted from menu items
- **WHEN** menu items are loaded
- **THEN** unique category names are extracted and displayed as filter chips

#### Scenario: Empty category list when no menu items
- **WHEN** no menu items are loaded
- **THEN** only the "Tất cả" chip is displayed

### Requirement: Categories are sorted alphabetically
The system SHALL display category chips in alphabetical order after the "Tất cả" chip.

#### Scenario: Categories sorted by name
- **WHEN** multiple categories exist
- **THEN** category chips are displayed in alphabetical order (excluding "Tất cả" which is always first)
