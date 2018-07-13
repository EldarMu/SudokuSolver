import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import static javax.swing.SwingConstants.CENTER;

//A 9x9 board of text fields
//Upon pressing the solve button, will attempt to solve the provided sudoku puzzle
//Will fill whichever cells it did manage to solve, even if it ultimately fails to complete the puzzle
public class SolverMenu extends JFrame{
    private JPanel pan;
    private JTextField[][] fields;
    private JButton solveBtn;
    private GridBagConstraints c1;
    private Font fnt;

    public static void main(String[] args){
        SolverMenu solver = new SolverMenu();
    }
    public SolverMenu(){
        super("SolverMenu");
        setSize(400,450);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        c1 = new GridBagConstraints();
        pan = new JPanel();
        pan.setLayout(new GridBagLayout());
        fnt = new Font("Arial Bold", Font.BOLD, 30);
        fields = new JTextField[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField field = new JTextField();
                field.setFont(fnt);
                field.setHorizontalAlignment(CENTER);
                c1.gridx = j;
                c1.gridy = i;
                c1.ipadx=60;
                c1.ipady=50;
                c1.weightx = 1;
                c1.weighty = 1;
                fields[i][j]=field;
                pan.add(fields[i][j], c1);
            }
        }
        solveBtn = new JButton();
        solveBtn.setText("Solve Puzzle");
        solveBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int[][] sudokuVals = new int[9][9];
                boolean validVals = true;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        String curText = fields[i][j].getText();
                        boolean validNum = curText.length() == 1 && Character.isDigit(curText.charAt(0));
                        if(!validNum && !curText.isEmpty()){
                            JOptionPane.showMessageDialog(new JFrame(), "please enter real sudoku numbers," +
                                    " or leave a line blank");
                            return;
                        }
                        if (curText.isEmpty()) {
                            sudokuVals[i][j] = 0;
                        } else if (validNum) {
                            sudokuVals[i][j] = Character.getNumericValue(curText.charAt(0));
                        }
                    }
                }
                SolverAlgorithm slv = new SolverAlgorithm();
                SudokuBoard result = slv.solveSudoku(sudokuVals, 0);
                if (!result.solveSuccess) {
                    JOptionPane.showMessageDialog(new JFrame(),"Could not solve Puzzle");
                }
                for (int i = 0; i < 9; i++){
                    for (int j = 0; j < 9; j++){
                        if(result.intBoard[i][j]!=0){
                            fields[i][j].setText(Integer.toString(result.intBoard[i][j]));
                        }
                    }
                }
            }
        });
        c1.gridx = 3;
        c1.gridy = 9;
        c1.gridwidth = 3;
        pan.add(solveBtn, c1);

        add(pan, 0);
        setVisible(true);
    }
}
