# News App

A modern Android news reader app built with Jetpack Compose following Clean Architecture principles.

## Features

- 📰 Browse latest news headlines with infinite scrolling
- 🔍 Search articles by keyword
- 🔖 Bookmark articles for offline reading
- 🔗 Share articles and open in browser
- 📱 Modern UI with Material Design 3

## Tech Stack

- **UI**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp
- **Database**: Room
- **Pagination**: Paging 3
- **Language**: Kotlin

## Setup

1. Clone the repository
2. Add your News API key in `local.properties`:
   ```
   NEWS_API_KEY=your_api_key_here
   ```
3. Sync and run the project

## Architecture

The app follows Clean Architecture with three layers:

- **Presentation**: Composable screens and ViewModels
- **Domain**: Business logic and use cases
- **Data**: Repository pattern with remote (API) and local (Room) data sources

## License

MIT License