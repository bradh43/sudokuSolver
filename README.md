# Sudoku Solver
Random side project I coded while on an airplane. Was inspired by the sudoku puzzle found in the magazine in the seat in front of me. This was a fun challenge and it made the plane ride go by quick.

## How it works
The program works by reading in a text file. The first line should contain the square board size. The following lines should contain the number found in its corresponding square, with a 0 if the square is black. A sample text file would look like:

```
9
9 0 0 0 0 0 0 0 1
0 5 1 2 0 4 9 7 0
0 8 4 0 0 0 6 5 0
0 4 0 9 0 5 0 8 0
0 0 0 0 7 0 0 0 0
0 7 0 8 0 6 0 2 0
0 1 8 0 0 0 2 3 0
0 6 3 4 0 1 7 9 0
7 0 0 0 0 0 0 0 6
```

After the program has loaded the board into its structure the program starts to solve the board. Each square keeps track of the number located there, if its blank, its display, if is solved, and all the possible numbers it could be. The first sweep updates each squares possible values by looping though each horizontal and vertical line, as well as the box it is in.

The second sweep is a little bit more advanced and it will go through and updates the board preception. This is done by checking each spot to see if there is a number that is possible in that position but no where else in the same row, col, or box.

The third sweep uses an advance technique of sudoku solving by comparing subsets to see if it can find a hidden subset or naked subset. This done by checking each possible subset of size n to see if it occurs exactly n times. Any where the hidden subset occured we can keep the values in that subset and remove all the other possibilities. 

The fourth sweep only occurs if it detects the previous three sweep were unable to make any updates in solving the board. This is done by comparing the hash of the board before and after the three sweeps. If there is no changes it will force a chain, which is another advance technique of temporarily placing a piece and trying to solve it and see if it is possible to solve if that piece were placed there.

The sweeps keep being called util the board is solved. The final output look like this:
```
+---------+---------+---------+
| 7  5  4 | 1  3  6 | 8  9  2 |
| 6  2  1 | 9  8  4 | 7  5  3 |
| 3  9  8 | 7  5  2 | 4  6  1 |
+---------+---------+---------+
| 2  8  5 | 4  9  1 | 6  3  7 |
| 1  4  3 | 8  6  7 | 5  2  9 |
| 9  6  7 | 5  2  3 | 1  8  4 |
+---------+---------+---------+
| 5  1  2 | 6  4  9 | 3  7  8 |
| 8  7  9 | 3  1  5 | 2  4  6 |
| 4  3  6 | 2  7  8 | 9  1  5 |
+---------+---------+---------+
Board Solved!

```



