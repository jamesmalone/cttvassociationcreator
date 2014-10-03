package obanminter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.net.URI;
import java.util.Scanner;

/**
 * Created by malone on 30/09/2014.
 */


public class SpreadsheetParser {

    /**
     * Method to parse tab delimited file
     * Input file is expected to be tab delimited with column headers (and order):
     * subject  object  PMID
     *
     * @param filelocation
     */
    public void parseFile(String filelocation, String outputpath) {

        int subjectLocation = -1; //location of subject ontology uri
        int objectLocation = -1; //location of object ontology uri
        int pmidLocation = 0; //location of pubmed id column


        try {
            Scanner sc = new Scanner(new FileReader(filelocation));

            //get first line
            String firstLine = sc.nextLine();
            String[] splitFirstLine = firstLine.split("\t");
            //first row: identify columns we need for rdf from column headers in first line
            for(int i=0; i<splitFirstLine.length; i++) {

                System.out.println("Testing element "+ i + " " + splitFirstLine[i]);
                if (splitFirstLine[i].toString().toLowerCase().matches("subject")) {
                    subjectLocation = i;
                    System.out.println("Found subject element  "+ i);

                }
                else if (splitFirstLine[i].toString().toLowerCase().matches("ontology-id")) {
                    objectLocation = i;
                    System.out.println("Found ontology-id element  "+ i);

                }
                else if (splitFirstLine[i].toString().toLowerCase().matches("pmid")){
                    System.out.println("Found pubmed id element  "+ i);
                    pmidLocation = i;
                }
            }

            //if we don't have a subject and object location flag error
            if(subjectLocation == -1 || objectLocation == -1){

                System.out.println("Can not process file: No subject and/or object location header in file.");
            }
            //otherwise continue to read file
            else {

                //prepare ontology to save RDF into
                OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                IRI ontologyIRI = IRI.create("http://cttv.org/associations/");
                IRI documentIRI = IRI.create("file:/Users/malone/cttvassociationcreator/assocs.owl");
                SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
                manager.addIRIMapper(mapper);
                OWLOntology ontology = manager.createOntology(ontologyIRI);



                while (sc.hasNextLine()) {
                    String line = (sc.nextLine());
                    String[] split = line.split("\t");

                    String subject = split[subjectLocation];
                    String pmid = split[pmidLocation];

                    String object = new StringBuilder().append("http://purl.obolibrary.org/obo/").append(split[objectLocation]).toString();

                    this.createOBANAssociation(subject, object, pmid, manager, ontology);

                }


                //save ontology
                manager.saveOntology(ontology);

            }
        }
        catch(Exception e){
           System.out.println(e.getMessage());

        }
    }


    /**
     * mint an OBAN style association in RDF

     * @param subject String version of IRI for subject
     * @param object String version of IRI for object
     * @param pmid String of pubmed ID
     */
    public void createOBANAssociation(String subject, String object, String pmid, OWLOntologyManager manager, OWLOntology ontology){

        try {

            /*
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            IRI ontologyIRI = IRI.create("http://cttv.org/associations/");
            IRI documentIRI = IRI.create("file:/Users/malone/cttvassociationcreator/assocs.owl");
            SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
            manager.addIRIMapper(mapper);
            OWLOntology ontology = manager.createOntology(ontologyIRI);

            */

            // Get hold of a data factory from the manager
            OWLDataFactory factory = manager.getOWLDataFactory();

            IRI subjectIRI = IRI.create(subject);
            IRI objectIRI = IRI.create(object);
            //generate hash for association & provenance URI fragment
            String assocHash = HashingIdGenerator.generateHashEncodedID(Math.random()+subject+object);
            String provHash = HashingIdGenerator.generateHashEncodedID(Math.random()+subject+object);
            //create IRI for association instance
            String assocString = new StringBuilder().append("http://purl.obolibrary.org/obo/").append(assocHash).toString();
            IRI assocIRI = IRI.create(assocString);
            String provString = new StringBuilder().append("http://purl.obolibrary.org/obo/").append(provHash).toString();
            IRI provIRI = IRI.create(provString);

            //mint classes
            OWLClass association = factory.getOWLClass(IRI.create("http://purl.org/oban/association"));
            OWLClass provenance = factory.getOWLClass(IRI.create("http://purl.org/oban/provenance"));

            //mint properties
            OWLObjectProperty hasSubject = factory.getOWLObjectProperty(IRI.create("http://purl.org/oban/association_has_subject"));
            OWLObjectProperty hasObject = factory.getOWLObjectProperty(IRI.create("http://purl.org/oban/association_has_object"));
            OWLObjectProperty hasProvenance = factory.getOWLObjectProperty(IRI.create("http://purl.org/oban/has_provenance"));
            OWLDataProperty hasPubmedID = factory.getOWLDataProperty(IRI.create("http://purl.org/oban/has_pubmed_id"));

            //create individuals for subject and object and an association instance
            OWLNamedIndividual subjectIndividual = factory.getOWLNamedIndividual(subjectIRI);
            OWLNamedIndividual objectIndividual = factory.getOWLNamedIndividual(objectIRI);
            OWLNamedIndividual associationIndividual = factory.getOWLNamedIndividual(assocIRI);
            OWLNamedIndividual provenanceIndividual = factory.getOWLNamedIndividual(provIRI);

            //assert types
            OWLClassAssertionAxiom assocTypeAssertion = factory.getOWLClassAssertionAxiom(association, associationIndividual);
            manager.addAxiom(ontology, assocTypeAssertion);
            OWLClassAssertionAxiom provTypeAssertion = factory.getOWLClassAssertionAxiom(provenance, provenanceIndividual);
            manager.addAxiom(ontology, provTypeAssertion);


            //add subject and object to association
            OWLObjectPropertyAssertionAxiom subjectAssertion = factory.
                    getOWLObjectPropertyAssertionAxiom(hasSubject, associationIndividual, subjectIndividual);
            manager.addAxiom(ontology, subjectAssertion);
            OWLObjectPropertyAssertionAxiom objectAssertion = factory.
                    getOWLObjectPropertyAssertionAxiom(hasObject, associationIndividual, objectIndividual);
            manager.addAxiom(ontology, objectAssertion);
            OWLObjectPropertyAssertionAxiom provAssertion = factory.
                    getOWLObjectPropertyAssertionAxiom(hasProvenance, associationIndividual, provenanceIndividual);
            manager.addAxiom(ontology, provAssertion);
            OWLDataPropertyAssertionAxiom pubmedAssertion = factory.
                    getOWLDataPropertyAssertionAxiom(hasPubmedID, provenanceIndividual, pmid);
            manager.addAxiom(ontology, pubmedAssertion);

            System.out.println("Axiom added");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }




}//end class
