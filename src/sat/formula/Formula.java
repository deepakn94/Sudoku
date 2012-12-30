/**
 * Author: dnj, Hank Huang
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package sat.formula;

import immutable.EmptyImList;
import immutable.ImList;
import immutable.ImListIterator;
import immutable.NonEmptyImList;

import java.util.Iterator;

import sat.env.Variable;

/**
 * Formula represents an immutable boolean formula in
 * conjunctive normal form, intended to be solved by a
 * SAT solver.
 */
public class Formula {
    private final ImList<Clause> clauses;
    // Rep invariant:
    //      clauses != null
    //      clauses contains no null elements (ensured by spec of ImList)
    //
    // Note: although a formula is intended to be a set,  
    // the list may include duplicate clauses without any problems. 
    // The cost of ensuring that the list has no duplicates is not worth paying.
    //
    //    
    //    Abstraction function:
    //        The list of clauses c1,c2,...,cn represents 
    //        the boolean formula (c1 and c2 and ... and cn)
    //        
    //        For example, if the list contains the two clauses (a,b) and (!c,d), then the
    //        corresponding formula is (a or b) and (!c or d).

    void checkRep() {
        assert this.clauses != null : "SATProblem, Rep invariant: clauses non-null";
    }

    /**
     * Create a new problem for solving that contains no clauses (that is the
     * vacuously true problem)
     * 
     * @return the true problem
     */
    public Formula() {
    	this.clauses = new EmptyImList<Clause> ();
    }

    /**
     * Create a new problem for solving that contains a single clause with a
     * single literal
     * 
     * @return the problem with a single clause containing the literal l
     */
    public Formula(Variable l) {
    	Literal literal = PosLiteral.make(l);
    	Clause clause = new Clause(literal);
        this.clauses = new NonEmptyImList<Clause> (clause);
    }
    
    /**
     * Create a new problem that contains the given set of clauses
     * 
     * @return the problem with the given set of clauses
     */
    private Formula(ImList<Clause> clauses) {
    	this.clauses = clauses;
    }

    /**
     * Create a new problem for solving that contains a single clause
     * 
     * @return the problem with a single clause c
     */
    public Formula(Clause c) {
        this.clauses = new NonEmptyImList<Clause> (c);
    }

    /**
     * Add a clause to this problem
     * 
     * @return a new problem with the clauses of this, but c added
     */
    public Formula addClause(Clause c) {
    	ImList<Clause> clauses = this.getClauses();
    	ImList<Clause> newClauses = clauses.add(c);
        return new Formula(newClauses);
    }

    /**
     * Get the clauses of the formula.
     * 
     * @return list of clauses
     */
    public ImList<Clause> getClauses() {
        return clauses;
    }

    /**
     * Iterator over clauses
     * 
     * @return an iterator that yields each clause of this in some arbitrary
     *         order
     */
    public Iterator<Clause> iterator() {
        return new ImListIterator<Clause>(this.clauses);
    }

    /**
     * @return a new problem corresponding to the conjunction of this and p
     */
    public Formula and(Formula p) {
    	ImList<Clause> finalClauses = this.getClauses();
        Iterator<Clause> formulaIterator = p.iterator();
        while (formulaIterator.hasNext()) {
        	Clause nextClause = formulaIterator.next();
        	finalClauses = finalClauses.add(nextClause); // Append the clause lists
        }
        
        return new Formula(finalClauses);
    }

    /**
     * @return a new problem corresponding to the disjunction of this and p
     */
    public Formula or(Formula p) {
        // Hint: you'll need to use the distributive law to preserve conjunctive normal form, i.e.:
        //   to do (a & b) .or (c & d),
        //   you'll need to make (a | d) & (a | c) & (b | c) & (b | d)  
    	ImList<Clause> clauses = new EmptyImList<Clause> ();
        for (Clause clause1 : this.clauses) {
        	for (Clause clause2 : p.clauses) {
        		Iterator<Literal> literalIterator1 = clause1.iterator();
        		
        		while (literalIterator1.hasNext()) {
        			Literal literal1 = literalIterator1.next();
        			Iterator<Literal> literalIterator2 = clause2.iterator();
        			while (literalIterator2.hasNext()) {
        				Literal literal2 = literalIterator2.next();
        				Clause newClause = new Clause(literal1);
        				newClause = newClause.add(literal2);
        				if (newClause != null)
                			clauses = clauses.add(newClause);
        			}
        		}
        		
        	}
        }
        return new Formula(clauses);
    }

    /**
     * @return a new problem corresponding to the negation of this
     */
    public Formula not() {
        // Hint: you'll need to apply DeMorgan's Laws (http://en.wikipedia.org/wiki/De_Morgan's_laws)
        // to move the negation down to the literals, and the distributive law to preserve 
        // conjunctive normal form, i.e.:
        //   if you start with (a | b) & c,
        //   you'll need to make !((a | b) & c) 
        //                       => (!a & !b) | !c            (moving negation down to the literals)
        //                       => (!a | !c) & (!b | !c)    (conjunctive normal form)
    	ImList<Formula> formulae = new EmptyImList<Formula> ();
        for (Clause clause : this.clauses) {
        	Iterator<Literal> literalIterator = clause.iterator();
        	ImList<Clause> clauses = new EmptyImList<Clause> ();
        	while (literalIterator.hasNext()) {
        		Literal literal = literalIterator.next();
        		Literal negationLiteral = literal.getNegation();
        		clauses = clauses.add(new Clause(negationLiteral));
        	}
        	formulae = formulae.add(new Formula(clauses));
        }
        Formula finalFormula = formulae.first();
        ImList<Formula> rest = formulae.rest();
        Iterator<Formula> formulaIterator = rest.iterator();
        while (formulaIterator.hasNext()) {
        	Formula nextFormula = formulaIterator.next();
        	finalFormula = finalFormula.or(nextFormula);
        }
        return finalFormula;
    }

    /**
     * 
     * @return number of clauses in this
     */
    public int getSize() {
        return clauses.size();
    }

    /**
     * @return string representation of this formula
     */
    public String toString() {
        String result = "Problem[";
        for (Clause c : clauses)
            result += "\n" + c;
        return result + "]";
    }
}
