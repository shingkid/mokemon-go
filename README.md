# MokemonGo
Inspired by Pokémon Go and my lecturer Mok's task to explore concurrency programming, this project simulates a special event where multiple trainers can battle a legendary Mokemon. Each time a trainer instructs his Mokemon to attack the legendary, there is a chance that the legendary may return blows. When the legendary Mokemon has been defeated, the player with a Mokemon that has not fainted and who dealt the most cumulative damage wins. When attacking an opponent, both Mokemons’ types have an effect on the damage the opponent receives.

## Overview
The `MokemonData` class contains standard Mokemon data loaded from `pokemon.csv` in `Mokedex.java`. `Mokemon` extends from `MokemonData` and `Legendary` is a subclass of `Mokemon`.

## Setup
If on Windows, simply compile with `compile.bat` and run with `run.bat`.

Otherwise, from the command line:
```
// Compile
javac -d . *.java

// Run
java aa.MokemonGo
```

## Acknowledgments
All Pokémon content belongs to Nintendo.

The dataset was obtained from https://www.kaggle.com/abcsds/pokemon and slightly modified for the purposes of this project.

Pokémon ASCII art was adapted from [MatheusFaria's gist](https://gist.github.com/MatheusFaria/4cbb8b6dbe33fd5605cf8b8f7130ba6d).

`LockFactory.java` and `StopWatch.java` were originally written by Kevin Steppe and later edited by Mok.