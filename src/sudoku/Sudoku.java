/**
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sudoku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

/**
 * Sudoku is an immutable abstract datatype representing instances of Sudoku.
 * Each object is a partially completed Sudoku puzzle.
 */
public class Sudoku {
    // dimension: standard puzzle has dim 3
    private final int dim;
    // number of rows and columns: standard puzzle has size 9
    private final int size;
    // known values: square[i][j] represents the square in the ith row and jth
    // column,
    // contains -1 if the digit is not present, else i>=0 to represent the digit
    // i+1
    // (digits are indexed from 0 and not 1 so that we can take the number k
    // from square[i][j] and
    // use it to index into occupies[i][j][k])
    private final int[][] square;
    // occupies [i,j,k] means that kth symbol occupies entry in row i, column j
    private final Variable[][][] occupies;

    // Rep invariant
    // square and occupies != null
    // no element of square has value less than -1 or greater than or equal to size
	private void checkRep() {
        assert this.square != null : "Sudoku, Rep invariant: square non-null";
        assert this.occupies != null : "Sudoku, Rep invariant: occupies non-null";
        for (int i = 0; i<size; ++i) {
        	for (int j = 0; j<size; ++j) {
        		assert this.square[i][j] >= -1 : "Sudoku, Rep invariant: square value greater than or equal to -1";
        		assert this.square[i][j] < size : "Sudoku, Rep invariant: square value less than size";
        	}
        }
    }

    /**
     * create an empty Sudoku puzzle of dimension dim.
     * 
     * @param dim
     *            size of one block of the puzzle. For example, new Sudoku(3)
     *            makes a standard Sudoku puzzle with a 9x9 grid.
     */
    public Sudoku(int dim) {
    	this.dim = dim;
    	this.size = dim * dim;
    	this.square = new int[size][size];
    	
    	// Create a board in which each square is unoccupied
    	for (int i =0; i<size; ++i) {
    		for (int j = 0; j<size; ++j) {
    			this.square[i][j] = -1;
    		}
    	}
    	
    	// Create new variable objects corresponding to each square on the Sudoku board
    	this.occupies = new Variable[size][size][size];
    	for (int i = 0; i<size; ++i) {
    		for (int j = 0; j<size; ++j) {
    			for (int k = 0; k<size; ++k) {
    				this.occupies[i][j][k] = new Variable(i + "," + j + "," + k); 
    			}
    		}
    	}
    	checkRep();
    }

    /**
     * create Sudoku puzzle
     * 
     * @param square
     *            digits or blanks of the Sudoku grid. square[i][j] represents
     *            the square in the ith row and jth column, contains 0 for a
     *            blank, else i to represent the digit i. So { { 0, 0, 0, 1 }, {
     *            2, 3, 0, 4 }, { 0, 0, 0, 3 }, { 4, 1, 0, 2 } } represents the
     *            dimension-2 Sudoku grid: 
     *            
     *            ...1 
     *            23.4 
     *            ...3
     *            41.2
     * 
     * @param dim
     *            dimension of puzzle Requires that dim*dim == square.length ==
     *            square[i].length for 0<=i<dim.
     */
    public Sudoku(int dim, int[][] square) {
    	this.dim = dim;
    	this.size = dim * dim;
    	this.square = new int[size][size];
    	
    	// Create a board in which squares are occupied as given in input parameter square
    	for (int i = 0; i<size; ++i) {
    		for (int j = 0; j<size; ++j) {
    			this.square[i][j] = square[i][j] - 1;
    		}
    	}
    	
    	// Create new variable objects corresponding to each square on the Sudoku board
    	this.occupies = new Variable[size][size][size];
    	for (int i = 0; i<size; ++i) {
    		for (int j = 0; j<size; ++j) {
    			for (int k = 0; k<size; ++k) {
    				this.occupies[i][j][k] = new Variable(i + "," + j + "," + k); 
    			}
    		}
    	}
    	checkRep();
    }

