# Pixelbit

Pixelbit is a modern Android application built with Kotlin and Jetpack Compose.

## Features

* **Modern UI:** Built entirely with Jetpack Compose for a declarative and modern UI.
* **Firebase Integration:** Utilizes Firebase for backend services.
* **Dependency Injection:** Uses Koin for managing dependencies.
* **Asynchronous Operations:** Leverages Kotlin Coroutines for asynchronous programming.
* **Navigation:** Implements navigation using Navigation 3.
* **Data Persistence:** Uses DataStore for simple, asynchronous key-value storage.
* **Image Loading:** Uses Coil for efficient image loading.

### E-commerce Features

* **Onboarding:** A smooth introduction for new users.
* **Authentication:** Secure user sign-up and sign-in.
* **Home:** The main screen showcasing products.
* **Products:** View and search for products.
* **Category:** Browse products by category.
* **Cart:** Add and manage items in the shopping cart.
* **Checkout:** A seamless checkout process.
* **Profile:** Manage user profile and settings.
* **Favorites:** Save and view favorite products.

## Tech Stack

* **Kotlin:** The primary programming language.
* **Jetpack Compose:** For building the user interface.
* **Navigation 3:** For navigating between screens.
* **ViewModel:** To store and manage UI-related data.
* **Koin:** For dependency injection.
* **Firebase:** For backend services.
* **DataStore:** For data persistence.
* **Coil:** For image loading.
* **Kotlinx Serialization:** For JSON serialization.

## Project Structure

The project is organized into the following packages:

* `core`: Contains core components and utilities shared across the application.
* `data`: Handles data sources, repositories, and models.
* `di`: Dependency injection modules using Koin.
* `domain`: Contains the business logic, use cases, and domain models.
* `presentation`: Contains the UI layer, including Composables, ViewModels, and navigation.
    * `features`: Contains the different features of the application.
        * `auth`: User authentication (sign-up, sign-in).
        * `cart`: Shopping cart functionality.
        * `category`: Product categories.
        * `checkout`: Checkout process.
        * `favorites`: User's favorite products.
        * `home`: The main home screen.
        * `onboarding`: Onboarding screens for new users.
        * `products`: Product details and lists.
        * `profile`: User profile management.
    * `navigation`: Navigation graph and related components.
    * `theme`: App theme, colors, and typography.

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* Android Studio
* Google Services configuration file (`google-services.json`)

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/Helmy2/PixelBit.git
   ```
2. Open the project in Android Studio.
3. Add your `google-services.json` file to the `app` directory.
4. Build and run the application.
