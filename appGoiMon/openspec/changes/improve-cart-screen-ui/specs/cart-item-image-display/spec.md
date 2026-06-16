## ADDED Requirements

### Requirement: Display food image thumbnail in cart item
The system SHALL display a 60x60dp food image thumbnail on the left side of each cart item card.

#### Scenario: Image loaded successfully
- **WHEN** cart item has a valid image URL
- **THEN** the food image is displayed as a 60x60dp thumbnail with 8dp rounded corners

#### Scenario: Image is loading
- **WHEN** image is being fetched from the server
- **THEN** a loading indicator is displayed in the image placeholder

#### Scenario: Image URL is empty or null
- **WHEN** cart item has no image URL
- **THEN** a fallback placeholder with text "Chưa có ảnh món" is displayed

#### Scenario: Image fails to load
- **WHEN** image URL is invalid or network error occurs
- **THEN** an error fallback placeholder is displayed

### Requirement: Resolve image URL from multiple formats
The system SHALL resolve food image URLs supporting absolute URLs, relative paths, and filename-only formats.

#### Scenario: Absolute URL provided
- **WHEN** image field contains "http://" or "https://" prefix
- **THEN** the URL is used directly without modification

#### Scenario: Relative path provided
- **WHEN** image field contains a path starting with "/"
- **THEN** the path is appended to BASE_URL after removing leading slash

#### Scenario: Filename only provided
- **WHEN** image field contains only a filename without slashes
- **THEN** the system constructs URL as BASE_URL + "uploads/foods/" + filename

### Requirement: Display category name below food name
The system SHALL display the category name in a smaller font below the food name.

#### Scenario: Category name exists
- **WHEN** menu item has a category_name value
- **THEN** the category name is displayed below the food name in OrangeAccent color

#### Scenario: Category name is null or empty
- **WHEN** menu item has no category_name
- **THEN** no category label is displayed (skip rendering, do not show placeholder)

### Requirement: Image maintains aspect ratio
The system SHALL display the food image with cropped content scale to maintain visual quality.

#### Scenario: Image aspect ratio differs from container
- **WHEN** food image has different aspect ratio than 1:1
- **THEN** the image is cropped to fill the 60x60dp container without distortion
