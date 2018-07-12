import java.util.LinkedList;
import java.util.List;

//A class representing one sudoku cell
//for the sake of not searching for each cell every time we need it,
//the cell stores its indexes in the two data structures that store it
//also stores a list of potential cell values, should the cell be unfilled.
public class SudokuCell implements Comparable<SudokuCell>{
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
