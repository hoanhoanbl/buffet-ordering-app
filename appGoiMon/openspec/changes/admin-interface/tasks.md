## 1. PHP Admin API

- [x] 1.1 Add `api/admin/get_dashboard_stats.php` in `C:\xampp\htdocs\appOrder\appGoiMon_API` to return paid-session revenue totals, today's paid revenue, table status counts, active/pending sessions, order-item counts, menu item count, and active category count.
- [x] 1.2 Add `api/admin/get_categories.php` to list categories with `id`, `category_name`, and `status`.
- [x] 1.3 Add `api/admin/manage_category.php` with `create`, `update`, `delete`, and `set_status` actions using soft delete via `status = 'inactive'`.
- [x] 1.4 Add `api/admin/get_menu_items.php` to list all menu items with category name and status, including hidden and out-of-stock items.
- [x] 1.5 Verify new API endpoints with local requests against the `buffet_ordering` database.

## 2. Android API Models and Repositories

- [x] 2.1 Add Retrofit DTOs and request models for dashboard stats, categories, menu items, order item actions, close table, and admin mutation responses.
- [x] 2.2 Add Retrofit API declarations for the new PHP endpoints and existing admin order/table action endpoints.
- [x] 2.3 Implement dashboard repository methods for loading real statistics.
- [x] 2.4 Implement category repository methods for list, create, update, soft delete, and status changes.
- [x] 2.5 Implement menu repository methods for list, create, update, soft delete, and status changes.
- [x] 2.6 Implement order repository methods for pending ordered items, approve, reject, and mark served.
- [x] 2.7 Extend table repository support for closing active table sessions.

## 3. Android Admin ViewModels

- [x] 3.1 Add `AdminDashboardViewModel` with loading, error, retry, and statistics state.
- [x] 3.2 Complete `AdminOrderViewModel` with pending item loading and approve/reject/served actions.
- [x] 3.3 Complete `AdminFoodViewModel` with menu item list, form state, create/update/delete/status actions, and category options.
- [x] 3.4 Add or complete category ViewModel state for category list and create/update/delete/status workflows.
- [x] 3.5 Update `AdminTableViewModel` to support close-table action and refresh state after confirm payment or close table.

## 4. Android Admin UI

- [x] 4.1 Add a mobile-first admin shell screen with bottom navigation for Dashboard, Tables, Orders, and Menu.
- [x] 4.2 Replace the current dashboard table list with statistics cards and operational summaries from `AdminDashboardViewModel`.
- [x] 4.3 Add a Tables tab that lists all tables by status and opens table session details.
- [x] 4.4 Update table detail UI to support confirm payment and close table actions with loading and error feedback.
- [x] 4.5 Implement Orders tab UI for pending items with approve, reject, and mark-served controls.
- [x] 4.6 Implement Menu tab with internal switching between menu items and categories.
- [x] 4.7 Implement menu item list, create/edit form, status control, and soft delete confirmation.
- [x] 4.8 Implement category list, create/edit form, status control, and soft delete confirmation.
- [x] 4.9 Update `MainActivity` admin routing to use the admin shell while preserving login and logout behavior.

## 5. Verification

- [x] 5.1 Run Android compilation checks with the project Gradle wrapper.
- [ ] 5.2 Manually verify admin login, dashboard load, table list/detail, confirm payment, close table, order actions, menu item CRUD, and category CRUD on a phone-sized emulator or device.
- [ ] 5.3 Manually verify existing user table selection and ordering flows still work.
- [x] 5.4 Confirm dashboard revenue uses `table_sessions.payment_status = 'paid'` and today's revenue uses the MySQL server date.
