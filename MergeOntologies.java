package obanminter;

import com.sun.prism.shader.Solid_ImagePattern_Loader;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.util.OWLOntologyURIChanger;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by malone on 24/03/2015.
 */
public class MergeOntologies {


    /**
     * Merge multiple ontologies such as multiple OBAN OWL files into one
     * @param ontologyIRI the IRI for the new, merged ontology
     * @param outputLocation path to the file to save single merged ontology to
     * @param ontologyLocations one or more paths to the ontology files which are to be merged into one
     *
     */
    public void mergeOntologies(String ontologyIRI, String outputLocation, String ... ontologyLocations) {
        try{

            // specify the URI of the new ontology that will be created.
            IRI mergedOntologyIRI = IRI
                    .create(ontologyIRI);

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();

            int i = 0;

            //load each ontology into manager
            for(String s : ontologyLocations ) {

                File f = new File(s);
                man.loadOntologyFromOntologyDocument(f);

                System.out.println("Loaded ontology: " + f.toString());

                Set<OWLOntology> ontologies = man.getOntologies();

                for(OWLOntology o : ontologies){

                    IRI iri = o.getOntologyID().getOntologyIRI();
                    IRI newiri = IRI.create(iri.toString()+i);

                    OWLOntologyURIChanger changer = new OWLOntologyURIChanger(man);
                    List<OWLOntologyChange> changes = changer.getChanges(o, newiri);
                    man.applyChanges(changes);
                }
                i++;
            }


            OWLOntologyMerger merger = new OWLOntologyMerger(man);

            //merged ontology
            OWLOntology merged = null;// man.createOntology(mergedOntologyIRI);
            merged = merger.createMergedOntology(man, mergedOntologyIRI);

            //create location to save ontology to
            File outputFile = new File(outputLocation);
            FileOutputStream fs = new FileOutputStream(outputFile);
            //save ontology to file as rdf/xml
            man.saveOntology(merged, new RDFXMLOntologyFormat(), fs);
            System.out.println("New merged ontology file saved.");


        }
        catch(OWLOntologyCreationException e){
            System.out.println("Error occured creating ontology during merge " + e.toString());
        }
        catch(OWLOntologyStorageException e){
            System.out.println("Error has occurred when trying to save to file " + e.toString());

        } catch (FileNotFoundException e) {
                System.out.println("Error has occurred when trying to save to file: file not found " + e.toString());
        }

    }


}
