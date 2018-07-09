import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by eldar on 7/8/2018.
 */

//though it won't crack Arto Inkala's "World's Hardest Sudoku", it beats anything else I throw at it.
public class SolverAlgorithm {
    public SudokuSolution solveSudoku(int[][] sudokuBoard){
        List<SudokuCell> disqualifiedCells = new LinkedList<>();
        return recursSolver(sudokuBoard);
    }
    private SudokuSolution recursSolver(int[][] sudokuBoard) {
        SudokuCell[][] sudokuCellBoard = constructBoard(sudokuBoard);
        SudokuCell[][] innerBoxes = fillInnerBoxes(sudokuCellBoard);
        boolean lastScanSolvedACell = true;
        boolean allValsFilled = false;
        List<SudokuCell> uncertainCells = new LinkedList<>();
        while (lastScanSolvedACell && !allValsFilled) {
            lastScanSolvedACell = false;
            allValsFilled = true;
            uncertainCells = new LinkedList<>();
            //basic scan of "only one value can go here"
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if(sudokuCellBoard[i][j].currentVal==0){
                        allValsFilled = false;
                        List<Integer> possibleVals = calculatePossibleCellVals(sudokuCellBoard[i][j],
                                sudokuCellBoard, innerBoxes);
                        if(possibleVals.size()==0){
                            return new SudokuSolution(sudokuBoard, false);
                        }
                        else if(possibleVals.size()==1){
                            sudokuCellBoard[i][j].currentVal = possibleVals.get(0);
                            sudokuCellBoard[i][j].possibleVals = new LinkedList<Integer>();
                            lastScanSolvedACell = true;
                        }
                        else{
                            sudokuCellBoard[i][j].possibleVals = possibleVals;
                            uncertainCells.add(sudokuCellBoard[i][j]);
                        }
                    }
                }
            }
            //follow-up scan of "only place where a value can go"
            //needs to only run if no cell was modified prior,
            //to ensure potential cell values match the situation on the board
            if(!lastScanSolvedACell){
                for(int i = 0; i < 9; i++){
                    SudokuCell[] colSlice = new SudokuCell[9];
                    SudokuCell[] boxSlice = new SudokuCell[9];
                    SudokuCell[] rowSlice = new SudokuCell[9];
                    for(int j = 0; j < 9; j++){
                        colSlice[j] = sudokuCellBoard[j][i];
                        boxSlice[j] = innerBoxes[i][j];
                        rowSlice[j] = sudokuCellBoard[i][j];
                    }
                    if(checkIfOnlyPossiblePlaceForValue(colSlice)){
                        lastScanSolvedACell=true;
                        continue;
                    }
                    if(checkIfOnlyPossiblePlaceForValue(rowSlice)){
                        lastScanSolvedACell=true;
                        continue;
                    }
                    if(checkIfOnlyPossiblePlaceForValue(boxSlice)){
                        lastScanSolvedACell=true;
                        continue;
                    }
                }
            }
        }
        if(allValsFilled){
            return new SudokuSolution(deconstructBoard(sudokuCellBoard), true);
        }
        if(!lastScanSolvedACell){
            Collections.sort(uncertainCells);
            for(SudokuCell c : uncertainCells){
                for(Integer val: c.possibleVals){
                    int[][] boardWithGuess = deconstructBoard(sudokuCellBoard);
                    boardWithGuess[c.row][c.column] = val;
                    SudokuSolution valueGuess = recursSolver(boardWithGuess);
                    if(valueGuess.solveSuccess){return valueGuess;}
                }
            }
        }
        return new SudokuSolution(deconstructBoard(sudokuCellBoard), false);
    }

    private boolean checkIfOnlyPossiblePlaceForValue(SudokuCell[] rowColOrBox){
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
                if(rowColOrBox[i].possibleVals.contains(soloVal)){
                    rowColOrBox[i].currentVal = soloVal;
                    rowColOrBox[i].possibleVals = new LinkedList<>();
                    return true;
                }
            }
        }
        return false;
    }

    private int[][] deconstructBoard(SudokuCell[][] board){
        int[][] deconstructedBoard = new int[9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j<9; j++){
                deconstructedBoard[i][j] = board[i][j].currentVal;
            }
        }
        return deconstructedBoard;
    }

    private SudokuCell[][] constructBoard(int[][] sudokuBoard){
        SudokuCell[][] sudBoard = new SudokuCell[9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                sudBoard[i][j] = new SudokuCell(sudokuBoard[i][j], i, j, innerBoxNumber(i,j));
            }
        }
        return sudBoard;
    }

    //a second representation needed to keep track of which numbers have already occured in each inner box
    private SudokuCell[][] fillInnerBoxes(SudokuCell[][] sudokuBoard) {
        SudokuCell[][] innerBoxes = new SudokuCell[9][9];
        int[][] centerCoords = {{1, 1}, {4, 1}, {7, 1}, {1, 4}, {4, 4}, {7, 4}, {1, 7}, {4, 7}, {7, 7}};
        for (int i = 0; i < 9; i++) {
            int centerY = centerCoords[i][0];
            int centerX = centerCoords[i][1];
            int counter = 0;
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    innerBoxes[i][counter] = sudokuBoard[centerX + k][centerY + j];
                    counter++;
                }
            }
        }
        return innerBoxes;
    }

    //return the inner 3x3 sudoku box in which this index lies
    //with the top left being box 0, and the bottom right being box 8
    private int innerBoxNumber(int row, int column){
        //using the fact that ints round down
        int colQuad = column/3;
        int rowQuad = row/3;
        return(rowQuad*3+colQuad);
    }

    private List<Integer> calculatePossibleCellVals(SudokuCell curCell, SudokuCell[][] board, SudokuCell[][] innerBoxes){
        List<Integer> options = new LinkedList<>();
        for(int i = 1; i < 10; i++){
            options.add(i);
        }
        for(int i = 0; i < 9; i++){
            if(options.contains(board[curCell.row][i].currentVal)){
                options.remove(options.indexOf(board[curCell.row][i].currentVal));
            }
            if(options.contains(board[i][curCell.column].currentVal)){
                options.remove(options.indexOf(board[i][curCell.column].currentVal));
            }
            if(options.contains(innerBoxes[curCell.quadrant][i].currentVal)){
                options.remove(options.indexOf(innerBoxes[curCell.quadrant][i].currentVal));
            }
        }
        return options;
    }

    class SudokuCell implements Comparable<SudokuCell>{
        List<Integer> possibleVals;
        int currentVal;
        int row;
        int column;
        int quadrant;
        SudokuCell(int currentVal, int row, int column, int quadrant){
            this.currentVal = currentVal;
            this.row = row;
            this.column = column;
            this.quadrant = quadrant;
            possibleVals = new LinkedList<>();
        }
        public int compareTo(SudokuCell differentCell)
        {
            return this.possibleVals.size() - differentCell.possibleVals.size();
        }
    }
}
