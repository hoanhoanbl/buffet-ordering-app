## 1. Read Current MenuScreen.kt Implementation

- [x] 1.1 Read MenuScreen.kt MenuItemCard composable (lines 231-330) to locate modification points
- [x] 1.2 Identify the Text component for food name (currently line ~278-284)
- [x] 1.3 Identify the quantity control IconButtons and text styles (currently line ~315-325)

## 2. Fix MenuScreen Grid Card Uniformity

- [x] 2.1 Add minLines = 2 parameter to the food name Text component
- [x] 2.2 Verify maxLines = 2 is already set (should be at line ~283)
- [x] 2.3 Ensure overflow = TextOverflow.Ellipsis is set for text truncation

## 3. Reduce MenuScreen Quantity Control Sizes

- [x] 3.1 Add modifier = Modifier.size(36.dp) to the decrement IconButton
- [x] 3.2 Add modifier = Modifier.size(36.dp) to the increment IconButton
- [x] 3.3 Change decrement button Text style from titleMedium to titleSmall
- [x] 3.4 Change increment button Text style from titleMedium to titleSmall
- [x] 3.5 Change quantity number Text style from titleSmall to titleSmall (verify consistency)

## 4. Read Current CartScreen.kt Implementation

- [x] 4.1 Read CartScreen.kt CartItemCard composable to locate quantity controls
- [x] 4.2 Identify the quantity control Row with IconButtons (currently line ~198-214)
- [x] 4.3 Confirm current spacing value in Arrangement.spacedBy (currently 8.dp)

## 5. Reduce CartScreen Quantity Control Sizes

- [x] 5.1 Add modifier = Modifier.size(36.dp) to the decrement IconButton
- [x] 5.2 Add modifier = Modifier.size(36.dp) to the increment IconButton
- [x] 5.3 Change decrement button Text style from titleMedium to titleSmall
- [x] 5.4 Change increment button Text style from titleMedium to titleSmall
- [x] 5.5 Change quantity number Text style from titleMedium to titleSmall

## 6. Tighten CartScreen Control Spacing

- [x] 6.1 Change Row horizontalArrangement from spacedBy(8.dp) to spacedBy(4.dp)
- [x] 6.2 Verify Row still maintains verticalAlignment = CenterVertically

## 7. Build Verification

- [x] 7.1 Run ./gradlew assembleDebug to verify no compilation errors
- [x] 7.2 Confirm build completes successfully with no warnings related to changes

## 8. Manual UI Testing (Optional - Requires Device/Emulator)

- [ ] 8.1 Visual test: Verify MenuScreen grid cards have uniform heights
- [ ] 8.2 Visual test: Verify short names (1-line) display with empty space in second line
- [ ] 8.3 Visual test: Verify long names (2+ lines) are truncated with ellipsis
- [ ] 8.4 Visual test: Verify quantity buttons appear smaller but still tappable on MenuScreen
- [ ] 8.5 Visual test: Verify quantity buttons appear smaller and more compact on CartScreen
- [ ] 8.6 Visual test: Verify button spacing on CartScreen creates grouped appearance
- [ ] 8.7 Interaction test: Confirm 36dp buttons are easy to tap on both screens
- [ ] 8.8 Interaction test: Test on different screen sizes if possible