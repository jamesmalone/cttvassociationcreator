package obanminter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by malone on 30/09/2014.
 *
 * Class walks through rows in a spreadsheet and mints OBAN associations
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

        int subjectLocation = -1; //location of subject ontology uri (required)
        int objectLocation = -1; //location of object ontology uri (required)
        int pmidLocation = -1; //location of pubmed id column (optional)
        int dateLocation = -1; //location of date when association was made (optional)
        int sourceDBLocation = -1; //location of source of association (optional)
        int freqLocation = -1; //location of frequeny info (optional)
        int creatorNameLocation = -1; //location of person name if source is a person (optional unless source is a person)



        try {
            Scanner sc = new Scanner(new FileReader(filelocation));

            //get first line
            String firstLine = sc.nextLine();
            String[] splitFirstLine = firstLine.split("\t");
            //first row: identify columns we need for rdf from column headers in first line
            for(int i=0; i<splitFirstLine.length; i++) {

                //subject uri
                System.out.println("Testing element "+ i + " " + splitFirstLine[i]);
                if (splitFirstLine[i].toString().toLowerCase().matches("subject_uri")) {
                    subjectLocation = i;
                    System.out.println("Found subject element  "+ i);

                //object uri
                }
                else if (splitFirstLine[i].toString().toLowerCase().matches("object_uri")) {
                    objectLocation = i;
                    System.out.println("Found object element  "+ i);

                //pubmed id
                }
                else if (splitFirstLine[i].toString().toLowerCase().matches("pmid")){
                    System.out.println("Found pubmed id element  "+ i);
                    pmidLocation = i;
                }
                //date id
                else if (splitFirstLine[i].toString().toLowerCase().matches("date")){
                    System.out.println("Found date id element  "+ i);
                    dateLocation = i;
                }
                //database source id
                else if (splitFirstLine[i].toString().toLowerCase().matches("sourcedb")){
                    System.out.println("Found source db id element  "+ i);
                    sourceDBLocation = i;
                }
                //frequency id
                else if (splitFirstLine[i].toString().toLowerCase().matches("frequency")){
                    System.out.println("Found frequency id element  "+ i);
                    freqLocation = i;
                }
                //person name id
                else if (splitFirstLine[i].toString().toLowerCase().matches("creator_name")){
                    System.out.println("Found frequency id element  "+ i);
                    creatorNameLocation = i;
                }

            }

            //if we don't have a subject and object location flag error
            if(subjectLocation == -1 || objectLocation == -1){

                System.out.println("Can not process file: No subject_uri and/or object location header in file.");
            }
            //otherwise continue to read file
            else {

                //prepare ontology to save RDF into
                OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                IRI ontologyIRI = IRI.create("http://cttv.org/associations/");
                IRI documentIRI = IRI.create(outputpath);
                SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
                manager.addIRIMapper(mapper);
                OWLOntology ontology = manager.createOntology(ontologyIRI);

                int i = 0;
                //parse the line to find all input for each association
                while (sc.hasNextLine()) {


                    try {
                        i++;
                        System.out.println("Line:" + i);

                        String line = (sc.nextLine());
                        String[] split = line.split("\t");
                        String pmid = null;
                        String assocDate = null;
                        String sourceDB = null;
                        String freq = null;
                        String name = null;

                        //get subject and object
                        String subject = split[subjectLocation].replaceAll("\\s","");
                        System.out.println("Subject: " + subject);
                        String object = split[objectLocation].replaceAll("\\s","");
                        System.out.println("Object: " + object);

                        if (subject.isEmpty() || object.isEmpty()) {

                            System.out.println("Ingoring line " + i + " as no subject & object");
                        } else {
                            System.out.println("Adding axiom, row:  " + i);

                            if (dateLocation != -1 && dateLocation < split.length) {
                                System.out.println("date " + split[dateLocation]);
                                assocDate = split[dateLocation];
                            }
                            if (sourceDBLocation != -1 && sourceDBLocation < split.length) {
                                System.out.println("source " + split[sourceDBLocation]);
                                sourceDB = split[sourceDBLocation];
                            }
                            if (freqLocation != -1 && freqLocation < split.length) {
                                freq = split[freqLocation];
                            }
                            if (pmidLocation != -1 && pmidLocation < split.length) {
                                System.out.println("pmid " + split[pmidLocation]);
                                pmid = split[pmidLocation];
                            }
                            if (creatorNameLocation!= -1 && creatorNameLocation < split.length) {
                                name = split[creatorNameLocation];
                            }

                            //String object = new StringBuilder().append(split[objectLocation]).toString();

                            // Get hold of a data factory from the manager
                            OWLDataFactory factory = manager.getOWLDataFactory();

                            //mint subject and object assertions
                            this.createOBANAssociation(manager, ontology, factory, subject, object, pmid, assocDate, sourceDB, freq, name);
                        }
                    }
                    catch(Exception e){
                        System.out.println("An error has occurred reading a line from file " + e.toString());
                    }

                }


                //save ontology
                manager.saveOntology(ontology);
                System.out.println("ontology saved");

            }
        }
        catch(Exception e){
           System.out.println("Einen error hast occurred" + e.toString());
        }
    }


    /**
     * create a single association between a subject and object with evidence and any provenance attached to it
     * @param manager
     * @param ontology
     * @param factory
     * @param subject string containing uri
     * @param object string containing uri
     * @param pmid numerical part of pubmed ID only
     * @param assocDate date association was made originally (not date this computational one was formed)
     */
    private void createOBANAssociation(OWLOntologyManager manager, OWLOntology ontology, OWLDataFactory factory, String subject, String object, String pmid, String assocDate, String sourceDB, String freq, String creatorName){


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        //get current date time with Date()
        String date = dateFormat.format(new Date());

        //create IRIs for both subject and object
        IRI subjectIRI = IRI.create(subject);
        IRI objectIRI = IRI.create(object);
        //generate hash for association & provenance URI fragment
        //association URI is generated from a combination of the subject and object URIs
        String assocHash = HashingIdGenerator.generateHashEncodedID(subject+object);
        //create IRI for association instance
        String assocString = new StringBuilder().append("http://purl.obolibrary.org/cttv/").append(assocHash).toString();
        IRI assocIRI = IRI.create(assocString);


        //form the string to hash for the provenance part
        StringBuilder sb = new StringBuilder();
        if(pmid != null && !pmid.isEmpty()){
            sb.append(pmid);
        }
        if(assocDate != null && !assocDate.isEmpty()){
            sb.append(assocDate);
        }
        if(creatorName != null && !creatorName.isEmpty()){
            sb.append(creatorName);
        }
        if(sourceDB != null && !sourceDB.isEmpty()){
            sb.append(sourceDB);
        }

        //the provenance is generated from the
        String provHash = HashingIdGenerator.generateHashEncodedID(sb.toString());
        String provString = new StringBuilder().append("http://purl.obolibrary.org/cttv/").append(provHash).toString();
        IRI provIRI = IRI.create(provString);

        //mint classes
        OWLClass association = factory.getOWLClass(IRI.create("http://purl.org/oban/association"));
        OWLClass provenance = factory.getOWLClass(IRI.create("http://purl.org/oban/provenance"));

        //mint properties used in minting associations
        //mint object properties
        OWLObjectProperty hasSubject = factory.getOWLObjectProperty(IRI.create("http://purl.org/oban/association_has_subject"));
        OWLObjectProperty hasObject = factory.getOWLObjectProperty(IRI.create("http://purl.org/oban/association_has_object"));
        OWLObjectProperty isAbout = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000136"));
        //mint datatype properties
        OWLDataProperty hasAssocCreatedDate = factory.getOWLDataProperty(IRI.create("http://purl.org/oban/date_association_created"));

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
                getOWLObjectPropertyAssertionAxiom(isAbout, provenanceIndividual, associationIndividual);
        manager.addAxiom(ontology, provAssertion);
        OWLDataPropertyAssertionAxiom dateAssertion = factory.
                getOWLDataPropertyAssertionAxiom(hasAssocCreatedDate, provenanceIndividual, date);
        manager.addAxiom(ontology, dateAssertion);


        //add evidence assertion
        OWLObjectProperty hasEvidence = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002558"));
        //eco class for: inference from background scientific knowledge used in manual assertion
        OWLNamedIndividual evidenceIndividual = factory.getOWLNamedIndividual(IRI.create("http://purl.obolibrary.org/obo/ECO_0000306"));
        OWLObjectPropertyAssertionAxiom evidenceAssertion = factory.
                getOWLObjectPropertyAssertionAxiom(hasEvidence, provenanceIndividual, evidenceIndividual);
        manager.addAxiom(ontology, evidenceAssertion);

        //add pubmedid to prov if it exists
        if(pmid != null && !pmid.isEmpty()){
            //create IRI for the pubmed ID
            OWLNamedIndividual pmidIndividual = factory.getOWLNamedIndividual(IRI.create("http://identifiers.org/pubmed/" + pmid));

            //make type of edam pubmedid
            OWLClass edampmidclass = factory.getOWLClass(IRI.create("http://edamontology.org/data_1187"));

            OWLClassAssertionAxiom pmidTypeAssertion = factory.getOWLClassAssertionAxiom(edampmidclass, pmidIndividual);
            manager.addAxiom(ontology, pmidTypeAssertion);

            OWLDataFactory df = manager.getOWLDataFactory();
            OWLAnnotation labelAnno = df.getOWLAnnotation(df.getRDFSLabel(),
                    df.getOWLLiteral("pubmed ID", "en"));
            OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(edampmidclass.getIRI(),
                    labelAnno);
            // Add the axiom to the ontology
            manager.applyChange(new AddAxiom(ontology, ax));

            //mint datatype properties
            OWLObjectProperty hasPubmedID = factory.getOWLObjectProperty(IRI.create("http://purl.org/oban/has_pubmed_id"));

            //make assertion
            OWLObjectPropertyAssertionAxiom pubmedAssertion = factory.
                    getOWLObjectPropertyAssertionAxiom(hasPubmedID, provenanceIndividual, pmidIndividual);
            manager.addAxiom(ontology, pubmedAssertion);
        }

        //add data to prov if it exists
        if(assocDate != null && !assocDate.isEmpty()){
            //mint datatype properties
            OWLDataProperty hasOriginCreatedDate = factory.getOWLDataProperty(IRI.create("http://purl.org/oban/date_orgin_created"));

            //make assertion
            OWLDataPropertyAssertionAxiom assocDateAssertion = factory.
                    getOWLDataPropertyAssertionAxiom(hasOriginCreatedDate, provenanceIndividual, assocDate);
            manager.addAxiom(ontology, assocDateAssertion);
        }


        if(sourceDB != null && !sourceDB.isEmpty()){
            //mint datatype properties
            OWLDataProperty hasSourceDB = factory.getOWLDataProperty(IRI.create("http://purl.org/oban/has_source_db"));

            //make assertion
            OWLDataPropertyAssertionAxiom sourceDBAssertion = factory.
                    getOWLDataPropertyAssertionAxiom(hasSourceDB, provenanceIndividual, sourceDB);
            manager.addAxiom(ontology, sourceDBAssertion);
        }


        if(creatorName != null && !creatorName.isEmpty()){
            //create instance for person name
            OWLNamedIndividual personIndividual = factory.getOWLNamedIndividual(IRI.create("http://purl.org/oban/" + HashingIdGenerator.generateHashEncodedID(creatorName)));
            //mint uri for creator
            OWLClass foafPersonClass = factory.getOWLClass(IRI.create("http://xmlns.com/foaf/spec/#term_Person"));
            //mint datatype properties
            OWLObjectProperty hasSourceDB = factory.getOWLObjectProperty(IRI.create("http://purl.org/oban/has_source_db"));

            OWLClassAssertionAxiom foafPersonTypeAssertion = factory.getOWLClassAssertionAxiom(foafPersonClass, personIndividual);
            manager.addAxiom(ontology, foafPersonTypeAssertion);


            //make assertion
            OWLObjectPropertyAssertionAxiom creatorAssertion = factory.
                    getOWLObjectPropertyAssertionAxiom(hasSourceDB, provenanceIndividual, personIndividual);
            manager.addAxiom(ontology, creatorAssertion);

        }

        if(freq != null && !freq.isEmpty()){
            //mint datatype properties
            OWLDataProperty hasFreq = factory.getOWLDataProperty(IRI.create("http://purl.org/oban/has_frequency"));

            //make assertion
            OWLDataPropertyAssertionAxiom freqAssertion = factory.
                    getOWLDataPropertyAssertionAxiom(hasFreq, provenanceIndividual, freq);
            manager.addAxiom(ontology, freqAssertion);
        }

        System.out.println("Axiom added");

    }



}//end class
