/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.provider;

import java.util.List;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;

/**
 * ForeignKeyAssociationDescriptor
 */
public class Uml2DependencyDescriptor extends AbstractAssociationDescriptor {

    private static final String TYPE = Uml2Plugin.getPluginResourceLocator().getString("_UI_Dependency_type"); //$NON-NLS-1$
    private static final String LABEL = Uml2Plugin.getPluginResourceLocator().getString("_UI_Dependency_type"); //$NON-NLS-1$

    private String text;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of Uml2DependencyDescriptor.
     * 
     * @param eObjects
     */
    public Uml2DependencyDescriptor( final List eObjects ) {
        super(eObjects);
        this.text = LABEL;
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
        if (!Uml2DependencyProvider.containsValidObjects(eObjects, Uml2DependencyProvider.VALID_CLASSES_TYPES)) {
            return false;
        }

        // Return false if there are not two classifiers implicitly or explicitly defined in the list
        final List classifiers = Uml2DependencyProvider.getClassifiers(eObjects);
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
        return getResourceLocator().getImage("full/obj16/dep_obj"); //$NON-NLS-1$
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
        final String msg = Uml2Plugin.Util.getString("Uml2DependencyDescriptor.Only_subtypes_of_UML_Dependency_can_be_created_1"); //$NON-NLS-1$
        throw new UnsupportedOperationException(msg);
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
