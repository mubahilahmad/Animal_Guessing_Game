```markdown
# Animal Guessing Game

Animal Guessing Game is a fun and interactive Android game where players guess the names of animals based on hints. It features different modes, including a predator mode for more challenging gameplay.

## Features

- Multiple game modes: guess animals or predators (specific animals for predator lovers).
- Hints to help you guess the animal.
- Tracks scores and keeps a history of the last 5 scores.
- User-friendly interface with a playful design.

## Installation

To get started with the Animal Guessing Game, follow these steps:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/mubahilahmad/animal_guessing_game.git
   ```
2. **Open the project in Android Studio:**
   - Select `File -> Open` and choose the cloned repository folder.
3. **Build the project:**
   - Click on `Build -> Make Project` or use the shortcut `Ctrl+F9`.
4. **Run the app:**
   - Connect an Android device or start an emulator.
   - Click on `Run -> Run 'app'` or use the shortcut `Shift+F10`.

## Usage

1. **Starting a game:**
   - Open the app and select the number of animals or predators you want to guess.
2. **Playing the game:**
   - Read the hints and enter your guess.
   - You can ask for more hints or skip to the next animal if you're stuck.
   - Your score will be displayed at the end of the game.

## Code Overview

### `Animal.kt`
Defines the `Animal` data class which implements the `AnimalInterface`. Contains a list of animal objects with their respective hints.

### `Predator.kt`
Defines the `Predator` data class which implements the `AnimalInterface`. Contains a list of predator objects with their respective hints.

### `AnimalInterface.kt`
Defines the `AnimalInterface` with properties for the name and hints of the animal or predator.

### `RoundActivity.kt`
Handles the selection of game mode and number of animals or predators to guess.

### `MainActivity.kt`
Contains the main gameplay logic where users guess the animal based on hints provided.

### `ScoreActivity.kt`
Displays the final score and allows users to save their score with a note. Also provides options to start a new game or view the profile.

### `ProfileActivity.kt`
Displays the last 5 scores saved by the user.

### `manifest.xml`
Configures the app’s activities and metadata.

### Layouts

#### `activity_round.xml`
Defines the layout for `RoundActivity` where the user selects the number of animals or predators to guess.

#### `activity_main.xml`
Defines the main gameplay layout where the user guesses the animals based on hints.

#### `activity_score.xml`
Defines the layout for `ScoreActivity` where the final score is displayed, and the user can save their score or start a new game.

#### `activity_profile.xml`
Defines the layout for `ProfileActivity` where the user's past scores are displayed.

## Data Storage

The app uses Android's DataStore for saving the user's score history. Scores are stored as JSON strings and only the last 5 entries are kept.

Have fun playing :-)
