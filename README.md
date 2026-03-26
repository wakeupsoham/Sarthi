# Sarthi (Productivity App)

Sarthi is a modern productivity application built with Jetpack Compose, Firebase, and AI-driven insights.
 
## Features
- **Smart Task Management**: Organize tasks with priorities and estimated effort.
- **Weekly AI Insights**: Get personalized productivity analysis and tips powered by Google Gemini.
- **Declutter Mode**: Quickly reschedule or clear unfinished tasks at the end of the day.
- **Guest Mode**: Try the app without creating an account.

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

### 5. Quick Setup (Using Antigravity)
If you are a friend/contributor who has been given the `google-services.json` and `AI_API_KEY` by the owner, you can use **Antigravity** to set up everything automatically. 

Simply open your Antigravity in Android Studio and use this prompt:

```text
I have just cloned this project and I need to set up my local secrets. 
Please do the following:
1. Create a file at `app/google-services.json` and paste the contents that the owner shared with me.
2. Open the `local.properties` file in the root directory and append `AI_API_KEY=YOUR_KEY_HERE`.
```

### 6. Build and Run
After completing the setup, click **"Sync Project with Gradle Files"** in Android Studio, then run on your device.

## Security Note
**Never commit your `local.properties` or `google-services.json` files.** These are excluded by `.gitignore` to protect your private keys and project configuration.
