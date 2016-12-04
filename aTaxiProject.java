/******************************************************************************
 *  Name: Evan Wood and Elizabeth Haile
 *  Class: ORF467
 * 
 * 
 *  Description: Autonomous taxi simulation
 *
 ******************************************************************************/
import edu.princeton.cs.algs4.In;

public class aTaxiProject {
    
    private String[][] tripArray;
    
    private boolean withinCircuity(int anchor, int a, int b, int c, int d, double circ) {
        
        int anchorX = Integer.parseInt(tripArray[anchor][8]);
        int anchorY = Integer.parseInt(tripArray[anchor][9]);
        
        double distTo1 = Math.sqrt((a - anchorX)*(a - anchorX)+(b - anchorY)*(b - anchorY));
        double distTo2 = Math.sqrt((c - anchorX)*(c - anchorX)+(d - anchorY)*(d - anchorY));
        double distTo3 = Math.sqrt((c - a)*(c - a)+(d - b)*(d - b));
                                       
        if ((1 + circ) * distTo2 < distTo1 + distTo3) {
            System.out.print("****circuity condition*****");
            return true;
        }
        else return false;
    }
    
    
    public void trip() {
        
        In in = new In("OriginPixel34021_1.csv");
        In in1 = new In("OriginPixel34021_1.csv"); // needs to read in a second time
        in.readLine();
        
        //******************************************************************************/
        // READ IN SECOND LINE (first line of data)
        //-------------------------------------------------//
        String line = in.readLine();
        String[] parsedStrings = line.split(",");

        int OXcoord = Integer.parseInt(parsedStrings[8]);
        int OYcoord = Integer.parseInt(parsedStrings[9]);
        
        // DETERMINE HOW MANY TRIPS HAVE SAME ORIGIN COORDINATES
        //-------------------------------------------------//
        
        int count = 1;
        while (in.hasNextLine()) {
           
            line = in.readLine();
            parsedStrings = line.split(",");
            int a = Integer.parseInt(parsedStrings[8]);
            int b = Integer.parseInt(parsedStrings[9]);
            
            if (OXcoord == a && OYcoord == b) {
                count++;
            }
            else {
                break;  // breaks out of while loop
            }
        }
        System.out.println("Number of trips in one pixel: " + count);
        
        //******************************************************************************/     
        System.out.println();
        
        // CREATE A TRIP ARRAY FOR EACH ORIGIN PIXEL
        // ignore first line 
        String line1 = in1.readLine();
        String[] parsedStrings1 = line1.split(",");
        
        // read in second line
        tripArray = new String[count][parsedStrings1.length + 1];
        
        
        for (int i = 0; i < count; i++) {
            line1 = in1.readLine();
            parsedStrings1 = line1.split(",");
            for (int j = 0; j < parsedStrings1.length; j++) {
                tripArray[i][j] = parsedStrings1[j];
                //System.out.print(tripArray[i][j] + " ");
            }
            // add in a boolean column to indicate if this line has been queued
            tripArray[i][parsedStrings1.length] = "false";
        }
        
        for (int j = 0; j < parsedStrings1.length + 1; j++) {
            System.out.print(tripArray[count-1][j] + " ");
        }
        
        System.out.println();
        
        //******************************************************************************/ 
        int anchor = 271; // index of anchor row
        int other = 1;  // index of other row
        double anchorTime = Double.parseDouble(tripArray[anchor][10]); // start time of anchor trip
        double otherTime = Double.parseDouble(tripArray[anchor + other][10]); // start time of other trip
        int passengerCount = 1;
        String bool;
        
        int a = Integer.parseInt(tripArray[anchor][16]); // anchor destination coordinates
        int b = Integer.parseInt(tripArray[anchor][17]);
        System.out.println(a + "  " + b);
        while (otherTime - anchorTime < 300.0) {
            int c = Integer.parseInt(tripArray[anchor + other][16]);
            int d = Integer.parseInt(tripArray[anchor + other][17]);
            bool = tripArray[anchor + other][parsedStrings1.length];
            System.out.print(c + "  " + d);
            
            if (a == c && b == d && bool == "false" || withinCircuity(anchor,a,b,c,d, 0.25)) {
                // mark this trip as having been taken care of
                bool = "true";
                passengerCount++;
                System.out.println("  Add him to the car! He's going to the same place!");
            }
            else System.out.println();
            
            other++;
            otherTime = Double.parseDouble(tripArray[anchor + other][10]);
        }
    }
    
    public static void main(String[] args) {
        aTaxiProject test = new aTaxiProject();
        test.trip();
    }
    
}
