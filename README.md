# ReactChess

ReactChess is a chess implementation in React and Kotlin/JS. It uses bitboards to store chess positions and fancy magic bitboards for efficient move generation.

Demonstration at https://jmccarthy.au/playground/ReactChess/

This chess implementation uses [BitBoards](https://www.chessprogramming.org/Bitboards) for storing the state of the chess position and [Magic BitBoards](https://www.chessprogramming.org/Magic_Bitboards) for move generation of sliding pieces. This technique dramatically decrease the computational time of move generation at the small cost of precomputing move bitboards for sliding pieces and storing them in memory.

![image](https://github.com/user-attachments/assets/78ee50a0-c196-4dda-9c61-0e764cc2aaf7)
