## MODIFIED Requirements

### Requirement: Image URL resolution for cart items

The cart screen SHALL correctly resolve food image URLs for all supported image path formats without duplicating path segments. The system MUST handle three distinct image path formats: filename only, absolute path with leading slash, and relative path without leading slash.

#### Scenario: Filename only image path
- **WHEN** the menu item image field contains only a filename (e.g., `"pho.jpg"`)
- **THEN** the system SHALL construct the URL as `BASE_URL + "uploads/foods/" + filename`
- **THEN** the image SHALL load successfully from the correct path

#### Scenario: Absolute path with leading slash
- **WHEN** the menu item image field contains an absolute path with leading slash (e.g., `"/uploads/foods/pho.jpg"`)
- **THEN** the system SHALL trim the leading slash and construct the URL as `BASE_URL + "uploads/foods/pho.jpg"`
- **THEN** the image SHALL load successfully without path duplication

#### Scenario: Relative path without leading slash
- **WHEN** the menu item image field contains a relative path without leading slash (e.g., `"uploads/foods/pho.jpg"`)
- **THEN** the system SHALL detect the path separator and construct the URL as `BASE_URL + "uploads/foods/pho.jpg"`
- **THEN** the system SHALL NOT duplicate the path by appending "uploads/foods/" prefix
- **THEN** the image SHALL load successfully from the correct path

#### Scenario: Full URL with protocol
- **WHEN** the menu item image field contains a full URL with http:// or https:// protocol
- **THEN** the system SHALL use the URL as-is without modification
- **THEN** the image SHALL load from the external URL

#### Scenario: Empty or null image path
- **WHEN** the menu item image field is null or empty
- **THEN** the system SHALL return null from the image URL resolver
- **THEN** the UI SHALL display a fallback placeholder image

#### Scenario: Consistent behavior across screens
- **WHEN** the same menu item is displayed on different screens (menu, cart, order history)
- **THEN** all screens SHALL use identical image URL resolution logic
- **THEN** the image SHALL display consistently across all screens
