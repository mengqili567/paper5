
/**
 * Created by AwaysAway on 2020/10/9
 */
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class BIP {
    public static void main(String[] args) {
        model1();
    }
/*
* min f=0.12x+0.15y
* s.t.
    60x+60y≥300
    12x+6y≥300
    10x+30y≥300
    x ≥ 0 , y ≥ 0
* */
    public static void model1() {
        try {
            IloCplex cplex = new IloCplex();

            // variables
            IloNumVar x = cplex.numVar(0, Double.MAX_VALUE, "x");//下限，上限，类型
            IloNumVar y = cplex.numVar(0, Double.MAX_VALUE, "y");

            // expressions
            IloLinearNumExpr objective = cplex.linearNumExpr();
            objective.addTerm(0.12, x);
            objective.addTerm(0.15, y);

            // define objective
            cplex.addMinimize(objective);

            // define constraints
            cplex.addGe(cplex.sum(cplex.prod(60, x), cplex.prod(60, y)), 300);
            cplex.addGe(cplex.sum(cplex.prod(12, x), cplex.prod(6, y)), 36);
            cplex.addGe(cplex.sum(cplex.prod(10, x), cplex.prod(30, y)), 90);

            // solve
            // cplex.solve();
            if (cplex.solve()) {
                cplex.output().println("Solution status = " + cplex.getStatus());
                cplex.output().println("obj = " + cplex.getObjValue());
                cplex.output().println("x   = " + cplex.getValue(x));
                cplex.output().println("y   = " + cplex.getValue(y));
            }
            // else{
            //    System.out.println("Model not solved");
            // }
            cplex.end();

        } catch (IloException e) {
            e.printStackTrace();
        }

    }

}
