/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.metamodels.xmlservice.XmlServiceComponent;
import com.metamatrix.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;


/** 
 * @since 4.2
 */
public abstract class XmlServiceComponentAspect extends AbstractMetamodelAspect implements
                                                                               UmlDiagramAspect {
    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;

    protected XmlServiceComponentAspect(final MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 4.2
     */
    public Object getImage(final Object eObject) {
        // assert it is a webservice metamodel object
        final EObject xmlCompObj = assertXmlServiceComponent(eObject);
        // get the adapter factory
        final AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
		// lookup item provider for the eobjet
		final IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(xmlCompObj,IItemLabelProvider.class);
		// look up image
		return provider.getImage(xmlCompObj);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     * @since 4.2
     */
    public int getVisibility(final Object eObject) {
        return VISIBILITY_PUBLIC;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     * @since 4.2
     */
    public String getSignature(final Object eObject,
                               final int showMask) {
        XmlServiceComponent XmlServiceComponent = assertXmlServiceComponent(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(XmlServiceComponent.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(XmlServiceComponent) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(XmlServiceComponent) );   
                result.append(">> "); //$NON-NLS-1$ 
                result.append(XmlServiceComponent.getName() );   
                break;
            default :
                throw new MetaMatrixRuntimeException(XmlServiceMetamodelPlugin.Util.getString("XmlServiceComponentAspect.0") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     * @since 4.2
     */
    public String getEditableSignature(final Object eObject) {
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }    

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    public IStatus setSignature(final Object eObject,
                                final String newSignature) {
        try {
            XmlServiceComponent XmlServiceComponent = assertXmlServiceComponent(eObject);
            XmlServiceComponent.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, XmlServiceMetamodelPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, XmlServiceMetamodelPlugin.PLUGIN_ID, 0, "OK", null); //$NON-NLS-1$
    }    
    
    protected abstract XmlServiceComponent assertXmlServiceComponent(final Object eObject);
}
