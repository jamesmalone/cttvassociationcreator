package obanminter;

/**
 * Created by malone on 30/09/2014.
 */
public class Driver {



    public static void main(String [] args){

        SpreadsheetParser parser = new SpreadsheetParser();
        //input file, output file
        parser.parseFile("/Users/malone/Dropbox/CTTV/text mining work/ibd work/Jatin IBD feedback/curated_IBD_JatinPatelreview.txt",
                "file:/Users/malone/ibd_associations_27_oct.owl");

//"file:/Users/malone/Dropbox/CTTV/text mining work/ibd work/Jatin IBD feedback/ibd_associations_27_oct.owl"
    }


}
