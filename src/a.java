import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class a {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        solveMe(10);
    }
    public static void solveMe(int n) {
        //random data
        double[] xPos = new double[n];
        double[] yPos = new double[n];
        for (int i=0; i<n; i++) {
            xPos[i] = Math.random()*100;
            yPos[i] = Math.random()*100;
        }
        double[][] c = new double[n][n];
        for (int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                c[i][j] = Math.sqrt(Math.pow(xPos[i]-xPos[j], 2)+Math.pow(yPos[i]-yPos[j], 2));
            }
        }
        //model
        try {
            IloCplex cplex = new IloCplex();
            //variables
            IloNumVar[][] x = new IloNumVar[n][];
            for (int i=0; i<n; i++) {
                x[i] = cplex.boolVarArray(n);
            }
            IloNumVar[] u = cplex.numVarArray(n, 0, Double.MAX_VALUE);
            //Objective
            IloLinearNumExpr obj = cplex.linearNumExpr();
            for (int i=0; i<n; i++) {
                for(int j=0; j<n; j++) {
                    if(j!=i) {
                        obj.addTerm(c[i][j], x[i][j]);
                    }
                }
            }
            cplex.addMinimize(obj);
            //constrains
            for(int j=0; j<n; j++) {
                IloLinearNumExpr expr = cplex.linearNumExpr();
                for (int i=0; i<n; i++) {
                    if(i!=j) {
                        expr.addTerm(1.0, x[i][j]);
                    }
                }
                cplex.addEq(expr, 1.0);
            }
            for(int i=0; i<n; i++) {
                IloLinearNumExpr expr = cplex.linearNumExpr();
                for (int j=0; j<n; j++) {
                    if(j!=i) {
                        expr.addTerm(1.0, x[i][j]);
                    }
                }
                cplex.addEq(expr, 1.0);
            }
            for(int i=1; i<n; i++) {			//没有理解这个约束 u[]的作用是什么？
                for (int j=1; j<n; j++) {
                    if(j!=i) {
                        IloLinearNumExpr expr = cplex.linearNumExpr();
                        expr.addTerm(1.0, u[i]);
                        expr.addTerm(-1.0, u[j]);
                        expr.addTerm(n-1, x[i][j]);
                        cplex.addLe(expr, n-2);
                    }
                }
            }
            //solve model
            cplex.solve();
            //end
            cplex.end();

        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}

