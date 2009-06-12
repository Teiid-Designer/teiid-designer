/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2.processor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.core.selection.TreeSelection;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.compare.ModelProducer;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.modelgenerator.processor.DatatypeFinder;
import com.metamatrix.modeler.modelgenerator.processor.RelationTracker;
import com.metamatrix.modeler.modelgenerator.uml2.Uml2ModelGeneratorPlugin;

/**
 * Uml2RelationalProcessor
 */
public class Uml2RelationalProcessor implements ModelProducer {

    // =========================================================================
    //                  Constants for execution errors
    // =========================================================================
    public static final int GENERATE_WITH_NO_PROBLEMS               = 70001;
    public static final int GENERATE_WITH_WARNINGS                  = 70002;
    public static final int GENERATE_WITH_ERRORS                    = 70003;
    public static final int GENERATE_WITH_WARNINGS_AND_ERRORS       = 70004;
    public static final int GENERATE_WITH_NO_WARNINGS_AND_ERRORS    = 70005;

    private final List inputModelSelectors;
    private final TreeSelection uml2InputSelections;
    private final ModelSelector relationalOutputModelSelector;
    private final Uml2RelationalOptions options;
    private RelationTracker relationTracker;
    private DatatypeFinder datatypeFinder;
    private final String description;
    private final RelationalFragmentGenerator fragmentGenerator;
    private final List mappingAdapters;

    /**
     * Construct an instance of Uml2RelationalProcessor.
     * 
     */
    public Uml2RelationalProcessor( final TreeSelection uml2InputSelections, final List inputModelSelectors, 
                                    final ModelSelector relationalOutputModelSelector,
                                    final Uml2RelationalOptions options, 
                                    final RelationTracker relationTracker,
                                    final DatatypeFinder datatypeFinder, final String desc,
                                    final RelationalFragmentGenerator fragmentGenerator ) {
        super();
        ArgCheck.isNotNull(uml2InputSelections);
        ArgCheck.isNotNull(inputModelSelectors);
        ArgCheck.isPositive(inputModelSelectors.size());
        ArgCheck.isNotNull(relationalOutputModelSelector);
        ArgCheck.isNotNull(options);
        ArgCheck.isNotNull(relationTracker);
        ArgCheck.isNotNull(datatypeFinder);
        ArgCheck.isNotNull(fragmentGenerator);
        this.uml2InputSelections = uml2InputSelections;
        this.inputModelSelectors = inputModelSelectors;
        this.relationalOutputModelSelector = relationalOutputModelSelector;
        this.relationTracker = relationTracker;
        this.options = options;
        this.datatypeFinder = datatypeFinder;
        this.description = desc != null ? desc : ""; //$NON-NLS-1$
        this.fragmentGenerator = fragmentGenerator;
        this.mappingAdapters = new LinkedList();
    }
    
 
    /**
     * @see com.metamatrix.modeler.compare.ModelProducer#execute(com.metamatrix.modeler.compare.selector.ModelSelector, org.eclipse.core.runtime.IProgressMonitor, java.util.List)
     */
    public void execute(final IProgressMonitor progressMonitor, 
                        final List problems) throws Exception {

        // Set the primary metamodel URI ...
        final ModelAnnotation modelAnnotation = this.relationalOutputModelSelector.getModelAnnotation();
        if ( modelAnnotation == null ) {
            throw new AssertionError(Uml2ModelGeneratorPlugin.Util.getString("Uml2RelationalProcessor.The_model_annotation_for_the_relational_model_may_not_be_null")); //$NON-NLS-1$
        }
        modelAnnotation.setPrimaryMetamodelUri( RelationalPackage.eNS_URI );

        // Generate the fragments and add to the destination selector ...
        this.fragmentGenerator.createModelFragments(this,problems,progressMonitor);
        
    }
    
    /**
     * @see com.metamatrix.modeler.compare.ModelProducer#getOutputSelector()
     */
    public ModelSelector getOutputSelector() {
        return this.relationalOutputModelSelector;
    }

    
    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return
     */
    public DatatypeFinder getDatatypeFinder() {
        return datatypeFinder;
    }

    /**
     * @return
     */
    public List getInputModelSelectors() {
        return inputModelSelectors;
    }

    /**
     * @return
     */
    public Uml2RelationalOptions getOptions() {
        return options;
    }

    /**
     * @return
     */
    public ModelSelector getRelationalOutputModelSelector() {
        return relationalOutputModelSelector;
    }

    /**
     * @return
     */
    public RelationTracker getRelationTracker() {
        return relationTracker;
    }

    /**
     * @return
     */
    public TreeSelection getUml2InputSelections() {
        return uml2InputSelections;
    }
    
    /**
     * @return
     */
    public List getMappingAdapters() {
        return this.mappingAdapters;
    }

}
