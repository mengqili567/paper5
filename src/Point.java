public class Point {
    double x;
    double y;
    public static double getDistance(Point p1,Point p2)
    {
        return Math.sqrt(Math.pow(p1.x-p2.x,2)+Math.pow(p1.y-p2.y,2));
    }
    public Point shift(double lr, double ud)
    {
        return new Point(this.x+lr,this.y+ud);
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static void main(String[] args) {
        System.out.printf(Double.toString(getDistance(new Point(1,1),new Point(2,2))));
    }
}
