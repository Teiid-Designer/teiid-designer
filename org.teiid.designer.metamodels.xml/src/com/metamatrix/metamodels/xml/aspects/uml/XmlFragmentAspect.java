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
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;

/**
 * SchemaAspect
 */
public class XmlFragmentAspect extends AbstractMetamodelAspect implements UmlPackage {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;

    /**
     * Construct an instance of SchemaAspect.
     * @param entity
     */
    public XmlFragmentAspect(MetamodelEntity entity){
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return "XmlFragment"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            XmlFragment xmlFrag = assertXmlFragment(eObject);
            xmlFrag.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, XmlDocumentPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, XmlDocumentPlugin.PLUGIN_ID, 0, XmlDocumentPlugin.Util.getString("XmlFragmentAspect.Aspect.OK_1"), null);  //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        XmlFragment xmlFrag = assertXmlFragment(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(xmlFrag.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(xmlFrag) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(xmlFrag) );   
                result.append(">> "); //$NON-NLS-1$ 
                result.append(xmlFrag.getName() );   
                break;
            default :
                throw new MetaMatrixRuntimeException(XmlDocumentPlugin.Util.getString("XmlFragmentAspect.Invalid_showMask_for_getSignature__2",showMask)); //$NON-NLS-1$
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
        assertXmlFragment(eObject);        
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

    protected XmlFragment assertXmlFragment(Object eObject) {
        ArgCheck.isInstanceOf(XmlFragment.class, eObject);
    
        return (XmlFragment)eObject;
    }

}
