import java.io.IOException;

public class WRUN {
    static final String fileUAVPathsDir="";//路径文件目录
    String[]Paths=new String[WRUN.UAVNum];
    public static final int UAVNum=6;
    public static final int WSCNum=10;
    public static final double R=150;
    public static UAV[]uavs=new UAV[UAVNum];
    public static WSC[]wscs=new WSC[WSCNum];
    public void initWRUN() throws IOException {
        /*初始化无人机集群*/
        for (int i = 0; i < Paths.length; i++) {
            uavs[i]=new UAV(i,Paths[i]);
        }
        /*初始化WSCs*/

        /*根据uavs的路径网格化地图areas*/
        Area[]areas=new Area[uavs.length];
        Area UAVArea=new Area("UAVsArea");
        for (int i = 0; i <uavs.length ; i++) {
            areas[i]=uavs[i].uavArea;
        }
        UAVArea=Area.mergeArea(areas);
        Area PlaceArea=UAVArea.expandAreaMargin(R);//扩展得到放置区域，已经切割好
        for (int i = 0; i < PlaceArea.grids_Area.size(); i++) {
            PlaceArea.grids_Area.get(i).id=i;
        }
        /*接下来该根据每个UAV的飞行路径和placeArea，筛选出UAVs的gik，gijk*/
        for (int i = 0; i < uavs.length; i++) {
            uavs[i].setGrids_uav(PlaceArea);
            uavs[i].setGrids_uav_wsc(PlaceArea);
        }
    }
}
