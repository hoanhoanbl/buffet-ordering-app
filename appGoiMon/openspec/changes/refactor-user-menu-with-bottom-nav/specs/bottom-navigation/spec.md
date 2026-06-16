## ADDED Requirements

### Requirement: Bottom navigation bar with five tabs
The system SHALL display a bottom navigation bar with five tabs: "Đồ ăn" (Menu), "Giỏ hàng" (Cart), "Lịch sử" (History), "Phản hồi" (Feedback), and "Liên hệ" (Contact).

#### Scenario: Initial load shows Menu tab selected
- **WHEN** user enters the active ordering screen
- **THEN** the "Đồ ăn" tab is selected by default and the menu screen is displayed

#### Scenario: Switching between tabs
- **WHEN** user taps on any tab in the bottom navigation
- **THEN** the corresponding screen is displayed and the tab indicator updates to show the selected tab

#### Scenario: Cart tab shows item count badge
- **WHEN** user has items in the cart
- **THEN** the "Giỏ hàng" tab displays a badge with the total item count

### Requirement: Tab navigation persists within session
The system SHALL maintain the selected tab state while the user remains in the active ordering session.

#### Scenario: Return to previous tab after app background
- **WHEN** user backgrounds the app and returns
- **THEN** the previously selected tab remains selected

### Requirement: Navigation bar remains visible across all tabs
The system SHALL keep the bottom navigation bar visible and accessible on all tab screens.

#### Scenario: Bottom bar visible on menu screen
- **WHEN** user is viewing the menu screen
- **THEN** the bottom navigation bar is visible at the bottom of the screen

#### Scenario: Bottom bar visible on cart screen
- **WHEN** user is viewing the cart screen
- **THEN** the bottom navigation bar is visible at the bottom of the screen

#### Scenario: Bottom bar visible on history screen
- **WHEN** user is viewing the order history screen
- **THEN** the bottom navigation bar is visible at the bottom of the screen
