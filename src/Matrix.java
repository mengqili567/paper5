import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Matrix{
//矩阵之间的基本操作
    //all functions are checked
    int row;
    int col;
    double [][]array;

    public Matrix(int row, int col) {
        this.row = row;
        this.col = col;
        array=new double[row][col];
    }

    public Matrix(double[][] array) {
        this.array = array;
        this.row=array.length;
        this.col=array[0].length;
    }

    public static Matrix mergeMatricesUD(Matrix[] matrices){
        //上下，同宽矩阵拼接
        int c=matrices[0].col;
        int r=0;
        for (int i = 0; i < matrices.length; i++) {
            r=r+matrices[i].row;
        }
        Matrix m=new Matrix(r,c);
        int row=0;
        for (int i = 0; i < matrices.length; i++) {
            for (int k = 0; k < matrices[i].row; k++) {
                for (int j = 0; j < matrices[i].col; j++) {
                    m.array[row][j]=matrices[i].array[k][j];
                }
                row++;
            }
        }
        return m;
    }
    void printMatrix()
    {
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.col; j++) {
                System.out.print(this.array[i][j]+" ");
            }
            System.out.print("\n");
        }
    }
    void writeToFile(String matrixName) throws IOException {
        String fileName=WRUN.fileMatrixDir+matrixName;
        File file=new File(fileName);
        FileWriter out=new FileWriter(file);
        for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < col; j++) {
                out.write(this.array[i][j]+" ");
            }
            out.write("\n");
        }
        out.close();
    }
   public static Matrix multi2Matrices(Matrix x, Matrix y){
        //两个矩阵相乘
        Matrix answer = new Matrix(x.row,y.col);
        for(int i =0;i< answer.row;i++){
            for(int j = 0;j< answer.col;j++){
                answer.array[i][j] = 0;
                for(int k = 0;k<x.col;k++){
                    answer.array[i][j]+=x.array[i][k]*y.array[k][j];
                }
            }
        }
        return answer;
    }
    public static Matrix matrixAddNum(Matrix x, double num)//矩阵里每个数都+num
    {
        Matrix answer=new Matrix(x.row, x.col);
        for (int i = 0; i < answer.row; i++) {
            for (int j = 0; j < answer.col; j++) {
                answer.array[i][j]=x.array[i][j]+num;
            }
        }
        return answer;
    }
    public static Matrix matrixMultiNum(Matrix x, double num)//矩阵里每个数都+num
    {
        Matrix answer=new Matrix(x.row, x.col);
        for (int i = 0; i < answer.row; i++) {
            for (int j = 0; j < answer.col; j++) {
                answer.array[i][j]=x.array[i][j]*num;
            }
        }
        return answer;
    }
/*    public static void main(String[] args) throws IOException {
        Matrix matrix1=new Matrix(3,3);
        int count=0;
        for (int i = 0; i < matrix1.row; i++) {
            for (int j = 0; j < matrix1.col; j++) {
                matrix1.array[i][j]=count++;
            }
        }
        Matrix matrix2=new Matrix(1,3);
        for (int i = 0; i < matrix2.row; i++) {
            for (int j = 0; j < matrix2.col; j++) {
                matrix2.array[i][j]=count++;
            }
        }
        matrix1.printMatrix();
        matrix2.printMatrix();
        System.out.println ("after");
        Matrix[]matrices=new Matrix[2];
        matrices[0]=matrix1;
        matrices[1]=matrix2;
        mergeMatricesUD(matrices).printMatrix();
        System.out.println ("multiply");
        multi2Matrices(matrix2,matrix1).printMatrix();
        System.out.println ("subtract");
        matrixAddNum(matrix1,-2).printMatrix();
        System.out.println ("MultiNum");
        matrixMultiNum(matrix1,-2).printMatrix();
        matrix1.writeToFile("matrix1.txt");
    }*/
}
