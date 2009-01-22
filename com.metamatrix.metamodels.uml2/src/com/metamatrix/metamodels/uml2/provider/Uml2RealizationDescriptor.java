/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.provider;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.ComponentRealization;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Realization;
import org.eclipse.uml2.uml.UMLFactory;
import com.metamatrix.metamodels.uml2.Uml2Plugin;

/**
 * ForeignKeyAssociationDescriptor
 */
public class Uml2RealizationDescriptor extends Uml2DependencyDescriptor {

    private static final String TYPE = Uml2Plugin.getPluginResourceLocator().getString("_UI_Realization_type"); //$NON-NLS-1$
    private static final String LABEL = Uml2Plugin.getPluginResourceLocator().getString("_UI_Realization_type"); //$NON-NLS-1$

    private String text;
    private Classifier client;
    private Classifier supplier;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of ForeignKeyAssociationDescriptor.
     * 
     * @param eObjects
     */
    public Uml2RealizationDescriptor( final List eObjects ) {
        super(eObjects);
        this.text = LABEL;
    }

    /**
     * Construct an instance of ForeignKeyAssociationDescriptor.
     * 
     * @param eObjects
     */
    public Uml2RealizationDescriptor( final Classifier client,
                                      final Classifier supplier ) {
        super(Arrays.asList(new Classifier[] {client, supplier}));
        this.text = LABEL;
        this.client = client;
        this.supplier = supplier;
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
        final Realization r = UMLFactory.eINSTANCE.createRealization();
        r.getClients().add(client);
        r.getSuppliers().add(supplier);

        if (r instanceof ComponentRealization) {
            ((ComponentRealization)r).setRealizingClassifier(supplier);
        }

        final Package pkg = client.getPackage();
        pkg.getOwnedMembers().add(r);
        return r;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    @Override
    protected void setText( String string ) {
        this.text = string;
    }

}
