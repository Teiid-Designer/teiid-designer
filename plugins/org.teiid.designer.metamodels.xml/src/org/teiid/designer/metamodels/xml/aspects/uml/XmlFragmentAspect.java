/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.metamodels.xml.XmlDocumentPlugin;
import org.teiid.designer.metamodels.xml.XmlFragment;


/**
 * SchemaAspect
 *
 * @since 8.0
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
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        return "XmlFragment"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
        try {
            XmlFragment xmlFrag = assertXmlFragment(eObject);
            xmlFrag.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, XmlDocumentPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, XmlDocumentPlugin.PLUGIN_ID, 0, XmlDocumentPlugin.Util.getString("XmlFragmentAspect.Aspect.OK_1"), null);  //$NON-NLS-1$
    }

    @Override
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
                throw new TeiidRuntimeException(XmlDocumentPlugin.Util.getString("XmlFragmentAspect.Invalid_showMask_for_getSignature__2",showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    @Override
	public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlPackage.SIGNATURE_NAME);
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 4.2
     */
    @Override
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
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     */
    @Override
	public int getVisibility(Object eObject) {
        return VISIBILITY_PUBLIC;
    }

    protected XmlFragment assertXmlFragment(Object eObject) {
        CoreArgCheck.isInstanceOf(XmlFragment.class, eObject);
    
        return (XmlFragment)eObject;
    }

}
