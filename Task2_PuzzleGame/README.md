# Simple Puzzle Game

**Project Name**: Simple Puzzle Game  
**Developer**: Pratap Singh

## Overview

This is a simple 3x3 sliding puzzle game where users rearrange shuffled image tiles to form a complete picture. The game provides a fun and interactive experience while also helping beginners learn the basics of Android development, particularly with Kotlin and XML. The puzzle grid consists of 9 pieces, one of which is always empty, allowing users to swap adjacent tiles to solve the puzzle.

## Features

- **Random Image Selection**: The game loads a random image each time it starts or is reset. The images are divided into 9 pieces, with one empty tile.
- **Dynamic Puzzle Grid**: The puzzle is presented in a 3x3 grid layout. Players can move tiles by tapping on them to swap with the adjacent empty tile.
- **Change and Reset**: The user can reset the puzzle or change the image at any time using the buttons provided.
- **Steps Counter**: The game tracks the number of steps a user takes to solve the puzzle and displays the count in a custom winner dialog box.
- **Image Preview**: A preview of the selected puzzle image is shown to help players visualize the final solution.
- **Automatic Shuffle**: Once the puzzle is solved, a new random image is automatically loaded, and the puzzle reshuffles for continuous gameplay.

## Game Logic

1. **Random Image Loading**: At the start of the game, an image is selected randomly from a predefined set of images.
2. **Splitting the Image**: The selected image is divided into 9 equal parts (3x3 grid) where the last piece remains empty.
3. **Tile Movement**: Users can click on any tile that has an adjacent empty space to swap their positions.
4. **Win Condition**: The game checks if all the tiles are arranged in their correct order. If the player solves the puzzle, a dialog is shown with the number of steps taken.
5. **Shuffling**: After winning, the game automatically reshuffles the puzzle pieces with a new image.

## Screenshots
_Add screenshots of your game in this section to visually represent how the game looks._

## How to Run the Project

1. Clone this repository:  
   `git clone https://github.com/username/simple-puzzle-game.git`
   
2. Open the project in Android Studio.
3. Build and run the project on an emulator or physical device.

## Tools and Technologies

- **Language**: Kotlin
- **Framework**: Android SDK
- **Design**: XML Layouts
- **Image Processing**: Bitmap manipulation for splitting images

## How the Puzzle Works

- The puzzle consists of a 3x3 grid where 8 of the pieces are visible, and 1 piece is always empty.
- Players can swap tiles by tapping on a tile next to the empty space.
- The objective is to arrange the tiles back to their original order.
- After solving, the game shuffles a new image and resets.

## Future Improvements

- Add a timer to challenge players to complete the puzzle faster.
- Implement different grid sizes (e.g., 4x4 or 5x5 grids).
- Include sound effects or animations for a more interactive experience.

## Contact

For any inquiries, feel free to reach out to me via email at prataprj.personal@gmail.com.

