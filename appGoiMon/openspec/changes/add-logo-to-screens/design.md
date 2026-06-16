## Context

Currently, the AdminLoginScreen displays "KichiKichi" as plain text for branding, and the MenuScreen has a full-width search TextField. The restaurant has a logo file (`logo.jpg`) stored on the server at `uploads/foods/` alongside food images. We need to integrate this logo into both screens to strengthen brand identity.

**Current state:**
- AdminLoginScreen: Text "KichiKichi" above title, no visual branding
- MenuScreen: TextField with leadingIcon for search, no branding
- Coil already integrated for food images (SubcomposeAsyncImage)
- RetrofitClient.BASE_URL available for constructing image URLs

**Constraints:**
- Logo file location is fixed: `uploads/foods/logo.jpg`
- Must work with existing Coil image loading infrastructure
- Cannot break existing login/register or search functionality
- Must handle loading states and errors gracefully

## Goals / Non-Goals

**Goals:**
- Replace "KichiKichi" text with visual logo on login/register screens
- Add logo to menu screen header alongside search functionality
- Use responsive sizing (120dp login, 48dp menu)
- Implement proper loading states and error fallbacks
- Maintain existing functionality on both screens

**Non-Goals:**
- Dynamic logo selection or admin upload feature
- Logo animation or splash screen
- Changing logo file location on server
- Adding logo to other screens (cart, history, etc.)
- Customizing logo per user or role

## Decisions

### Decision 1: Logo size - 120dp login, 48dp menu
**Choice:** Use 120dp on login screen, 48dp on menu header

**Rationale:**
- **Login screen (120dp)** - prominent branding opportunity, lots of vertical space, user's first impression
- **Menu screen (48dp)** - compact header, preserves space for content, still visible but not dominant
- **Proportional to context** - larger when focus is on branding, smaller when focus is on content
- **Touch-friendly** - both sizes meet minimum touch target if logo becomes interactive later

**Alternative considered:** Single size for both (80dp)
- **Rejected** - login screen would look too small, menu header would be too cramped

**Alternative considered:** Flexible size based on screen dimensions
- **Rejected** - over-engineering, adds complexity, fixed sizes are predictable and testable

### Decision 2: Circular clipping vs. original shape
**Choice:** Clip logo to circular shape with `CircleShape`

**Rationale:**
- **Visual consistency** - matches food images which are rounded/circular
- **Modern aesthetic** - circular logos are common in mobile apps
- **Handles any aspect ratio** - even if logo is rectangular, circular clip makes it consistent
- **Less whitespace** - circular logos feel more compact than rectangular

**Alternative considered:** Display original aspect ratio with rounded corners
- **Rejected** - unknown aspect ratio could break layouts, inconsistent with food images

### Decision 3: Logo URL construction - hardcoded vs. config
**Choice:** Hardcode logo URL as `"${RetrofitClient.BASE_URL}uploads/foods/logo.jpg"`

**Rationale:**
- **Simple** - no config file needed, no new constants
- **Consistent** - follows same pattern as food images
- **Static** - logo location unlikely to change
- **Easy to find** - grep for "logo.jpg" finds all usages

**Alternative considered:** Constants file with LOGO_FILENAME
- **Deferred** - YAGNI (You Aren't Gonna Need It), can extract later if logo becomes configurable

**Alternative considered:** API endpoint for logo URL
- **Rejected** - overkill for static asset, adds API call overhead

### Decision 4: MenuScreen layout - Row vs. maintain TextField
**Choice:** Restructure as `Row { Logo + Spacer + TextField + Spacer + IconButton }`

**Rationale:**
- **Clean separation** - logo is branding, search is functionality, button is action
- **Flexible search field** - weight(1f) adapts to screen width
- **Right-aligned action** - search button on right is intuitive (common pattern)
- **No TextField clutter** - removing leadingIcon simplifies TextField API

**Alternative considered:** Keep TextField with leadingIcon, add logo above
- **Rejected** - wastes vertical space, pushes content down

**Alternative considered:** Logo as TextField leadingIcon, button as trailingIcon
- **Rejected** - logo too large for leadingIcon slot, cramped appearance

### Decision 5: Error fallback - empty space vs. placeholder
**Choice:** Display empty space (or minimal invisible Box) on logo load failure

**Rationale:**
- **Non-intrusive** - error doesn't distract from primary task (login/search)
- **Layout preserved** - spacing remains consistent
- **No noise** - no "Image not found" text cluttering UI
- **Graceful degradation** - app remains functional without logo

**Alternative considered:** Show "KichiKichi" text as fallback on login screen
- **Rejected** - adds complexity, inconsistent with menu screen behavior

**Alternative considered:** Show placeholder icon or generic image
- **Rejected** - generic placeholder worse than no logo

### Decision 6: Search button behavior
**Choice:** Search button focuses the TextField (onFocusRequester)

**Rationale:**
- **Enhances discoverability** - tapping icon makes search field more obvious
- **No duplicate action** - search happens on text input, button just aids discovery
- **Simple implementation** - use FocusRequester, no additional state

**Alternative considered:** Search button submits/triggers search
- **Rejected** - search already happens on text change (real-time filtering), no submit needed

**Alternative considered:** Search button toggles search field visibility
- **Rejected** - search field should always be visible in menu screen (core functionality)

## Risks / Trade-offs

**Risk:** Logo file missing or server unreachable → blank space in UI.  
→ **Mitigation:** Loading indicator provides feedback. Empty fallback is acceptable (app still functional). Can add retry logic later if needed.

**Risk:** Logo aspect ratio doesn't fit well in circular clip → image looks cropped.  
→ **REVISED DECISION (Option 3 - Hybrid Logo):** After discovering that logo.jpg is 2450x1100 (rectangular with badge + text), circular clipping caused text to be cropped. Solution: Use two different approaches per screen context:
  - **AdminLoginScreen:** Use logo.jpg (full rectangular logo) with RoundedCornerShape(8.dp) and ContentScale.Fit at 200x90dp. Shows full branding (badge + "KICHI-KICHI" text).
  - **MenuScreen:** Use logo_tron.jpg (square circular badge only) with CircleShape and ContentScale.Crop at 48dp. Compact header, saves space.
  - **Rationale:** Login screen has space for full branding; menu header needs compact icon. Both use same brand identity but optimized for context.

**Risk:** 120dp logo too large on small screens → pushes content down.  
→ **UPDATED:** Changed to 200x90dp rectangular logo on login screen. Aspect ratio matches original 2450x1100 logo. Still reasonable for login screen vertical space.

**Risk:** Hardcoded "uploads/foods/logo.jpg" path makes logo change require code update.  
→ **ACCEPTED:** Logo location is static. If admin needs to update logo, they replace file on server (no path change). Now using two files: logo.jpg (login) and logo_tron.jpg (menu).

**Trade-off:** Search button doesn't add new functionality (just focuses field).  
→ **Accepted:** Enhances discoverability and maintains visual balance. Real-time search means no submit needed. Future enhancement could add voice search or advanced filters.

**Trade-off:** No loading indicator on menu header logo (too small, would be distracting).  
→ **Accepted:** Login logo has loading indicator (prominent). Menu logo loads quickly (likely cached from login screen). Empty fallback is acceptable.

**Risk:** Focus management on search button might not work on all Android versions.  
→ **Mitigation:** FocusRequester is standard Compose API. If fails, button does nothing (harmless).
