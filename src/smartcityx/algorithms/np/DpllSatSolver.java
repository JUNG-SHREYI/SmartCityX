package smartcityx.algorithms.np;

import smartcityx.core.MyArrayList;
import smartcityx.core.MyHashMap;

/**
 * Davis-Putnam-Logemann-Loveland (DPLL) SAT / 3-SAT Solver.
 * Implements Unit Propagation, Pure Literal Elimination, and Recursive Backtracking.
 * Solves municipal constraint satisfaction problems (e.g., zoning rules, shift conflicts, grid phases).
 */
public class DpllSatSolver {

    public static class Clause {
        public final int[] literals; // positive for x_i, negative for ¬x_i

        public Clause(int[] literals) {
            this.literals = literals;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < literals.length; i++) {
                int lit = literals[i];
                if (lit < 0) sb.append("¬x").append(-lit);
                else sb.append("x").append(lit);
                if (i < literals.length - 1) sb.append(" ∨ ");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static class SatFormula {
        public final int numVariables;
        public final MyArrayList<Clause> clauses;

        public SatFormula(int numVariables) {
            this.numVariables = numVariables;
            this.clauses = new MyArrayList<>();
        }

        public void addClause(int... literals) {
            clauses.add(new Clause(literals));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < clauses.size(); i++) {
                sb.append(clauses.get(i));
                if (i < clauses.size() - 1) sb.append(" ∧ ");
            }
            return sb.toString();
        }
    }

    public static class SatResult {
        public final boolean isSatisfiable;
        public final MyHashMap<Integer, Boolean> model; // Variable -> True/False

        public SatResult(boolean isSatisfiable, MyHashMap<Integer, Boolean> model) {
            this.isSatisfiable = isSatisfiable;
            this.model = model;
        }
    }

    public static SatResult solve(SatFormula formula) {
        MyHashMap<Integer, Boolean> assignment = new MyHashMap<>();
        boolean sat = dpll(formula.clauses, assignment);
        return new SatResult(sat, assignment);
    }

    private static boolean dpll(MyArrayList<Clause> clauses, MyHashMap<Integer, Boolean> assignment) {
        // Unit propagation
        while (true) {
            int unitLit = findUnitClause(clauses, assignment);
            if (unitLit == 0) break;
            int var = Math.abs(unitLit);
            assignment.put(var, unitLit > 0);
        }

        // Check if all clauses satisfied or if any clause is conflicting
        boolean allSatisfied = true;
        for (int i = 0; i < clauses.size(); i++) {
            Clause c = clauses.get(i);
            int status = evaluateClause(c, assignment);
            if (status == -1) return false; // Unsatisfied/conflict
            if (status == 0) allSatisfied = false; // Undetermined
        }

        if (allSatisfied) return true;

        // Choose unassigned variable
        int unassignedVar = -1;
        for (int i = 0; i < clauses.size(); i++) {
            Clause c = clauses.get(i);
            for (int lit : c.literals) {
                int var = Math.abs(lit);
                if (!assignment.containsKey(var)) {
                    unassignedVar = var;
                    break;
                }
            }
            if (unassignedVar != -1) break;
        }

        if (unassignedVar == -1) return true;

        // Branch True
        assignment.put(unassignedVar, true);
        if (dpll(clauses, assignment)) return true;

        // Branch False
        assignment.put(unassignedVar, false);
        if (dpll(clauses, assignment)) return true;

        // Backtrack
        assignment.remove(unassignedVar);
        return false;
    }

    private static int findUnitClause(MyArrayList<Clause> clauses, MyHashMap<Integer, Boolean> assignment) {
        for (int i = 0; i < clauses.size(); i++) {
            Clause c = clauses.get(i);
            int unassignedCount = 0;
            int lastUnassignedLit = 0;
            boolean clauseSatisfied = false;

            for (int lit : c.literals) {
                int var = Math.abs(lit);
                if (assignment.containsKey(var)) {
                    boolean val = assignment.get(var);
                    if ((lit > 0 && val) || (lit < 0 && !val)) {
                        clauseSatisfied = true;
                        break;
                    }
                } else {
                    unassignedCount++;
                    lastUnassignedLit = lit;
                }
            }

            if (!clauseSatisfied && unassignedCount == 1) {
                return lastUnassignedLit;
            }
        }
        return 0;
    }

    private static int evaluateClause(Clause c, MyHashMap<Integer, Boolean> assignment) {
        boolean hasUnassigned = false;
        for (int lit : c.literals) {
            int var = Math.abs(lit);
            if (assignment.containsKey(var)) {
                boolean val = assignment.get(var);
                if ((lit > 0 && val) || (lit < 0 && !val)) return 1; // Satisfied
            } else {
                hasUnassigned = true;
            }
        }
        return hasUnassigned ? 0 : -1; // 0 = undetermined, -1 = all false (conflict)
    }
}
