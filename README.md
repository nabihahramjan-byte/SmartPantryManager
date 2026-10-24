# Smart Pantry Manager

An Android app written in **Java** that helps reduce food waste. The user records the ingredients they
have at home and the app suggests only the recipes they can cook right now, using strictly what is already in the pantry - no shopping trip required.

## Features
- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Pantry list screen (RecyclerView with a custom adapter, loaded from the database)
- 22 pre-loaded recipes (seeded the first time the app runs)
- **Suggested Recipes** screen using the strict-matching rule: a recipe is shown only if *every* ingredient is in the pantry in at least the required quantity
- Matching copes with simple differences ("tomato" vs "tomatoes", g vs kg, ml vs l)
- Recipe detail screen (ingredients and method)
- Settings screen (expiring-soon alerts on/off, sort pantry by name or expiry date)
- Friendly message when no recipes match
- Input validation on the Add/Edit form

## Database choice: SQLite
I chose SQLite (using `SQLiteOpenHelper`) because it is built into Android, stores the data on the device so it works offline and needs no accounts, servers or internet connection. Data still stays the same after the app is closed and reopened.

Tables: `ingredient`, `recipe`, `recipe_ingredient`.

## Setup / run instructions
1. Install [Android Studio](https://developer.android.com/studio).
2. Clone this repository: `git clone https://github.com/nabihahramjan-byte/SmartPantryManager`
3. In Android Studio choose **File > Open** and select the project folder. Wait for Gradle to finish syncing.
4. Create an emulator (**Tools > Device Manager**) or connect an Android phone with USB debugging on.
5. Press the green **Run** button.

## Not included (out of scope)
No Google Maps, mapping SDKs or GPS/location features are used.

## Author
Nabihah Ramjan
Studnet number - 402310002
