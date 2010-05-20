/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.SingleElementSymbol;

/**
 * BuilderTreeProvider is the modeler's content and label provider for the Criteria and
 * Expression builder dialogs in query.iu.  Creating this object will cause it to be
 * hooked up properly to the ElementViewerFactory.
 */
public class BuilderTreeProvider implements ITreeContentProvider, ILabelProvider {

    ILabelProvider emfLabelProvider;

    /**
     * Construct an instance of BuilderTreeProvider.
     */
    public BuilderTreeProvider() {
        ElementViewerFactory.setContentProvider(this);
        ElementViewerFactory.setLabelProvider(this);
        emfLabelProvider = ModelUtilities.getEMFLabelProvider();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        Object[] result = new Object[0];
        if ( parentElement instanceof EObject ) {
            if (parentElement instanceof InputSet) {
                GroupSymbol group = new GroupSymbol(getText(parentElement));
                group.setMetadataID(parentElement);
                return getChildren(group);
            } else if  ( com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable((EObject) parentElement) ) {
                SqlTableAspect tableAspect = (SqlTableAspect) com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject) parentElement);
                result = tableAspect.getColumns((EObject) parentElement).toArray();
            } else if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure((EObject)parentElement)) {
                List inParams = TransformationHelper.getInAndInoutParameters((EObject)parentElement);
                result = new Object[inParams.size()];

                GroupSymbol group = new GroupSymbol(getText(parentElement));
                group.setMetadataID(parentElement);

                for (int i = 0; i < result.length; i++) {
                    result[i] = TransformationSqlHelper.createElemSymbol((EObject)inParams.get(i), group);
                }
            } else if ( com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet( (EObject)parentElement) ) {
                SqlColumnSetAspect colSetAspect = (SqlColumnSetAspect) com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject) parentElement);
                GroupSymbol group = new GroupSymbol(colSetAspect.getFullName((EObject)parentElement));
                group.setMetadataID(parentElement);
                List cols = colSetAspect.getColumns((EObject)parentElement);
                result = new Object[cols.size()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = TransformationSqlHelper.createElemSymbol((EObject) cols.get(i), group);
                }
            }
        } else if ( parentElement instanceof GroupSymbol ) {
            GroupSymbol groupSymbol = (GroupSymbol) parentElement;
            EObject group = null;
            if ( groupSymbol.getMetadataID() instanceof EObject ) {
                // get the object out of the ID and work directly with it
                group = (EObject) groupSymbol.getMetadataID();
                if ( group instanceof InputSet ) {
                    List children = ((InputSet) group).getInputParameters();
                    result = new Object[children.size()];
                    for ( int i=0 ; i<children.size() ; ++i ) {
                        Object child = children.get(i);
                        ElementSymbol elementSymbol = new ElementSymbol(getText(child));
                        elementSymbol.setMetadataID(child);
                        elementSymbol.setGroupSymbol(groupSymbol);
                        result[i] = elementSymbol;
                    }
                } else if ( com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet( group ) ) {
                    SqlColumnSetAspect colSetAspect = (SqlColumnSetAspect) com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(group);
                    List cols = colSetAspect.getColumns(group);
                    result = new Object[cols.size()];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = TransformationSqlHelper.createElemSymbol((EObject) cols.get(i), groupSymbol);
                    }
                } else {
                    result = new Object[group.eContents().size()];
                    for ( int i=0 ; i<result.length ; ++i ) {
                        Object child = group.eContents().get(i);
                        ElementSymbol elementSymbol = new ElementSymbol(getText(child));
                        elementSymbol.setMetadataID(child);
                        elementSymbol.setGroupSymbol(groupSymbol);
                        elementSymbol.setDisplayFullyQualified(true);
                        result[i] = elementSymbol;
                    }
                }
            } else if (groupSymbol.getMetadataID() instanceof TempMetadataID) {
                TempMetadataID tempMetadataID = (TempMetadataID) groupSymbol.getMetadataID();
                List children = tempMetadataID.getElements();
                result = new Object[children.size()];
                for ( int i=0 ; i<result.length ; ++i ) {
                    Object child = children.get(i);
                    ElementSymbol elementSymbol = new ElementSymbol(getText(child));
                    elementSymbol.setMetadataID(child);
                    elementSymbol.setGroupSymbol(groupSymbol);
                    elementSymbol.setDisplayFullyQualified(true);
                    result[i] = elementSymbol;
                }
            } else {
                // use TransformationSqlEditor to resolve the object
                group = TransformationSqlHelper.getGroupSymbolEObject((GroupSymbol) parentElement);
                result = getChildren(group);
                // always create element symbols for the tree
                Object[] aliasedElements = new Object[result.length];
                for ( int i=0 ; i<result.length ; ++i ) {
                    SingleElementSymbol elementSymbol = TransformationSqlHelper.createElemSymbol((EObject) result[i], (GroupSymbol) parentElement);
                    aliasedElements[i] = elementSymbol;
                }
                result = aliasedElements;                
            }
        } else if (parentElement instanceof StoredProcedure) {
            String procName = ((StoredProcedure)parentElement).getGroup().getName();
            EObject element = TransformationSqlHelper.getStoredProcedureEObject((StoredProcedure)parentElement);
            if(element!=null && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(element)) {
                SqlProcedureAspect procAspect = (SqlProcedureAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(element);
                Object resultSet = procAspect.getResult(element);
                if(com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet((EObject)resultSet)) {
                    SqlColumnSetAspect rsAspect = (SqlColumnSetAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)resultSet);
                    String rsName = rsAspect.getName((EObject)resultSet);
                    GroupSymbol rsGroup = new GroupSymbol(procName+'.'+rsName); 
                    rsGroup.setMetadataID(resultSet);

                    result = new Object[1];
                    result[0] = rsGroup;
                }
            }
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        Object result = null;
        if ( element instanceof EObject ) {
            result = ((EObject)element).eContainer();

            if ( result == null ) {
                result = ModelUtilities.getModelResourceForModelObject((EObject) element);
            }
        } else if (element instanceof ElementSymbol) {
        	//This class was apparently not written with the intent that Objects passed
        	//in might be ElementSymbols rather than EObjects, but apparently due to 
        	//changes this can now happen within criteria builder.  So if ElementSymbol, 
        	//returning its GroupSymbol as parent.  BWP 11/24/03
        	ElementSymbol es = (ElementSymbol)element;
        	result = es.getGroupSymbol();
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object obj) {
        if ( obj instanceof GroupSymbol ) {
            EObject group = TransformationSqlHelper.getGroupSymbolEObject((GroupSymbol) obj);
            return emfLabelProvider.getImage(group);
        } else if ( obj instanceof ElementSymbol ) {
            EObject element = TransformationSqlHelper.getElementSymbolEObject((ElementSymbol) obj);
            return emfLabelProvider.getImage(element);
        } else if (obj instanceof StoredProcedure) {
            EObject element = TransformationSqlHelper.getStoredProcedureEObject((StoredProcedure)obj);
            return emfLabelProvider.getImage(element);
        } else if ( obj instanceof MetadataRecord ) {
            return emfLabelProvider.getImage(((MetadataRecord) obj).getEObject());
        }
        return emfLabelProvider.getImage(obj);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object obj) {
        if ( obj instanceof GroupSymbol ) {
            GroupSymbol symbol = (GroupSymbol) obj;
            
            String result = null;
            // if symbol has a non-null definition, then it is an alias:
            if ( symbol.getDefinition() == null ) {
                result = symbol.getName();
            } else {
                result = symbol.getDefinition();
                result += " AS " + symbol.getName(); //$NON-NLS-1$  "AS" is SQL, not English - do not internationalize
            }
            
            return result;
        } else if (obj instanceof ElementSymbol ) {
            return ((ElementSymbol) obj).getName();
        } else if (obj instanceof InputSet) {
            return "InputSet"; //$NON-NLS-1$
        } else if ( obj instanceof ModelResource ) {
            return ModelerCore.getModelEditor().getModelName((ModelResource) obj);
        } if (obj instanceof TempMetadataID) {
            return ((TempMetadataID) obj).getID();
        } else if ( obj instanceof EObject && 
                    (   TransformationHelper.isSqlColumn(obj) || 
                        TransformationHelper.isSqlTable(obj)) ||
                        TransformationHelper.isSqlProcedure(obj) ||
                        TransformationHelper.isSqlProcedureParameter(obj)) {
            return TransformationHelper.getSqlEObjectFullName((EObject)obj);
        } else if( obj instanceof StoredProcedure ) {
            StoredProcedure proc = (StoredProcedure)obj;
            return proc.getGroup().getName();
        }
        return emfLabelProvider.getText(obj);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        Object[] result = new Object[0];
        if ( inputElement instanceof Collection ) {
            result = ((Collection) inputElement).toArray();
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return emfLabelProvider.isLabelProperty(element, property);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {

    }

}
