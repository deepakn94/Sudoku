package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.NegLiteral;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        Environment environment = new Environment();
        ImList<Clause> clauses = formula.getClauses();
        return solve(clauses, environment);
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
       if (clauses.size() == 0) {
    	   return env;
       }
       int minSize = Integer.MAX_VALUE;
       Clause minClause = null;
       for (Clause c : clauses) {
    	   if (c.size() < minSize) {
    		   minSize = c.size();
    		   minClause = c;
    	   }
       }
       if (minSize == 0) {
    	   return null; // This needs to be handled in the calling function; must back propagate now
       } else if (minSize == 1) {
    	   Literal literal = minClause.chooseLiteral();
    	   Variable var = literal.getVariable();
    	   Literal newLiteral = PosLiteral.make(var);
    	   if (newLiteral.negates(literal)) {
    		   Environment newEnv = env.putFalse(var);
    		   ImList<Clause> newClauses = substitute(clauses,literal);
    		   return solve(newClauses, newEnv);
    	   } else {
    		   Environment newEnv = env.putTrue(var);
    		   ImList<Clause> newClauses = substitute(clauses,literal);
    		   return solve(newClauses, newEnv);
    	   }
       } else {
    	   Literal literal = minClause.chooseLiteral();
    	   Variable var = literal.getVariable();
    	   Environment newEnv = env.putTrue(var);
    	   ImList<Clause> newClauses = substitute(clauses, literal);
    	   Environment solution = solve(newClauses, newEnv);
    	   if (solution == null) {
    		   newEnv = env.putFalse(var);
    		   Literal negLiteral = NegLiteral.make(var);
    		   newClauses = substitute(clauses, negLiteral);
    		   return solve(newClauses, newEnv);
    	   }
    	   return solution;
       }
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
    	ImList<Clause> newClauses = new EmptyImList<Clause> ();
    	for (Clause clause : clauses) {
    		Clause newClause = clause.reduce(l);
    		if (newClause != null) {
    			newClauses = newClauses.add(newClause);
    		}
    	}
    	return newClauses;
    }

}
