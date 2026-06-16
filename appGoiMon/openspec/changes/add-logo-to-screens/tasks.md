## 1. AdminLoginScreen - Add Required Imports

- [x] 1.1 Add import for SubcomposeAsyncImage from coil.compose
- [x] 1.2 Add import for CircleShape from androidx.compose.foundation.shape
- [x] 1.3 Add import for ContentScale from androidx.compose.ui.layout
- [x] 1.4 Add import for clip modifier from androidx.compose.ui.draw
- [x] 1.5 Add import for RetrofitClient from com.example.appgoimon.data.remote

## 2. AdminLoginScreen - Add Logo Display

- [x] 2.1 Add SubcomposeAsyncImage before "KichiKichi" text in Column
- [x] 2.2 Set model to "${RetrofitClient.BASE_URL}uploads/foods/logo.jpg"
- [x] 2.3 Set modifier to size(120.dp) and clip(CircleShape)
- [x] 2.4 Set contentScale to ContentScale.Crop
- [x] 2.5 Add loading composable with CircularProgressIndicator in centered Box
- [x] 2.6 Add error composable with empty Box (or minimal fallback)
- [x] 2.7 Add Spacer of 16.dp after logo

## 3. AdminLoginScreen - Remove Text Branding

- [x] 3.1 Remove the Text composable displaying "KichiKichi"
- [x] 3.2 Verify Column still contains title Text ("Dang nhap"/"Dang ky")
- [x] 3.3 Verify verticalArrangement spacing is preserved

## 4. MenuScreen - Add Required Imports

- [x] 4.1 Add import for SubcomposeAsyncImage from coil.compose (if not already present)
- [x] 4.2 Add import for CircleShape from androidx.compose.foundation.shape (if not already present)
- [x] 4.3 Add import for ContentScale from androidx.compose.ui.layout (if not already present)
- [x] 4.4 Add import for clip modifier from androidx.compose.ui.draw (if not already present)
- [x] 4.5 Add import for Icons.Default.Search if not already present
- [x] 4.6 Add import for IconButton from androidx.compose.material3

## 5. MenuScreen - Restructure Search Bar to Row

- [x] 5.1 Wrap existing TextField in Row layout
- [x] 5.2 Set Row modifier to fillMaxWidth() and horizontalArrangement to spacedBy(8.dp)
- [x] 5.3 Set Row verticalAlignment to Alignment.CenterVertically

## 6. MenuScreen - Add Logo to Header Row

- [x] 6.1 Add SubcomposeAsyncImage as first element in Row
- [x] 6.2 Set model to "${RetrofitClient.BASE_URL}uploads/foods/logo.jpg"
- [x] 6.3 Set modifier to size(48.dp) and clip(CircleShape)
- [x] 6.4 Set contentScale to ContentScale.Crop
- [x] 6.5 Add loading composable with small CircularProgressIndicator (20.dp)
- [x] 6.6 Add error composable with empty Box

## 7. MenuScreen - Update TextField in Row

- [x] 7.1 Add Modifier.weight(1f) to TextField modifier so it fills available space
- [x] 7.2 Remove leadingIcon parameter from TextField (no longer showing search icon inside)
- [x] 7.3 Verify placeholder, value, onValueChange, and other params remain unchanged

## 8. MenuScreen - Add Search IconButton

- [x] 8.1 Add IconButton as last element in Row after TextField
- [x] 8.2 Set IconButton size to 48.dp
- [x] 8.3 Add Icon with Icons.Default.Search and contentDescription "Search"
- [x] 8.4 Wire IconButton onClick to focus TextField (or leave empty for now)

## 9. Verification

- [x] 9.1 Run gradlew assembleDebug to verify build succeeds with no errors

## 10. Revised Implementation (Option 3: Hybrid Logo)

- [x] 10.1 Update AdminLoginScreen to use logo.jpg (rectangular) with RoundedCornerShape(8.dp)
- [x] 10.2 Change AdminLoginScreen logo size to width(200.dp) and height(90.dp)
- [x] 10.3 Change AdminLoginScreen contentScale to ContentScale.Fit
- [x] 10.4 Update MenuScreen to use logo_tron.jpg (circular badge)
- [x] 10.5 Keep MenuScreen CircleShape and ContentScale.Crop (48.dp size)
- [x] 10.6 Rebuild app to verify changes compile

## 11. Manual Testing (Requires Device/Emulator)

- [ ] 11.1 Manual test: verify full logo (badge + text) appears on login screen (200x90dp, rectangular with rounded corners)
- [ ] 11.2 Manual test: verify logo loading indicator shows while image loads on login screen
- [ ] 11.3 Manual test: verify login screen layout is correct with full branding
- [ ] 11.4 Manual test: verify circular badge logo appears on menu header (48dp, circular, left-aligned)
- [ ] 11.5 Manual test: verify search field fills space between logo and search button
- [ ] 11.6 Manual test: verify search button appears on right at 48dp
- [ ] 11.7 Manual test: verify search functionality still works correctly
- [ ] 11.8 Manual test: verify both logos load correctly (logo.jpg on login, logo_tron.jpg on menu)
- [ ] 11.9 Manual test: verify error fallback works when logos fail to load
- [ ] 11.10 Manual test: verify spacing between elements is correct (8dp in menu header)
- [ ] 11.11 Manual test: verify full "KICHI-KICHI" text is visible on login screen (not cropped)
