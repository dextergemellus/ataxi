/******************************************************************************
 *  Name: Evan Wood and Elizabeth Haile
 *  Class: ORF467
 * 
 * 
 *  Description: Autonomous taxi simulation
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class ATaxi {

    private static final int TRIP_ARRAY_WIDTH = 19+1+4;
    
    private Queue<double[]> vehicleTrips;
    
    public ATaxi() {
        
        // read in file
        In in = new In("aTaxi1.csv");
        In in1 = new In("aTaxi1.csv");
        in.readLine();
        in1.readLine();
        
        // initialize queue
        vehicleTrips = new Queue<double[]>();
        
        String save = in.readLine(); 
        while (in.hasNextLine()) {
            
            // read in origin pixel for this tripArray
            String[] parsedStrings = save.split(",");
            
            int OXcoord = Integer.parseInt(parsedStrings[8]);
            int OYcoord = Integer.parseInt(parsedStrings[9]);
            
            int count = 1;
            while (in.hasNextLine()) {
           
                String line = in.readLine();
                save = line;
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
            
            tripArray(count, in1);
        }
    }
        
    private void tripArray(int count, In in) {
        
        String[][] tripArray = new String[count][TRIP_ARRAY_WIDTH];
        
        for (int i = 0; i < count; i++) {
            String line1 = in.readLine();
            String[] parsedStrings1 = line1.split(",");
            for (int j = 0; j < parsedStrings1.length; j++) {
                tripArray[i][j] = parsedStrings1[j];
            }
            tripArray[i][parsedStrings1.length] = "not assigned";
        }
        
        int anchor = 0; // initial index of anchor row
        
        for (int i = 0; i < count; i++) {
            
            if (tripArray[anchor][19] == "assigned") {
                
                int other = 1; // index of other row
                
                double anchorTime = Double.parseDouble(tripArray[anchor][10]); // start time of anchor trip
                double otherTime = Double.POSITIVE_INFINITY;
                if (anchor + other < count) {
                    otherTime = Double.parseDouble(tripArray[anchor + other][10]); // start time of other trip
                }
                int passengerCount = 1;
                String assignment;
                
                // anchor destination coordinates
                int a = Integer.parseInt(tripArray[anchor][16]);
                int b = Integer.parseInt(tripArray[anchor][17]);
                
                // initialize aTaxi trip
                double[] aTaxiTrip = new double[7];
                aTaxiTrip[0] = Double.parseDouble(tripArray[anchor][8]); // aTaxiTrip[0] = origin x-coord
                aTaxiTrip[1] = Double.parseDouble(tripArray[anchor][9]); // aTaxiTrip[1] = origin y-coord
                aTaxiTrip[2] = (double) a; // aTaxiTrip[0] = final destination x coordinate
                aTaxiTrip[3] = (double) b; // aTaxiTrip[1] = final y coordinate
                aTaxiTrip[4] = anchorTime + 300.00; // aTaxiTrip[2] = depart time
                aTaxiTrip[5] = Double.parseDouble(tripArray[anchor][18]); // aTaxiTrip[3] = distance of path to final destination
                
                while (otherTime - anchorTime < 300.0) {
                    int c = Integer.parseInt(tripArray[anchor + other][16]);
                    int d = Integer.parseInt(tripArray[anchor + other][17]);
                    assignment = tripArray[anchor + other][19];
                    
                    // check if trip is in walking distance (i.e. same origin, dest. pixel)
                    if (c == Integer.parseInt(tripArray[anchor + other][8]) && 
                        d == Integer.parseInt(tripArray[anchor + other][9])) {
                    }
                    
                    else {
                        
                        // 3 destinations already associated w/ given aTaxi trip                        
                        if (tripArray[anchor][19 + 3] != null) {
                            
                            int x = Integer.parseInt(tripArray[anchor][20]);
                            int y = Integer.parseInt(tripArray[anchor][21]);
                            int x1 = Integer.parseInt(tripArray[anchor][22]);
                            int y1 = Integer.parseInt(tripArray[anchor][23]);
                            
                            if (c == a && d == b && assignment == "not assigned" ||
                                c == x && d == y && assignment == "not assigned" ||
                                c == x1 && d == y1 && assignment == "not assigned") {
                                
                                tripArray[anchor][19] = "assigned";
                                // passenger count: increment
                                // update departure time of aTaxi
                                //aTaxiTrip[2] = Double.parseDouble(tripArray[anchor + other][18]);
                            }
                        }
                        
                        // 2 destinations already associated w/ given aTaxi trip
                        else if (tripArray[anchor][19 + 1] != null) {
                            
                            int x = Integer.parseInt(tripArray[anchor][20]);
                            int y = Integer.parseInt(tripArray[anchor][21]);
                            
                            if (a == c && b == d && assignment == "not assigned" || 
                                c == x && d == y && assignment == "not assigned" || 
                                withinCircuity(anchor, tripArray, aTaxiTrip, a, b, x, y, c, d, 0.25) && assignment == "not assigned") {
                                
                                // mark this trip as having been taken care of
                                tripArray[anchor][19] = "assigned";
                                
                                // update tripArray with 3rd destination
                                tripArray[anchor][22] = String.valueOf(c);
                                tripArray[anchor][23] = String.valueOf(d);
                                
                                // update departure time of aTaxi
                                //aTaxiTrip[2] = Double.parseDouble(tripArray[anchor + other][18]);
                            }
                        }     
                        
                        // only 1 destination associated w/ given aTaxi trip
                        else if (a == c && b == d && assignment == "not assigned" || 
                                 withinCircuity(anchor, tripArray, aTaxiTrip, a,b,c,d, 0.25) && assignment == "not assigned") {
                            
                            // mark this trip as having been taken care of
                            tripArray[anchor + other][19] = "assigned";
                            
                            // update tripArray with 2nd destination
                            tripArray[anchor][19 + 1] = String.valueOf(c);
                            tripArray[anchor][19 + 2] = String.valueOf(d);
                            
                            // update departure time of aTaxi
                            //aTaxiTrip[2] = Double.parseDouble(tripArray[anchor + other][18]);
                        }
                    }
                    
                    other++;
                    otherTime = Double.parseDouble(tripArray[anchor + other][10]);
                }
                vehicleTrips.enqueue(aTaxiTrip);
            }
            anchor++;
        }
    }
    
    private boolean withinCircuity(int anchor, String[][] tripArray, double[] array, int a, int b, int c, int d, double circ) {
        
        int anchorX = Integer.parseInt(tripArray[anchor][8]);
        int anchorY = Integer.parseInt(tripArray[anchor][9]);
        
        double distTo1 = Math.sqrt((a - anchorX)*(a - anchorX)+(b - anchorY)*(b - anchorY));
        double distTo2 = Math.sqrt((c - anchorX)*(c - anchorX)+(d - anchorY)*(d - anchorY));
        double distTo12 = Math.sqrt((c - a)*(c - a)+(d - b)*(d - b));
                                       
        
        // path: origin --> 1 --> 2
        if ((1 + circ) * distTo2 < distTo1 + distTo12) {
            
            // update final destination in tripArray
            array[0] = (double) c;
            array[1] = (double) d;
            
            // add distance of path taken in tripArray
            array[3] = distTo1 + distTo12;
            
            return true;
        }
        
        // path: origin --> 2 --> 1
        else if ((1 + circ) * distTo2 < distTo2 + distTo12) {
            
            // no need to change final destination
            
            // add distance of path taken in tripArray
            array[3] = distTo2 + distTo12;
            
            return true;
        }
        
        else return false;
    }
    
    private boolean withinCircuity(int anchor, String[][] tripArray, double[] array, int a, int b, int c, int d, int e, int f, double circ) {
        
        int anchorX = Integer.parseInt(tripArray[anchor][8]);
        int anchorY = Integer.parseInt(tripArray[anchor][9]);
        
        double distTo1 = Math.sqrt((a - anchorX)*(a - anchorX)+(b - anchorY)*(b - anchorY));
        double distTo2 = Math.sqrt((c - anchorX)*(c - anchorX)+(d - anchorY)*(d - anchorY)); 
        double distTo3 = Math.sqrt((e - anchorX)*(e - anchorX)+(f - anchorY)*(f - anchorY));
        
        double distTo12 = Math.sqrt((c - a)*(c - a)+(d - b)*(d - b));
        double distTo13 = Math.sqrt((e - a)*(e - a)+(f - b)*(f - b));
        double distTo23 = Math.sqrt((c - e)*(c - e)+(d - f)*(d - f));
        
        // path: origin --> 1 --> 2 --> 3
        if ((1 + circ) * distTo3 < distTo1 + distTo12 + distTo23) {
            
            // update final destination to tripArray
            array[0] = (double) e;
            array[1] = (double) f;
            
            // update distance of path taken to tripArray
            array[3] = distTo1 + distTo12 + distTo23;
            
            return true;
        }
        
        // path: origin --> 1 --> 3 --> 2
        else if ((1 + circ) * distTo3 < distTo1 + distTo13 + distTo23) {
            
            // update final destination in tripArray
            array[0] = (double) c;
            array[1] = (double) d;
            
            // update distance of path taken in tripArray
            array[3] = distTo1 + distTo13 + distTo23;
            
            return true;
        }
        
        // path: origin --> 2 --> 1 --> 3
        else if ((1 + circ) * distTo3 < distTo2 + distTo12 + distTo13) {
            
            // update final destination in tripArray
            array[0] = (double) e;
            array[1] = (double) f;
            
            // update distance of path taken in tripArray
            array[3] = distTo2 + distTo12 + distTo13;
            
            return true;
        }
        
        // path: origin --> 2 --> 3 --> 1
        else if ((1 + circ) * distTo3 < distTo2 + distTo23 + distTo13) {
            
            // add final destination to tripArray
            array[0] = (double) a;
            array[1] = (double) b;

            // add distance of path taken to tripArray
            array[3] = distTo2 + distTo23 + distTo13;
            
            return true;
        }
        // path: origin --> 3 --> 1 --> 2
        else if ((1 + circ) * distTo3 < distTo3 + distTo13 + distTo12) {
            
            // add final destination to tripArray
            array[0] = (double) c;
            array[1] = (double) d;

            // add distance of path taken to tripArray
            array[3] = distTo3 + distTo13 + distTo12;
            
            return true;
        }
        
        // path: origin --> 3 --> 2 --> 1
        else if ((1 + circ) * distTo3 < distTo3 + distTo23 + distTo12) {
            
            // add final destination to tripArray
            array[0] = (double) a;
            array[1] = (double) b;

            // add distance of path taken to tripArray
            array[3] = distTo3 + distTo23 + distTo12;
            
            return true;
        }
        
        else return false;
    }
    
    public Queue<double[]> iterate() {
        return vehicleTrips;
    }
    
    public static void main(String[] args) {
        ATaxi test = new ATaxi();
        for (double[] array : test.iterate()) {
            for (int i = 0; i < 4; i++) {
                StdOut.print(array[i] + ", ");
            }
            StdOut.println();
        }
    }
}