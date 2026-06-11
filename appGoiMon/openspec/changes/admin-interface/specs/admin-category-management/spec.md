## ADDED Requirements

### Requirement: Admin can view categories
The system SHALL allow admins to view all categories with active or inactive status.

#### Scenario: Categories load
- **WHEN** an admin opens the Categories view
- **THEN** the app SHALL load categories from the admin categories API and show category name and status.

### Requirement: Admin can create categories
The system SHALL allow admins to create a category with a name and active status.

#### Scenario: Category is created
- **WHEN** an admin submits a valid new category name
- **THEN** the app SHALL call the category create API and refresh the category list after success.

### Requirement: Admin can update categories
The system SHALL allow admins to update a category name and status.

#### Scenario: Category is updated
- **WHEN** an admin edits and saves an existing category
- **THEN** the app SHALL call the category update API and show the updated category after success.

### Requirement: Admin can soft delete categories
The system SHALL allow admins to delete a category by setting it inactive instead of removing the database row.

#### Scenario: Category is deleted
- **WHEN** an admin deletes a category
- **THEN** the API SHALL set category status to `inactive` and the app SHALL refresh the category list after success.

### Requirement: Inactive categories are not selected by default for new menu items
The system SHALL avoid presenting inactive categories as default choices when creating or editing visible menu items.

#### Scenario: Creating a menu item
- **WHEN** an admin creates a menu item
- **THEN** the category selector SHALL prefer active categories and SHALL NOT auto-select an inactive category.
