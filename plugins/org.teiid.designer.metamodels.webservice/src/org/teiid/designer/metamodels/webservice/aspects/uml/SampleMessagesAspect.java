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
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlProperty;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.metamodels.webservice.SampleMessages;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;
import org.teiid.designer.metamodels.webservice.provider.WebServicesEditPlugin;



/** 
 * @since 8.0
 */
public class SampleMessagesAspect extends WebServiceComponentAspect implements
                                                                   UmlProperty {

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Object getImage(Object eObject) {
        return WebServicesEditPlugin.INSTANCE.getImage("full/obj16/Column"); //$NON-NLS-1$
    }
    /** 
     * @param entity
     * @since 4.2
     */
    public SampleMessagesAspect(MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.metamodels.webservice.aspects.uml.WebServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected WebServiceComponent assertWebServiceComponent(Object eObject) {
        CoreArgCheck.isInstanceOf(SampleMessages.class, eObject);
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        return WebServiceMetamodelPlugin.Util.getString("_UI_Column_type"); //$NON-NLS-1$
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     */
    @Override
	public boolean isAssociationEnd(Object property) {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
    public IStatus setSignature(Object eObject, String newSignature) {
        return new Status(IStatus.OK, WebServiceMetamodelPlugin.PLUGIN_ID, 0, "OK", null); //$NON-NLS-1$
    }

    @Override
    public String getSignature(Object eObject, int showMask) {
        assertWebServiceComponent(eObject);
        StringBuffer result = new StringBuffer();        
        final String dtName  = DatatypeConstants.BuiltInNames.XML_LITERAL;
        final String name = "xml"; //$NON-NLS-1$

        //case 16 is for properties, which should return an empty string, so 
        //it has been added in to the remaining cases where applicable.
        switch (showMask) {
            case 1 :
            case 17:
                //Name
                result.append(name);
                break;
            case 2 :
            case 18:
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
            case 19:
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );     
                result.append(">> "); //$NON-NLS-1$ 
                result.append(name);        
                break;
            case 4 :
            case 20: 
                //Type
                result.append(dtName);
                break;
            case 5 :
            case 21:
                //Name and type
                result.append(name);
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 6 :
            case 22:
                //Type and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );     
                result.append(">>"); //$NON-NLS-1$                 
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 7 :
            case 23:
                //Name, Stereotype and type
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );     
                result.append(">> "); //$NON-NLS-1$                 
                result.append(name);
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 8 :
            case 24:
                //Initial Value
                result.append(""); //$NON-NLS-1$
                break;
            case 9 :
            case 25:
                //Name and Initial Value
                result.append(name);
                break;
            case 10 :
            case 26 :
                //Initial Value and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">>"); //$NON-NLS-1$
                break;
            case 11 :
            case 27 :
                //Stereotype, Name and Initial Value, 
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append(name);
                break;
            case 12 :
            case 28 :
                //Initial Value and Type
                result.append(dtName);
            break;
            case 13 :
            case 29 :
                //Name, Type, InitialValue 
                result.append(name);
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 14 :
            case 30 :
                //Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">>"); //$NON-NLS-1$
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 15 :
            case 31 :
                //Name, Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$
                result.append(name);
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 16 :
                //Properties
                return (""); //$NON-NLS-1$
            default :
                throw new TeiidRuntimeException(WebServiceMetamodelPlugin.Util.getString("WebServiceComponentAspect.0") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }
}
