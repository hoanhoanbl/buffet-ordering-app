## ADDED Requirements

### Requirement: Search bar for filtering menu items
The system SHALL display a search text field at the top of the menu screen that allows users to filter menu items by name.

#### Scenario: Empty search shows all items
- **WHEN** the search field is empty
- **THEN** all menu items from the combo are displayed

#### Scenario: Search filters by item name
- **WHEN** user types "sườn" in the search field
- **THEN** only menu items containing "sườn" in their name are displayed (case-insensitive)

#### Scenario: Search with no matches
- **WHEN** user types a search query that matches no menu items
- **THEN** an empty state message is displayed indicating no items match the search

#### Scenario: Clearing search restores full list
- **WHEN** user clears the search field
- **THEN** all menu items are displayed again

### Requirement: Real-time search filtering
The system SHALL filter menu items as the user types without requiring a submit action.

#### Scenario: Immediate filter on keystroke
- **WHEN** user types each character in the search field
- **THEN** the menu list updates immediately to reflect the filtered results

### Requirement: Search field has clear visual design
The system SHALL display the search field with a search icon and placeholder text.

#### Scenario: Search field shows placeholder
- **WHEN** search field is empty and not focused
- **THEN** placeholder text "Tìm món ăn..." is displayed with a search icon
