## ADDED Requirements

### Requirement: Display logo image at top of login/register screen
The system SHALL display the restaurant logo image centered at the top of the login/register screen in place of the "KichiKichi" text.

#### Scenario: Logo displayed on login screen
- **WHEN** user views the login screen
- **THEN** the logo image is displayed centered above the "Đăng nhập" title

#### Scenario: Logo displayed on register screen
- **WHEN** user views the register screen
- **THEN** the logo image is displayed centered above the "Đăng ký" title

### Requirement: Logo is 120dp in size and circular
The system SHALL display the logo at 120dp size with circular clipping.

#### Scenario: Logo size is appropriate for screen
- **WHEN** logo is displayed on login/register screen
- **THEN** the logo is rendered at 120dp height and width

#### Scenario: Logo has circular shape
- **WHEN** logo is displayed
- **THEN** the logo is clipped to a circular shape

### Requirement: Logo loads from server URL
The system SHALL load the logo image from BASE_URL + "uploads/foods/logo.jpg".

#### Scenario: Logo URL is correctly constructed
- **WHEN** system needs to display logo
- **THEN** the image URL is constructed as BASE_URL concatenated with "uploads/foods/logo.jpg"

### Requirement: Display loading indicator while logo loads
The system SHALL display a circular loading indicator while the logo image is being fetched.

#### Scenario: Loading indicator shown during fetch
- **WHEN** logo image is being downloaded
- **THEN** a circular progress indicator is displayed in the logo placeholder

### Requirement: Handle logo loading errors gracefully
The system SHALL display a fallback when logo fails to load without breaking the screen layout.

#### Scenario: Logo fails to load
- **WHEN** logo image fails to load due to network error or invalid URL
- **THEN** the screen layout remains intact without crashing

#### Scenario: Fallback preserves spacing
- **WHEN** logo fails to load
- **THEN** the vertical spacing is preserved as if logo were present

### Requirement: Logo is positioned above title text
The system SHALL position the logo above the "Đăng nhập"/"Đăng ký" title text with appropriate spacing.

#### Scenario: Logo has spacing below it
- **WHEN** logo is displayed
- **THEN** there is 16dp spacing between logo and title text below
