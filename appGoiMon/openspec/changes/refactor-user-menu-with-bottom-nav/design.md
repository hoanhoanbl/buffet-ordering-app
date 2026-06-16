## Context

The current user ordering flow presents the menu as a single screen (`TableOrderScreen`) within the `ACTIVE_MENU` step. The cart is implemented as a modal bottom sheet that overlays the menu. There is no navigation structure for organizing different aspects of the ordering experience (browsing, cart management, viewing order history).

The refactor introduces a bottom navigation pattern to organize the interface into distinct, focused screens. This aligns with modern mobile app conventions and provides a foundation for future feature additions.

**Current architecture:**
- `MainActivity` → User flow based on `UserOrderStep` enum
- `ACTIVE_MENU` step → `TableOrderScreen` (single screen with bottom sheet cart)
- Cart logic in `OrderViewModel` with `CartItem` data class
- API calls handled by `UserSessionRepository` and `OrderRepository`

**Constraints:**
- Must preserve existing cart functionality (add/remove, quantity controls, notes, submit order)
- Must work with existing ViewModel state management (StateFlow)
- Must maintain buffet model (no price display)
- Backend API additions must be minimal and focused

## Goals / Non-Goals

**Goals:**
- Introduce bottom navigation scaffold with 5 tabs: Menu, Cart, History, Feedback, Contact
- Refactor menu display to 2-column grid layout with search capability
- Extract cart from modal bottom sheet to dedicated full-screen page
- Add order history screen with backend API integration
- Provide empty placeholder screens for future features (Feedback, Contact)
- Maintain existing cart state management and order submission flow

**Non-Goals:**
- Implement actual Feedback or Contact functionality (placeholders only)
- Add price display, promotional banners, or favorite features (buffet model)
- Change table selection, combo setup, or payment confirmation flows
- Modify admin dashboard or admin-side functionality
- Implement real-time order status updates (polling or WebSocket)

## Decisions

### Decision 1: Bottom navigation state management
**Choice:** Store selected tab index in local `remember { mutableStateOf() }` within `UserMainScaffold`.

**Rationale:**
- Tab selection is UI-only state that doesn't need to survive process death
- Keeps ViewModel focused on business logic (cart, orders, menu data)
- Simpler than creating a new `MainViewModel` for minimal state

**Alternative considered:** Add navigation state to `OrderViewModel` — Rejected because it couples UI navigation with domain logic and adds unnecessary complexity.

### Decision 2: Refactor approach for TableOrderScreen
**Choice:** Create new `MenuScreen` composable by extracting and refactoring `TableOrderScreen` content, rather than modifying in place.

**Rationale:**
- Cleaner separation: `UserMainScaffold` becomes the parent coordinator, `MenuScreen` focuses purely on menu display
- Easier to remove Scaffold, FAB, and ModalBottomSheet logic without breaking existing structure
- Original `TableOrderScreen` can be deprecated cleanly after migration

**Alternative considered:** Modify `TableOrderScreen` directly — Rejected because it mixes too many concerns (navigation scaffold, menu display, cart sheet) making the refactor error-prone.

### Decision 3: Search implementation
**Choice:** Add `searchQuery: String` to `UserOrderUiState` and compute filtered list using a derived property `filteredMenuItems`.

**Rationale:**
- Keeps search state centralized in ViewModel for consistency
- Filtering logic can be tested independently
- No need for separate search state management

**Alternative considered:** Local state in `MenuScreen` — Rejected because it would duplicate filtering logic and make it harder to test.

### Decision 4: Cart screen implementation
**Choice:** Extract existing `CartBottomSheet` composable into a new `CartScreen` with minimal changes (remove sheet-specific wrappers, add Scaffold).

**Rationale:**
- Preserves existing cart UI and behavior with minimal risk
- `CartItemCard` composable can be reused as-is
- No need to reimplement cart rendering logic

**Alternative considered:** Rebuild cart UI from scratch — Rejected as unnecessary risk when existing implementation works well.

### Decision 5: Order history data structure
**Choice:** Create new DTOs (`OrderHistoryDto`, `OrderHistoryItemDto`) for the `/api/user/get_order_history.php` endpoint.

**Rationale:**
- Order history has different data requirements than order creation (includes timestamps, aggregated items, status tracking)
- Keeps concerns separated between creating orders and viewing history
- Allows backend to optimize query structure for history retrieval

**Alternative considered:** Reuse `CreateOrderResponseDto` — Rejected because history needs additional fields (timestamps, status) not present in creation response.

### Decision 6: Backend API design for order history
**Choice:** Single endpoint `GET /api/user/get_order_history.php?session_id={id}` returns all orders for the session.

**Rationale:**
- Simple REST pattern consistent with existing API structure
- Session-scoped history is sufficient for current use case
- Easy to implement pagination later if needed

**Alternative considered:** Separate endpoints for orders and order items — Rejected as over-engineered for current requirements.

### Decision 7: Grid layout implementation
**Choice:** Use `LazyVerticalGrid` with `GridCells.Fixed(2)` for menu display.

**Rationale:**
- Native Compose component designed for 2-column layouts
- Handles scrolling, item recycling, and performance automatically
- Easy to adjust column count or spacing later

**Alternative considered:** Manual Row/Column layout — Rejected due to poor scrolling performance and complexity.

### Decision 8: Empty placeholder screens
**Choice:** Single reusable `EmptyPlaceholderScreen(title: String)` composable for both Feedback and Contact tabs.

**Rationale:**
- DRY principle for identical placeholder UI
- Easy to replace with real implementation later
- Keeps codebase minimal during this phase

**Alternative considered:** Separate composables for each tab — Rejected as unnecessary duplication.

## Risks / Trade-offs

**Risk:** Search filtering on large menus (100+ items) may cause UI lag.  
→ **Mitigation:** Filter operation is on String matching (fast). If needed later, can debounce search input or implement virtual scrolling.

**Risk:** Order history API could return large datasets for long-running sessions.  
→ **Mitigation:** Start with simple full-load approach. Backend can add pagination (`?limit=20&offset=0`) if performance issues emerge.

**Risk:** Bottom navigation adds another layer of navigation complexity (table → combo → session → tabs).  
→ **Mitigation:** Tabs only appear in `ACTIVE_MENU` step, so earlier flows remain simple. Users only see tabs when they're actively ordering.

**Trade-off:** Extracting cart to full-screen loses "quick peek" benefit of bottom sheet.  
→ **Accepted:** Badge on Cart tab provides at-a-glance count. Full-screen view is better for managing multiple items with notes.

**Trade-off:** Empty placeholder screens provide no value in current release.  
→ **Accepted:** Low implementation cost, establishes navigation structure for future features without requiring immediate content.

**Risk:** Refactoring `TableOrderScreen` could break existing cart submission flow.  
→ **Mitigation:** Preserve all existing ViewModel callbacks and state. `MenuScreen` and `CartScreen` call the same `onAddToCart`, `onUpdateQuantity`, `onSubmitOrder` functions as before.
