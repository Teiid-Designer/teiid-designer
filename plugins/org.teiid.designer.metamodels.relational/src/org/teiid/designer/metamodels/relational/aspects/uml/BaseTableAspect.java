/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.uml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.LogicalRelationship;
import org.teiid.designer.metamodels.relational.LogicalRelationshipEnd;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.RelationalMetamodelConstants;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Table;


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
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        return RelationalPlugin.getPluginResourceLocator().getString("_UI_BaseTable_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
        try {
            Table dt = assertBaseTable(eObject);
            dt.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, RelationalMetamodelConstants.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, RelationalMetamodelConstants.PLUGIN_ID, 0, RelationalPlugin.Util.getString("Aspect.ok"), null); //$NON-NLS-1$
    }

    @Override
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

    @Override
	public Collection getSupertypes(Object eObject) {
        return new ArrayList();
    }

    @Override
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
                throw new TeiidRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    @Override
	public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    @Override
	public boolean isAbstract(Object eObject) {
        return false;
    }

    protected BaseTable assertBaseTable(Object eObject) {
        CoreArgCheck.isInstanceOf(BaseTable.class, eObject);
    
        return (BaseTable)eObject;
    }

}
