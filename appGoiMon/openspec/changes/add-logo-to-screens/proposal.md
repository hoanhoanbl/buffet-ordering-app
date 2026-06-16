## Why

The app currently uses text-based branding ("KichiKichi") on the login/register screen and a standard search bar on the menu screen. Adding the restaurant's logo image will strengthen brand identity, create a more professional appearance, and improve visual consistency across screens. The logo file already exists on the server and needs to be integrated into the Android app UI.

## What Changes

- Replace "KichiKichi" text with logo image on AdminLoginScreen (login/register screens)
- Add logo to MenuScreen header alongside search functionality
- Redesign MenuScreen header layout from full-width search to Row with logo + search field + search button
- Use 120dp logo size on login screen (centered, circular)
- Use 48dp logo size on menu screen (left-aligned, circular)
- Implement image loading with Coil (SubcomposeAsyncImage)
- Add loading states and error fallbacks for logo display
- Logo URL: BASE_URL + "uploads/foods/logo.jpg"

## Capabilities

### New Capabilities
- `login-screen-logo`: Display circular logo image at top of login/register screen replacing text branding
- `menu-header-with-logo`: Redesigned menu header with logo on left, search field in center, and search button on right

### Modified Capabilities
<!-- No existing capabilities are having their requirements changed -->

## Impact

**Code:**
- `AdminLoginScreen.kt`: Remove "KichiKichi" Text, add SubcomposeAsyncImage for logo (120dp, circular, centered)
- `MenuScreen.kt`: Restructure search bar area from TextField to Row layout with logo (48dp) + TextField (weight 1f) + IconButton
- Add Coil image loading imports to both screens

**APIs:**
- No new API endpoints required
- Logo URL: `BASE_URL + "uploads/foods/logo.jpg"` (static path)

**Dependencies:**
- Already using Coil for image loading (no new dependencies)

**UI/UX:**
- Login screen: Logo replaces text branding, creates stronger visual identity
- Menu screen: Logo adds brand presence, search remains functional with icon button on right
- Both screens: Loading indicators and error fallbacks ensure graceful degradation
