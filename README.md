# Where2Go 📍
### Find Your Vibe. Find Your Space.
Where2Go is an Android application designed to help users discover places based on their mood, activities, comfort preferences, and budget.

The application allows users to explore recommended places, view place details, share experiences through check-ins, and discover popular places through community rankings.

## ✨ Features
### 1. Splash Screen
- Displays the Where2Go branding and application tagline and mascot.
- Introduces users to the application.

### 2. Authentication
- Login screen with email and password fields.
- Password visibility toggle.
- Input validation.
- Registration entry point for new users.

### 3. Location Permission
- Introduces the purpose of location access.
- Provides options to grant permission or skip for now.

### 4. Explore
- Browse recommended places using visual cards.
- Search by place name, vibe, or area.
- Filter places by preferences:
  - All
  - Relaxing
  - Studying
  - Budget
- View place images, distance information, and available amenities.

### 5. Place Details
- View detailed information about a selected place.
- Display vibe scores and check-in statistics.
- Explore popular visiting hours and recommended activities.
- View comfort indicators and culinary recommendations.
- Access the check-in feature.

### 6. Community Check-in
- Share experiences at a selected place.
- Select vibe preferences, such as Relaxing, Crowded, Cozy, Good Vibes, and Focus.
- Select comfort features, including air conditioning, comfortable chairs, and spacious tables.
- Choose activities and price categories.
- Add comments or reviews.
- Receive a success confirmation and XP reward after check-in.

### 7. Ranking
- **Top Contributor:** Displays contributors ranked by contribution activity.
- **Top Places:** Displays places ranked by check-in counts.
- Highlights leading contributors and popular destinations.

### 8. Bottom Navigation
The application provides five main navigation sections:
- Map
- Explore
- Check-in
- Ranking
- Profile

Map and Profile may display placeholder screens in the current version.

## 🛠️ Technology Stack
- **Language:** Kotlin
- **IDE:** Android Studio
- **Platform:** Android
- **UI:** Android UI components and reusable Kotlin components
- **Project organization:** Data, navigation, screens, themes, and ViewModels
- **Data source:** Dummy data for demonstration and testing

## 📂 Project Structure
```text
Where2Go/
└── app/
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   └── java/
        │       └── org/umn/where2go/
        │           ├── data/
        │           │   ├── DummyData.kt
        │           │   ├── Models.kt
        │           │   └── Validators.kt
        │           ├── navigation/
        │           │   └── AppNav.kt
        │           ├── ui/
        │           │   ├── components/
        │           │   │   ├── Components.kt
        │           │   │   └── PlaceCard.kt
        │           │   ├── screen/
        │           │   │   ├── AuthScreen.kt
        │           │   │   ├── BottomNavBar.kt
        │           │   │   ├── CheckInScreen.kt
        │           │   │   ├── ExploreScreen.kt
        │           │   │   ├── LeaderboardScreen.kt
        │           │   │   ├── PermissionScreen.kt
        │           │   │   ├── PlaceDetailScreen.kt
        │           │   │   ├── PlaceholderScreen.kt
        │           │   │   └── SplashScreen.kt
        │           │   └── theme/
        │           │       ├── Color.kt
        │           │       ├── Shape.kt
        │           │       ├── Theme.kt
        │           │       └── Type.kt
        │           ├── viewmodel/
        │           │   ├── AuthViewModel.kt
        │           │   ├── CheckInViewModel.kt
        │           │   └── ExploreViewModel.kt
        │           └── MainActivity.kt
        └── test/
            └── java/
                └── org/umn/where2go/
                    └── ExampleUnitTest.kt
```

## 🚀 Getting Started
### Prerequisites
Before running the application, make sure you have:
- Android Studio installed.
- A compatible Android SDK.
- An Android emulator or physical Android device.
- Git installed if you plan to clone the repository.

### Installation
1. Clone the repository:
   ```bash
   git clone <YOUR_REPOSITORY_URL>
   ```
2. Open Android Studio.
3. Select **Open** and choose the Where2Go project directory.
4. Wait for Gradle synchronization to complete.
5. Start an Android emulator or connect an Android device.
6. Select the `app` run configuration.
7. Click **Run** to launch the application.

Replace `<YOUR_REPOSITORY_URL>` with the actual URL of your Git repository.

## 🧪 Testing and Validation
The project includes a test directory and a validation component.

Testing areas include:
- Email and password input validation.
- Required fields in the check-in form.
- Review comment length validation.
- Navigation between application screens.
- Check-in success feedback and XP rewards.
- Ranking tab interactions.

The test directory contains `ExampleUnitTest.kt`. Additional tests should be added as the application develops.

## 🎨 UI Design
Where2Go uses an orange-based visual identity with a clean interface, reusable cards, rounded buttons, and category labels.

The interface is designed to make place discovery and check-in interactions straightforward. Theme configuration is organized in the `ui/theme` directory.

## 🚧 Development Status
Where2Go is currently under development.

The current project includes interfaces for authentication, place discovery, place details, check-in, and ranking. Some screens, including Map and Profile, still use placeholder content.

The application uses dummy data for demonstration purposes. Live location services, persistent user data, and backend integrations should not be assumed to be available unless implemented separately.

## 🔮 Future Improvements
Potential future improvements include:
- Integration with a map service and real-time location data.
- Complete user profile management.
- Backend authentication and persistent user accounts.
- Persistent storage for check-ins and reviews.
- Live place information and updated check-in statistics.
- More advanced search and personalized recommendations.

## 🎯 Project Goal
Where2Go aims to help users find places that match their vibe, needs, and activities while encouraging a community-driven place discovery experience.

**Where2Go — Find Your Vibe. Find Your Space.**
