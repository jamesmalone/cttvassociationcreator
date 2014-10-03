package obanminter;

/**
 * Created by malone on 30/09/2014.
 */
public class Driver {



    public static void main(String [] args){


        SpreadsheetParser parser = new SpreadsheetParser();

        parser.parseFile("/Users/malone/bone_fracture.txt", "file:/Users/malone/cttvassociationcreator/assocs.owl");


    }


}
