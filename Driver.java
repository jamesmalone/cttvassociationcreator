package obanminter;

/**
 * Created by malone on 30/09/2014.
 */
public class Driver {



    public static void main(String [] args){

        SpreadsheetParser parser = new SpreadsheetParser();
        //input file, output file
        parser.parseFile("/Users/malone/Dropbox/CTTV/text mining work/ibd work/Clinician IBD feedback/curated_OBAN_IBD_JatinPatelreview.txt", "file:/Users/malone/Dropbox/CTTV/text%20mining%20work/ibd%20work/Clinician%20IBD%20feedback/ibd_associations_25_nov.owl");

        //parser.parseFile("/Users/malone/Dropbox/CTTV/common to rare/hpo_ORDO_141104_tab.txt", "file:/Users/malone/Dropbox/CTTV/common%20to%20rare/ordo_hpo_mappings.owl");

//"file:/Users/malone/Dropbox/CTTV/text mining work/ibd work/Jatin IBD feedback/ibd_associations_27_oct.owl"
    }


}
