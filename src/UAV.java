import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class UAV {
    int IdUAV;
    String flightPathName;
    double flyT;
    double flyV;//无人机的飞行速度
    double consumeV;
    Area uavArea=new Area("uav"+this.IdUAV+"Area");//无人机飞行的区域
    Map<Integer, ReadPath.PointLoca> allLocations=new HashMap<>();
    class Grid_UAV{
        //这个格子包含了UAV的路径点
        LinkedList<ReadPath.PointLoca>locasInAGrid=new LinkedList<>();
        Grid grid=new Grid();
        public Grid_UAV(Grid grid) {
            this.grid = grid;
        }
    }
    LinkedList<Grid_UAV>grids_uav=new LinkedList<>();//路径覆盖的格子
    Map<Grid,LinkedList<Grid_UAV>>grids_uav_wsc=new HashMap<>();//被不同位置放置充电器分割的路径,每个grid都有一个LinkedList
    Map<Grid,LinkedList<ReadPath.PointLoca>>locas_uav_wsc=new HashMap<>();//被不同位置放置充电器分割的路径点
    //第一个grid是placeArea上的每个格子，后面是在这个格子放WSC的话，在半径内的无人机的路径
    public UAV(int idUAV, String flightPathName) throws IOException {
        IdUAV = idUAV;
        this.flightPathName = flightPathName;
        this.allLocations=new ReadPath(flightPathName).allPoints;
        Point[]points= (Point[]) this.allLocations.values().toArray();//不知道是否转型成功
        this.uavArea= uavArea.points2AreaMargin(points);
    }
    /*路径点经过的格子*/
    LinkedList<Grid_UAV>setGrids_uav(Area area)
    {
        Map<Integer, UAV.ReadPath.PointLoca> path=this.allLocations;
        LinkedList<Grid_UAV>allGrids_uav=new LinkedList<>();
        /**/
        for (int i = 0; i < area.grids_Area.size(); i++) {
            for (int j = 0; j <path.size() ; j++) {
                if (area.grids_Area.get(i).isInGrid(path.get(j))){
                    //这个位置点在格子中
                    Grid_UAV g=new Grid_UAV(area.grids_Area.get(i));
                    g.locasInAGrid.add(path.get(j));
                    allGrids_uav.add(g);
                }
            }
        }
        this.grids_uav=allGrids_uav;
        return allGrids_uav;
    }
    /*充电器放在placeArea时，在充电半径内的位置点的格子
    * 其实我后面算充电电量时，还是用的每个点，这个格子只是在形式化说明
    * 所以这里也晒出来可以充电的位置点*/
    Map<Grid,LinkedList<Grid_UAV>>setGrids_uav_wsc(Area area)
    {
        Map<Integer, UAV.ReadPath.PointLoca> path=this.allLocations;
        Map<Grid,LinkedList<Grid_UAV>>grids_uav_wsc=new HashMap<>();
        Map<Grid,LinkedList<ReadPath.PointLoca>>locas_uav_wsc=new HashMap<>();
        /*关于一个格子，一个UAV对每一个grid放置充电器都会有这样一个LinkedList*/
        for (int i = 0; i <area.grids_Area.size(); i++) {
            Grid g=area.grids_Area.get(i);
            LinkedList<ReadPath.PointLoca>pointLocas=new LinkedList<>();
            int j=0;
            while (j<path.size())
            {
                //记录在格子内的点
                if (Point.getDistance(g.centerPoint,path.get(j).p)<=WRUN.R)
                    pointLocas.add(path.get(j));
                j++;
            }
            if (pointLocas.size()!=0)
                grids_uav_wsc.put(g,pointLocas);
        }
        this.locas_uav_wsc=locas_uav_wsc;
        return grids_uav_wsc;
    }
    public LinkedList<Grid_UAV> getGrids_uav() {
        return grids_uav;
    }

    public Map<Grid, LinkedList<Grid_UAV>> getGrids_uav_wsc() {
        return grids_uav_wsc;
    }

    /*UAV 的内部类*/
    class ReadPath {
        Map<Integer, PointLoca> allPoints = new HashMap<>();//读文件读出来的结果
        /*Integer 是序号，PointLoca包括位置和时间*/
        String pathName;

        class PointLoca {
            Point p;
            double t;

            public PointLoca(Point p, double t) {
                this.p = p;
                this.t = t;
            }

            public PointLoca(Point p) {
                this.p = p;
            }

            public PointLoca(double x, double y, double t) {
                this.p = new Point(x, y);
                this.t = t;
            }
        }

        public ReadPath(String pathName) throws IOException {
            this.pathName = pathName;
            this.loadPath();
        }

        private void loadPath() throws IOException {
            int count = 0;//总的行数
            File file = new File(pathName);
            FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                count++;
            }
            FileInputStream inputStream = new FileInputStream(pathName);
            BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            int i = 0;//记录txt中的每一行
            while ((str = bufferedInputStream.readLine()) != null) {
                if (i < count) {
                    String ss[] = str.split(" ");
                    Point pn = new Point(Double.parseDouble(ss[0]), Double.parseDouble(ss[1]));
                    PointLoca lo;
                    if (i == 0) {
                        lo = new PointLoca(pn, 0);
                    } else {
                        double deltaT = Point.getDistance(pn, allPoints.get(i - 1).p)/UAV.this.flyV;/*还没÷飞行速度*/
                        lo = new PointLoca(pn, allPoints.get(i - 1).t + deltaT);
                    }
                    allPoints.put(i, lo);//读入位置点，每个点有记录的时间
                }
            }
        }
    }
}
