## Context

The Android app's user flow currently progresses through table entry, combo selection, payment confirmation, and displays the menu. The OrderViewModel manages this flow through a state machine (UserOrderStep enum) and holds session/menu state. TableOrderScreen displays menu items loaded from the get_menu_by_combo.php API.

The existing create_order.php API expects:
- session_id (from active session)
- items array with food_id, quantity, note
- Returns order_id and created items with statuses

The app uses Jetpack Compose with ViewModel/Repository pattern. No cart functionality exists—CartViewModel and CartScreen are empty stub classes.

## Goals / Non-Goals

**Goals:**
- Allow users to add menu items to a cart before ordering
- Provide visual feedback on cart contents (item count badge)
- Enable cart review and editing (quantity adjustment, item removal, notes)
- Submit orders to the kitchen via create_order.php API
- Handle API success/failure with clear user feedback
- Keep cart state in memory during active session

**Non-Goals:**
- Cart persistence across app restarts (memory-only is acceptable)
- Order status tracking (separate change)
- Checkout/payment request (separate change)
- Multi-session cart (cart is tied to current session)
- Undo/redo for cart operations

## Decisions

### Store cart state in OrderViewModel, not separate CartViewModel

Add cart state directly to OrderViewModel's UserOrderUiState rather than using the existing CartViewModel stub.

**Rationale:** Cart is tightly coupled to the active session and menu context already managed by OrderViewModel. Keeping state unified avoids synchronization issues and simplifies the data flow.

**Alternative considered:** Separate CartViewModel. This would require passing session_id between ViewModels and coordinating state, adding complexity without clear benefit.

### Use ModalBottomSheet for cart UI, not full-screen CartScreen

Display cart in a bottom sheet overlay triggered by FAB, rather than navigating to a separate screen.

**Rationale:**
- Users retain visual context of the menu while reviewing cart
- Faster interaction (no navigation overhead)
- Modern mobile UX pattern (seen in food delivery apps)
- Simpler state management (no navigation state to track)

**Alternative considered:** Full-screen CartScreen. This would require navigation logic, back button handling, and loses menu context. Bottom sheet is simpler and more intuitive.

### Clear cart after successful order submission

Empty cart state after create_order.php returns success, rather than keeping items for potential re-ordering.

**Rationale:**
- Prevents accidental duplicate orders
- Matches expected behavior (ordered items move to "order status" domain)
- Simpler implementation (no "already ordered" flags needed)

**Alternative considered:** Keep cart and mark items as ordered. This adds complexity and UI clutter; users can view ordered items via order status screen (future change).

### Use existing OrderRepository pattern, add createOrder method

Extend the repository pattern used elsewhere in the app rather than creating a new UserOrderRepository.

**Rationale:**
- Consistent with project conventions (OrderRepository already exists for admin order management)
- Avoids proliferation of repository classes
- create_order.php is semantically an order operation

**Alternative considered:** Create UserOrderRepository. This separates concerns but adds boilerplate for a single method; not worth the overhead.

### Add cart item notes as optional, not per-item required field

Let users optionally add notes to cart items via a TextField in the bottom sheet, but don't require it.

**Rationale:**
- Most orders won't need special instructions
- API supports optional notes field
- Keeps UI simple and fast for common case

## Risks / Trade-offs

- **Cart state lost on app kill or logout** → Acceptable trade-off; users are in active session and ordering is quick. Warn user on logout if cart is not empty.
- **No validation that menu items are still available when submitting** → API will fail with clear error if items are no longer available or not in combo; handle gracefully and prompt retry.
- **Network failure during order submission** → Show error message with retry button; cart state is preserved so user doesn't lose their selections.
- **User adds items, admin ends session** → Session validation happens at API level; show error and return user to table entry step.
- **Large carts may not fit in bottom sheet** → Bottom sheet content is scrollable; usability should be fine up to ~10-15 items (reasonable for buffet ordering).

## Migration Plan

1. Add cart data structures to OrderViewModel (CartItem data class, cart state fields)
2. Add cart management methods to OrderViewModel (add, remove, update)
3. Add createOrder API binding and repository method
4. Update TableOrderScreen UI with FAB and bottom sheet
5. Test order submission end-to-end with existing create_order.php API
6. No rollback needed (purely additive change; existing menu display unaffected)

## Open Questions

None. Design is straightforward and follows existing patterns in the codebase.
