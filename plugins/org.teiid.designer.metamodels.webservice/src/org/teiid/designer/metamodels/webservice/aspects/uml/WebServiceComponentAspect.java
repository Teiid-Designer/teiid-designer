/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;



/** 
 * @since 8.0
 */
public abstract class WebServiceComponentAspect extends AbstractMetamodelAspect implements
                                                                               UmlDiagramAspect {
    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;

    protected WebServiceComponentAspect(final MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Object getImage(final Object eObject) {
        // assert it is a webservice metamodel object
        final EObject webCompObj = assertWebServiceComponent(eObject);
        // get the adapter factory
        final AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
		// lookup item provider for the eobjet
		final IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(webCompObj,IItemLabelProvider.class);
		// look up image
		return provider.getImage(webCompObj);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     * @since 4.2
     */
    @Override
	public int getVisibility(final Object eObject) {
        return VISIBILITY_PUBLIC;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     * @since 4.2
     */
    @Override
	public String getSignature(final Object eObject,
                               final int showMask) {
        WebServiceComponent webServiceComponent = assertWebServiceComponent(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(webServiceComponent.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(webServiceComponent) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(webServiceComponent) );   
                result.append(">> "); //$NON-NLS-1$ 
                result.append(webServiceComponent.getName() );   
                break;
            default :
                throw new TeiidDesignerRuntimeException(WebServiceMetamodelPlugin.Util.getString("WebServiceComponentAspect.0") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     * @since 4.2
     */
    @Override
	public String getEditableSignature(final Object eObject) {
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }    

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    @Override
	public IStatus setSignature(final Object eObject,
                                final String newSignature) {
        try {
            WebServiceComponent webServiceComponent = assertWebServiceComponent(eObject);
            webServiceComponent.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, WebServiceMetamodelPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, WebServiceMetamodelPlugin.PLUGIN_ID, 0, "OK", null); //$NON-NLS-1$
    }    
    
    protected abstract WebServiceComponent assertWebServiceComponent(final Object eObject);
}
