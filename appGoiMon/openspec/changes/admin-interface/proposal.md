## Why

The current admin area only supports login, a table list, table session details, and payment confirmation, while menu, category, order management, and real dashboard statistics are missing or stubbed. Restaurant staff need a mobile-first admin interface backed by real API data to monitor revenue, manage tables, approve ordered items, and maintain menu/category data from the app.

## What Changes

- Add a mobile-first admin home experience with bottom navigation for Dashboard, Tables, Orders, and Menu.
- Replace the current table-list dashboard with a real statistics dashboard backed by a new admin stats API.
- Add table management entry points for viewing table status, opening active session details, confirming payment, and closing active sessions.
- Add order management for pending ordered items, including approve, reject, and mark-served actions.
- Add menu item management, including listing, creating, updating, soft deleting/hiding, and status changes.
- Add category management with create, update, and soft delete behavior using active/inactive status.
- Extend the PHP API under `C:\xampp\htdocs\appOrder\appGoiMon_API` with the missing admin endpoints needed by the Android app.
- Keep the user ordering flow unchanged.

## Capabilities

### New Capabilities
- `admin-dashboard`: Mobile admin dashboard showing real operational and revenue statistics.
- `admin-table-management`: Admin table list and table session management workflows.
- `admin-order-management`: Admin workflows for reviewing and updating ordered item statuses.
- `admin-menu-management`: Admin menu item listing and CRUD/status workflows.
- `admin-category-management`: Admin category listing and CRUD/status workflows.

### Modified Capabilities

None.

## Impact

- Android app code under `C:\xampp\htdocs\appOrder\appGoiMon`, especially admin screens, admin ViewModels, repositories, API DTOs, and `MainActivity` admin navigation.
- PHP API code under `C:\xampp\htdocs\appOrder\appGoiMon_API\api\admin`.
- MySQL database `buffet_ordering`, using existing tables: `table_sessions`, `restaurant_tables`, `orders`, `order_items`, `menu_items`, `categories`, `buffet_combos`, and `combo_menu_items`.
- New API endpoints are expected for dashboard stats, category management, and menu item listing.
- Existing endpoints remain compatible and should continue serving the user ordering flow.
