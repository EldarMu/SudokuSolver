import java.util.LinkedList;
import java.util.List;

/**
 * Created by eldar on 7/12/2018.
 */
public class SudokuBoard {
    public int[][] intBoard;
    public SudokuCell[][] cellBoard;
    public SudokuCell[][] innerBoxes;

    public SudokuBoard(int[][] intBoard){
        this.intBoard = intBoard;
        cellBoard = constructBoard();
        innerBoxes = fillInnerBoxes();
        populatePotentialValuesLists();
    }
    private SudokuCell[][] constructBoard(){
        SudokuCell[][] sudBoard = new SudokuCell[9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                sudBoard[i][j] = new SudokuCell(intBoard[i][j], i, j, innerBoxNumber(i,j));
            }
        }
        return sudBoard;
    }

    //return the inner 3x3 sudoku box in which this index lies
    //with the top left being box 0, and the bottom right being box 8
    private int innerBoxNumber(int row, int column){
        //using the fact that ints round down
        int colQuad = column/3;
        int rowQuad = row/3;
        return(rowQuad*3+colQuad);
    }

    //a second representation needed to keep track of which numbers have already occured in each inner box
    private SudokuCell[][] fillInnerBoxes() {
        SudokuCell[][] innerBoxes = new SudokuCell[9][9];
        int[][] centerCoords = {{1, 1}, {4, 1}, {7, 1}, {1, 4}, {4, 4}, {7, 4}, {1, 7}, {4, 7}, {7, 7}};
        for (int i = 0; i < 9; i++) {
            int centerY = centerCoords[i][0];
            int centerX = centerCoords[i][1];
            int counter = 0;
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    innerBoxes[i][counter] = cellBoard[centerX + k][centerY + j];
                    counter++;
                }
            }
        }
        return innerBoxes;
    }
    private void populatePotentialValuesLists(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                SudokuCell curCell = cellBoard[i][j];
                if(curCell.currentVal!=0){
                    continue;
                }
                List<Integer> options = new LinkedList<>();
                for(int k = 1; k < 10; k++){
                    options.add(k);
                }
                for(int k = 0; k < 9; k++){
                    if(options.contains(cellBoard[curCell.row][k].currentVal)){
                        options.remove(options.indexOf(cellBoard[curCell.row][k].currentVal));
                    }
                    if(options.contains(cellBoard[k][curCell.column].currentVal)){
                        options.remove(options.indexOf(cellBoard[k][curCell.column].currentVal));
                    }
                    if(options.contains(innerBoxes[curCell.quadrant][k].currentVal)){
                        options.remove(options.indexOf(innerBoxes[curCell.quadrant][k].currentVal));
                    }
                }
                curCell.possibleVals = options;
            }
        }
    }

    //called when a value is assigned to one of the cells
    //removes the value assigned from the possible values lists of the cells in its row, column and box
    //also updates the int representation of the sudoku board
    public void updatePossibleValuesLists(SudokuCell cell){
        for(int i = 0; i < 9; i++){
            SudokuCell sameColCell = cellBoard[i][cell.column];
            SudokuCell sameRowCell = cellBoard[cell.row][i];
            SudokuCell sameBoxCell = innerBoxes[cell.quadrant][i];
            if(sameColCell.possibleVals.contains(cell.currentVal)){
                sameColCell.possibleVals.remove(sameColCell.possibleVals.indexOf(cell.currentVal));
            }
            if(sameRowCell.possibleVals.contains(cell.currentVal)){
                sameRowCell.possibleVals.remove(sameRowCell.possibleVals.indexOf(cell.currentVal));
            }
            if(sameBoxCell.possibleVals.contains(cell.currentVal)){
                sameBoxCell.possibleVals.remove(sameBoxCell.possibleVals.indexOf(cell.currentVal));
            }
        }
    }
}
