/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.uml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.LogicalRelationshipEnd;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalMetamodelConstants;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;

/**
 * TableAspect
 */
public class BaseTableAspect extends RelationalEntityAspect implements UmlClassifier {
    /**
     * Construct an instance of TableAspect.
     * @param entity
     */
    public BaseTableAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_BaseTable_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            Table dt = assertBaseTable(eObject);
            dt.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, RelationalMetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
    }

    public Collection getRelationships(Object eObject) {
        BaseTable table = assertBaseTable(eObject);
        Collection results = new ArrayList();
        // Add all ForeignKeys owned by this table ...
        results.addAll(table.getForeignKeys());
        
        // Add all ForeignKeys referenced by the PrimaryKey owned by this table ...
        final PrimaryKey pk = table.getPrimaryKey();
        if (pk != null) {
            final List fkRefs = pk.getForeignKeys();
            for (Iterator iter = fkRefs.iterator(); iter.hasNext();) {
                ForeignKey fk = (ForeignKey)iter.next();
                if (!results.contains(fk)) {
                    results.add(fk);
                }
            }
        }
       
        // Add the logical relationships for the table
        final List lres = table.getLogicalRelationships();
        for (Iterator iter = lres.iterator(); iter.hasNext();) {
            LogicalRelationshipEnd lre = (LogicalRelationshipEnd)iter.next();
            LogicalRelationship lr = lre.getRelationship();
            if (!results.contains(lr)) {
                results.add(lr);
            }
        }
        return results;
    }

    public Collection getSupertypes(Object eObject) {
        return new ArrayList();
    }

    public String getSignature(Object eObject, int showMask) {
        Table table = assertBaseTable(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(table.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$                
                result.append(table.getName() );        
                break;
            default :
                throw new MetaMatrixRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    public boolean isAbstract(Object eObject) {
        return false;
    }

    protected BaseTable assertBaseTable(Object eObject) {
        CoreArgCheck.isInstanceOf(BaseTable.class, eObject);
    
        return (BaseTable)eObject;
    }

}
