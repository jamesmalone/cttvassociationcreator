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
        parser.parseFile("/Users/malone/Dropbox/CTTV/common to rare/hpo_ORDO_25Mar15update.txt", "/Users/malone/Dropbox/CTTV/common to rare/OBAN rdf/ordo_hpo_OBAN_25_mar_15.owl");


        /*
        parser.parseFile("/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_OBAN_Jatin.txt", "/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_Jatin_associations.owl");

        parser.parseFile("/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_OBAN_SG.txt", "/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_SG_associations.owl");

        parser.parseFile("/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_OBAN_MLW.txt", "/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_MLW_associations.owl");


        MergeOntologies merger = new MergeOntologies();
        merger.mergeOntologies("http://cttv.org/associations/", "/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/ibd_2_pheno_associations.owl", "/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_MLW_associations.owl",
                "/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_SG_associations.owl",
                "/Users/malone/Dropbox/CTTV/OBAN association work/ibd work/Clinician IBD feedback/IBD_Jatin_associations.owl");

    */
    }


}
