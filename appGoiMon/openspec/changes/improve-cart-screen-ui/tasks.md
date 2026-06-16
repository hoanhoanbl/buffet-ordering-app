## 1. Add Image URL Resolution Helper

- [x] 1.1 Add resolveFoodImageUrl private function to CartScreen.kt that handles absolute URLs, relative paths, and filename-only formats
- [x] 1.2 Implement logic to return URL directly if it starts with "http://" or "https://"
- [x] 1.3 Implement logic to append BASE_URL for relative paths starting with "/"
- [x] 1.4 Implement logic to construct BASE_URL + "uploads/foods/" + filename for filename-only inputs
- [x] 1.5 Handle null or empty image field by returning null

## 2. Add Required Imports to CartScreen

- [x] 2.1 Add import for SubcomposeAsyncImage from coil.compose
- [x] 2.2 Add import for RoundedCornerShape from androidx.compose.foundation.shape
- [x] 2.3 Add import for ContentScale from androidx.compose.ui.layout
- [x] 2.4 Add import for clip modifier from androidx.compose.ui.draw
- [x] 2.5 Add import for remember and mutableStateOf from androidx.compose.runtime
- [x] 2.6 Add import for RetrofitClient from com.example.appgoimon.data.remote
- [x] 2.7 Add import for OrangeAccent color from ui.theme

## 3. Update CartItemCard Layout Structure

- [x] 3.1 Replace root Column with Row in CartItemCard composable
- [x] 3.2 Set Row modifier to fillMaxWidth() and horizontalArrangement to spacedBy(8.dp)
- [x] 3.3 Add verticalAlignment CenterVertically to Row for proper image alignment

## 4. Add Food Image Display

- [x] 4.1 Add SubcomposeAsyncImage as first element in Row with resolveFoodImageUrl(cartItem.menuItem.image) as model
- [x] 4.2 Set image modifier to size(60.dp) and clip(RoundedCornerShape(8.dp))
- [x] 4.3 Set contentScale to ContentScale.Crop for proper aspect ratio handling
- [x] 4.4 Add loading composable that shows CircularProgressIndicator in centered Box
- [x] 4.5 Add error composable that shows fallback Box with "Khong tai duoc anh" text and background Color(0xFFFFF3D8)

## 5. Update Content Column Structure

- [x] 5.1 Wrap existing content (name, quantity controls, note) in Column with weight(1f) modifier in Row
- [x] 5.2 Set Column verticalArrangement to spacedBy(8.dp)
- [x] 5.3 Move food name Text, quantity controls Row, and note field into this Column

## 6. Add Category Display

- [x] 6.1 Add category name Text below food name using cartItem.menuItem.category_name
- [x] 6.2 Set category Text style to MaterialTheme.typography.bodySmall
- [x] 6.3 Set category Text color to OrangeAccent
- [x] 6.4 Wrap category Text in category_name?.let {} block to handle null case

## 7. Implement Collapsible Note Field

- [x] 7.1 Add remember { mutableStateOf } for isNoteExpanded boolean state initialized to cartItem.note.isNotEmpty()
- [x] 7.2 Add if condition to check isNoteExpanded || cartItem.note.isNotEmpty()
- [x] 7.3 Show TextField inside if block (existing note TextField code)
- [x] 7.4 Add else block with TextButton showing "Thêm ghi chú" text
- [x] 7.5 Wire TextButton onClick to set isNoteExpanded to true
- [x] 7.6 Set TextButton contentPadding to small values (4.dp) for compact appearance

## 8. Update Card Spacing

- [x] 8.1 Update LazyColumn verticalArrangement in CartScreen from spacedBy(8.dp) to spacedBy(12.dp)

## 9. Verification

- [x] 9.1 Run gradlew assembleDebug to verify build succeeds with no errors
- [ ] 9.2 Manual test: verify food images appear in cart items with proper sizing and rounded corners
- [ ] 9.3 Manual test: verify image loading indicator shows while images load
- [ ] 9.4 Manual test: verify error fallback displays when image fails to load
- [ ] 9.5 Manual test: verify category name displays below food name in OrangeAccent color
- [ ] 9.6 Manual test: verify category is hidden when category_name is null
- [ ] 9.7 Manual test: verify "Thêm ghi chú" button appears when note is empty
- [ ] 9.8 Manual test: verify tapping "Thêm ghi chú" expands TextField
- [ ] 9.9 Manual test: verify existing notes are shown expanded by default
- [ ] 9.10 Manual test: verify note field state is independent per cart item
- [ ] 9.11 Manual test: verify card spacing increased to 12dp between items
- [ ] 9.12 Manual test: verify quantity controls still work correctly with new layout
