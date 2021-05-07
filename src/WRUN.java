import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class WRUN {
    static final String fileUAVPathsDir="G:\\paper5_静态WSC部署\\expdata\\";;//路径文件目录
    static final String fileMatrixDir="G:\\paper5_静态WSC部署\\matrix\\";
    static final String fileMatlabDir="G:\\paper5_静态WSC部署\\MATLAB_code\\";
    public   int UAVNum;
    public   int WSCNum;
    public  static double R;
    public  UAV[]uavs;
    public  WSC[]wscs;
    Area UAVsArea;
    Area PlaceArea;
    public static double alpha=200;//包含P0
    public static double beta=0.5;
    public WRUN(int uvaNum, int wscNum, double RR){
        UAVNum=uvaNum;
        uavs=new UAV[UAVNum];
        WSCNum=wscNum;
        wscs=new WSC[WSCNum];
        R=RR;
    }
    public void initWRUN() throws IOException {
        String[]Paths=new String[UAVNum];
        Area[]UAV_areas=new Area[UAVNum];
        /*初始化无人机集群*/
        for (int i = 0; i < Paths.length; i++) {
            Paths[i]=fileUAVPathsDir+"u"+Integer.toString(i+1)+".txt";
            uavs[i]=new UAV(i,Paths[i],1,1,500,10,2000);
            UAV_areas[i]=uavs[i].uavArea;//无人机的区域，等下要合并
        }
         UAVsArea=Area.mergeArea(UAV_areas);
         PlaceArea=UAVsArea.expandAreaMargin(R);//扩展得到放置区域，已经切割好
        for (int i = 0; i < PlaceArea.grids_Area.size(); i++) {
            PlaceArea.grids_Area.get(i).id=i;
        }
        /*接下来该根据每个UAV的飞行路径和placeArea，筛选出UAVs的gik，gijk*/
        for (int i = 0; i < uavs.length; i++) {
            uavs[i].setGrids_uav(PlaceArea);
            uavs[i].setGrids_uav_wsc(PlaceArea);
            Iterator<Map.Entry<Grid, LinkedList<UAV.Grid_UAV>>> it=uavs[i].grids_uav_wsc.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry=(Map.Entry) it.next();
                Grid key= (Grid) entry.getKey();
                LinkedList<UAV.Grid_UAV> value= (LinkedList<UAV.Grid_UAV>) entry.getValue();
                //System.out.print (key.id+":");
                for (int j = 0; j < value.size();j++) {
              //      System.out.print (value.get(j).grid.id+" ");
                }
              //  System.out.println ("\n");
            }
        }

    }
    void printParasToMatlab(FileWriter file) throws IOException {
        // out.write("uavNum\t" + "EDGE\t" + "flyV\t" + "conV\t" + "initE\t" + "maxE\t" + "alpha\t" + "beta\t" + "G\t"+"R\n");
       file.write(this.UAVNum+"\t"+Grid.EDGE+"\t"+this.uavs[0].flyV+"\t"+this.uavs[0].consumeV+"\t"+this.uavs[0].initE+
               "\t"+this.uavs[0].maxE+"\t"+WRUN.alpha+"\t"+WRUN.beta+"\t"+this.PlaceArea.grids_Area.size()+"\t"+WRUN.R+"\n");
    }
   /* //调uavNum
    public static void adUAVNum(FileWriter out) throws IOException {
        for (int i = 1; i < 7; i++) {
            WRUN wrun=new WRUN(i,2000,Grid.EDGE);
            wrun.initWRUN();
            wrun.PlaceArea.writeAreaGrids("adjustI"+i+"PlaceArea_grids.txt");
            wrun.UAVsArea.writeAreaGrids("adjustI"+i+"UAVsArea_grids.txt");
            Matrices_BIP matrices_bip=new Matrices_BIP(wrun);
            matrices_bip.printAllMatricesToBIP();
            wrun.printParasToMatlab(out);
        }
    }
    public static void adEDGE(FileWriter out) throws IOException {
        //调EDGE

            for (int e = 10; e < 31; e = e + 5) {
                Grid.setEDGE(e);
                WRUN wrun = new WRUN(4, 2000, Grid.EDGE);
                wrun.initWRUN();
                wrun.PlaceArea.writeAreaGrids("adjustEDGE" + e + "PlaceArea_grids.txt");
                wrun.UAVsArea.writeAreaGrids("adjustEDGE" + e + "UAVsArea_grids.txt");
                Matrices_BIP matrices_bip = new Matrices_BIP(wrun);
                matrices_bip.printAllMatricesToBIP();
                wrun.printParasToMatlab(out);
            }
    }
    public static void adAlpha(FileWriter out) throws IOException
    //调alpha
    {
        WRUN wrun=new WRUN(4,2000,Grid.EDGE);
        for (int i = 150; i < 210; i=i+10) {
            WRUN.alpha=i;
            wrun.initWRUN();
            wrun.PlaceArea.writeAreaGrids("adjustalpha"+i+"PlaceArea_grids.txt");
            wrun.UAVsArea.writeAreaGrids("adjustalpha"+i+"UAVsArea_grids.txt");
            Matrices_BIP matrices_bip=new Matrices_BIP(wrun);
            matrices_bip.printAllMatricesToBIP();
            wrun.printParasToMatlab(out);
        }
    }
    public static void adBeta(FileWriter out)throws IOException
    //调beta
    {
        WRUN wrun=new WRUN(4,2000,Grid.EDGE);
        for (double i = 0.1; i < 1; i=i+0.1) {
            WRUN.beta=i;
            wrun.initWRUN();
            wrun.PlaceArea.writeAreaGrids("adjustbeta"+i+"PlaceArea_grids.txt");
            wrun.UAVsArea.writeAreaGrids("adjustbeta"+i+"UAVsArea_grids.txt");
            Matrices_BIP matrices_bip=new Matrices_BIP(wrun);
            matrices_bip.printAllMatricesToBIP();
            wrun.printParasToMatlab(out);
        }
    }
    public static void adR(FileWriter out)throws IOException
    //调R
    {
        WRUN wrun=new WRUN(4,2000,Grid.EDGE);
        for (double i = Grid.EDGE; i < Grid.EDGE+30; i=i+5) {
            WRUN.R=i;
            wrun.initWRUN();
            wrun.PlaceArea.writeAreaGrids("adjustR"+i+"PlaceArea_grids.txt");
            wrun.UAVsArea.writeAreaGrids("adjustR"+i+"UAVsArea_grids.txt");
            Matrices_BIP matrices_bip=new Matrices_BIP(wrun);
            matrices_bip.printAllMatricesToBIP();
            wrun.printParasToMatlab(out);
        }
    }*/
    public static void main(String[] args) throws IOException {
        WRUN wrun=new WRUN(6,2000,Grid.EDGE);
        wrun.initWRUN();
        wrun.PlaceArea.writeAreaGrids("PlaceArea_grids.txt");
        wrun.UAVsArea.writeAreaGrids("UAVsArea_grids.txt");
       // wrun.PlaceArea.drawArea();
        for (int i = 0; i < wrun.UAVNum; i++) {
            System.out.println("K"+i+"="+wrun.uavs[i].grids_uav.size());
        }
        System.out.println ("G="+wrun.PlaceArea.grids_Area.size());
        Matrices_BIP matrices_bip=new Matrices_BIP(wrun);
        matrices_bip.printAllMatricesToBIP();
    }
}
