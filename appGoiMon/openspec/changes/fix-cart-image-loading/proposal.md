## Why

The cart screen fails to load food images while the menu screen loads them correctly. Root cause analysis shows that `CartScreen.kt` has a buggy `resolveFoodImageUrl()` function that duplicates the path when the image value contains a relative path without a leading slash (e.g., `"uploads/foods/pho.jpg"`). This results in malformed URLs like `BASE_URL + "uploads/foods/uploads/foods/pho.jpg"`, causing 404 errors.

## What Changes

- Fix the `resolveFoodImageUrl()` logic in `CartScreen.kt` to align with the correct implementation in `MenuScreen.kt`
- Change the path detection from `value.startsWith("/")` to `!value.contains('/')` to properly handle relative paths without leading slashes
- Ensure all three image URL resolution patterns are handled correctly:
  - Filename only: `"pho.jpg"` → `BASE_URL + "uploads/foods/pho.jpg"`
  - Absolute path with leading slash: `"/uploads/foods/pho.jpg"` → `BASE_URL + "uploads/foods/pho.jpg"`
  - Relative path without leading slash: `"uploads/foods/pho.jpg"` → `BASE_URL + "uploads/foods/pho.jpg"`

## Capabilities

### New Capabilities
<!-- None - this is a bug fix -->

### Modified Capabilities
- `cart-image-display`: Fix image URL resolution logic to prevent path duplication and 404 errors

## Impact

- **Code**: `CartScreen.kt` (lines 239-252, `resolveFoodImageUrl()` function)
- **User Experience**: Cart screen will correctly display food images for all menu items
- **No Breaking Changes**: This is a pure bug fix with no API or data structure changes
- **No Dependencies**: Only internal logic change, no external dependencies affected
