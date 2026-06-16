## Why

Users can currently view the menu after their session is confirmed but cannot order food. The app displays menu items but has no way to add items to a cart or submit orders to the kitchen. This blocks the core ordering workflow and makes the app unusable for its primary purpose.

## What Changes

- Add cart state management to OrderViewModel for tracking selected menu items with quantities and notes
- Add floating cart button with item count badge on TableOrderScreen
- Add cart bottom sheet UI showing cart items with quantity controls and order submission
- Integrate with existing create_order.php API to submit orders to the kitchen
- Add success and error handling for order submission with user feedback
- Implement cart item management (add, remove, update quantity, add notes)

## Capabilities

### New Capabilities
- `user-cart-management`: Users can add menu items to a shopping cart, adjust quantities, add notes, and review their cart before ordering
- `user-order-submission`: Users can submit their cart as an order to the kitchen through the existing create_order.php API

### Modified Capabilities

None. This change adds new ordering capabilities to the existing user-combo-session-flow without modifying its requirements.

## Impact

- Android OrderViewModel (add cart state and order submission logic)
- Android TableOrderScreen (add cart FAB button and bottom sheet UI)
- Android ApiService (add createOrder method)
- Android data repository layer (new UserOrderRepository or extend existing)
- Existing create_order.php API (already implemented, will be integrated)
- No database schema changes
- No impact on admin flow or other user flow steps
