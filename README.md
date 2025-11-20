# ByajPay - Pending Payments & Interest Tracker

An Android app for small lenders and shopkeepers to track pending payments from customers and calculate interest automatically.

## Features

- **OTP-based Authentication**: Simple phone number login with OTP verification
- **Customer Management**: Add and manage customers with their contact information
- **Transaction Tracking**: Record credit (money given) and debit (money received) transactions
- **Automatic Interest Calculation**: 
  - Set interest rates per credit transaction
  - Support for Daily, Weekly, Monthly, and Yearly interest cycles
  - Simple interest calculation (Principal × Rate × Time)
  - Automatic interest accrual when app opens
- **Outstanding Balance Tracking**: Real-time calculation of outstanding amounts per customer
- **Offline-First**: All features work offline; data syncs when online
- **Modern UI**: Built with Jetpack Compose and Material Design 3

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose

## Setup Instructions

1. **Clone the repository** (if applicable) or navigate to the project directory

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory

3. **Sync Gradle**
   - Android Studio should automatically sync Gradle
   - If not, click "Sync Now" when prompted

4. **Build the project**
   - Click "Build" → "Make Project" or press `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)

5. **Run on device/emulator**
   - Connect an Android device or start an emulator (API 24+)
   - Click "Run" → "Run 'app'" or press `Shift+F10`

## Project Structure

```
app/src/main/java/com/byajpay/app/
├── data/
│   ├── dao/              # Room DAOs for database access
│   ├── database/          # Room database setup
│   ├── model/             # Data models (User, Customer, Transaction, InterestRule)
│   └── repository/        # Repository layer for data operations
├── di/                    # Dependency injection modules
├── ui/
│   ├── navigation/        # Navigation setup
│   ├── screen/            # UI screens
│   ├── theme/             # App theme
│   └── viewmodel/         # ViewModels for UI logic
└── MainActivity.kt        # Main activity
```

## Usage

### First Time Setup

1. **Login/Signup**
   - Enter your 10-digit phone number
   - Click "Send OTP"
   - Enter the 6-digit OTP (currently accepts any 6-digit code for MVP)
   - Optionally enter your name and business name

2. **Add Customers**
   - Tap the "+" button on the home screen
   - Enter customer name (required)
   - Optionally add phone number and notes
   - Tap "Add Customer"

3. **Add Transactions**
   - Navigate to a customer's detail page
   - Tap the "+" button
   - Enter amount
   - Select transaction type (Credit/Debit)
   - For Credit transactions, optionally enable interest:
     - Toggle "Charge Interest"
     - Enter interest rate (%)
     - Select interest cycle (Daily/Weekly/Monthly/Yearly)
   - Add notes (optional)
   - Tap "Add Transaction"

### Interest Calculation

- Interest is calculated using **Simple Interest**: `Principal × Rate × Time / 100`
- Interest accrues automatically when you open the app
- Interest entries appear in the ledger with "Interest Added" label
- Each interest entry updates the outstanding balance

### Viewing Data

- **Home Screen**: Shows total outstanding balance across all customers
- **Customer List**: Browse all customers with their outstanding balances
- **Customer Detail**: View complete ledger with all transactions and current balance

## Current Limitations (MVP)

- OTP verification is simulated (accepts any 6-digit code)
- No cloud sync implemented (offline-first, ready for sync integration)
- No data export/backup UI (database is stored locally)
- Interest accrual happens on app open (not in background)

## Future Enhancements

- Real OTP integration (SMS gateway)
- Cloud backup and sync
- WhatsApp/SMS reminders
- PDF export
- Multiple user support
- Analytics dashboard
- Compound interest option

## Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Java Version**: 17

## License

[Add your license here]

## Support

For issues or questions, please [create an issue](link-to-issues) or contact the development team.

