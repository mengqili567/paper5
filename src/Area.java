import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

/*
readFlightPaths,读入无人机飞行路径的数据，确定UAVs area
根据UAVs area平移R得到placement area
切割placement area 得到小格子*/
/*抽象成对区域的一些处理
* 1.区域向四周扩展R
* 2.区域切割成小格子
* */
class Area{
    String areaName;//区域的名字
    LinkedList<Grid> grids_Area =new LinkedList<>();
    Point[]corners_Area=new Point[4];//区域的四个顶点
    double[]margin_Area=new double[4];//区域的四个边界值
    /*1.margin to grids
    * 2.margin to corners
    * 1.边界 平移shift,没用到
    * 2.边界扩展 expand
    * 3.根据很多位置点，确定margin
    * 4.多个margin，混合成大的margin
    * */
    public Area(String areaName) {
        this.areaName = areaName;
    }

    public Area(double[] margin_Area) {
        this.margin_Area = margin_Area;
        this.grids_Area=this.margin2Grids_Area(margin_Area,Grid.EDGE);
    }

    /*由区域的边界得到区域的四个顶点
    *  0    1   2   3
    *  左上 右上 右下 左下
    */
    public LinkedList<Grid>margin2Grids_Area(double[] margin,double edgeLength)
    {
        LinkedList<Grid>grids=new LinkedList<>();
        /*开始分割*/
        double length=margin[3]-margin[2];
        double height=margin[0]-margin[1];
        int lNum=(int)(length/edgeLength)+1;//横向,格子数
        int hNum=(int)(height/edgeLength)+1;//纵向，格子数，根据边长分割，R是边长的倍数，且假设区域长度可以整除格子边长
        Point lu=new Point(margin[2],margin[0]);//区域左上的坐标点
        for (int i = 0; i < hNum; i++) {//i
            for (int j = 0; j < lNum; j++) {//j
                //先确定中心点
                Point center=new Point(lu.x-(i+1)*Grid.EDGE/2,lu.y-(j+1)*Grid.EDGE/2);
                Grid grid=new Grid(center);
                grid.corners=grid.getCorners(grid.centerPoint,Grid.EDGE);
                grids.add(grid);
            }
        }
        return grids;
    }
    /*margin to corners*/
    public Point[]margin2Corners_Area(Area area){
        Point[]corners=new Point[4];
        corners[0]=new Point(area.margin_Area[2],area.margin_Area[0]);
        corners[1]=new Point(area.margin_Area[3],area.margin_Area[0]);
        corners[2]=new Point(area.margin_Area[3],area.margin_Area[1]);
        corners[3]=new Point(area.margin_Area[2],area.margin_Area[1]);
        return corners;
    }
    /*根据Margin向四周拓展R*/
    public Area expandAreaMargin(double R) throws IOException {
        Area areaAfterExpand=new Area("area_expandedFrom"+this.areaName);
       // double[]margin=new double[4];
        areaAfterExpand.margin_Area[0]=this.margin_Area[0]+R;
        areaAfterExpand.margin_Area[1]=this.margin_Area[1]-R;
        areaAfterExpand.margin_Area[2]=this.margin_Area[2]-R;
        areaAfterExpand.margin_Area[3]=this.margin_Area[3]+R;
        areaAfterExpand.grids_Area=areaAfterExpand.margin2Grids_Area(areaAfterExpand.margin_Area,Grid.EDGE);
        areaAfterExpand.corners_Area=areaAfterExpand.margin2Corners_Area(areaAfterExpand);
        return areaAfterExpand;
    }
    /*输入位置点，得到边界（区域）*/
    public Area points2AreaMargin(Point[] points) throws IOException {
        double[]margin=new double[4];
        LinkedList<Double>x=new LinkedList<>();
        for (int i = 0; i < points.length; i++) {
            x.add(points[i].getX());
        }
        LinkedList<Double>y=new LinkedList<>();
        for (int i = 0; i < points.length; i++) {
            y.add(points[i].getY());
        }
        Collections.sort(x);
        Collections.sort(y);
        margin[0]=y.getLast();//up
        margin[1]=y.getFirst();//down
        margin[2]=x.getFirst();//left
        margin[3]=x.getLast();//right
        /*上下左右围成的举行就是UAV area*/
        return new Area(margin);
    }
    /*不同区域合并*/
    static Area mergeArea(Area[] areas){
        double[]marginMerged=new double[4];
        for (int i = 0; i < areas.length; i++) {
            if(areas[i].margin_Area[0]>marginMerged[0])
                marginMerged[0]=areas[i].margin_Area[0];
            if(areas[i].margin_Area[1]<marginMerged[1])
                marginMerged[1]=areas[i].margin_Area[1];
            if(areas[i].margin_Area[2]<marginMerged[2])
                marginMerged[2]=areas[i].margin_Area[2];
            if(areas[i].margin_Area[4]>marginMerged[4])
                marginMerged[4]=areas[i].margin_Area[4];
        }
        return new Area(marginMerged);
    }
}
