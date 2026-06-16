## Context

**Current State:**
- `CartScreen.kt` has a `resolveFoodImageUrl()` function at lines 239-252 that resolves image paths to full URLs
- `MenuScreen.kt` has an identical function at lines 332-344 with **different logic**
- `TableOrderScreen.kt` also has this function at line 337 (implementation unknown)

**Problem:**
CartScreen's implementation uses `value.startsWith("/")` to detect paths, which fails for relative paths without leading slashes (e.g., `"uploads/foods/pho.jpg"`). This causes path duplication: `BASE_URL + "uploads/foods/uploads/foods/pho.jpg"` → 404 error.

MenuScreen's implementation uses `!value.contains('/')` to detect filenames vs paths, which correctly handles all three cases:
1. Filename only: `"pho.jpg"` → append `"uploads/foods/"`
2. Absolute path: `"/uploads/foods/pho.jpg"` → trim leading `/`
3. Relative path: `"uploads/foods/pho.jpg"` → trim leading `/` (no duplication)

**Constraints:**
- Cannot break existing functionality on MenuScreen (already working)
- Must maintain backward compatibility with all three image path formats
- Coil image loading library is already integrated
- RetrofitClient.BASE_URL is the source of truth for base URL

## Goals / Non-Goals

**Goals:**
- Fix CartScreen's `resolveFoodImageUrl()` to match MenuScreen's correct logic
- Ensure all three image path formats are handled correctly without path duplication
- Maintain consistent image loading behavior across all screens

**Non-Goals:**
- Extracting to shared utility function (can be done in future refactoring)
- Changing database schema or image path storage format
- Modifying TableOrderScreen (unless discovered to have same bug)
- Adding new image loading features or optimizations

## Decisions

### Decision 1: Align CartScreen with MenuScreen logic
**Choice:** Copy the working logic from MenuScreen to CartScreen

**Rationale:**
- MenuScreen already handles all three path formats correctly
- Minimal risk - proven working implementation
- No new logic to introduce bugs
- Quick fix with clear before/after comparison

**Alternative considered:** Create shared utility function
- **Rejected:** Adds complexity for a 10-line function, can be refactored later if more duplication appears

### Decision 2: Replace `startsWith("/")` with `!contains('/')`
**Choice:** Change the path detection logic from checking leading slash to checking for any slash

**Rationale:**
- **Filename detection:** `!contains('/')` correctly identifies pure filenames like `"pho.jpg"`
- **Path detection:** Any string containing `/` is treated as a path (absolute or relative)
- **Handles edge case:** Relative paths without leading slash (e.g., `"uploads/foods/pho.jpg"`) are correctly identified as paths, not filenames
- **Logic flow:**
  ```
  if (!value.contains('/')) {
      // It's a filename → append "uploads/foods/"
      return BASE_URL + "uploads/foods/" + value
  }
  // It has a slash → it's a path → just trim leading slash if any
  return BASE_URL + value.trimStart('/')
  ```

**Alternative considered:** Check for `startsWith("uploads/")` to detect relative paths
- **Rejected:** Too specific, hardcodes path convention, fragile if path structure changes

## Risks / Trade-offs

**Risk:** Image paths with unusual formats might still break.
→ **Mitigation:** The three formats (filename, absolute, relative) cover all current database values. If new formats appear, they'll be caught in testing.

**Risk:** TableOrderScreen might have the same bug.
→ **Mitigation:** Out of scope for this fix, but should be checked in code review. If found, apply same fix in follow-up change.

**Trade-off:** Not extracting to shared utility means duplication remains.
→ **Accepted:** Duplication of 10-line function is acceptable. Can be refactored later when multiple files need updates, following "rule of three" refactoring principle.

**Trade-off:** No automated tests for URL resolution logic.
→ **Accepted:** Manual testing is sufficient for this fix. Unit tests for utility functions can be added during future refactoring.
