import java.io.IOException;

interface processGrid{
    double[] UAVAreaMargin() throws IOException;
    double[] PlaceAreaMargin(double R) throws IOException;
    void setGrids() throws IOException;
}