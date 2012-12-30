package sat;

import org.junit.Test;

import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    // make sure assertions are turned on!  
    // we don't want to run test cases without assertions too.
    // see the handout to find out how to turn them on.
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    @Test
    public void simpleSATTest() {
    	Clause clause1 = new Clause();
    	clause1 = clause1.add(na);
    	clause1 = clause1.add(b);
    	
    	Clause clause2 = new Clause();
    	clause2 = clause2.add(na);
    	clause2 = clause2.add(nb);
  
    	Formula formula = new Formula();
    	formula = formula.addClause(clause1);
    	formula = formula.addClause(clause2);
    	
    	System.out.println(formula);
    	System.out.println(SATSolver.solve(formula));
    }
    
    @Test
    public void moreComplicatedSATTest() {
    	Clause clause1 = new Clause(a);
    	Formula formula = new Formula(clause1);
    	
    	Clause clause2 = new Clause(b);
    	Formula formula2 = new Formula(clause2);
    	
    	formula = formula.and(formula2);
    	
    	Clause clause3 = new Clause(nb);
    	Formula formula3 = new Formula(clause3);
    	
    	Clause clause4 = new Clause(c);
    	Formula formula4 = new Formula(clause4);
    	
    	formula3 = formula3.or(formula4);
    	
    	formula = formula.and(formula3);
    	
    	System.out.println(formula);
    	System.out.println(SATSolver.solve(formula));
    }
    
    @Test
    public void negationTest() {
    	Clause clause1 = new Clause(na);
    	clause1 = clause1.add(nb);
    	Formula formula = new Formula(clause1);
    	
    	System.out.println(formula);
    	System.out.println(SATSolver.solve(formula));
    }
    
    
}