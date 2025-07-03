# Sports Management Android Application

A comprehensive end-to-end sports management Android application built with Kotlin, following MVVM architecture pattern and using Firebase Realtime Database for real-time data synchronization.

## Features

### 🏆 Core Functionality
- **Multi-Sport Support**: Chess, Carom, Table Tennis, Badminton, Cricket, Billiards
- **User Registration**: Register for different sport categories
- **Real-time Live Scores**: Live match score updates
- **Match Scheduling**: View upcoming matches and schedules
- **Results & Winners**: Complete match results and winner details
- **Admin Panel**: Administrative controls for score updates and participant management

### 📱 User Features
- **Dashboard**: Personalized home screen with live matches, upcoming games, and recent results
- **Sport Categories**: Browse and register for different sports
- **Match Center**: View all matches with real-time updates
- **Profile Management**: User profile with registration history
- **Push Notifications**: Real-time notifications for match updates

### 👨‍💼 Admin Features
- **Score Management**: Real-time score updates during matches
- **Participant Management**: Add/remove participants from categories
- **Match Creation**: Schedule new matches
- **Category Management**: Manage sport categories and settings

## Technical Stack

### 🛠️ Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **Language**: Kotlin
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)

### 🗄️ Backend & Database
- **Firebase Realtime Database**: Real-time data synchronization
- **Firebase Authentication**: User authentication and management
- **Firebase Cloud Messaging**: Push notifications
- **Firebase Storage**: File and image storage

### 🎨 UI/UX
- **Material Design 3**: Modern and appealing user interface
- **Navigation Component**: Fragment-based navigation
- **View Binding**: Type-safe view references
- **Responsive Design**: Optimized for different screen sizes

### 📚 Libraries & Dependencies
```gradle
// Core Android
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'

// Architecture Components
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'

// Firebase
implementation platform('com.google.firebase:firebase-bom:32.7.0')
implementation 'com.google.firebase:firebase-database-ktx'
implementation 'com.google.firebase:firebase-auth-ktx'
implementation 'com.google.firebase:firebase-messaging-ktx'

// UI & Image Loading
implementation 'com.github.bumptech.glide:glide:4.16.0'
implementation 'de.hdodenhof:circleimageview:3.1.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
```

## Project Structure

```
app/src/main/java/com/example/sportsmanagement/
├── data/
│   ├── model/
│   │   ├── User.kt
│   │   ├── SportCategory.kt
│   │   ├── Match.kt
│   │   └── Tournament.kt
│   └── repository/
│       ├── UserRepository.kt
│       ├── SportRepository.kt
│       └── MatchRepository.kt
├── ui/
│   ├── fragment/
│   │   ├── HomeFragment.kt
│   │   ├── CategoriesFragment.kt
│   │   ├── MatchesFragment.kt
│   │   └── ProfileFragment.kt
│   ├── viewmodel/
│   │   ├── HomeViewModel.kt
│   │   ├── CategoryViewModel.kt
│   │   └── MatchViewModel.kt
│   └── adapter/
│       ├── SportCategoryAdapter.kt
│       ├── MatchAdapter.kt
│       └── LiveMatchAdapter.kt
├── service/
│   └── FirebaseMessagingService.kt
└── MainActivity.kt
```

## Setup Instructions

### 1. Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Firebase project setup

### 2. Firebase Configuration

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project
   - Add an Android app with package name: `com.example.sportsmanagement`

2. **Download Configuration**:
   - Download `google-services.json` from Firebase Console
   - Place it in the `app/` directory (replace the placeholder file)

3. **Enable Firebase Services**:
   ```bash
   # Enable required Firebase services
   - Authentication (Email/Password)
   - Realtime Database
   - Cloud Messaging
   - Storage (optional)
   ```

4. **Database Rules** (For development):
   ```json
   {
     "rules": {
       ".read": "auth != null",
       ".write": "auth != null"
     }
   }
   ```

### 3. Installation

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd sports-management-android
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync Project**:
   - Let Gradle sync the project
   - Resolve any dependency issues

4. **Configure Firebase**:
   - Replace the placeholder `google-services.json` with your actual Firebase configuration
   - Update Firebase project settings if needed

5. **Build and Run**:
   ```bash
   ./gradlew build
   ```

## Database Schema

