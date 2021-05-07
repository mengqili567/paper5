import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DrawPersonDemo {
    public static Area area;

    public static Area getArea() {
        return area;
    }

    public static void setArea(Area area) {
        DrawPersonDemo.area = area;
    }

    public DrawPersonDemo() {
    }
    public void main() {
        // 创建相框
        JFrame jFrame = new JFrame();
        // 创建画板
        JPanel jpanel = new JPanel() {
            //序列号（可省略）
            private static final long serialVersionUID = 1L;

            // 重写paint方法
           // @Override
            public void paint(Graphics graphics) {
                // 必须先调用父类的paint方法
                super.paint(graphics);
                // 用画笔Graphics，在画板JPanel上画area
                //graphics.drawOval(100, 70, 30, 30);// 头部（画圆形）
                for (int i = 0; i < area.grids_Area.size(); i++) {//画内部的格子
                    graphics.drawLine((int) area.grids_Area.get(i).corners[0].x, (int) area.grids_Area.get(i).corners[0].y, (int) area.grids_Area.get(i).corners[1].x, (int) area.grids_Area.get(i).corners[1].y);// 左臂（画直线）
                    graphics.drawLine((int) area.grids_Area.get(i).corners[1].x, (int) area.grids_Area.get(i).corners[1].y, (int) area.grids_Area.get(i).corners[2].x, (int) area.grids_Area.get(i).corners[2].y);// 左臂（画直线）
                    graphics.drawLine((int) area.grids_Area.get(i).corners[2].x, (int) area.grids_Area.get(i).corners[2].y, (int) area.grids_Area.get(i).corners[3].x, (int) area.grids_Area.get(i).corners[3].y);// 左臂（画直线）
                    graphics.drawLine((int) area.grids_Area.get(i).corners[3].x, (int) area.grids_Area.get(i).corners[3].y, (int) area.grids_Area.get(i).corners[0].x, (int) area.grids_Area.get(i).corners[0].y);
                  //  graphics.drawRect((int) area.grids_Area.get(i).corners[3].x, (int) area.grids_Area.get(i).corners[3].y, (int)Grid.EDGE/2,(int)Grid.EDGE/2);// 身体（画矩形）
                }//区域边缘
                graphics.drawLine((int) area.corners_Area[0].x, (int) area.corners_Area[0].y, (int) area.corners_Area[1].x, (int) area.corners_Area[1].y);// 左臂（画直线）
                graphics.drawLine((int) area.corners_Area[1].x, (int) area.corners_Area[1].y, (int) area.corners_Area[2].x, (int) area.corners_Area[2].y);// 左臂（画直线）
                graphics.drawLine((int) area.corners_Area[2].x, (int) area.corners_Area[2].y, (int) area.corners_Area[3].x, (int) area.corners_Area[3].y);// 左臂（画直线）
                graphics.drawLine((int) area.corners_Area[3].x, (int) area.corners_Area[3].y, (int) area.corners_Area[0].x, (int) area.corners_Area[0].y);// 左臂（画直线）
            }
        };
        //将绘有小人图像的画板嵌入到相框中
        jFrame.add(jpanel);
        // 设置画框大小（宽度，高度），默认都为0
        //jFrame.setSize(10, 10);
        // 将画框展示出来。true设置可见，默认为false隐藏
        jFrame.setVisible(true);
    }
}
