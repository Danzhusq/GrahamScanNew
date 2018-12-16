import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by shuqizhu on 12/16/18.
 */
public class test {
    public static void main(String[] args) {
        Point2D a = new Point2D(0, 3);
        Point2D b = new Point2D(1, 0.5);
        Point2D c = new Point2D(2, 2);
        Point2D d = new Point2D(4, 4);
        ArrayList<Point2D> list = new ArrayList<>();
        list.add(c);
        list.add(b);
        list.add(a);
        list.add(d);
        Collections.sort(list, Comparator.comparingDouble(Point2D::getY));
        System.out.println(list.get(0));
    }
}
