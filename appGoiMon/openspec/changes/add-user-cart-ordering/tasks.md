## 1. Data Layer - API and DTOs

- [x] 1.1 Add CreateOrderRequest data class with session_id and items array (food_id, quantity, note)
- [x] 1.2 Add CreateOrderResponse data class with order_id and created items
- [x] 1.3 Add createOrder method to ApiService interface for POST api/user/create_order.php
- [x] 1.4 Add createOrder method to OrderRepository that calls ApiService and returns Result<CreateOrderResponse>

## 2. ViewModel - Cart State

- [x] 2.1 Add CartItem data class to OrderViewModel with menuItem, quantity, and note fields
- [x] 2.2 Add cartItems List field to UserOrderUiState
- [x] 2.3 Add cartItemCount computed property to UserOrderUiState that sums all quantities
- [x] 2.4 Add isSubmittingOrder Boolean field to UserOrderUiState for loading state

## 3. ViewModel - Cart Management Functions

- [x] 3.1 Add addToCart(menuItem) function that adds item or increments quantity if already in cart
- [x] 3.2 Add removeFromCart(menuItem) function that removes item from cart
- [x] 3.3 Add updateCartItemQuantity(menuItem, newQuantity) function with logic to remove if quantity reaches 0
- [x] 3.4 Add updateCartItemNote(menuItem, note) function that updates note for cart item
- [x] 3.5 Add clearCart() function that empties cart state

## 4. ViewModel - Order Submission

- [x] 4.1 Add submitOrder() function that validates cart is not empty
- [x] 4.2 Implement API call to OrderRepository.createOrder with session_id and cart items
- [x] 4.3 Handle success response by clearing cart and showing success message
- [x] 4.4 Handle error response by showing error message and keeping cart intact
- [x] 4.5 Add loading state management during order submission

## 5. UI - TableOrderScreen Updates

- [x] 5.1 Add "+" FAB button to each menu item card that calls OrderViewModel.addToCart
- [x] 5.2 Add floating cart FAB in bottom-right corner with shopping cart icon
- [x] 5.3 Add badge to cart FAB showing cartItemCount from UiState
- [x] 5.4 Add onClick handler to cart FAB that shows bottom sheet modal

## 6. UI - Cart Bottom Sheet

- [x] 6.1 Create CartBottomSheet composable that receives UiState and ViewModel callbacks
- [x] 6.2 Add header showing "Giỏ hàng" title and total item count
- [x] 6.3 Add LazyColumn displaying cart items with menu item name, quantity controls, and note field
- [x] 6.4 Add quantity controls (- and + buttons) for each cart item that call updateCartItemQuantity
- [x] 6.5 Add TextField for notes on each cart item that calls updateCartItemNote
- [x] 6.6 Add "Gọi món" button at bottom that calls submitOrder with disabled state when cart is empty or isSubmittingOrder is true
- [x] 6.7 Add loading indicator overlay when isSubmittingOrder is true
- [x] 6.8 Add empty state message when cartItems is empty

## 7. UI - Success and Error Handling

- [x] 7.1 Show success SnackBar or AlertDialog when order submission succeeds with message showing item count
- [x] 7.2 Close bottom sheet automatically after successful order submission
- [x] 7.3 Show error AlertDialog when order submission fails with error message and Retry button
- [x] 7.4 Handle session expired error by returning user to TABLE_ENTRY step

## 8. Verification

- [x] 8.1 Compile check with gradlew assembleDebug to verify no build errors
- [ ] 8.2 Manual test: add items to cart and verify badge count updates correctly
- [ ] 8.3 Manual test: adjust quantities with +/- buttons and verify cart updates
- [ ] 8.4 Manual test: add notes to cart items and verify they persist
- [ ] 8.5 Manual test: submit order and verify API call succeeds and cart clears
- [ ] 8.6 Manual test: verify error handling when API fails (e.g., network off)
- [ ] 8.7 Manual test: verify empty cart shows message and submit button is disabled
