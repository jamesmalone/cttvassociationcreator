package obanminter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by malone on 27/02/2015.
 */
public class ClassWalker {


    public void testWalker(){

        walkClass(IRI.create("http://www.ebi.ac.uk/efo/EFO_0001185"));

    }

    public void walkClass(IRI classIRI) {


        //inner class for visiting restrictions
        class RestrictionVisitor extends OWLClassExpressionVisitorAdapter {

            private final Set<OWLClass> processedClasses;
            private final Set<OWLObjectPropertyExpression> restrictedProperties;
            private final Set<OWLOntology> onts;

            public RestrictionVisitor(Set<OWLOntology> onts) {
                restrictedProperties = new HashSet<OWLObjectPropertyExpression>();
                processedClasses = new HashSet<OWLClass>();
                this.onts = onts;
            }

            public Set<OWLObjectPropertyExpression> getRestrictedProperties() {
                return restrictedProperties;
            }

            @Override
            public void visit(OWLClass desc) {
                if (!processedClasses.contains(desc)) {
                    // If we are processing inherited restrictions then we
                    // recursively visit named supers. Note that we need to keep
                    // track of the classes that we have processed so that we don't
                    // get caught out by cycles in the taxonomy
                    processedClasses.add(desc);
                    for (OWLOntology ont : onts) {
                        for (OWLSubClassOfAxiom ax : ont
                                .getSubClassAxiomsForSubClass(desc)) {
                            ax.getSuperClass().accept(this);
                        }
                    }
                }
            }

            @Override
            public void visit(OWLObjectSomeValuesFrom desc) {
                // This method gets called when a class expression is an existential
                // (someValuesFrom) restriction and it asks us to visit it
                restrictedProperties.add(desc.getProperty());
            }
        }


        System.setProperty("entityExpansionLimit", "1000000000");
        // Get hold of an ontology manager
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // load an ontology from the web
        IRI iri = IRI
                .create("http://www.ebi.ac.uk/efo/efo.owl");
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(iri);

            System.out.println("Loaded ontology: " + ontology);



            //get class
            OWLClass efoOntology = manager.getOWLDataFactory().getOWLClass(classIRI);

            for (OWLSubClassOfAxiom ax : ontology.getSubClassAxiomsForSubClass(efoOntology)) {
                OWLClassExpression superCls = ax.getSuperClass();
                // Ask our superclass to accept a visit from the RestrictionVisitor
                // - if it is an existential restiction then our restriction visitor
                // will answer it - if not our visitor will ignore it
                System.out.println("SuperCls " + superCls);

                //print out all the classes referenced in expressions for this class
                for (OWLClass c : superCls.getClassesInSignature()) {

                    System.out.println("classes in expressions " + c.toString());
                }


                //to interrogate class expressions which are nested eg (property some (property some class))
                if (superCls instanceof OWLObjectSomeValuesFrom) {
                    OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) superCls;
                    OWLClassExpression expression = some.getFiller();
                    if (!expression.isAnonymous()) {
                        System.out.println("first nested expression " + expression.asOWLClass().toString());
                    }
                }

                RestrictionVisitor restrictionVisitor = new RestrictionVisitor(
                        Collections.singleton(ontology));
                superCls.accept(restrictionVisitor);

                Set<OWLObjectPropertyExpression> setex = restrictionVisitor.getRestrictedProperties();

                for (OWLObjectPropertyExpression ex : setex) {
                    System.out.println("object property " + ex.getNamedProperty());


                }

                System.out.println("");
            }


        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

}
