## ADDED Requirements

### Requirement: Filter menu items by category and search query simultaneously
The system SHALL apply both category filter and search query filter together when both are active.

#### Scenario: Both filters applied together
- **WHEN** user has selected a category and entered a search query
- **THEN** displayed menu items match both the selected category AND the search query

#### Scenario: Category filter alone when no search query
- **WHEN** user has selected a category but search field is empty
- **THEN** displayed menu items match only the selected category filter

#### Scenario: Search filter alone when "Tất cả" selected
- **WHEN** user has "Tất cả" selected and enters a search query
- **THEN** displayed menu items match the search query across all categories

#### Scenario: No filters when "Tất cả" and empty search
- **WHEN** user has "Tất cả" selected and search field is empty
- **THEN** all menu items are displayed

### Requirement: Case-insensitive search filtering
The system SHALL perform case-insensitive matching when filtering by search query.

#### Scenario: Search matches regardless of case
- **WHEN** user enters "SƯỜN" in the search field
- **THEN** items with "sườn", "Sườn", or "SƯỜN" in the name are displayed

### Requirement: Empty state for filtered results
The system SHALL display an appropriate empty state message when filters produce no results.

#### Scenario: Empty state when category has no items
- **WHEN** user selects a category that has no menu items
- **THEN** the message "Chưa có món trong danh mục này" is displayed

#### Scenario: Empty state when search has no matches
- **WHEN** user enters a search query that matches no items in the selected category
- **THEN** the message "Không tìm thấy món ăn phù hợp" is displayed

### Requirement: Filter updates are immediate
The system SHALL update the displayed menu items immediately when category or search filters change.

#### Scenario: Grid updates immediately on category selection
- **WHEN** user taps a category chip
- **THEN** the menu grid updates immediately without additional user action

#### Scenario: Grid updates as user types in search
- **WHEN** user types characters in the search field
- **THEN** the menu grid updates in real-time as the search query changes

### Requirement: Preserve filter state during session
The system SHALL maintain the selected category filter while the user remains on the menu screen.

#### Scenario: Selected category persists when adding items to cart
- **WHEN** user selects a category and adds items to cart
- **THEN** the selected category filter remains active when returning focus to the menu

#### Scenario: Filter state resets when navigating away
- **WHEN** user navigates to a different tab and returns to the menu tab
- **THEN** the category filter resets to "Tất cả" and search query is cleared
