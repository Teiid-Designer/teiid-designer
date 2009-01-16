/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.metamodels.uml2.provider;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Classifier;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.metamodels.uml2.util.Uml2Util;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;

/**
 * ForeignKeyAssociationDescriptor
 */
public class Uml2AssociationDescriptor extends AbstractAssociationDescriptor {

    private static final String TYPE = Uml2Plugin.getPluginResourceLocator().getString("_UI_Association_type"); //$NON-NLS-1$
    private static final String LABEL = Uml2Plugin.getPluginResourceLocator().getString("_UI_Association_type"); //$NON-NLS-1$

    private String text;
    private Classifier type1;
    private boolean end1IsNavigable;
    private AggregationKind end1Aggregation;
    private String end1Name;
    private int end1LowerBound;
    private int end1UpperBound;
    private Classifier type2;
    private boolean end2IsNavigable;
    private AggregationKind end2Aggregation;
    private String end2Name;
    private int end2LowerBound;
    private int end2UpperBound;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of ForeignKeyAssociationDescriptor.
     * 
     * @param eObjects
     */
    public Uml2AssociationDescriptor( List eObjects ) {
        super(eObjects);
        this.text = LABEL;
    }

    /**
     * Construct an instance of ForeignKeyAssociationDescriptor.
     * 
     * @param eObjects
     */
    public Uml2AssociationDescriptor( final Classifier type1,
                                      final boolean end1IsNavigable,
                                      final AggregationKind end1Aggregation,
                                      final String end1Name,
                                      final int end1LowerBound,
                                      final int end1UpperBound,
                                      final Classifier type2,
                                      final boolean end2IsNavigable,
                                      final AggregationKind end2Aggregation,
                                      final String end2Name,
                                      final int end2LowerBound,
                                      final int end2UpperBound ) {
        super(Arrays.asList(new Classifier[] {type1, type2}));
        this.text = LABEL;
        this.type1 = type1;
        this.end1IsNavigable = end1IsNavigable;
        this.end1Aggregation = end1Aggregation;
        this.end1Name = end1Name;
        this.end1LowerBound = end1LowerBound;
        this.end1UpperBound = end1UpperBound;
        this.type2 = type2;
        this.end2IsNavigable = end2IsNavigable;
        this.end2Aggregation = end2Aggregation;
        this.end2Name = end2Name;
        this.end2LowerBound = end2LowerBound;
        this.end2UpperBound = end2UpperBound;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getType()
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#isComplete()
     */
    @Override
    public boolean isComplete() {
        final List eObjects = this.getEObjects();
        if (eObjects == null || eObjects.isEmpty()) {
            return false;
        }

        // Return false if the list contains invalid objects
        if (!Uml2AssociationProvider.containsValidObjects(eObjects, Uml2AssociationProvider.VALID_CLASSES_TYPES)) {
            return false;
        }

        // Return false if there are not two classifiers implicitly or explicitly defined in the list
        final List classifiers = Uml2AssociationProvider.getClassifiers(eObjects);
        if (classifiers.size() != 2) {
            return false;
        }

        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getImage()
     */
    @Override
    public Object getImage() {
        if (end1Aggregation == AggregationKind.SHARED_LITERAL || end2Aggregation == AggregationKind.SHARED_LITERAL) {
            return getResourceLocator().getImage("full/obj16/assocshare_obj"); //$NON-NLS-1$
        } else if (end1Aggregation == AggregationKind.COMPOSITE_LITERAL || end2Aggregation == AggregationKind.COMPOSITE_LITERAL) {
            return getResourceLocator().getImage("full/obj16/assoccomp_obj"); //$NON-NLS-1$
        }
        return getResourceLocator().getImage("full/obj16/assoc_obj"); //$NON-NLS-1$
    }

    private ResourceLocator getResourceLocator() {
        return Uml2EditPlugin.INSTANCE;
    }

    /**
     * @see com.metamatrix.modeler.core.association.AssociationDescriptor#getText()
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @see com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor#canCreate()
     */
    @Override
    public boolean canCreate() {
        final List eObjects = this.getEObjects();
        return Uml2AssociationProvider.containsValidObjects(eObjects, Uml2AssociationProvider.VALID_CLASSES_TYPES);
    }

    /**
     * @see com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor#execute()
     */
    @Override
    public EObject create() {
        if (!isComplete()) {
            return null;
        }
        return Uml2Util.createAssociation(this.type1,
                                          this.end1IsNavigable,
                                          this.end1Aggregation,
                                          this.end1Name,
                                          this.end1LowerBound,
                                          this.end1UpperBound,
                                          this.type2,
                                          this.end2IsNavigable,
                                          this.end2Aggregation,
                                          this.end2Name,
                                          this.end2LowerBound,
                                          this.end2UpperBound);

    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    protected void setText( String string ) {
        this.text = string;
    }

}
