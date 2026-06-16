## 1. ViewModel - Add Category Filter State

- [x] 1.1 Add selectedCategoryName String? field to UserOrderUiState with default null
- [x] 1.2 Add categories computed property to UserOrderUiState that derives unique category names from menuItems using mapNotNull and distinct, sorted alphabetically
- [x] 1.3 Add onCategorySelected function to OrderViewModel that updates selectedCategoryName in state

## 2. ViewModel - Update Filtering Logic

- [x] 2.1 Update filteredMenuItems computed property to filter by selectedCategoryName first (if not null) before applying search query filter
- [x] 2.2 Ensure null selectedCategoryName means no category filter applied (equivalent to "Tất cả")
- [x] 2.3 Verify case-insensitive search logic is preserved in combined filtering

## 3. UI - MenuScreen Category Chips Row

- [x] 3.1 Add LazyRow with horizontal scrolling between search TextField and menu grid in MenuScreen
- [x] 3.2 Add horizontalArrangement spacedBy 8.dp to LazyRow for chip spacing
- [x] 3.3 Add contentPadding to LazyRow for proper edge spacing

## 4. UI - Implement FilterChip Components

- [x] 4.1 Add "Tất cả" FilterChip as first item in LazyRow using item {} block
- [x] 4.2 Set "Tất cả" chip selected state to true when uiState.selectedCategoryName == null
- [x] 4.3 Wire "Tất cả" chip onClick to call onCategorySelected(null)
- [x] 4.4 Add items loop for uiState.categories to create FilterChip for each category
- [x] 4.5 Set each category chip selected state to true when uiState.selectedCategoryName matches the category
- [x] 4.6 Wire each category chip onClick to call onCategorySelected with the category name
- [x] 4.7 Set label parameter on each FilterChip to display category name text

## 5. UI - Empty States

- [x] 5.1 Update empty search results message condition to check if searchQuery is not empty
- [x] 5.2 Add separate empty state check for when selectedCategoryName is not null and filteredMenuItems is empty
- [x] 5.3 Display "Chưa có món trong danh mục này" message for empty category case
- [x] 5.4 Ensure existing "Không tìm thấy món ăn phù hợp" message shows for empty search results

## 6. Integration - UserMainScaffold Callback Wiring

- [x] 6.1 Add onCategorySelected parameter to UserMainScaffold function signature with type (String?) -> Unit
- [x] 6.2 Pass onCategorySelected callback to MenuScreen when rendering tab 0
- [x] 6.3 Wire callback in UserMainScaffold to call OrderViewModel::onCategorySelected

## 7. Integration - MainActivity Callback

- [x] 7.1 Pass orderViewModel::onCategorySelected to UserMainScaffold in MainActivity when step is ACTIVE_MENU

## 8. Verification

- [x] 8.1 Run gradlew assembleDebug to verify build succeeds with no errors
- [ ] 8.2 Manual test: verify category chips appear between search bar and menu grid
- [ ] 8.3 Manual test: verify "Tất cả" chip is first and selected by default
- [ ] 8.4 Manual test: verify tapping category chip filters menu items to that category only
- [ ] 8.5 Manual test: verify tapping "Tất cả" shows all items again
- [ ] 8.6 Manual test: verify categories are sorted alphabetically after "Tất cả"
- [ ] 8.7 Manual test: verify searching within a selected category shows only matching items in that category
- [ ] 8.8 Manual test: verify empty category shows "Chưa có món trong danh mục này" message
- [ ] 8.9 Manual test: verify search with no results shows "Không tìm thấy món ăn phù hợp" message
- [ ] 8.10 Manual test: verify horizontal scrolling works when many categories exist
