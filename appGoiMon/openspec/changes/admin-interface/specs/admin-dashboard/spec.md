## ADDED Requirements

### Requirement: Admin dashboard uses real API statistics
The system SHALL provide a mobile admin dashboard backed by a dedicated admin statistics API response.

#### Scenario: Dashboard loads statistics
- **WHEN** an admin opens the Dashboard tab
- **THEN** the app SHALL request dashboard statistics from the PHP admin API and render the returned values.

#### Scenario: Dashboard request fails
- **WHEN** the dashboard statistics request fails
- **THEN** the app SHALL show a readable error state and allow the admin to retry without logging out.

### Requirement: Dashboard exposes revenue metrics
The system SHALL display total revenue and today's revenue using paid table sessions as the revenue source.

#### Scenario: Revenue values are shown
- **WHEN** the dashboard statistics API returns revenue totals
- **THEN** the dashboard SHALL show total revenue and today's revenue formatted as Vietnamese currency.

### Requirement: Dashboard exposes operational metrics
The system SHALL display table, session, order-item, menu-item, and category counts relevant to daily restaurant operations.

#### Scenario: Operational counts are shown
- **WHEN** the dashboard statistics API returns operational counts
- **THEN** the dashboard SHALL show counts for table statuses, active sessions, pending payments, pending order items, served order items, menu items, and active categories.

### Requirement: Dashboard is optimized for phones
The dashboard UI SHALL be usable on mobile phone screens without requiring horizontal scrolling.

#### Scenario: Dashboard renders on a phone viewport
- **WHEN** the admin views the dashboard on a phone-sized device
- **THEN** statistics SHALL be arranged in vertically scrollable sections with text and controls contained within their parent elements.
