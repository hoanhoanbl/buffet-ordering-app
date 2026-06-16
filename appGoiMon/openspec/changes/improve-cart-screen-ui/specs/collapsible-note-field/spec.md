## ADDED Requirements

### Requirement: Note field is collapsible to save space
The system SHALL display the note input field in a collapsed state by default when the note is empty.

#### Scenario: Cart item has no note
- **WHEN** cart item note field is empty
- **THEN** a "Thêm ghi chú" button is displayed instead of the full TextField

#### Scenario: Cart item has existing note
- **WHEN** cart item note field contains text
- **THEN** the full TextField is displayed showing the note content

#### Scenario: User taps "Thêm ghi chú" button
- **WHEN** user taps the "Thêm ghi chú" button
- **THEN** the TextField expands and becomes editable with focus

### Requirement: Expanded note field remains visible while editing
The system SHALL keep the note field expanded while the user is actively editing.

#### Scenario: User types in note field
- **WHEN** user is typing in the expanded note field
- **THEN** the field remains expanded and accepts text input

#### Scenario: Note field loses focus with empty content
- **WHEN** user finishes editing and note field is empty
- **THEN** the field collapses back to "Thêm ghi chú" button

#### Scenario: Note field loses focus with content
- **WHEN** user finishes editing and note field contains text
- **THEN** the field remains expanded showing the note content

### Requirement: Collapse state is independent per cart item
The system SHALL maintain separate expand/collapse state for each cart item's note field.

#### Scenario: Multiple cart items with different note states
- **WHEN** cart contains multiple items with varying note states
- **THEN** each item's note field expand/collapse state is independent

#### Scenario: Expanding one note does not affect others
- **WHEN** user expands note field for one cart item
- **THEN** other cart items' note fields remain in their current state (collapsed or expanded)

### Requirement: Note changes are saved immediately
The system SHALL save note changes to the cart item state as the user types.

#### Scenario: User types note text
- **WHEN** user enters text in the note field
- **THEN** the note is immediately saved to the cart item without requiring explicit save action

#### Scenario: Note persists when collapsing and re-expanding
- **WHEN** user enters note text, field loses focus, then user re-expands the field
- **THEN** the previously entered note text is still present

### Requirement: Visual indication for items with notes
The system SHALL provide visual feedback when a cart item has a note even when collapsed.

#### Scenario: Cart item has note in collapsed state
- **WHEN** note field is collapsed but contains text
- **THEN** a note count or indicator is displayed on the collapsed button (e.g., "Ghi chú (1)")
