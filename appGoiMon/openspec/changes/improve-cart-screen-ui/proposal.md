## Why

The current cart screen displays only text (food name and note field) without visual elements. Users cannot easily identify items in their cart without food images, making the interface less engaging and harder to scan. Adding food images, category labels, and a collapsible note field will improve visual hierarchy and user experience, aligning with modern food delivery app patterns.

## What Changes

- Add food image thumbnail (60x60dp) to the left side of each cart item card
- Display category name below food name for better context
- Implement collapsible note field that expands on user tap or when note exists
- Improve cart item card layout using Row with image + Column content structure
- Increase card spacing from 8dp to 12dp for better visual separation
- Add image loading states (loading placeholder and error fallback)
- Reuse existing image resolution logic from MenuScreen

## Capabilities

### New Capabilities
- `cart-item-image-display`: Display food images in cart items with loading and error states
- `collapsible-note-field`: Expandable/collapsible note input field to save screen space

### Modified Capabilities
<!-- No existing capabilities are having their requirements changed -->

## Impact

**Code:**
- `CartScreen.kt`: Update CartItemCard composable to include food image, category display, and collapsible note field
- `CartScreen.kt`: Add image URL resolution helper (or import from MenuScreen if extracted)
- `CartScreen.kt`: Add expand/collapse state management for note field per cart item

**APIs:**
- No new API endpoints required (uses existing `MenuItemDto.image` and `MenuItemDto.category_name` fields)

**Dependencies:**
- Already using Coil for image loading (SubcomposeAsyncImage component)
- No new external dependencies required

**UI/UX:**
- Cart items become more visually identifiable with food images
- Better visual hierarchy with category labels
- More compact layout with collapsible notes (saves space for items without notes)
- Increased card spacing improves readability
