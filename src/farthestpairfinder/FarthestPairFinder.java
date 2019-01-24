package farthestpairfinder;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.ArrayList;

public class FarthestPairFinder extends JFrame {

     int pointSize = 5; //size of the points
     int numPoints = 100; 
     
     Point2D[] S = new Point2D[ numPoints ]; //the set S
     static Point2D[] farthestPair = new Point2D[ 2 ]; //the two points of the farthest pair
     
     static ArrayList<Point2D> convexHull = new ArrayList(); //the vertices of the convex hull of S
     
     Color convexHullColour = Color.white;
     Color genericColour = Color.yellow;

    
    //fills S with random points
    public void makeRandomPoints() {
        Random rand = new Random();
 
        for (int i = 0; i < numPoints; i++) {
            int x = 50 + rand.nextInt(500);
            int y = 50 + rand.nextInt(500);
            S[i] = new Point2D( x, y );            
        }        
    }
    
      public void paint( Graphics g){
        Image img = createImage(); 
        g.drawImage(img,8,30,this); 
        
      }
    
    public Image createImage(){
        BufferedImage bufferedImage = new BufferedImage(700,700,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        
        //Paint the background
        g.setColor(Color.black);
        g.fillRect(0,0, 700, 700);
        
        //paint the regular points
        g.setColor(Color.yellow);
        for (int i = 0; i < S.length ; i++){
            g.fillOval((int)S[i].x, (int)S[i].y, pointSize, pointSize);
        }
        
        g.setColor(Color.red);
        for (int i = 0; i < convexHull.size() ; i++){
            g.fillOval((int)convexHull.get(i).x, (int)convexHull.get(i).y, pointSize, pointSize);
        }
        
        //paint the Convex Hull lines 
        g.setColor(Color.blue);
        for (int a = 0; a < convexHull.size();a++){
            if (a == convexHull.size()-1){ //if its the last point in the hull
                g.drawLine((int)convexHull.get(a).x + pointSize/2, (int)convexHull.get(a).y+ pointSize/2, (int)convexHull.get(0).x+ pointSize/2, (int)convexHull.get(0).y + pointSize/2); 
                //connects the last point to the first point in the hull
            }
            
            else{
                g.drawLine((int)convexHull.get(a).x+ pointSize/2, (int)convexHull.get(a).y+ pointSize/2, (int)convexHull.get(a+1).x+ pointSize/2, (int)convexHull.get(a+1).y + pointSize/2);
                //connects the point to the next point in the hull
            }
        }
        
        //paints the farthest distance line and colors the two dots white
        g.setColor(Color.white);
        g.drawLine((int)farthestPair[0].x + pointSize/2, (int)farthestPair[0].y + pointSize/2, (int)farthestPair[1].x + pointSize/2, (int)farthestPair[1].y + pointSize/2);
        g.fillOval((int)farthestPair[0].x , (int)farthestPair[0].y , pointSize, pointSize);
        g.fillOval((int)farthestPair[1].x , (int)farthestPair[1].y , pointSize, pointSize);
        
        return bufferedImage;
    }
    
    public void findConvexHull() {
        
        //Find first Point
        Point2D bottomMost = S[0]; //finding the bottom most point in the diagram 
        for (int i = 1 ; i < S.length ; i++){ 
            if (S[i].y > bottomMost.y){ 
                bottomMost = S[i];
            }
        }
        convexHull.add(bottomMost); //add the point to the Convex Hull ArrayList
        bottomMost.visited = true; //mark lowest point as visited
       
        //Find the other points in the Convex Hull
        Point2D point1 = bottomMost; // the point we are comparing to other points (start from the bottom most one)
        Point2D point2 = S[0]; //set the second point to a random point in the arrray
        Vector initialVector = new Vector (1,0); //initial random vector to start the comparisons 
        
        boolean complete = false; //start with the convex hull incomplete
        while (complete == false){
            double minAngle = Math.PI + 1; //bigger than pi

            for (int i = 0; i < S.length; i++){
                Vector comparisonVector = S[i].subtract(point1); //create new vector 
                double angle = comparisonVector.getAngle(initialVector); //finding angle between the initial vector and the new vector
                
                if (angle<minAngle){
                    minAngle=angle; //finding the smallest angle
                    point2 = S[i]; //the point providing smallest angle becomes the nextpoint to start at
                }
            }
                
            if (point2.visited==false){ //if point2 hasn't been visited before
                point2.visited = true; //change to visited status
                convexHull.add(point2); //add the point to the convex hull lost
                initialVector = point2.subtract(point1); //resets this to the vector to make comparisons to in the next loop
                point1 = point2; 
            }
            
            else{
                complete = true; //if the points has been visited, it means it has reached every point and started to repeat points, so stop the loop
            }
        }
        
    }
    
    public void findFarthestPair_EfficientWay() {
        double maxDistance = 0;
        for (int i =0; i< convexHull.size()-1; i++){
            for (int j=i; j< convexHull.size(); j++){
                double distance = Math.sqrt(Math.pow(convexHull.get(i).x - convexHull.get(j).x, 2) + Math.pow(convexHull.get(i).y-convexHull.get(j).y, 2));
                if (distance>maxDistance){
                    maxDistance=distance; //replace maxDistance if the current distance calculated is larger
                    farthestPair[0] = convexHull.get(i); //add the point to the farthest pair array
                    farthestPair[1] = convexHull.get(j);
                }
            }
        }
    }
    
    public void findFarthestPair_BruteForceWay() {
        double maxDistance = 0;
        for (int i =0; i< S.length-1; i++){
            for (int j=i; j< S.length; j++){
                double distance = Math.sqrt(Math.pow(S[i].x - S[j].x, 2) + Math.pow(S[i].y - S[j].y, 2)); //calculating distance
                if (distance>maxDistance){
                    maxDistance=distance;
                    farthestPair[0] = S[i]; //add points to the farthest pair array
                    farthestPair[1] = S[j];
                }
            }
        }
    }
    
    public static void main(String[] args) {

        //no changes are needed in main().  Just code the blank methods above.
        
        FarthestPairFinder fpf = new FarthestPairFinder();

        fpf.setBackground(Color.black);
        fpf.setSize(700, 700);
        fpf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fpf.makeRandomPoints();
        
        fpf.findConvexHull();
        
        fpf.findFarthestPair_EfficientWay();
        //fpf.findFarthestPair_BruteForceWay();
        
        fpf.setVisible(true); 

    }
}
