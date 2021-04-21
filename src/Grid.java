import java.io.IOException;
import java.util.LinkedList;

class Grid{
    public static final double EDGE=2;//grids边长
    int id;
    Point centerPoint;//格子的中心点坐标
    Point[]corners=new Point[4];//四个顶点,左上开始顺时针0123
    /*
     *  0    1   2   3
     *  左上 右上 右下 左下
     */
    public Grid(Point centerPoint, Point[] corners) {
        this.centerPoint = centerPoint;
        this.corners = corners;
    }
    public Grid(Point centerPoint) {
        this.centerPoint = centerPoint;
    }

    public Grid(int id) {
        this.id = id;
    }

    public Grid() {
    }

    //由中心点+边长，得到四个顶点坐标
    public Point[] getCorners(Point centerPoint,double e)
    {
        Point[]corners=new Point[4];
        corners[0]=new Point(centerPoint.x-EDGE/2,centerPoint.y+e/2);//左上
        corners[1]=new Point(centerPoint.x+EDGE/2,centerPoint.y+e/2);//右上
        corners[2]=new Point(centerPoint.x+EDGE/2,centerPoint.y-e/2);//右下
        corners[3]=new Point(centerPoint.x-EDGE/2,centerPoint.y-e/2);//左下
        return corners;
    }
    //格子上下左右平移
    public Grid shiftGrid(double lr,double ud){
        Grid grid=new Grid(this.centerPoint.shift(lr, ud));
        grid.corners=getCorners(this.centerPoint,EDGE);
        return grid;
    }
    /*判断一个位置点是否在格子内*/
    boolean isInGrid( UAV.ReadPath.PointLoca loca){
        return ((loca.p.x>=this.corners[0].x&&loca.p.x<=this.corners[1].x)&&
                (loca.p.y>=this.corners[3].y&&loca.p.y<=this.corners[0].y));
    }
}