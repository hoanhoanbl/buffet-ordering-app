## Context

The MenuScreen currently displays menu items in a 2-column grid with search functionality. Menu items already contain a `category_name` field from the backend, but there's no UI to filter by category. Users must scroll through all items or use text search to find specific dishes.

**Current architecture:**
- `MenuScreen` composable receives `UserOrderUiState` with `menuItems` and `searchQuery`
- `filteredMenuItems` computed property filters by search query only
- `OrderViewModel` manages state and filtering logic

**Constraints:**
- Must work with existing `MenuItemDto` structure (contains `category_name`)
- Must not require new backend API endpoints
- Must maintain existing search functionality
- Must fit within existing buffet ordering flow

## Goals / Non-Goals

**Goals:**
- Add category filter UI using horizontal chips above menu grid
- Derive categories dynamically from loaded menu items
- Support combined filtering (category AND search)
- Maintain existing search functionality without regression
- Provide clear visual feedback for selected category

**Non-Goals:**
- Add backend API for categories (derive from menu items instead)
- Persist filter state across app restarts or sessions
- Add category management for admin
- Support multi-select categories (single selection only)
- Show item counts per category

## Decisions

### Decision 1: Derive categories from menu items vs. separate API call
**Choice:** Derive categories from `menuItems.mapNotNull { it.category_name }.distinct().sorted()`

**Rationale:**
- **Simpler implementation** — no new API endpoint needed
- **No extra network call** — categories extracted from data already loaded
- **Always in sync** — categories reflect exactly what items are available
- **Sufficient for use case** — users only need to see categories that have items

**Alternative considered:** Call `/api/admin/get_categories.php` to get full category list
- **Rejected** because:
  - Adds unnecessary API call (performance impact)
  - Could show empty categories (confusing UX)
  - Over-engineering for current requirements
  - Admin endpoint may require auth the user role doesn't have

### Decision 2: Store selectedCategoryName vs. selectedCategoryId
**Choice:** Store `selectedCategoryName: String?` in UiState (null = "Tất cả")

**Rationale:**
- **Simpler filtering logic** — direct string comparison with `menuItem.category_name`
- **No ID lookup needed** — categories are display strings, not database entities
- **Consistent with existing pattern** — `searchQuery` is also a String
- **Backend already provides name** — `MenuItemDto.category_name` is the display value

**Alternative considered:** Store `selectedCategoryId: Int?` and maintain category-to-id mapping
- **Rejected** because:
  - Adds complexity (need to maintain category list with IDs)
  - Categories aren't database entities in this context (derived from items)
  - String comparison is sufficient for filtering performance

### Decision 3: Filter state management location
**Choice:** Manage filter state in `OrderViewModel` as part of `UserOrderUiState`

**Rationale:**
- **Consistent with existing pattern** — `searchQuery` is already in UiState
- **Single source of truth** — all menu state in one place
- **Testable** — ViewModel logic can be unit tested
- **Reusable** — filtering logic in computed property, UI just displays

**Alternative considered:** Local state in MenuScreen composable
- **Rejected** because:
  - Inconsistent with `searchQuery` pattern
  - Harder to test filtering logic
  - Doesn't allow potential future features (e.g., saving filter preferences)

### Decision 4: Combined filter logic (category + search)
**Choice:** Apply both filters in sequence: first category, then search query

```kotlin
val filteredMenuItems: List<MenuItemDto>
    get() {
        var items = menuItems
        
        // Filter by category first
        if (selectedCategoryName != null) {
            items = items.filter { it.category_name == selectedCategoryName }
        }
        
        // Then filter by search
        if (searchQuery.isNotEmpty()) {
            items = items.filter { it.name.lowercase().contains(searchQuery.lowercase()) }
        }
        
        return items
    }
```

**Rationale:**
- **Intuitive UX** — both filters narrow down results
- **Predictable behavior** — category scopes the search
- **Performance** — filters on already-filtered list (smaller dataset for search)

**Alternative considered:** Search first, then category
- **Rejected** — less intuitive (why does category filter skip search-matched items?)

### Decision 5: FilterChip vs. custom chip implementation
**Choice:** Use Material3 `FilterChip` component

**Rationale:**
- **Native component** — built-in support for selected state
- **Consistent with Material Design** — familiar UX pattern
- **Accessibility** — proper semantics and touch targets
- **Less code** — no need to implement selection state manually

**Alternative considered:** Custom chip with Card + clickable modifier
- **Rejected** — reinventing the wheel, more code, accessibility concerns

### Decision 6: Category list ordering
**Choice:** "Tất cả" first, then alphabetically sorted categories

**Rationale:**
- **"All" is default** — placing it first reinforces it as the default state
- **Alphabetical is predictable** — users can find categories quickly
- **No manual ordering needed** — automatic sorting is maintenance-free

**Alternative considered:** Order by item count (most items first)
- **Rejected** — requires counting items per category (performance cost), less predictable

## Risks / Trade-offs

**Risk:** If menu has 10+ categories, horizontal scroll might hide some categories from view.  
→ **Mitigation:** LazyRow supports horizontal scroll naturally. Most menus have 4-6 categories. If this becomes an issue later, could add visual scroll indicators.

**Risk:** Deriving categories on every recomposition could impact performance with large menus.  
→ **Mitigation:** `categories` is a computed property that only recalculates when `menuItems` changes (StateFlow optimization). With typical menu sizes (<100 items), distinct + sort is negligible.

**Risk:** If backend sends `null` or empty string for `category_name`, items won't be filterable.  
→ **Mitigation:** Use `mapNotNull` to skip items without category. Those items only appear when "Tất cả" is selected.

**Trade-off:** No item count per category (e.g., "Món chính (12)").  
→ **Accepted:** Keeps UI cleaner. Counting would require additional computation. User can tap to see items.

**Trade-off:** Filter state resets when navigating away from menu tab.  
→ **Accepted:** Simpler implementation (no need to preserve state). User likely wants fresh view when returning to menu.

**Trade-off:** Categories with no items won't appear in filter chips.  
→ **Accepted:** This is actually desirable UX (don't show empty categories). Aligns with decision to derive from items.
