# Sarthi (Productivity App)

Sarthi is a modern productivity application built with Jetpack Compose, Firebase, and AI-driven insights.

## Getting Started

To get this project running on your local machine, follow these steps:

### 1. Prerequisites
- Android Studio Ladybug or newer
- JDK 17
- A Firebase project

### 2. Clone the Repository
```bash
git clone https://github.com/wakeupsoham/Sarthi.git
cd Sarthi
```

### 3. Configure Secrets
We use the [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin) to keep API keys safe.

1.  Copy the example properties file:
    ```bash
    cp local.properties.example local.properties
    ```
2.  Open `local.properties` and add your Gemini API Key:
    ```properties
    AI_API_KEY=your_gemini_api_key_here
    ```

### 4. Setup Firebase
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Create a new project and add an Android App with the package name `com.example.productivity`.
3.  Download the `google-services.json` file.
4.  Place the `google-services.json` file in the `app/` directory of this project.
5.  Enable **Authentication** (Email/Password & Guest) and **Cloud Firestore** in your Firebase console.

### 5. Build and Run
Open the project in Android Studio, wait for Gradle to sync, and run it on your emulator or physical device.

## Security Note
**Never commit your `local.properties` or `google-services.json` files.** These are excluded by `.gitignore` to protect your private keys and project configuration.
