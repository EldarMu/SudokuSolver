//A simple container returning the sudoku board
//And a boolean to indicate whether it managed to solve the puzzle
public class SudokuSolution{
    public int[][] sudokuBoard;
    public boolean solveSuccess;
    SudokuSolution(int[][] sudokuBoard, boolean solveSuccess){
        this.sudokuBoard = sudokuBoard;
        this.solveSuccess = solveSuccess;
    }
}
