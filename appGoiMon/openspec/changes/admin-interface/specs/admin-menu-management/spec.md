## ADDED Requirements

### Requirement: Admin can view menu items
The system SHALL allow admins to view menu items across categories, including non-visible statuses.

#### Scenario: Menu items load
- **WHEN** an admin opens the Menu items view
- **THEN** the app SHALL load menu items from an admin menu listing API and show name, category, description, image reference, and status.

### Requirement: Admin can create menu items
The system SHALL allow admins to create menu items with category, name, optional image, optional description, and status.

#### Scenario: Menu item is created
- **WHEN** an admin submits valid menu item data
- **THEN** the app SHALL call the menu item create API and refresh the menu list after success.

### Requirement: Admin can update menu items
The system SHALL allow admins to update existing menu item details.

#### Scenario: Menu item is updated
- **WHEN** an admin edits and saves an existing menu item
- **THEN** the app SHALL call the menu item update API and refresh the menu item details after success.

### Requirement: Admin can change menu item status
The system SHALL allow admins to set a menu item status to `available`, `out_of_stock`, or `hidden`.

#### Scenario: Menu item status changes
- **WHEN** an admin changes the status of a menu item
- **THEN** the app SHALL call the menu item status API and show the updated status after success.

### Requirement: Admin can soft delete menu items
The system SHALL allow admins to delete a menu item by hiding it instead of removing the database row.

#### Scenario: Menu item is deleted
- **WHEN** an admin deletes a menu item
- **THEN** the API SHALL set the menu item status to `hidden` and the app SHALL refresh the menu list after success.
