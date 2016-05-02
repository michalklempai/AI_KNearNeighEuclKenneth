/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aitest;

import static aitest.SimpleMath.dot;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author mklempai
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.print("START (AI TEST - K-nearest-neighbors, Euclidean (L2), Kenneth Wilder)\n");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        System.out.println(dateFormat.format(Calendar.getInstance().getTime()));  // 2014/08/06 16:00:22

        
        // INPUT
        MnistManager mm_train = new MnistManager("train-images.idx3-ubyte", "train-labels.idx1-ubyte");
        MnistManager mm_test = new MnistManager("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte");
        
        
        // images to learn
        int training_set_image_res = 28;  // 28x28 je rozlisenie obrazkov v trenovaceh mnozine
        double [][] training_set = new double[mm_train.getImages().getCount()][training_set_image_res*training_set_image_res];  // mm_train.getImages().getCount() v trenovacich zaznamoch, 28 je rozlisenie obrazka
        int [] training_set_result = new int[mm_train.getImages().getCount()]; 
        
        int training_set_size = 0;
        for (int i = 1;  i <= mm_train.getImages().getCount();  i++) {     
            mm_train.setCurrent(i);  // index of the image that we are interested in
            int[][] image = mm_train.readImage();
            int lbl = mm_train.readLabel();
            
            for (int y = 0;  y < training_set_image_res;  y++) {  // skopirujeme obrazok do nasej struktury
                for (int x = 0;  x < training_set_image_res;  x++) {  
                    training_set[training_set_size][x+y*training_set_image_res] = image[x][y];
                }
            }
            training_set_result[training_set_size] = lbl;  // what's on training image (for example 0 for image with 0, 1 for image with 1, etc.)

            training_set_size++;
        }
        System.out.println("Num of training set:" + training_set_size);
        
        int training_set_dimension = training_set[0].length;  // DIMENSION OF VECTOR FOR TESTING SET

        
        // images to test
        int testing_set_image_res = 28;  // 28x28 je rozlisenie obrazkov v testovacej mnozine
        double [][] testing_set = new double[mm_test.getImages().getCount()][testing_set_image_res*testing_set_image_res]; 
        int [] testing_set_result = new int[mm_test.getImages().getCount()]; 
        
        int testing_set_size = 0;
        for (int i = 1;  i <= mm_test.getImages().getCount();  i++) {     
            mm_test.setCurrent(i);  // index of the image that we are interested in
            int[][] image = mm_test.readImage();
            int lbl = mm_test.readLabel();
            
            for (int y = 0;  y < testing_set_image_res;  y++) {  // skopirujeme obrazok do nasej struktury
                for (int x = 0;  x < testing_set_image_res;  x++) {  
                    testing_set[testing_set_size][x+y*testing_set_image_res] = image[x][y];
                }
            }
            testing_set_result[testing_set_size] = lbl;  // what's on testing image (for example 0 for image with 0, 1 for image with 1, etc.)

            testing_set_size++;
        }
        System.out.println("Num of testing set:" + testing_set_size);
        
        int testing_set_dimension = testing_set[0].length;  // DIMENSION OF VECTOR FOR TRAINING SET
        // END OF INPUT
        

        // INPUT CHECK
        // Note: training_set_dimension must be equal to testing_set_dimension!
        if (training_set.length == 0) {
            System.out.println("Training set is empty!");
            return;
        }
        if (testing_set.length == 0) {
            System.out.println("Testing set is empty!");
            return;
        }
        if (training_set_dimension != testing_set_dimension) {
            System.out.println("Dimensions of training (" + training_set_dimension + ") and testing (" + testing_set_dimension + ") sets must be equal!");
            return;
        }
        if (training_set_image_res != testing_set_image_res) {
            System.out.println("Resolution of training (" + training_set_image_res + ") and testing (" + testing_set_image_res + ") images must be equal!");
            return;
        }
        // END OF INPUT CHECK


        // ALGHORITHM
        int[] num_fail = {0,0,0,0,0,0,0,0,0,0}, num_success = {0,0,0,0,0,0,0,0,0,0};  // number of fail recognitions, number of successful recognitions
        
        for (int i = 0;  i < testing_set_size;  i++) {  // calculate result for all testing images 
            System.out.println("\nTEST[" + (i+1) + "]\n----------------");
            
            
            // find distance of testing_set[i] item to all items in training_set
            List<IndexWithDistance> idxWithDist = new ArrayList<IndexWithDistance>();  
            for (int j = 0;  j < training_set_size;  j++) {
                IndexWithDistance iWD = new IndexWithDistance();
                iWD.index = j;
                iWD.distance = SimpleMath.distance_approx(testing_set[i], training_set[j], training_set_dimension);
                
                idxWithDist.add(iWD);
            }
            
            // sort by distance from lowest to highest
            Collections.sort(
                idxWithDist, 
                new Comparator<IndexWithDistance>() {
                    @Override
                    public int compare(IndexWithDistance lhs, IndexWithDistance rhs) {
                        if (lhs.distance < rhs.distance) return -1; else
                        if (lhs.distance > rhs.distance) return 1; else return 0;
                    }                    
                }
            );            
            /*            
            for(int x = 0;  x < idxWithDist.size();  x++) {  // CHECK!
                System.out.println("dist[" + x + ", " + idxWithDist.get(x).index + ", " + idxWithDist.get(x).distance + "]");
            } //*/
            
            
            // CALCULATE RESULT
            // Calculate result - nearest point value
            double result = training_set_result[idxWithDist.get(0).index];  // value of nearest point
            
            System.out.println("RESULT = " + result + "% (real val = " + testing_set_result[i] + ")\n"); //*/
            
            if (testing_set_result[i] == result) {  // if result is the same as is defined for that testing image
                num_success[testing_set_result[i]]++;
            } else {  // else our result and real value (digit) on image differs
                num_fail[testing_set_result[i]]++;

                int curr_img = i+1;  
                mm_test.setCurrent(curr_img);  // index of the image that we are interested in
                int[][] image = mm_test.readImage();
                int lbl = mm_test.readLabel();

                System.out.println("Label:" + lbl);
                MnistManager.writeImageToPpm(image, "fail_test_idx" + curr_img + "=res" + (int)result + "_real" + lbl + ".ppm"); 
            }
            
            if ((i % 100) == 0) {  // we print actual result every 100-th test
                System.out.println("Successes = "); 
                for (int s = 0;  s < 10;  s++) System.out.print(num_success[s] + (s < 9 ? ", " : "\n")); 
                
                System.out.println("Fails = "); 
                for (int s = 0;  s < 10;  s++) System.out.print(num_fail[s] + (s < 9 ? ", " : "\n")); 
            }
            // END OF CALCULATE RESULT
        }          
        // END OF ALGHORITHM

        
        // OUTPUT
        System.out.println("------------------------\nFINAL RESULT:\n"); 
        System.out.println("Successes = "); 
        for (int s = 0;  s < 10;  s++) System.out.print(num_success[s] + (s < 9 ? ", " : "\n")); 

        System.out.println("Fails = "); 
        for (int s = 0;  s < 10;  s++) System.out.print(num_fail[s] + (s < 9 ? ", " : "\n")); 

        System.out.println("------------------------\n"); 
        System.out.println("Percentage (of successes) = "); 
        for (int s = 0;  s < 10;  s++) System.out.print(SimpleMath.round((double)(num_success[s]*100)/(double)(num_fail[s]+num_success[s]), 2) + "%" + (s < 9 ? ", " : "\n")); 

        System.out.println("\n"); 
        
        System.out.println("Percentage (of fails) = "); 
        for (int s = 0;  s < 10;  s++) System.out.print(SimpleMath.round((double)(num_fail[s]*100)/(double)(num_fail[s]+num_success[s]), 2) + "%" + (s < 9 ? ", " : "\n")); 
        // END OF OUTPUT

        
        System.out.println(dateFormat.format(Calendar.getInstance().getTime()));  // 2014/08/06 16:00:22
        
        System.out.print("END (AI TEST - K-nearest-neighbors, Euclidean (L2), Kenneth Wilder)\n");
    }
    
}
