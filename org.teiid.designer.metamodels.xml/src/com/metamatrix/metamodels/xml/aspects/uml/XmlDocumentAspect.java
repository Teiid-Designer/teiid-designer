/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * SchemaAspect
 */
public class XmlDocumentAspect extends AbstractMetamodelAspect implements UmlPackage {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;

    /**
     * Construct an instance of SchemaAspect.
     * @param entity
     */
    public XmlDocumentAspect(MetamodelEntity entity){
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        String theStereoType = null;

        theStereoType = XmlDocumentPlugin.Util.getString("_UI_XmlDocument_type"); //$NON-NLS-1$
        if ( theStereoType == null ) {
            theStereoType = "XML Document"; //$NON-NLS-1$
        }
            
        // If Virtual, then use mapping class
        EmfResource emfResource = (EmfResource)((EObject)eObject).eResource();
        if (emfResource.getModelAnnotation() != null) {
            ModelType type = emfResource.getModelAnnotation().getModelType();
            if (type.equals(ModelType.VIRTUAL_LITERAL)) {
                theStereoType = XmlDocumentPlugin.Util.getString("_UI_XmlDocument_type"); //$NON-NLS-1$
            } else if (type.equals(ModelType.LOGICAL_LITERAL)) {
                theStereoType = XmlDocumentPlugin.Util.getString("_UI_XmlMessageStructure_type"); //$NON-NLS-1$
            }
        }
        return theStereoType;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            XmlDocument xmlDoc = assertXmlDocument(eObject);
            xmlDoc.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, XmlDocumentPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, XmlDocumentPlugin.PLUGIN_ID, 0, XmlDocumentPlugin.Util.getString("XmlDocumentAspect.Aspect.OK_1"), null);  //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        XmlDocument xmlDoc = assertXmlDocument(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(xmlDoc.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(xmlDoc) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(xmlDoc) );   
                result.append(">> "); //$NON-NLS-1$ 
                result.append(xmlDoc.getName() );   
                break;
            default :
                throw new MetaMatrixRuntimeException(XmlDocumentPlugin.Util.getString("XmlDocumentAspect.Invalid_showMask_for_getSignature__2",showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlPackage.SIGNATURE_NAME);
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 4.2
     */
    public Object getImage(Object eObject) {
        assertXmlDocument(eObject);        
        // get the adapter factory
        final AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
		// lookup item provider for the eobjet
		final IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(eObject,IItemLabelProvider.class);
		// look up image
		return provider.getImage(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     */
    public int getVisibility(Object eObject) {
        return VISIBILITY_PUBLIC;
    }

    protected XmlDocument assertXmlDocument(Object eObject) {
        ArgCheck.isInstanceOf(XmlDocument.class, eObject);
    
        return (XmlDocument)eObject;
    }

}
