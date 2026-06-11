## Context

The Android app currently routes admin users from `MainActivity` into `AdminDashboardScreen`, which displays the table list and opens `ManageTableScreen` for table session details. Several admin screens and ViewModels already exist as stubs, including menu, category, and order management. The PHP API lives outside the Android project at `C:\xampp\htdocs\appOrder\appGoiMon_API` and already contains table, payment, order-item status, menu-item mutation, and combo endpoints.

The admin interface must be optimized for mobile phones. Restaurant staff need dense, fast operational screens rather than a desktop-style admin console. Dashboard metrics must come from database-backed API responses, not local estimates from partial Android state.

## Goals / Non-Goals

**Goals:**
- Provide a mobile-first admin shell with bottom navigation for Dashboard, Tables, Orders, and Menu.
- Add real dashboard statistics sourced from the PHP API and MySQL database.
- Complete admin workflows for tables, ordered items, menu items, and categories.
- Add missing PHP admin endpoints while preserving existing endpoint behavior.
- Keep implementation aligned with the existing Retrofit, repository, ViewModel, and Jetpack Compose patterns.

**Non-Goals:**
- No desktop or tablet-specific layout is required.
- No authentication redesign or token/session security change is included.
- No database schema migration is expected; use existing tables and status columns.
- Combo management is not part of this change beyond preserving compatibility with menu items already assigned to combos.
- The user ordering flow must not be redesigned.

## Decisions

### Use a Mobile Admin Shell

Use a single admin shell screen with bottom navigation. Tabs should be Dashboard, Tables, Orders, and Menu. Menu should contain item and category management as internal tabs or segmented controls.

Rationale: Mobile bottom navigation keeps the most important admin workflows one tap away and avoids overcrowding the app with five or more top-level destinations.

Alternative considered: Add every admin area as a top-level screen in `MainActivity`. This would scale poorly and make back navigation brittle as admin workflows grow.

### Add Dedicated Dashboard Stats API

Add `api/admin/get_dashboard_stats.php` to compute statistics from MySQL. The Android dashboard should call this endpoint directly through Retrofit.

Rationale: Revenue and operational totals should be authoritative and consistent across app launches. Computing these from `get_tables.php` or pending orders would omit closed sessions and historical totals.

Expected metrics:
- total revenue from paid sessions
- today's revenue from paid sessions with `start_time` on the current date
- active sessions
- pending payment sessions
- table counts by status
- pending order item count
- served order item count
- menu item count and active category count

Alternative considered: Reuse `get_tables.php` and `get_pending_orders.php` for dashboard summaries. This is acceptable for lightweight badges but insufficient for total revenue.

### Use Soft Delete for Menu Items and Categories

Menu item delete should continue mapping to `status = 'hidden'`. Category delete should map to `status = 'inactive'`.

Rationale: Existing tables already have status fields, and soft delete avoids breaking historical orders and menu relationships.

Alternative considered: Hard delete category/menu rows. This risks foreign key or historical display issues for orders, combo assignments, and reports.

### Add Missing List and Category APIs

Add:
- `api/admin/get_menu_items.php`
- `api/admin/get_categories.php`
- `api/admin/manage_category.php`

Rationale: `manage_menu_item.php` mutates menu items but there is no admin endpoint for listing all menu items. Categories currently have no admin list or mutation endpoint.

Alternative considered: Reuse user menu endpoints. User endpoints are combo-scoped and only return available menu items, which is not suitable for admin maintenance.

### Keep API Request Style Consistent

New PHP endpoints should use existing helper functions: `run_endpoint`, `require_method`, `input`, `int_param`, `str_param`, `ensure_positive`, and `json_response`.

Rationale: Consistency reduces implementation risk and keeps error responses compatible with existing Android repository handling.

## Risks / Trade-offs

- Dashboard totals may depend on business interpretation of revenue -> Use `payment_status = 'paid'` as the authoritative revenue condition and document it in the endpoint/spec.
- Category deletion could affect visible menu organization -> Implement inactive categories as soft deleted and prevent inactive categories from appearing in active creation/edit selection unless explicitly needed.
- Existing Android text contains mojibake in some screens -> New UI text should be saved as UTF-8 and existing broken labels may need cleanup when touched.
- Bottom navigation with nested Menu tabs adds local state -> Keep navigation simple using Compose state unless the implementation adopts Navigation Compose consistently for the admin shell.
- API and Android projects are separate directories -> Implementation tasks must explicitly include both roots to avoid only changing one side.

## Migration Plan

1. Add PHP API endpoints in `C:\xampp\htdocs\appOrder\appGoiMon_API\api\admin`.
2. Add Android DTOs and Retrofit declarations for the new endpoints.
3. Implement repositories and ViewModels for dashboard, orders, menu items, and categories.
4. Replace admin routing with the mobile admin shell while preserving login/logout behavior.
5. Verify existing user ordering and admin table payment flows still work.

Rollback is file-level: remove new Android admin shell usage and revert `MainActivity` to the previous dashboard/table-detail flow, while leaving additive PHP endpoints harmless if unused.

## Open Questions

- Should today's revenue use the server timezone from MySQL `CURDATE()` or an explicit app-selected business day? Initial implementation should use MySQL server date.
- Should category delete be blocked when visible menu items still reference it, or simply mark inactive? Initial implementation should soft delete and leave referenced menu items unchanged.
