//import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class UAV {
    int IdUAV;
    String flightPathName;
    double flyT;
    double flyV;//无人机的飞行速度
    double consumeV;
    double initE;//初始电量
    double deadE;//死亡阈值
    double maxE;//最大电池容量
    Area uavArea=new Area("uav"+this.IdUAV+"Area");//无人机飞行的区域
    Map<Integer, ReadPath.PointLoca> allLocations=new HashMap<>();
    static class Grid_UAV{
        //这个格子包含了UAV的路径点
        LinkedList<ReadPath.PointLoca>locasInAGrid=new LinkedList<>();
        Grid grid=new Grid();
        public Grid_UAV(Grid grid) {
            this.grid = grid;
        }
    }
    LinkedList<Grid_UAV>grids_uav=new LinkedList<>();//路径覆盖的格子
    //HashMap key部分无序
    Map<Grid,LinkedList<Grid_UAV>>grids_uav_wsc=new HashMap<>();//被不同位置放置充电器分割的路径,每个grid都有一个LinkedList
    Map<Grid,LinkedList<ReadPath.PointLoca>>locas_uav_wsc=new HashMap<>();//被不同位置放置充电器分割的路径点
    //第一个grid是placeArea上的每个格子，后面是在这个格子放WSC的话，在半径内的无人机的路径
    public UAV(int idUAV, String flightPathName) throws IOException {
        IdUAV = idUAV;
        this.flightPathName = flightPathName;
        this.allLocations=new ReadPath(flightPathName).allPoints;
        Point[]points = new Point[allLocations.size()];
        for (int i = 0; i < allLocations.size(); i++) {
            points[i]=allLocations.get(i).p;
        }
        this.uavArea= uavArea.points2AreaMargin(points);
    }

    public UAV(int idUAV, String flightPathName, double flyV, double consumeV, double initE,double deadE,double maxE) throws IOException {
        IdUAV = idUAV;
        this.flightPathName = flightPathName;
        this.flyV = flyV;
        this.consumeV = consumeV;
        this.initE = initE;
        this.deadE=deadE;
        this.maxE=maxE;
        this.allLocations=new ReadPath(flightPathName).allPoints;
        Point[]points = new Point[allLocations.size()];
        for (int i = 0; i < allLocations.size(); i++) {
            points[i]=allLocations.get(i).p;
        }
        this.uavArea= uavArea.points2AreaMargin(points);
    }

    /*路径点经过的格子*/
    LinkedList<Grid_UAV>setGrids_uav(Area area)
    {
        //checked：经过两个格子交叉点算哪个？好像是算在第一次比较的格子
/*        Map<Integer, UAV.ReadPath.PointLoca> path=this.allLocations;
        LinkedList<Grid_UAV>allGrids_uav=new LinkedList<>();

        for (int i = 0; i < area.grids_Area.size(); i++) {
            for (int j = 0; j <path.size() ; j++) {
                if (area.grids_Area.get(i).isInGrid(path.get(j).p)){
                    //这个位置点在格子中
                    Grid_UAV g=new Grid_UAV(area.grids_Area.get(i));
                    g.locasInAGrid.add(path.get(j));
                    allGrids_uav.add(g);
                }
            }
        }*/
        LinkedList<ReadPath.PointLoca>locas=new LinkedList<>() ;
        for (int i = 0; i < this.allLocations.size(); i++) {
            locas.add(this.allLocations.get(i));
        }
   //     this.grids_uav=allGrids_uav;
        this.grids_uav=this.seperateLocasToGridsUAV(area, locas);
        return this.grids_uav;
    }
    /*充电器放在placeArea时，在充电半径内的位置点的格子
    * 其实后面算充电电量时，还是用的每个点，这个格子只是在形式化说明
    * 所以这里也筛选出来可以充电的位置点*/
    Map<Grid,LinkedList<Grid_UAV>>setGrids_uav_wsc(Area area)
    {
        Map<Integer, ReadPath.PointLoca> path=this.allLocations;
        Map<Grid,LinkedList<Grid_UAV>>grids_uav_wsc=new HashMap<>();
        Map<Grid,LinkedList<ReadPath.PointLoca>>locas_uav_wsc=new HashMap<>();
        /*这个Grid_UAV，包含了格子和对应的点*/
        /*关于一个格子，一个UAV对每一个grid放置充电器都会有这样一个LinkedList*/
        for (int i = 0; i <area.grids_Area.size(); i++) {
            Grid g_wsc=area.grids_Area.get(i);//充电器放置的点
            LinkedList<ReadPath.PointLoca>pointLocas=new LinkedList<>();//统计一个格子对应的无人机i可以充电的位置点，然后把他放进去
            int j=0;
            while (j<path.size())
            {
                //记录在格子内的点
                double dd=Point.getDistance(g_wsc.centerPoint,path.get(j).p);
                if (dd<=WRUN.R)
                    pointLocas.add(path.get(j));//这个包含了所有的点，应该把它们拆成每个格子内的点，放到new Grid_UAV中
                j++;
            }
            if (pointLocas.size()!=0)//说明这个格子i可以充电，有可充电点
            {
                locas_uav_wsc.put(g_wsc,pointLocas);
                grids_uav_wsc.put(g_wsc,seperateLocasToGridsUAV(area,pointLocas));//这个要好好检查
            }
        }
        this.locas_uav_wsc=locas_uav_wsc;
        this.grids_uav_wsc=grids_uav_wsc;
        return grids_uav_wsc;
    }
    LinkedList<Grid_UAV>seperateLocasToGridsUAV(Area placearea, LinkedList<ReadPath.PointLoca>pointLocas){
        //checked
        //将一组location分成每个格子，每个格子有点
        /*根据每个位置点所在的grid进行判断*/
        LinkedList<Grid_UAV>grids_uavs=new LinkedList<>();
        this.setLocaPoint_Grid(placearea);//设置每个点所在的格子
        for (int i = 0; i < pointLocas.size(); i++) {
            //当前结点
            ReadPath.PointLoca p_uav=pointLocas.get(i);
            if (grids_uavs.size()==0||p_uav.grid!=grids_uavs.get(grids_uavs.size() - 1).grid)//这个点所在的格子还没加入     这里有错误，已经存在这个格子 但还是创建了
                grids_uavs.add(new Grid_UAV(p_uav.grid));
            for (int j = 0; j < grids_uavs.size(); j++) {
                if (grids_uavs.get(j).grid.isInGrid(p_uav.p))
                    grids_uavs.get(j).locasInAGrid.add(p_uav);//p_uav.grid=null?
            }
        }
   //     this.grids_uav=grids_uavs;
        return grids_uavs;//返回的是UAV路径经过的格子，以及每个格子中的点，Grid_UAV类型的链表
    }
    void setLocaPoint_Grid(Area area){
        //checked：最后一个位置点的格子为null？
        //给无人机的路径点，标记上所在的grid
        for (int j = 0; j < this.allLocations.size(); j++) {
            for (int i = 0; i < area.grids_Area.size(); i++) {
                if (area.grids_Area.get(i).isInGrid(this.allLocations.get(j).p))
                    this.allLocations.get(j).grid=area.grids_Area.get(i);
            }
        }
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
            Grid grid;//该点所在的格子
            public PointLoca(Point p, double t) {
                this.p = p;
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
                    String ss[] = str.split("\t");
                    Point pn = new Point(Double.parseDouble(ss[0]), Double.parseDouble(ss[1]));
                    PointLoca lo;
                    if (i == 0) {
                        lo = new PointLoca(pn, 0);
                    } else {
                        double deltaT = Point.getDistance(pn, allPoints.get(i - 1).p)/UAV.this.flyV;/*还没÷飞行速度*/
                        lo = new PointLoca(pn, allPoints.get(i - 1).t + deltaT);
                    }
                    allPoints.put(i, lo);//读入位置点，每个点有记录的时间
                    i++;
                }
            }
        }
    }
