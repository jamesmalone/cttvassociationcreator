package obanminter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import uk.ac.manchester.cs.owl.owlapi.OWLValueRestrictionImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by malone on 30/09/2014.
 */
public class Driver {



    public static void main(String [] args){

        SpreadsheetParser parser = new SpreadsheetParser();
        //input file, output file

        parser.parseFile("/Users/malone/Dropbox/CTTV/OBAN association work/immune/immune_oban_format.txt", "file:/Users/malone/meow.owl");

        //parser.parseFile("/Users/malone/Dropbox/CTTV/OBAN association work/immune/immune_oban_format.txt", "file:/Users/malone/Dropbox/CTTV/OBAN%20association%20work/immune/immune_associations_17_feb.owl");

        //parser.parseFile("/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/curated_OBAN_IBD_merged.txt", "file:/Users/malone/Dropbox/CTTV/OBAN%20association%20work/ibd%20work/Clinician%20IBD%20feedback/ibd_associations_20_feb.owl");

        //parser.parseFile("/Users/malone/Dropbox/CTTV/common to rare/hpo_ORDO_141104_tab.txt", "file:/Users/malone/Dropbox/CTTV/common%20to%20rare/ordo_hpo_mappings.owl");

        //parser.parseFile("/Users/malone/Dropbox/CTTV/common to rare/hpo_ORDO_17Feb15update.txt", "file:/Users/malone/Dropbox/CTTV/common%20to%20rare/OBAN%20rdf/ordo_hpo_mappings_17_feb_15.owl");

//"file:/Users/malone/Dropbox/CTTV/text mining work/ibd work/Jatin IBD feedback/ibd_associations_27_oct.owl"





    }


}
