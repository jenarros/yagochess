# Yet Another Game of Chess
Run with:
```
./gradlew run
```

## Interesting implementation areas

### UI

#### Java Swing

### Board

Balancing readability and performance Use immutable structures as much as possible but avoid making unnecessary copies
of objects. Wrap positions into Square objects for readability Keep positions in array for performance

Optimised for accessing the current value of a square means using a stateful board. If we used a board backed by "
moves" (event sourced), it would be too slow to access the current position of a square.

### Moves

#### Validation

If user moves, is that move correct?

#### Generation

Given a particular board, what are the possible moves?

#### Undo

Stateful vs stateless considerations, it seems that if the board is immutable, the game becomes much slower.

### Artificial Intelligence

#### alpha-beta algorithm

## TODO

### Distribution

- Configure github build

### Implementation

- Cleanup alphabeta implementation
- Make alphabeta iterative

### Performance

- Review if additional use of @JvmStatic and @JvmField is needed

### Gameplay

- Should restart reset to defaults or to current options?
- Checkmate due to repeated configuration 3 times