/*    public static void main(String[] args) throws IOException {
        //测试用例
        String path1="G:\\paper5_静态WSC部署\\data\\u1.txt";
        UAV uav1=new UAV(1,path1,0.1,5,200,10,1000);
        String path2="G:\\paper5_静态WSC部署\\data\\u2.txt";
        UAV uav2=new UAV(2,path2,0.1,5,200,10,1000);
        for (int i = 0; i < uav1.allLocations.size(); i++) {
            System.out.println(uav1.allLocations.get(i));
        }
        Area[]areas=new Area[2];
        areas[0]=uav1.uavArea;
        areas[1]=uav2.uavArea;
        Area UAVArea=Area.mergeArea(areas);
        Area PlaceArea=UAVArea.expandAreaMargin(WRUN.R);//扩展得到放置区域，已经切割好
  *//*      LinkedList<ReadPath.PointLoca>locas=new LinkedList<>() ;
        for (int i = 0; i < uav1.allLocations.size(); i++) {
            locas.add(uav1.allLocations.get(i));
        }*//*
      //  uav1.grids_uav=uav1.seperateLocasToGridsUAV(PlaceArea, locas);
        uav1.setGrids_uav(PlaceArea);//setGrids_uav和SeperateLocasTo是一个功能？
        uav1.setGrids_uav_wsc(PlaceArea);//执行完这步之后 为什么grids_uav变成一个只有10了，6呢?，因为里面嵌套了一个Seperate,已修改
        Iterator<Map.Entry<Grid, LinkedList<Grid_UAV>>> it=uav1.grids_uav_wsc.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry=(Map.Entry) it.next();
            Grid key= (Grid) entry.getKey();
            LinkedList<Grid_UAV> value= (LinkedList<Grid_UAV>) entry.getValue();
            System.out.print (key.id+":");
            for (int i = 0; i < value.size(); i++) {
                System.out.print (value.get(i).grid.id+" ");
            }
            System.out.println ("\n");
        }
    }*/
}
