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
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.UMLFactory;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;

/**
 * ForeignKeyAssociationDescriptor
 */
public class Uml2GeneralizationDescriptor extends AbstractAssociationDescriptor {

    private static final String TYPE = Uml2Plugin.getPluginResourceLocator().getString("_UI_Generalization_type"); //$NON-NLS-1$
    private static final String LABEL = Uml2Plugin.getPluginResourceLocator().getString("_UI_Generalization_type"); //$NON-NLS-1$

    private String text;
    private Classifier general;
    private Classifier specific;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of ForeignKeyAssociationDescriptor.
     * 
     * @param eObjects
     */
    public Uml2GeneralizationDescriptor( List eObjects ) {
        super(eObjects);
        this.text = LABEL;
    }

    /**
     * Construct an instance of ForeignKeyAssociationDescriptor.
     * 
     * @param eObjects
     */
    public Uml2GeneralizationDescriptor( final Classifier general,
                                         final Classifier specific ) {
        super(Arrays.asList(new Classifier[] {general, specific}));
        this.text = LABEL;
        this.general = general;
        this.specific = specific;
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
        if (!Uml2GeneralizationProvider.containsValidObjects(eObjects, Uml2GeneralizationProvider.VALID_CLASSES_TYPES)) {
            return false;
        }

        // Return false if there are not two classifiers implicitly or explicitly defined in the list
        final List classifiers = Uml2GeneralizationProvider.getClassifiers(eObjects);
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
        return getResourceLocator().getImage("full/obj16/gen_obj"); //$NON-NLS-1$
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
        final Generalization gen = UMLFactory.eINSTANCE.createGeneralization();
        gen.setGeneral(this.general);
        gen.setSpecific(this.specific);
        specific.getGeneralizations().add(gen);
        return gen;

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
