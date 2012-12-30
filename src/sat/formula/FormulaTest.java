package sat.formula;

import org.junit.Test;

public class FormulaTest {    
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal d = PosLiteral.make("d");
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
    public void andTest() {
    	Clause clause1 = new Clause(a);
    	clause1 = clause1.add(nb);
    	
    	Clause clause2 = new Clause(c);
    	clause2 = clause2.add(na);
    	
    	Formula formula1 = new Formula(clause1);
    	Formula formula2 = new Formula(clause2);
    	
    	System.out.println(formula1.and(formula2));
    }
    
    @Test
    public void orTest() {
    	Clause clause1 = new Clause(a);
    	clause1 = clause1.add(b);
    	
    	Clause clause2 = new Clause(c);
    	clause2 = clause2.add(d);
    	
    	Clause clause3 = new Clause(na);
    	clause3 = clause3.add(nb);
    	
    	Formula formula1 = new Formula(clause1);
    	Formula formula2 = new Formula(clause2);
    	formula2 = formula2.addClause(clause3);
    	
    	System.out.println(formula1.or(formula2));
    }
    
    @Test
    public void notTest() {
    	Clause clause1 = new Clause(a);
    	clause1 = clause1.add(b);
    	
    	Clause clause2 = new Clause(c);
    	clause2 = clause2.add(d);
    	
    	Formula formula = new Formula(clause1);
    	formula = formula.addClause(clause2);
    	
    	System.out.println(formula.not());
    }

    
    
    // Helper function for constructing a clause.  Takes
    // a variable number of arguments, e.g.
    //  clause(a, b, c) will make the clause (a or b or c)
    // @param e,...   literals in the clause
    // @return clause containing e,...
    @SuppressWarnings("unused")
	private Clause make(Literal... e) {
        Clause c = new Clause();
        for (int i = 0; i < e.length; ++i) {
            c = c.add(e[i]);
        }
        return c;
    }
}