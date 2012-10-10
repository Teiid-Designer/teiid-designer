/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.aspects.uml;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlProperty;
import org.teiid.designer.xsd.PluginConstants;
import org.teiid.designer.xsd.util.ModelerXsdUtils;



/** 
 * @since 8.0
 */
public class EnumeratedTypeValueAspect extends AbstractXsdEntityAspect
                                       implements PluginConstants,
                                                  UmlProperty {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public EnumeratedTypeValueAspect(MetamodelEntity theEntity) {
        super(theEntity);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private XSDEnumerationFacet assertEnumeratedValue(Object theObject) {
        if ((theObject instanceof EObject) && ModelerXsdUtils.isEnumeratedTypeValue((EObject)theObject)) {
            return (XSDEnumerationFacet)theObject;
        }
        
        String msg = Util.getString(I18nUtil.getPropertyPrefix(EnumeratedTypeValueAspect.class) + "nullOrWrongType"); //$NON-NLS-1$
        throw new IllegalArgumentException(msg);
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public String getEditableSignature(Object theObject) {
        return getSignature(theObject, UmlProperty.SIGNATURE_NAME);
    }
    
    private Object getNormalizedValue(Object theValue) {
        Object result = theValue;
        
        if (theValue != null) {
            if ((theValue instanceof Collection) && !((Collection)theValue).isEmpty()) {
                result = ((Collection)theValue).iterator().next();
            } else if ((theValue instanceof Object[]) && (((Object[])theValue).length != 0)) {
                result = ((Object[])theValue)[0];
            }
        }
        
        return result;
    }
    
    /** 
     * @see org.teiid.designer.xsd.aspects.uml.AbstractXsdEntityAspect#getSignature(java.lang.Object, int)
     * @since 5.0.2
     */
    @Override
	public String getSignature(Object theObject,
                               int theShowMaskFlag) {
        XSDEnumerationFacet value = assertEnumeratedValue(theObject);
        StringBuffer result = new StringBuffer();

        switch (theShowMaskFlag) {
            case 1 :
            case 17:
                // Name
                result.append(getNormalizedValue(value.getEffectiveValue()));
                break;
            case 2:
            case 18:
                // Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(theObject));
                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
            case 19:
                // Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(theObject));
                result.append(">> "); //$NON-NLS-1$                
                result.append(getNormalizedValue(value.getEffectiveValue()));
                break;
            default:
                // just return name
                result.append(getSignature(theObject, UmlProperty.SIGNATURE_NAME));
                break;
        }

        return result.toString();
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public String getStereotype(Object theObject) {
        return Util.getString(I18nUtil.getPropertyPrefix(EnumeratedTypeValueAspect.class) + "stereoType"); //$NON-NLS-1$
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public boolean isAssociationEnd(Object theProperty) {
        return false;
    }
    
    /** 
     * @see org.teiid.designer.xsd.aspects.uml.AbstractXsdEntityAspect#setSignature(java.lang.Object, java.lang.String)
     * @since 5.0.2
     */
    @Override
	public IStatus setSignature(Object theObject,
                                String theNewSignature) {
        // cannot change a value
        return Status.OK_STATUS;
    }

}
