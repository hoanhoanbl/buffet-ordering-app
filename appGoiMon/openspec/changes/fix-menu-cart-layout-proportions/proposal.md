## Why

MenuScreen grid cards have uneven heights when food names wrap to multiple lines, breaking visual uniformity. CartScreen quantity buttons are oversized, taking excessive space in the compact list layout. Both issues harm visual consistency and efficient space usage.

## What Changes

- Fix MenuScreen grid card height uniformity by enforcing consistent text line heights
- Reduce quantity control button sizes from 48dp (default) to 36dp on both screens
- Tighten text styles and spacing for more compact, balanced layouts
- Maintain touch accessibility while improving visual density

## Capabilities

### New Capabilities
<!-- None - this is a UI refinement of existing features -->

### Modified Capabilities
- `menu-item-display`: Update text and button sizing for uniform grid card heights
- `cart-item-display`: Reduce quantity control button sizes and tighten spacing for compact layout

## Impact

- **Code**: `MenuScreen.kt` (MenuItemCard composable), `CartScreen.kt` (CartItemCard composable)
- **User Experience**: More visually consistent grid on menu screen, more compact and balanced cart screen layout
- **No Breaking Changes**: Pure UI refinement with no functional or data structure changes
- **Accessibility**: Button sizes remain above 32dp minimum, maintaining reasonable touch targets
