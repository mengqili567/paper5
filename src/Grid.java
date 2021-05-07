import java.io.IOException;

class Grid{
    public static double EDGE=20;//grids边长
    int id;
    Point centerPoint;//格子的中心点坐标
    Point[]corners=new Point[4];//四个顶点,左上开始顺时针0123
    double[]margin=new double[4];
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
        this.corners=getCorners(centerPoint,Grid.EDGE);

    }

    public static double getEDGE() {
        return EDGE;
    }

    public static void setEDGE(double EDGE) {
        Grid.EDGE = EDGE;
    }

    public Grid(int id) {
        this.id = id;
    }

    public Grid() {
    }
//格子内所有位置收到的电量
    public static double getE(UAV uav,UAV.Grid_UAV grids_uav, Grid grid) {
        //未check,4.27checked，5.7checked
        /*grids_uav: 无人机经过的一个格子
        * grid: 充电器位于的一个格子*/
        double e=0;
        for (int i = 0; i+1 <grids_uav.locasInAGrid.size() ; i++) {
            double ee=0;
            UAV.ReadPath.PointLoca uavloca=grids_uav.locasInAGrid.get(i);
            double dd=Point.getDistance(uavloca.p,grid.centerPoint);
            double deltaT=grids_uav.locasInAGrid.get(i+1).t-grids_uav.locasInAGrid.get(i).t;
            //要把格子中的点uavloca转到uav的整体路径点上求deltaT?
            if (dd<=WRUN.R&&deltaT<=Grid.EDGE/uav.flyV){
             //   deltaT=uav.allLocations.get(i).t-uav.allLocations.get(i-1).t;
                ee=WRUN.alpha/Math.pow(WRUN.beta+dd,2)*deltaT;
            }
            e=ee+e;
        }
        /*int is=0;
        int ie=0;
        for (int j = 0; j < uav.allLocations.size(); j++) {
            if (uav.allLocations.get(j).t == grids_uav.locasInAGrid.get(0).t)
                is = j;//当前格子内第一个点，转到，总的路径上
            if (uav.allLocations.get(j).t == grids_uav.locasInAGrid.getLast().t)
                ie = j;//当前格子内最后一个点，转到，总的路径上
        }
        for (int i = is; i <uav.allLocations.size() ; i++) {
            double ee=0;
            double dd=Point.getDistance(uav.allLocations.get(i).p,grid.centerPoint);
            if (dd<=WRUN.R){
                double deltaT=0;
                if (is==0)//这个不对
                    deltaT=0;
                else
                    deltaT=uav.allLocations.get(i).t-uav.allLocations.get(i-1).t;
                ee=WRUN.alpha/Math.pow(WRUN.beta+dd,2)*deltaT;
            }
            e=ee+e;
        }*/
  /*      for (int i = 0; i < grids_uav.locasInAGrid.size(); i++) {
            double ee=0;
            double dd=Point.getDistance(grids_uav.locasInAGrid.get(i).p,grid.centerPoint);
            if (dd<=WRUN.R)
            {
                double deltaT=0;//这里要把格子内的点转到alllocas上计算时间
                if (grids_uav.locasInAGrid.get(i).t==0)//初始结点
                    deltaT=0;
                else//怎样找到i在总路径上前面的一个结点？
                {
                    for (int j = 0; j < uav.allLocations.size(); j++) {
                        if (uav.allLocations.get(j).t==grids_uav.locasInAGrid.get(i).t)
                            deltaT=grids_uav.locasInAGrid.get(i).t-grids_uav.locasInAGrid.get(j-1).t;//讨论i的边界情况
                    }
                }
                //一个点到充电器格子中心的距离小于R，可以充上电
                e=WRUN.alpha/Math.pow(WRUN.beta+dd,2)*deltaT;
            }
            e=ee+e;
        }*/
        return e;
    }

    //由中心点+边长，得到四个顶点坐标
    public Point[] getCorners(Point centerPoint,double edgeLength)
    {
        Point[]corners=new Point[4];
        corners[0]=new Point(centerPoint.x-edgeLength/2,centerPoint.y+edgeLength/2);//左上
        corners[1]=new Point(centerPoint.x+edgeLength/2,centerPoint.y+edgeLength/2);//右上
        corners[2]=new Point(centerPoint.x+edgeLength/2,centerPoint.y-edgeLength/2);//右下
        corners[3]=new Point(centerPoint.x-edgeLength/2,centerPoint.y-edgeLength/2);//左下
        margin[0]=corners[0].y;
        margin[1]=corners[3].y;
        margin[2]=corners[0].x;
        margin[3]=corners[2].x;
        this.corners=corners;
        return corners;
    }
    //格子上下左右平移
    public Grid shiftGrid(double x,double y,double edgeLength){
        Grid grid=new Grid(this.centerPoint.shift(x, y));
        grid.corners=getCorners(grid.centerPoint,edgeLength);
        return grid;
    }
    /*判断一个位置点是否在格子内*/
    boolean isInGrid( Point loca){
        return ((loca.x>=this.corners[0].x&&loca.x<this.corners[1].x)&&
                (loca.y>=this.corners[3].y&&loca.y<this.corners[0].y));
    }

/*    public static void main(String[] args) throws IOException {
        *//*Grid grid=new Grid(new Point(2.5,4.5));
        grid.getCorners(grid.centerPoint,1);
        System.out.println(grid.corners[2].x+" "+grid.corners[2].y);
       // Grid grid2=grid.shiftGrid(1,-1,1);
       // System.out.println(grid2.corners[2].x+" "+grid2.corners[2].y);
        Point loca=new Point(2.3,4.4);
        System.out.println(grid.isInGrid(loca));*//*
        WRUN wrun=new WRUN(2,2000,1);
        wrun.initWRUN();
      //  UAV uav=new UAV(0,WRUN.fileUAVPathsDir+"u2.txt",5,5,2000,0,200000);
        double e=getE(wrun.uavs[1], wrun.uavs[1].grids_uav.get(3),new Grid(new Point(3.5,1.5)));
        System.out.println (e);
    }*/
}