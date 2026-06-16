## 1. Read CartScreen.kt Current Implementation

- [x] 1.1 Read CartScreen.kt to locate the resolveFoodImageUrl() function (lines 239-252)
- [x] 1.2 Confirm the current buggy logic using value.startsWith("/")
- [x] 1.3 Identify the exact lines to be modified

## 2. Fix CartScreen Image URL Resolution Logic

- [x] 2.1 Replace the condition `if (value.startsWith("/"))` with `if (!value.contains('/'))`
- [x] 2.2 Swap the logic blocks so filename-only case appends "uploads/foods/"
- [x] 2.3 Ensure path case (with slash) uses BASE_URL + value.trimStart('/')
- [x] 2.4 Verify the fixed logic matches MenuScreen.kt implementation exactly

## 3. Verify Build Success

- [x] 3.1 Run gradlew assembleDebug to verify no compilation errors
- [x] 3.2 Confirm build completes successfully with no warnings

## 4. Manual Testing (Requires Device/Emulator)

- [ ] 4.1 Manual test: Add menu items to cart and verify images load correctly
- [ ] 4.2 Manual test: Test with filename-only image paths (e.g., "pho.jpg")
- [ ] 4.3 Manual test: Test with absolute paths (e.g., "/uploads/foods/pho.jpg")
- [ ] 4.4 Manual test: Test with relative paths (e.g., "uploads/foods/pho.jpg")
- [ ] 4.5 Manual test: Verify empty/null images show fallback placeholder
- [ ] 4.6 Manual test: Confirm images load consistently on menu and cart screens

## 5. Optional: Check TableOrderScreen (Out of Scope)

- [ ] 5.1 Manual check: Read TableOrderScreen.kt to verify if it has the same bug
- [ ] 5.2 Manual check: If bug exists, note for future fix (not part of this change)
