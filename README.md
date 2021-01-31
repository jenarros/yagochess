# Yet Another Game of Chess
Run with:
```
./gradlew run
```
## TODO
- Should I have a Set containing each of the sets? 
- Should restart reset to defaults or to current options?
- Checkmate due to repeated configuration 3 times

## Interesting implementation areas
### UI
#### Java Swing
### Board
Functional vs stateful
### Moves
#### Validation
If user moves, is that move correct?
#### Generation
Given a particular board, what are the possible moves?
#### Undo
Stateful vs stateless considerations, it seems that if the board is immutable, the game becomes much slower.
### Artificial Intelligence
#### alpha-beta algorithm