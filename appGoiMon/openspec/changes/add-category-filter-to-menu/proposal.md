## Why

The current menu screen displays all food items in a 2-column grid without category organization. Users cannot easily filter items by category (e.g., "Món chính", "Tráng miệng", "Đồ uống"), making it difficult to browse large menus. Adding a category filter with horizontal chips will improve discoverability and align the UX with modern food delivery app patterns.

## What Changes

- Add horizontal scrolling row of category filter chips above the menu grid
- Add "Tất cả" (All) chip to show all items across categories
- Derive category list from existing menu items (no new API needed)
- Update menu filtering logic to support both category and search filters simultaneously
- Show empty state message "Chưa có món trong danh mục này" when selected category has no items
- Maintain existing search functionality alongside category filtering

## Capabilities

### New Capabilities
- `category-filter-chips`: Horizontal row of FilterChip components for category selection with "Tất cả" option
- `combined-menu-filtering`: Filter menu items by both category name and search query simultaneously

### Modified Capabilities
<!-- No existing capabilities are having their requirements changed -->

## Impact

**Code:**
- `OrderViewModel.kt`: Add `selectedCategoryName` state, `categories` computed property, and `onCategorySelected()` function
- `OrderViewModel.kt`: Update `filteredMenuItems` logic to filter by category in addition to search query
- `MenuScreen.kt`: Add LazyRow with FilterChip components between search bar and menu grid
- `UserMainScaffold.kt`: Pass new `onCategorySelected` callback to MenuScreen

**APIs:**
- No new API endpoints required (derives categories from existing menu items)

**Dependencies:**
- No new external dependencies (uses existing Material3 FilterChip component)

**Systems:**
- Category filtering works independently and in combination with existing search functionality
- Categories are dynamically derived from `MenuItemDto.category_name` field
