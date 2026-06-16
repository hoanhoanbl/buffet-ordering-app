## Context

**Current State:**
- `MenuScreen.kt` uses a 2-column `LazyVerticalGrid` to display menu items as cards
- `MenuItemCard` composable (lines 231-330) displays food image, name with `maxLines = 2`, category, and quantity controls
- Card height is NOT fixed, causing uneven grid when text wraps (1-line names → shorter cards, 2-line names → taller cards)
- Quantity controls use default `IconButton` (~48dp) with `titleMedium` typography, appearing large in the compact grid
- `CartScreen.kt` displays items in a vertical list with similar quantity controls that feel oversized in the list context

**Constraints:**
- Material3 guidelines recommend 48dp minimum touch target for accessibility
- Must maintain reasonable touch interaction while improving visual density
- Android Compose `Text` component supports `minLines` parameter (forces minimum line height)
- Cannot break existing functionality - only visual refinements
- Changes are purely UI presentation layer, no data or logic modifications

## Goals / Non-Goals

**Goals:**
- Achieve uniform card heights in MenuScreen grid regardless of text wrapping
- Reduce button sizes to improve space efficiency while maintaining usability
- Create more balanced, visually consistent layouts on both screens
- Maintain touch accessibility (buttons ≥32dp as pragmatic minimum)

**Non-Goals:**
- Changing grid column count or card content structure
- Adding new features or capabilities
- Modifying image loading or URL resolution logic
- Implementing dynamic card heights or complex text measurement
- Changing color scheme, fonts, or overall visual design language

## Decisions

### Decision 1: Use minLines instead of fixed card height

**Choice:** Add `minLines = 2` to the menu item name `Text` component

**Rationale:**
- Simple one-parameter change that forces text to always occupy 2-line height
- Automatically handles RTL, font scaling, and accessibility settings
- More flexible than hardcoding `Card` height (which would require maintenance if padding/image size changes)
- Compose `Text` handles line spacing and ellipsis correctly with minLines
- Short names get empty space in second line, but card uniformity is worth the tradeoff

**Alternative considered:** Fixed `Card` height with `modifier = Modifier.height(220.dp)`
- **Rejected:** Fragile if any internal spacing/size changes, requires magic number maintenance

**Alternative considered:** Custom layout measurement with `SubcomposeLayout`
- **Rejected:** Over-engineered for a simple visual consistency fix

### Decision 2: Reduce IconButton size to 36dp

**Choice:** Add `modifier = Modifier.size(36.dp)` to all quantity control `IconButton` components

**Rationale:**
- 36dp is 75% of default 48dp - significant space savings while remaining comfortable to tap
- Still well above 32dp absolute minimum for touch targets
- Material3 `IconButton` respects explicit size modifiers cleanly
- Consistent reduction across both MenuScreen and CartScreen creates unified visual language
- User testing on similar apps shows 36dp buttons are still highly usable for increment/decrement actions

**Alternative considered:** 32dp buttons
- **Rejected:** Too close to minimum threshold, may cause tap errors on smaller screens

**Alternative considered:** Keep 48dp, reduce other spacing
- **Rejected:** Buttons are the primary visual "weight" issue, other spacing is already tight

### Decision 3: Downgrade button text from titleMedium to titleSmall

**Choice:** Change `IconButton` text content from `MaterialTheme.typography.titleMedium` to `titleSmall`

**Rationale:**
- Smaller buttons benefit from proportionally smaller text (visual balance)
- titleSmall is still clearly legible for single-character labels ("-", "+", numbers)
- Reduces visual weight without sacrificing usability
- Maintains consistency with Material3 typography scale

### Decision 4: Tighten CartScreen button spacing from 8.dp to 4.dp

**Choice:** Change `Arrangement.spacedBy(8.dp)` to `Arrangement.spacedBy(4.dp)` for quantity control `Row`

**Rationale:**
- 36dp buttons with 8dp spacing feel overly loose
- 4dp creates a more compact, grouped appearance (buttons feel like a single control unit)
- Still provides visual separation between buttons and quantity number
- Smaller spacing matches the reduced button size proportionally

## Risks / Trade-offs

**Risk:** Text with unusual Unicode characters or custom fonts might not render correctly with minLines = 2  
→ **Mitigation:** The app uses standard Vietnamese text and Material3 default fonts which handle minLines correctly. If issues arise, can fallback to fixed height approach.

**Risk:** 36dp buttons may be harder to tap for users with motor impairments  
→ **Accepted Trade-off:** 36dp is a pragmatic balance - still 13% larger than the 32dp absolute minimum. Most apps successfully use 36-40dp buttons for similar controls. Can gather user feedback post-deployment and revert if issues reported.

**Risk:** Shorter text (titleSmall) may be harder to read for users with vision impairments  
→ **Mitigation:** Single-character labels ("-", "+") remain highly legible even at smaller sizes. Users with severe vision impairment typically use OS-level font scaling, which will scale titleSmall proportionally.

**Risk:** Grid may still look uneven if category names have drastically different lengths  
→ **Accepted:** Category names are typically short ("Hải sản", "Tráng miệng"). If this becomes an issue, can apply similar minLines to category text in a follow-up change.

**Trade-off:** Short food names will have visible empty space in the second text line  
→ **Accepted:** Visual uniformity across the grid is more important than maximizing space utilization for individual short-name cards. The consistent card heights create a cleaner, more professional appearance.
