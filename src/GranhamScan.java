/**
 * Created by shuqizhu on 12/16/18.
 */
import javafx.geometry.Point2D;
import java.util.*;
import java.io.*;
public class GranhamScan {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the file name");
        String fileName = input.nextLine();
        try {
            ArrayList<String> content = processFile(fileName);
            ArrayList<Point2D> points = processPoint(content);
            ArrayList<Point2D> convexHull = granhamScan(points);

            for(Point2D i: convexHull) {
                System.out.println(i);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<String> processFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        ArrayList<String> content = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null) {
                content.add(text);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not Found");;
        } catch (IOException e) {
            System.out.println("File corrupted");;
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            System.out.println("Error Detected");
        }
        return content;
    }

    private static ArrayList<Point2D> processPoint(ArrayList<String> content)
    throws Exception{
        int pointSize = Integer.parseInt(content.remove(0));
        ArrayList<Point2D> points = new ArrayList<>();
        if(pointSize != content.size()) {
            throw new Exception("File Corrupted");
        }
        else {
            for(String line: content) {
                int length = line.length();

                String[] temp = line.substring(1, length-1).split(",");
                double xCord = Double.parseDouble(temp[0]);
                double yCord = Double.parseDouble(temp[1]);

                Point2D newPoint = new Point2D(xCord, yCord);
                points.add(newPoint);
            }
        }
        return points;
    }


    //actual processing part
    private static ArrayList<Point2D> granhamScan(ArrayList<Point2D> points)
            throws Exception{
        //Find bottom-most
        Collections.sort(points, Comparator.comparingDouble(Point2D::getY));
        Point2D bot = points.remove(0);

        //find start and stop
        Point2D start = bot;
        Point2D end = bot;


        //handle colinear
        ArrayList<Point2D> init = new ArrayList<>();
        double lowY = bot.getY();
        for(Point2D temp: points) {
            if(temp.getY() == lowY) {
                init.add(temp);
                points.remove(temp);
            }
        }
        if(init.size()>0) {
            Collections.sort(init, Comparator.comparingDouble(Point2D::getX));
            if(bot.getX() < init.get(0).getX()) {
                end = bot;
            }
            else {
                end = init.remove(0);
                init.add(bot);
            }
            if(bot.getX() > init.get(init.size()).getX()) {
                start = bot;
            }
            else {
                start = init.remove(init.size()-1);
                init.add(bot);
            }
            Collections.sort(init, Comparator.comparingDouble(Point2D::getX));
        }


        //process horizontal colinear using cot against start
        points = angleSort(points, start);

        //Sort based on angle

        //Loop through

        //Return
        points.add(end);
        if(init.size() > 0) {
            points.addAll(init);
        }

        //colinear test
        if(!colinear(points)) {
            return points;
        }
        else {
            throw new Exception("All points colinear");
        }
    }

    private static boolean colinear(ArrayList<Point2D> points) {
        int result = 0;
        for(int i = 0; i < points.size()-2; i++) {
            result += direction(points.get(i), points.get(i+1), points.get
                    (i+2));
        }
        return result == 0;
    }

    private static ArrayList<Point2D> angleSort (ArrayList<Point2D> points,
                                               Point2D
            start) {
        ArrayList<Double> cotList = calculate(points, start);
        ArrayList<Integer> indexList = angleSortHelper(cotList, start);
        return scan(points, indexList, start);
    }

    private static ArrayList<Point2D> scan(ArrayList<Point2D> points,
                                    ArrayList<Integer> indexList, Point2D
                                            start) {
        Stack<Point2D> stack = new Stack<>();
        stack.push(start);
        stack.push(points.get(indexList.get(0)));

        for (int i = 1; i < points.size(); i++) {

            Point2D head = points.get(indexList.get(i));
            Point2D middle = stack.pop();
            Point2D tail = stack.peek();

            int turn = direction(tail, middle, head);

            switch(turn) {
                case 1:
                    stack.push(middle);
                    stack.push(head);
                    break;
                case -1:
                    i--;
                    break;
                case 0:
                    stack.push(head);
                    break;
            }
        }

        return new ArrayList<Point2D>(stack);
    }

    private static int direction(Point2D a, Point2D b, Point2D c) {
        double crossProduct = ((b.getX() - a.getX()) * (c.getY() - a.getY())) -
                ((b.getY() - a.getY()) * (c.getX() - a.getX()));
        if(crossProduct > 0) {
            return 1;
        }
        else if(crossProduct < 0) {
            return -1;
        }
        return 0;
    }


    private static ArrayList<Double> calculate (ArrayList<Point2D> points,
                                               Point2D start) {
        ArrayList<Double> result = new ArrayList<>();
        for(Point2D temp: points) {
            double cot = (temp.getX() - start.getX())/ (temp.getY() - start.getY());
            result.add(cot);
        }
        return result;
    }

    private static ArrayList<Integer> angleSortHelper (ArrayList<Double>
                                                              cotList, Point2D
            start) {
        ArrayList<Double> cotList1 = new ArrayList<>(cotList);
        ArrayList<Integer> indexList = new ArrayList<>();
        double localMin = Collections.min(cotList1);

        double min = localMin - 1.1;

        for(int i = 0; i < cotList1.size(); i++) {
            int index = cotList1.indexOf(Collections.max(cotList1));
            indexList.add(index);
            cotList1.remove(index);
            cotList1.add(index, min);
        }
        return indexList;
    }
}
