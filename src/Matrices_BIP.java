import java.io.IOException;
import java.text.DecimalFormat;

public class Matrices_BIP {
    //每个WRUN都会有一个Matrices_BIP，用来输出求解BIP问题
    WRUN wrun;
    public Matrices_BIP(WRUN wrun) {
       setWRUN(wrun);
    }
    void setWRUN(WRUN wrun){
        this.wrun=wrun;
        G=wrun.PlaceArea.grids_Area.size();//格子数量
        N=wrun.UAVNum;
        M=wrun.WSCNum;
        E=new Matrix[wrun.UAVNum];
        A=new Matrix[wrun.UAVNum];
        BRi=new Matrix[wrun.UAVNum];
        BLi=new Matrix[wrun.UAVNum];
        ti=new Matrix[wrun.UAVNum];
        K_allUAVs=0;//为了确定矩阵维度，先计算。所有无人机的
        K=new int[wrun.UAVNum];//每个无人机的路径占多少格子
        {
            for (int i = 0; i < K.length; i++) {
                K[i]=wrun.uavs[i].grids_uav.size();
                K_allUAVs=K_allUAVs+K[i];
                E[i]=new Matrix(K[i],G);
                A[i]=new Matrix(K[i],K[i]);
                ti[i]=new Matrix(K[i],1);//每个格子呆的时间
            }
        }
        X=new Matrix(G,1);//自变量x的取值
        BR=new Matrix(K_allUAVs,1);//自己的第K个格子
        BL=new Matrix(K_allUAVs,1);//自己的第K个格子
        O=new Matrix(1,G);//自己的第K个格子
        W=new Matrix(K_allUAVs,G);
    }
    //用到的所有数组
    int G;//格子数量
    int N;
    int M;
    Matrix[]E;
    Matrix[]A;
    Matrix[]BRi;
    Matrix[]BLi;
    Matrix[]ti;
    int K_allUAVs=0;//为了确定矩阵维度，先计算。所有无人机的
    int[]K;//每个无人机的路径占多少格子
    Matrix X;//自变量x的取值
    Matrix BR;//自己的第K个格子
    Matrix BL;//自己的第K个格子
    Matrix O;//自己的第K个格子
    Matrix W;
    /*计算*/
    void calO(){
        for (int i = 0; i < G; i++) {
            this.O.array[0][i]=1;
        }
    }
    void calX(){
        for (int i = 0; i < G; i++) {
            X.array[i][0]=i+1;
        }
    }
    void calEachMatrix_uavi() throws IOException {
        ///计算和i相关的每个矩阵，后面函数进行组合得到最终输出的矩阵
        for (int i = 0; i < wrun.UAVNum; i++) {
            //计算A[i]
            for (int k = 0; k < K[i]; k++) {
                for (int j = 0; j <= k; j++) {
                    A[i].array[k][j] = 1;
                }
            }
          //  A[i].writeToFile("A"+i+"matrix.txt");
        }
        for (int i = 0; i < wrun.UAVNum; i++) {
            for (int k = 0; k < K[i]; k++) {
                //计算E[i]
                DecimalFormat df = new DecimalFormat("#0.00000");//小数点后保留5位
                for (int j = 0; j < G; j++) {
                    double e = Grid.getE(wrun.uavs[i], wrun.uavs[i].grids_uav.get(k), wrun.PlaceArea.grids_Area.get(j));//这里getE不对
                    E[i].array[k][j] = Double.parseDouble(df.format(-e));//无人机的每个格子，收到第j个格子的充电器的电量，一个值
                  //  E[i].array[k][j] = Double.parseDouble(df.format(e));//不调整不等号
                }
            }
            E[i].writeToFile("E"+i+"matrix.txt");//第一行Ei全为0？ 在自己的第一个格子收到其他G个位置的充电器的电量都为0？
        }
        for (int i = 0; i < wrun.UAVNum; i++) {
            for (int k = 0; k < K[i]; k++) {
                //计算ti[i]
                double t=0;
                for (int j = 0; j < wrun.uavs[i].grids_uav.get(k).locasInAGrid.size(); j++) {
                    if (j!=0)
                        t=t+wrun.uavs[i].grids_uav.get(k).locasInAGrid.get(j).t-wrun.uavs[i].grids_uav.get(k).locasInAGrid.get(j-1).t;
                }
                ti[i].array[k][0]=t;
            }
            //计算BLi和BRi
           // BRi[i]=Matrix.matrixAddNum(Matrix.matrixMultiNum(Matrix.multi2Matrices(A[i],ti[i]),wrun.uavs[i].consumeV),-wrun.uavs[i].initE);//不调整不等号
            BRi[i]=Matrix.matrixAddNum(Matrix.matrixMultiNum(Matrix.multi2Matrices(A[i],ti[i]),-wrun.uavs[i].consumeV),wrun.uavs[i].initE);
            BLi[i]=Matrix.matrixAddNum(BRi[i],wrun.uavs[i].maxE);
        }
    }
    void calBRandBL(){
        BL=Matrix.mergeMatricesUD(BLi);
        BR=Matrix.mergeMatricesUD(BRi);
    }
    /*计算*/
    void calW(){
        //计算得到最后的系数矩阵
        Matrix[]Wi=new Matrix[wrun.UAVNum];
        for (int i = 0; i < Wi.length; i++) {
            Wi[i]=Matrix.multi2Matrices(A[i],E[i]);
        }
        W=Matrix.mergeMatricesUD(Wi);
    }

    /*输出到txt供matlab求解*/
    void printAllMatricesToBIP() throws IOException {
        /*每个都计算出来*/
        calEachMatrix_uavi();
        calW();
        calBRandBL();
        calX();
        calO();
        /*输出到文件中*/
        O.writeToFile("O.txt");
        X.writeToFile("X.txt");
        W.writeToFile("W.txt");
        BL.writeToFile("BL.txt");
        BR.writeToFile("BR.txt");
        //E[1].printMatrix(); //checked
       // A[1].printMatrix();//checked
        //System.out.println (G);
       // BR.printMatrix();
    }
}
