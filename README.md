[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/N3RLIwPA)

# ForcedSocial

ForcedSocial is a forum-like application built with Android Jetpack Compose and Firebase. It offers
features such as user authentication, forum posting, real-time communication, user profiles, and
more. The app follows a unidirectional data flow with ViewModels.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)

## Features

- **User Authentication**: Sign in and sign out using Google or email and password.
- **Forum Posting**: Create and view posts within different topics.
- **Real-Time Communication**: View real-time updates for posts and comments.
- **User Profiles**: Create and edit user profiles with profile pictures.
- **Search**: Search for users and posts within the forum.
- **Moderation**: Users with moderator privileges can create topics, delete comments and posts.

## Architecture

The app is designed with a unidirectional data flow and uses ViewModels for managing UI-related data
in a lifecycle-conscious way. The architecture includes:

- **ViewModel**: Manages UI-related data and handles business logic.
- **Firebase Firestore**: Used for storing and syncing app data in real-time.
- **Firebase Authentication**: Manages user authentication and security.
- **Jetpack Compose**: Modern toolkit for building native Android UI.

## Technologies Used

- **Kotlin**: Programming language for Android development.
- **Jetpack Compose**: Toolkit for building UI.
- **Firebase Firestore**: NoSQL cloud database.
- **Firebase Authentication**: User authentication and security.
- **Firebase Storage**: Storing user-uploaded images.
- **Material Design 3 (Material You)**: UI components and theming.

## Getting Started

### Prerequisites

- Android Studio installed
- Firebase project set up

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/freeuni-cs-android-23-24/final-project-forced.git
    ```
2. Open the project in Android Studio.
3. Set up Firebase:
    - Add your `google-services.json` file to the app directory.
    - Ensure Firebase Firestore, Authentication, and Storage are enabled in your Firebase project.
4. Sync the project with Gradle files.
5. Run the app on an emulator or physical device.