### Users Collection
```json
{
  "users": {
    "userId": {
      "id": "string",
      "name": "string",
      "email": "string",
      "phone": "string",
      "profileImageUrl": "string",
      "isAdmin": "boolean",
      "registeredCategories": ["categoryId1", "categoryId2"],
      "createdAt": "timestamp"
    }
  }
}
```

### Sport Categories Collection
```json
{
  "sport_categories": {
    "categoryId": {
      "id": "string",
      "name": "string",
      "description": "string",
      "maxParticipants": "number",
      "participants": ["userId1", "userId2"],
      "isActive": "boolean",
      "createdAt": "timestamp"
    }
  }
}
```

### Matches Collection
```json
{
  "matches": {
    "matchId": {
      "id": "string",
      "categoryId": "string",
      "player1Id": "string",
      "player2Id": "string",
      "scheduledTime": "timestamp",
      "status": "SCHEDULED|LIVE|COMPLETED",
      "score": {
        "player1Score": "number",
        "player2Score": "number"
      },
      "winnerId": "string",
      "venue": "string"
    }
  }
}
```

## App Screens & Navigation

### 🏠 Home Screen
- Welcome card with user greeting
- Live matches section with real-time updates
- Featured sports categories
- Upcoming matches
- Recent results

### 🏃 Categories Screen
- Grid view of all sport categories
- Registration status for each category
- Participant count and limits
- Registration/Unregistration functionality

### 🏆 Matches Screen
- All matches with filtering options
- Live score updates
- Match details and schedules
- Admin controls for score updates

### 👤 Profile Screen
- User information and settings
- Registered categories
- Match history
- Admin panel (for admin users)

## Real-time Features

### Live Score Updates
- Real-time score synchronization using Firebase Realtime Database
- Automatic UI updates when scores change
- Live match indicators and status updates

### Push Notifications
- Match start notifications
- Score update notifications
- Registration confirmations
- Admin announcements

## Admin Features

### Score Management
```kotlin
// Update match score in real-time
matchViewModel.updateMatchScore(matchId, newScore)
```

### Participant Management
```kotlin
// Add/remove participants
sportViewModel.addParticipantToCategory(categoryId, userId)
sportViewModel.removeParticipantFromCategory(categoryId, userId)
```

## Security Rules

### Authentication
- Email/password authentication required
- User session management
- Secure data access based on user roles

### Database Security
```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid || root.child('users').child(auth.uid).child('isAdmin').val() === true",
        ".write": "$uid === auth.uid || root.child('users').child(auth.uid).child('isAdmin').val() === true"
      }
    },
    "matches": {
      ".read": "auth != null",
      ".write": "root.child('users').child(auth.uid).child('isAdmin').val() === true"
    }
  }
}
```

## Color Scheme

### Primary Colors
- **Primary**: `#2E7D32` (Green)
- **Primary Dark**: `#1B5E20`
- **Accent**: `#FF5722` (Orange)

### Sport Category Colors
- **Chess**: `#8E24AA` (Purple)
- **Carom**: `#D32F2F` (Red)
- **Table Tennis**: `#1976D2` (Blue)
- **Badminton**: `#388E3C` (Green)
- **Cricket**: `#F57C00` (Orange)
- **Billiards**: `#5D4037` (Brown)

## Performance Optimizations

### Database Optimization
- Efficient query patterns
- Data pagination for large datasets
- Offline data caching
- Real-time listener management

### UI Optimization
- RecyclerView with DiffUtil
- Image loading with Glide
- Memory leak prevention
- Smooth animations and transitions

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

## Deployment

### Build Release APK
```bash
./gradlew assembleRelease
```

### Build App Bundle
```bash
./gradlew bundleRelease
```

## Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, email support@sportsmanagement.com or create an issue in the repository.

## Acknowledgments

- Firebase for real-time database and authentication
- Material Design for UI guidelines
- Android Jetpack for modern Android development
- Community contributors and testers

---

## 🎨 UI Design Specifications

### Modern Material Design 3
- **Card-based layout** with elevated surfaces
- **Rounded corners** (12dp radius)
- **Consistent spacing** (16dp margins)
- **Typography hierarchy** with proper text sizes
- **Color-coded categories** for easy identification
- **Floating Action Buttons** for primary actions
- **Bottom Navigation** for main app sections

### Responsive Design
- Support for different screen sizes
- Tablet-optimized layouts
- Landscape orientation support
- Accessibility features compliance

This comprehensive Sports Management application provides a complete solution for organizing and managing sports tournaments with real-time updates, modern UI, and robust backend integration.