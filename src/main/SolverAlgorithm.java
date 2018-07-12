import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eldar on 7/8/2018.
 */

//though it won't crack Arto Inkala's "World's Hardest Sudoku", it beats anything else I throw at it.
public class SolverAlgorithm {
    //if we have to guesstimate this many values while already penciling in,
    //the odds of the result being the right one are very low
    //However, it may be possible to create such a sudoku that deep guesses are required
    private static final int MAX_STACK_DEPTH = 3;

    public SudokuSolution solveSudoku(int[][] board, int stackLayer){
        SudokuBoard sudokuBoard = new SudokuBoard(board);
        if(stackLayer>=MAX_STACK_DEPTH){return new SudokuSolution(sudokuBoard.intBoard,false);}
        boolean lastScanSolvedACell = true;
        boolean allValsFilled = false;
        List<SudokuCell> uncertainCells = new LinkedList<>();

        while (lastScanSolvedACell && !allValsFilled) {
            lastScanSolvedACell = false;
            allValsFilled = true;
            uncertainCells = new LinkedList<>();
            //basic scan of "if there's only one value possible in this cell, write it in"
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    SudokuCell curCell = sudokuBoard.cellBoard[i][j];
                    if(curCell.currentVal==0){
                        if(curCell.possibleVals.size()==0){
                            return new SudokuSolution(sudokuBoard.intBoard, false);
                        }
                        else if(curCell.possibleVals.size()==1){
                            curCell.currentVal = curCell.possibleVals.get(0);
                            curCell.possibleVals.clear();
                            sudokuBoard.intBoard[i][j] = curCell.currentVal;
                            sudokuBoard.updatePossibleValuesLists(curCell);
                            lastScanSolvedACell = true;
                        }
                        else{
                            allValsFilled = false;
                        }
                    }
                }
            }
            //for each row, column, and 3x3 box
            //see if there's any value that hasn't been put in, that can only go in one place
            //the previous loop checked if a cell had only one possible value
            //this loop checks if any possible value in a row/col/box can go into only one cell
            for(int i = 0; i < 9; i++){
                SudokuCell[] colSlice = new SudokuCell[9];
                SudokuCell[] boxSlice = new SudokuCell[9];
                SudokuCell[] rowSlice = new SudokuCell[9];
                for(int j = 0; j < 9; j++){
                    colSlice[j] = sudokuBoard.cellBoard[j][i];
                    boxSlice[j] = sudokuBoard.innerBoxes[i][j];
                    rowSlice[j] = sudokuBoard.cellBoard[i][j];
                }
                if(checkIfOnlyPossiblePlaceForValue(colSlice, sudokuBoard)){
                    lastScanSolvedACell=true;
                    continue;
                }
                if(checkIfOnlyPossiblePlaceForValue(rowSlice, sudokuBoard)){
                    lastScanSolvedACell=true;
                    continue;
                }
                if(checkIfOnlyPossiblePlaceForValue(boxSlice, sudokuBoard)){
                    lastScanSolvedACell=true;
                    continue;
                }
            }
        }

        if(allValsFilled){
            return new SudokuSolution(sudokuBoard.intBoard, true);
        }

        //find any cells with several possible values possible in them
        //then sort that list by the ones that have the least options
        //and "pencil them in"
        //(aka run the method recursively as if they were valid values, and see if it returns false)
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                SudokuCell curCell = sudokuBoard.cellBoard[i][j];
                if(curCell.possibleVals.size()>1){
                    uncertainCells.add(curCell);
                }
            }
        }
        Collections.sort(uncertainCells);

        for(SudokuCell c : uncertainCells){
            for(Integer val: c.possibleVals){
                int[][] boardWithGuess = new int[9][9];
                for(int i = 0; i < 9; i++){
                    for(int j = 0; j < 9; j++){
                        boardWithGuess[i][j] = sudokuBoard.intBoard[i][j];
                    }
                }
                boardWithGuess[c.row][c.column] = val;
                SudokuSolution valueGuess = solveSudoku(boardWithGuess, stackLayer+1);
                if(valueGuess.solveSuccess){return valueGuess;}
            }
        }
        return new SudokuSolution(sudokuBoard.intBoard, false);
    }

    private boolean checkIfOnlyPossiblePlaceForValue(SudokuCell[] rowColOrBox, SudokuBoard cellBoard){
        List<Integer> possVals = new LinkedList<>();
        for(int i = 1; i < 10; i++){
            possVals.add(i);
        }
        //remove any values that are already located in this row/column/box
        for(int i = 0; i < 9; i++){
            if(possVals.contains(rowColOrBox[i].currentVal)){
                possVals.remove(possVals.indexOf(rowColOrBox[i].currentVal));
            }
        }
        //find any such possible values that can only be in one position
        //and write those values in as the only option
        List<Integer> onePlaceOnly = new LinkedList<>();
        int counter = 0;
        for(Integer val : possVals){
            for(int i = 0; i < 9; i++){
                if(rowColOrBox[i].possibleVals.contains(val)){
                    counter++;
                }
            }
            if(counter==1){onePlaceOnly.add(val);}
            counter = 0;
        }
        for(Integer soloVal : onePlaceOnly){
            for(int i = 0; i < 9; i++){
                SudokuCell curCell = rowColOrBox[i];
                if(curCell.possibleVals.contains(soloVal)){
                    curCell.currentVal = soloVal;
                    cellBoard.intBoard[curCell.row][curCell.column] = soloVal;
                    cellBoard.updatePossibleValuesLists(curCell);
                    rowColOrBox[i].possibleVals = new LinkedList<>();
                    return true;
                }
            }
        }
        return false;
    }
}
