package sudoku;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import sudoku.Sudoku.ParseException;


public class SudokuTest {
    

    // make sure assertions are turned on!  
    // we don't want to run test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test
    public void testReadFile() throws IOException, ParseException {
    	Sudoku testSudoku = Sudoku.fromFile(3, "samples/sudoku_easy.txt");
    	System.out.println(testSudoku);
    }
    
}