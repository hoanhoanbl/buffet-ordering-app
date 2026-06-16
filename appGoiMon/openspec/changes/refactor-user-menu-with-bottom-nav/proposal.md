## Why

The current user ordering interface presents the menu as a single-screen experience without navigation structure. To improve usability and align with modern food delivery app patterns, we need to refactor the interface to include a bottom navigation bar that organizes menu browsing, cart management, and order history into separate, focused screens. This change will make the app feel more intuitive and scalable for future features.

## What Changes

- Replace `TableOrderScreen` with a new `UserMainScaffold` component that includes a bottom navigation bar (5 tabs)
- Refactor menu display from single-column list to 2-column grid layout
- Add search functionality to filter menu items by name
- Extract cart bottom sheet into a dedicated full-screen `CartScreen`
- Create new `OrderHistoryScreen` to display past orders with items and statuses
- Add empty placeholder screens for "Feedback" and "Contact" tabs
- Update `MainActivity` to render `UserMainScaffold` when user session is active
- Add backend endpoint `/api/user/get_order_history.php` to retrieve order history data
- Maintain existing cart functionality (add/remove items, quantity controls, notes, submit order)
- Keep [+] button per menu item (no price display, no favorite hearts, no promotional banners - buffet model)

## Capabilities

### New Capabilities
- `bottom-navigation`: Bottom navigation bar with 5 tabs (Menu, Cart, History, Feedback, Contact) for organizing user experience
- `menu-search`: Search bar to filter menu items by name in real-time
- `menu-grid-layout`: Two-column grid display for menu items with compact card design
- `full-screen-cart`: Dedicated full-screen cart page with item management
- `order-history`: Display past orders with items, quantities, notes, and status tracking
- `empty-placeholder-screens`: Placeholder screens for Feedback and Contact tabs (future development)

### Modified Capabilities
<!-- No existing capabilities are having their requirements changed -->

## Impact

**Code:**
- `MainActivity.kt`: Update user flow to render `UserMainScaffold` instead of `TableOrderScreen`
- New files: `UserMainScaffold.kt`, `MenuScreen.kt`, `CartScreen.kt`, `OrderHistoryScreen.kt`, `EmptyPlaceholderScreen.kt`
- `OrderViewModel.kt`: Add search query state and filtering logic
- `UserSessionRepository.kt`: Add `getOrderHistory()` method
- `ApiService.kt`: Add order history DTOs and API endpoint

**APIs:**
- New endpoint: `GET /api/user/get_order_history.php` (backend PHP)

**Dependencies:**
- No new external dependencies required (uses existing Jetpack Compose Material3 components)

**Systems:**
- Cart functionality remains unchanged (existing `CartItem`, add/remove/update methods)
- Session and table flow unchanged (still goes through SELECT_TABLE → COMBO_SETUP → WAITING_PAYMENT → ACTIVE_MENU)
