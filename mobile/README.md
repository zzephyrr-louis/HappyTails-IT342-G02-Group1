# HappyTails Mobile App (Android)

This is the Android mobile application for the HappyTails pet adoption platform.

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit2 + OkHttp
- **Dependency Injection**: Hilt
- **UI**: Jetpack Compose / XML Layouts
- **Navigation**: Navigation Component
- **Async**: Coroutines + Flow
- **Local Storage**: Room Database + SharedPreferences

## Project Structure

```
mobile/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/happytails/mobile/
│   │   │   │   ├── data/          # Data layer (repositories, API, local DB)
│   │   │   │   ├── domain/        # Domain layer (use cases, models)
│   │   │   │   ├── ui/            # UI layer (activities, fragments, composables)
│   │   │   │   ├── utils/         # Utility classes
│   │   │   │   └── HappyTailsApp.kt
│   │   │   ├── res/               # Resources (layouts, drawables, strings)
│   │   │   └── AndroidManifest.xml
│   │   └── test/                  # Unit tests
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle                   # Root build file
├── settings.gradle
└── README.md
```

## Features (Per Requirements)

### Functional Requirements Implemented:

- **FR-1, FR-2**: User authentication (Adopter registration and login)
- **FR-3**: Saved Application Profile management
- **FR-4**: Staff registration and login
- **FR-6**: View detailed pet profiles
- **FR-9, FR-10, FR-11**: Pet search and advanced filtering
- **FR-12**: View detailed pet information
- **FR-13**: Submit adoption applications with pre-filled profile data
- **FR-14**: Shelter staff view and manage applications

### Non-Functional Requirements:

- **NFR-3**: Compatibility with Android platform (API Level 26+, Android 8.0+)
- **NFR-4**: Secure password storage and encrypted communication (HTTPS, JWT)
- **NFR-5**: User-friendly Material Design interface

## Setup Instructions

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK (API 26-34)
- Gradle 7.0+

### Building the App

1. Open the project in Android Studio
2. Update `local.properties` with your Android SDK path
3. Sync Gradle files
4. Update `app/src/main/res/values/strings.xml` with your backend API URL
5. Build and run on emulator or physical device

### Configuration

The backend API URL should be configured in:
- `app/src/main/java/com/happytails/mobile/data/remote/ApiConfig.kt`

Default configuration:
```kotlin
const val BASE_URL = "http://10.0.2.2:8080/api/" // For Android Emulator
// const val BASE_URL = "http://YOUR_IP:8080/api/" // For physical devices
```

## API Integration

The app communicates with the Spring Boot backend using REST APIs:

- **Authentication**: `/api/auth/login`, `/api/auth/register-adopter`, `/api/auth/register-staff`
- **Pets**: `/api/pets`, `/api/pets/{id}`, `/api/pets/search`
- **Applications**: `/api/applications`, `/api/applications/me`, `/api/applications/shelter`
- **Profile**: `/api/adopters/me`, `/api/adopters/me/profile`

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## Deployment

### Building Release APK:

```bash
./gradlew assembleRelease
```

The APK will be generated in: `app/build/outputs/apk/release/`

### Signing Configuration:

Create a keystore and update `app/build.gradle` with signing config for production builds.

## Project Timeline

- **Phase 1** (Weeks 1-2): Setup, Authentication, Basic UI
- **Phase 2** (Weeks 3-4): Pet Discovery, Filtering, Search
- **Phase 3** (Weeks 5-6): Adoption Application Workflow
- **Phase 4** (Weeks 7-8): Shelter Staff Features, Testing
- **Deployment**: By end of December 2026 (per Constraint 1)

## Contributors

HappyTails Development Team (4-person team, per Constraint 4)

## License

Proprietary - HappyTails IT342 Group Project