    /**
     * Reads in a file containing a Sudoku puzzle.
     * 
     * @param dim
     *            Dimension of puzzle. Requires: at most dim of 3, because
     *            otherwise need different file format
     * @param filename
     *            of file containing puzzle. The file should contain one line
     *            per row, with each square in the row represented by a digit,
     *            if known, and a period otherwise. With dimension dim, the file
     *            should contain dim*dim rows, and each row should contain
     *            dim*dim characters.
     * @return Sudoku object corresponding to file contents
     * @throws IOException
     *             if file reading encounters an error
     * @throws ParseException
     *             if file has error in its format
     */
    public static Sudoku fromFile(int dim, String filename) throws IOException,
            ParseException {
    	int size = dim * dim;
    	int[][] newSquare = new int[size][size];
    	
    	FileReader fileReader;
		
    	// Creates a new fileReader object to read the given file 
		try {
			fileReader = new FileReader(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("File not found");
		}
		
		BufferedReader reader = new BufferedReader(fileReader);
		String line = "";
		
		int rowCount = 0;
		try {
			while ((line = reader.readLine()) != null) {
				if (rowCount >= size) {
					throw new ParseException("Too many rows");
				}
				int[] newRow = new int[size];
				char[] arrayCopy = line.toCharArray();
				int lineLength = line.length();
				if (lineLength != (dim * dim)) {
					throw new ParseException("Too many columns");
				}
				for (int i=0; i<size; i++) {
					if (arrayCopy[i] == '.') 
						newRow[i] = 0;
					else
						newRow[i] = arrayCopy[i] - 48; //Converting ASCII code to normal integer
				}
				newSquare[rowCount] = newRow; // Add the new row to the double dimensional square array
				rowCount++;
			}
			
			return new Sudoku(dim, newSquare);
		} finally {
			//Close all the readers
			fileReader.close();
			reader.close();
		}

    }

    /**
     * Exception used for signaling grammatical errors in Sudoku puzzle files
     */
    @SuppressWarnings("serial")
    public static class ParseException extends Exception {
        public ParseException(String msg) {
            super(msg);
        }
    }

    /**
     * Produce readable string representation of this Sukoku grid, e.g. for a 4
     * x 4 sudoku problem: 
     *   12.4 
     *   3412 
     *   2.43 
     *   4321
     * 
     * @return a string corresponding to this grid
     */
    public String toString() {
    	StringBuilder stringRep = new StringBuilder();
    	for (int i = 0; i < size; ++i ) {
    		String tempString = "";
			for (int j = 0; j < size; ++j) {
				if (j==0) 
					tempString += (square[i][j] + 1);
				else 
					tempString += (" " + (square[i][j] + 1));
			}
			tempString += "\n";
			stringRep.append(tempString);
		}
    	
    	return stringRep.toString();
    }

    /**
     * @return a SAT problem corresponding to the puzzle, using variables with
     *         names of the form occupies(i,j,k) to indicate that the kth symbol
     *         occupies the entry in row i, column j
     */
    public Formula getProblem() {
    	Formula formula = new Formula();

    	// Takes into account the initial board state
    	for (int i = 0; i< size; ++i) {
    		for (int j = 0; j<size; ++j) {
    			if (square[i][j] >= 0) {
    				Literal literal = PosLiteral.make(occupies[i][j][square[i][j]]);
    				Clause clause = new Clause(literal);
    				formula = formula.addClause(clause);
    			}
    		}
    	}
    	
    	// Takes into account the fact that each square can contain only one number, and not multiple numbers
    	for (int i = 0; i<size; ++i) {
    		for (int j = 0; j<size; ++j) {
    			for (int k = 0; k<size; ++k) {
    				for (int kp = k + 1; kp<size; ++kp) {
    					Literal negLiteral1 = NegLiteral.make(occupies[i][j][k]);
    					Literal negLiteral2 = NegLiteral.make(occupies[i][j][kp]);
    					Clause clause = new Clause(negLiteral1);
    					clause = clause.add(negLiteral2);
    					formula = formula.addClause(clause);
    				}
    			}
    		}
    	}
    	
    	//Row condition; ensures that every row is some permutation of the set {1,2,...,size}
    	for (int i = 0; i<size; ++i) {
    		for (int k = 0; k<size; ++k) {
    			Clause clause = new Clause();
    			for (int j = 0; j<size; ++j) {
    				Literal literal = PosLiteral.make(occupies[i][j][k]);
    				clause = clause.add(literal);
    			}
    			// Ensures that every row contains at least one k, for all k in {1,2,...,size}
    			formula = formula.addClause(clause);
    		}
    	}
    	
    	for (int i = 0; i<size; ++i) {
    		for (int k = 0; k<size; ++k) {
    			for (int j = 0; j<size; ++j) {
    				for (int jp = j+1; jp<size; ++jp) {
    					Literal negLiteral1 = NegLiteral.make(occupies[i][j][k]);
    					Literal negLiteral2 = NegLiteral.make(occupies[i][jp][k]);
    					Clause clause = new Clause(negLiteral1);
    					clause = clause.add(negLiteral2);
    					// Ensures that every row contains at most one k, for all k in {1,2,...,size}
    					formula = formula.addClause(clause);
    				}
    			}
    		}
    	}
    	
    	//Column condition; ensures that every column is some permutation of the set {1,2,...,size}
    	for (int j = 0; j<size; ++j) {
    		for (int k = 0; k<size; ++k) {
    			Clause clause = new Clause();
    			for (int i = 0; i<size; ++i) {
    				Literal literal = PosLiteral.make(occupies[i][j][k]);
    				clause = clause.add(literal);
    			}
    			formula = formula.addClause(clause);
    		}
    	}
    	
    	for (int j = 0; j<size; ++j) {
    		for (int k = 0; k<size; ++k) {
    			for (int i = 0; i<size; ++i) {
    				for (int ip = i+1; ip<size; ++ip) {
    					Literal negLiteral1 = NegLiteral.make(occupies[i][j][k]);
    					Literal negLiteral2 = NegLiteral.make(occupies[ip][j][k]);
    					Clause clause = new Clause(negLiteral1);
    					clause = clause.add(negLiteral2);
    					formula = formula.addClause(clause);
    				}
    			}
    		}
    	}
    	
    	//Block condition; ensures that every block contains exactly one k for all k in {1,2,...,size}
    	for (int xBlock = 0; xBlock < dim; ++xBlock) {
    		for (int yBlock = 0; yBlock < dim; ++yBlock) {
    			for (int k = 0; k<size; ++k) {
    				Clause clause = new Clause();
    				for (int i = 0; i < dim; ++i) {
    					for (int j = 0; j < dim; ++j) {
    						Literal literal = PosLiteral.make(occupies[(xBlock * dim) + i][(yBlock * dim) + j][k]);
    						clause = clause.add(literal);
    					}
    				}
    				formula = formula.addClause(clause);
    			}
    		}
    	}
    	
    	for (int k = 0; k < size; ++k) {
    		for (int xBlock = 0; xBlock < dim; ++xBlock) {
    			for (int yBlock = 0; yBlock < dim; ++yBlock) {
    				for (int i = 0; i < dim; ++i) {
    					for (int j = 0; j < dim; ++j) {
    						for (int ip = i; ip < dim; ++ip) {
    							for (int jp = j + 1; jp < dim; ++jp) {
    								Literal literal1 = NegLiteral.make(occupies[(xBlock * dim) + i][(yBlock * dim) + j][k]);
    								Literal literal2 = NegLiteral.make(occupies[(xBlock * dim) + ip][(yBlock * dim) + jp][k]);
    								Clause clause = new Clause(literal1);
    								clause = clause.add(literal2);
    								formula = formula.addClause(clause);
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	return formula;
    }

    /**
     * Interpret the solved SAT problem as a filled-in grid.
     * 
     * @param e
     *            Assignment of variables to values that solves this puzzle.
     *            Requires that e came from a solution to this.getProblem().
     * @return a new Sudoku grid containing the solution to the puzzle, with no
     *         blank entries.
     */
    public Sudoku interpretSolution(Environment e) {
    	int[][] newSquares = new int[size][size];
    	if (e == null) {
    		return null; // Returns null if the Sudoku is unsolvable
    	}
    	for (int i=0; i<size; ++i) {
    		for (int j = 0; j<size; ++j) {
    			for (int k = 0; k<size; ++k) {
    				Bool value = e.get(occupies[i][j][k]);
    				if (value.equals(Bool.TRUE)) {
    					newSquares[i][j] = k + 1;
    				}
    			}
    		}
    	}
    	return new Sudoku(dim, newSquares);
    }

}
