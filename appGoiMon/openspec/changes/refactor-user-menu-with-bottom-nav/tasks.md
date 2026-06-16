## 1. Data Layer - Order History API

- [x] 1.1 Add OrderHistoryItemDto data class with food_name, quantity, note, and status fields
- [x] 1.2 Add OrderHistoryDto data class with order_id, created_at, and items list
- [x] 1.3 Add getOrderHistory method to ApiService interface for GET api/user/get_order_history.php
- [x] 1.4 Add getOrderHistory method to UserSessionRepository that calls ApiService and returns Result<List<OrderHistoryDto>>

## 2. ViewModel - Search State

- [x] 2.1 Add searchQuery String field to UserOrderUiState with default empty string
- [x] 2.2 Add onSearchQueryChange function to OrderViewModel that updates searchQuery
- [x] 2.3 Add filteredMenuItems computed property to UserOrderUiState that filters menuItems by searchQuery (case-insensitive)

## 3. ViewModel - Order History State

- [x] 3.1 Add orderHistory List<OrderHistoryDto> field to UserOrderUiState with default empty list
- [x] 3.2 Add isLoadingHistory Boolean field to UserOrderUiState with default false
- [x] 3.3 Add loadOrderHistory function to OrderViewModel that fetches from repository and updates state
- [x] 3.4 Add refreshOrderHistory function to OrderViewModel for pull-to-refresh functionality

## 4. UI - EmptyPlaceholderScreen

- [x] 4.1 Create EmptyPlaceholderScreen.kt in ui/screen/user/ package
- [x] 4.2 Implement EmptyPlaceholderScreen composable with title parameter
- [x] 4.3 Display centered text using MutedBrown color with message "Tính năng {title} đang phát triển"

## 5. UI - MenuScreen (Refactor from TableOrderScreen)

- [x] 5.1 Create MenuScreen.kt in ui/screen/user/ package
- [x] 5.2 Create MenuScreen composable that receives UiState and callbacks (no Scaffold, no FAB)
- [x] 5.3 Add search TextField at top with search icon and placeholder "Tìm món ăn..."
- [x] 5.4 Wire search TextField to onSearchQueryChange callback
- [x] 5.5 Replace LazyColumn with LazyVerticalGrid using GridCells.Fixed(2)
- [x] 5.6 Create compact MenuItemCard composable with image, name, category, and action button
- [x] 5.7 Show [+] button when item not in cart, show [-] [qty] [+] when item is in cart
- [x] 5.8 Remove price display and status text from cards
- [x] 5.9 Use filteredMenuItems from UiState instead of menuItems
- [x] 5.10 Handle empty search results with message "Không tìm thấy món ăn phù hợp"

## 6. UI - CartScreen

- [x] 6.1 Create CartScreen.kt in ui/screen/user/ package
- [x] 6.2 Create CartScreen composable with Scaffold and TopAppBar showing "Giỏ hàng (N món)"
- [x] 6.3 Reuse CartItemCard composable from TableOrderScreen (with quantity controls and note field)
- [x] 6.4 Display empty state "Giỏ hàng trống" when cartItems is empty
- [x] 6.5 Add "Gọi món" button at bottom that calls onSubmit callback
- [x] 6.6 Disable submit button when cart is empty or isSubmitting is true
- [x] 6.7 Show loading indicator in button when isSubmitting is true

## 7. UI - OrderHistoryScreen

- [x] 7.1 Create OrderHistoryScreen.kt in ui/screen/user/ package
- [x] 7.2 Create OrderHistoryScreen composable with Scaffold and TopAppBar "Lịch sử gọi món"
- [x] 7.3 Add pull-to-refresh functionality using PullRefreshIndicator
- [x] 7.4 Display LazyColumn of order cards showing order_id and created_at in header
- [x] 7.5 Display order items with food name, quantity, note, and status for each order
- [x] 7.6 Show loading indicator when isLoadingHistory is true
- [x] 7.7 Show empty state "Chưa có đơn hàng nào" when orderHistory is empty
- [x] 7.8 Show error message with retry button when API call fails
- [x] 7.9 Call loadOrderHistory on screen initialization

## 8. UI - UserMainScaffold

- [x] 8.1 Create UserMainScaffold.kt in ui/screen/user/ package
- [x] 8.2 Create UserMainScaffold composable with Scaffold containing bottom NavigationBar
- [x] 8.3 Add 5 NavigationBarItem components: "Đồ ăn", "Giỏ hàng", "Lịch sử", "Phản hồi", "Liên hệ"
- [x] 8.4 Add selectedTab state using remember { mutableStateOf(0) }
- [x] 8.5 Show badge on "Giỏ hàng" tab with cartItemCount when count > 0
- [x] 8.6 Wire tab selection to update selectedTab state
- [x] 8.7 Implement when expression to display correct screen based on selectedTab
- [x] 8.8 Tab 0: MenuScreen with all required callbacks
- [x] 8.9 Tab 1: CartScreen with all required callbacks
- [x] 8.10 Tab 2: OrderHistoryScreen with all required callbacks
- [x] 8.11 Tab 3: EmptyPlaceholderScreen("Phản hồi")
- [x] 8.12 Tab 4: EmptyPlaceholderScreen("Liên hệ")

## 9. Integration - MainActivity Update

- [x] 9.1 Update MainActivity to render UserMainScaffold instead of TableOrderScreen when step is ACTIVE_MENU
- [x] 9.2 Pass all existing callbacks (onBack, onLogout, onRetryMenu, onAddToCart, etc.) to UserMainScaffold
- [x] 9.3 Pass OrderViewModel and UiState to UserMainScaffold

## 10. Backend - Order History API

- [x] 10.1 Create get_order_history.php in api/user/ directory
- [x] 10.2 Accept session_id parameter from GET query or POST body
- [x] 10.3 Query database for orders joined with order_items and menu_items filtered by session_id
- [x] 10.4 Return JSON response with success flag and data array containing orders with items
- [x] 10.5 Include order_id, created_at, food_name, quantity, note, and status for each item
- [x] 10.6 Handle error cases (invalid session_id, database errors) with appropriate error messages

## 11. Verification

- [x] 11.1 Run gradlew assembleDebug to verify build succeeds with no errors
- [ ] 11.2 Manual test: verify bottom navigation displays all 5 tabs
- [ ] 11.3 Manual test: verify search filters menu items correctly
- [ ] 11.4 Manual test: verify menu displays in 2-column grid layout
- [ ] 11.5 Manual test: verify adding items to cart shows quantity controls on menu cards
- [ ] 11.6 Manual test: verify cart tab shows badge with item count
- [ ] 11.7 Manual test: verify cart screen displays all cart items and allows order submission
- [ ] 11.8 Manual test: verify order history screen displays past orders with items and statuses
- [ ] 11.9 Manual test: verify placeholder screens show "đang phát triển" message for Feedback and Contact tabs
- [ ] 11.10 Manual test: verify tab navigation persists when switching between tabs
