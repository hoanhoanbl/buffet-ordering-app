## 1. Build Configuration

- [x] 1.1 Update app/build.gradle.kts to read base.url property from local.properties with default fallback to http://10.0.2.2/appOrder/appGoiMon_API/
- [x] 1.2 Add buildConfigField for BASE_URL in defaultConfig block to expose the URL as BuildConfig.BASE_URL
- [x] 1.3 Ensure BuildConfig is generated correctly by running a clean build

## 2. Retrofit Client Update

- [x] 2.1 Update RetrofitClient.kt to replace hardcoded BASE_URL constant with BuildConfig.BASE_URL
- [x] 2.2 Verify RetrofitClient compiles without errors after the change

## 3. Local Configuration

- [x] 3.1 Create or update local.properties file with base.url property set to current hardcoded value (http://192.168.1.56/appOrder/appGoiMon_API/) as example
- [x] 3.2 Verify local.properties is in .gitignore to prevent committing developer-specific configuration

## 4. Verification

- [x] 4.1 Perform clean build (./gradlew clean assembleDebug) to verify BuildConfig generation
- [x] 4.2 Manually inspect BuildConfig.BASE_URL value in generated sources to confirm correct URL
- [ ] 4.3 Install app on device/emulator and verify API connection works (test any API call like login or check table)
- [ ] 4.4 Test with different base.url values (emulator localhost vs network IP) to verify configuration flexibility
