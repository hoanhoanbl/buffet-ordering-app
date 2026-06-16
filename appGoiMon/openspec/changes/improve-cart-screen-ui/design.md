## Context

The CartScreen currently displays cart items as text-only cards with food name, quantity controls, and an always-visible note TextField. Each CartItemCard uses a simple Column layout without visual elements like food images.

**Current architecture:**
- `CartScreen` renders a LazyColumn of `CartItemCard` composables
- Each card shows: food name (Bold), quantity controls (-/+), note TextField
- Card background: Color(0xFFFFFAF0), 12dp padding
- No images, no category display
- TextField always visible (takes space even when empty)

**Constraints:**
- Must work with existing `MenuItemDto` structure (has `image` and `category_name` fields)
- Image loading already handled by Coil (SubcomposeAsyncImage)
- MenuScreen already has image resolution logic (`resolveFoodImageUrl`)
- Must maintain existing cart functionality (quantity updates, note editing)

## Goals / Non-Goals

**Goals:**
- Add food image thumbnails (60x60dp) to cart items for visual identification
- Display category name for context
- Implement collapsible note field to save space when notes are empty
- Improve visual hierarchy with better layout structure
- Reuse existing image loading patterns from MenuScreen

**Non-Goals:**
- Changing cart data structure or API contracts
- Adding new image processing features (zoom, gallery, etc.)
- Implementing swipe-to-delete or other gestures
- Changing bottom "Gọi món" button behavior
- Adding item prices (this is a buffet ordering system)

## Decisions

### Decision 1: Row layout with image on left vs. other layouts
**Choice:** Use `Row { Image (60x60) + Spacer(8dp) + Column { content } }`

**Rationale:**
- **Standard pattern** — food delivery apps universally use left-aligned thumbnails
- **Scannable** — users' eyes flow left-to-right, image anchors each item
- **Compact** — 60x60dp image doesn't dominate, leaves room for content
- **Familiar** — MenuScreen uses similar pattern (image + content), consistent UX

**Alternative considered:** Image on right side
- **Rejected** — less common pattern, users expect images on left

**Alternative considered:** Image above content (vertical stack)
- **Rejected** — wastes vertical space, reduces items visible without scrolling

### Decision 2: Image size 60x60dp vs. other sizes
**Choice:** 60x60dp with ContentScale.Crop

**Rationale:**
- **Balance** — large enough to identify food, small enough to not dominate
- **Consistent** — MenuScreen uses similar-sized images (160dp height in 2-column grid ≈ 80dp per item visually)
- **Touch target** — 60dp is accessible but not primary interaction point
- **Performance** — smaller image size = less memory, faster loading

**Alternative considered:** 80x80dp
- **Rejected** — too large for cart context, makes cards unnecessarily tall

**Alternative considered:** 50x50dp
- **Rejected** — too small, harder to identify food visually

### Decision 3: Collapsible note field implementation
**Choice:** Use `remember { mutableStateOf(false) }` per card + conditional rendering

```kotlin
var isNoteExpanded by remember { mutableStateOf(cartItem.note.isNotEmpty()) }

if (isNoteExpanded || cartItem.note.isNotEmpty()) {
    TextField(value = cartItem.note, ...)
} else {
    TextButton("Thêm ghi chú") { isNoteExpanded = true }
}
```

**Rationale:**
- **Simple state** — boolean flag per card, no complex state management
- **Automatic expansion** — if note exists, show it immediately
- **Independent** — each card manages its own expand state
- **No data persistence needed** — expand state is UI-only, doesn't need to survive recreation

**Alternative considered:** Single bottom sheet for all notes
- **Rejected** — adds navigation complexity, less intuitive for quick edits

**Alternative considered:** Icon button → dialog
- **Rejected** — dialog is overkill for single-line text input, breaks flow

### Decision 4: Image URL resolution - reuse vs. duplicate
**Choice:** Extract `resolveFoodImageUrl` helper to shared location OR duplicate in CartScreen

**Decision:** Duplicate the function in CartScreen (copy from MenuScreen)

**Rationale:**
- **Quick implementation** — no refactoring needed across files
- **Self-contained** — CartScreen has everything it needs
- **Low duplication cost** — small function (10 lines), rarely changes
- **Future refactor** — can extract later if more screens need it

**Alternative considered:** Extract to a shared util file
- **Deferred** — would require creating new file, updating imports in multiple places
- **YAGNI** — currently only 2 usages (MenuScreen, CartScreen), not worth abstraction yet

### Decision 5: Category display placement
**Choice:** Display category name below food name, small font, OrangeAccent color

**Rationale:**
- **Visual hierarchy** — food name is primary (Bold), category is secondary (small)
- **Consistent color** — OrangeAccent used for category elsewhere (MenuScreen uses it for various accents)
- **Context without clutter** — small text doesn't overwhelm, provides helpful context

**Alternative considered:** Display category as a chip/badge
- **Rejected** — too prominent for secondary information, adds visual noise

### Decision 6: Note field collapse logic
**Choice:** Collapse when empty AND loses focus, expand when has content OR user taps button

**Rationale:**
- **Saves space** — empty notes don't waste vertical space
- **Preserves content** — if note exists, always show it (don't force re-expansion)
- **Clear affordance** — "Thêm ghi chú" button is obvious action

**Alternative considered:** Always collapse, show preview badge
- **Rejected** — forces extra tap to see existing notes, less discoverable

## Risks / Trade-offs

**Risk:** If many items have notes, collapsible feature provides no benefit (all expanded anyway).  
→ **Mitigation:** Acceptable trade-off. Most cart items won't have notes. When they do, user wants to see them. Feature still provides value for empty notes.

**Risk:** Image loading failures could make cart look broken.  
→ **Mitigation:** Use same error fallback as MenuScreen ("Khong tai duoc anh"). Users already familiar with this pattern. Loading indicator provides feedback during fetch.

**Risk:** 60x60dp images load slower than text-only cards.  
→ **Mitigation:** Coil handles image caching automatically. Most images already loaded from MenuScreen (user added from menu → cart). Cache hit rate will be high.

**Trade-off:** Duplicating `resolveFoodImageUrl` function creates maintenance burden.  
→ **Accepted:** Low burden (small function, stable logic). Can extract later if more screens need it. Premature abstraction is worse than small duplication.

**Trade-off:** Per-item expand state doesn't survive recreation (e.g., screen rotation).  
→ **Accepted:** Acceptable UX. Collapsing on recreation is reasonable behavior. User can re-expand if needed. Not worth hoisting state to ViewModel for this.

**Trade-off:** No visual indicator on collapsed note button when note exists.  
→ **Accepted:** Spec mentions "Ghi chú (1)" indicator, but implementation can start simple (just show button). If users struggle to find existing notes, add indicator in follow-up.

**Risk:** Category name null handling could cause crashes.  
→ **Mitigation:** Use safe call `category_name?.let { }` or skip rendering if null. Spec explicitly says "no placeholder" for null category.
