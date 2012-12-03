/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.util;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.metadata.IMetadataID;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.TransformationSqlHelper;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.query.ui.builder.util.ElementViewerFactory;

/**
 * BuilderTreeProvider is the modeler's content and label provider for the Criteria and
 * Expression builder dialogs in query.iu.  Creating this object will cause it to be
 * hooked up properly to the ElementViewerFactory.
 *
 * @since 8.0
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
    @Override
	public Object[] getChildren(Object parentElement) {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        
        Object[] result = new Object[0];
        if ( parentElement instanceof EObject ) {
            if (parentElement instanceof InputSet) {
                IGroupSymbol group = factory.createGroupSymbol(getText(parentElement));
                group.setMetadataID(parentElement);
                return getChildren(group);
            } else if  ( org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isTable((EObject) parentElement) ) {
                SqlTableAspect tableAspect = (SqlTableAspect) org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject) parentElement);
                result = tableAspect.getColumns((EObject) parentElement).toArray();
            } else if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure((EObject)parentElement)) {
                List inParams = TransformationHelper.getInAndInoutParameters((EObject)parentElement);
                result = new Object[inParams.size()];

                IGroupSymbol group = factory.createGroupSymbol(getText(parentElement));
                group.setMetadataID(parentElement);

                for (int i = 0; i < result.length; i++) {
                    result[i] = TransformationSqlHelper.createElemSymbol((EObject)inParams.get(i), group);
                }
            } else if ( org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet( (EObject)parentElement) ) {
                SqlColumnSetAspect colSetAspect = (SqlColumnSetAspect) org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject) parentElement);
                IGroupSymbol group = factory.createGroupSymbol(colSetAspect.getFullName((EObject)parentElement));
                group.setMetadataID(parentElement);
                List cols = colSetAspect.getColumns((EObject)parentElement);
                result = new Object[cols.size()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = TransformationSqlHelper.createElemSymbol((EObject) cols.get(i), group);
                }
            }
        } else if ( parentElement instanceof IGroupSymbol ) {
            IGroupSymbol groupSymbol = (IGroupSymbol) parentElement;
            EObject group = null;
            if ( groupSymbol.getMetadataID() instanceof EObject ) {
                // get the object out of the ID and work directly with it
                group = (EObject) groupSymbol.getMetadataID();
                if ( group instanceof InputSet ) {
                    List children = ((InputSet) group).getInputParameters();
                    result = new Object[children.size()];
                    for ( int i=0 ; i<children.size() ; ++i ) {
                        Object child = children.get(i);
                        IElementSymbol elementSymbol = factory.createElementSymbol(getText(child));
                        elementSymbol.setMetadataID(child);
                        elementSymbol.setGroupSymbol(groupSymbol);
                        result[i] = elementSymbol;
                    }
                } else if ( org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet( group ) ) {
                    SqlColumnSetAspect colSetAspect = (SqlColumnSetAspect) org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(group);
                    List cols = colSetAspect.getColumns(group);
                    result = new Object[cols.size()];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = TransformationSqlHelper.createElemSymbol((EObject) cols.get(i), groupSymbol);
                    }
                } else {
                    result = new Object[group.eContents().size()];
                    for ( int i=0 ; i<result.length ; ++i ) {
                        Object child = group.eContents().get(i);
                        IElementSymbol elementSymbol = factory.createElementSymbol(getText(child));
                        elementSymbol.setMetadataID(child);
                        elementSymbol.setGroupSymbol(groupSymbol);
                        elementSymbol.setDisplayFullyQualified(true);
                        result[i] = elementSymbol;
                    }
                }
            } else if (groupSymbol.getMetadataID() instanceof IMetadataID) {
                IMetadataID tempMetadataID = (IMetadataID) groupSymbol.getMetadataID();
                List children = tempMetadataID.getElements();
                result = new Object[children.size()];
                for ( int i=0 ; i<result.length ; ++i ) {
                    Object child = children.get(i);
                    IElementSymbol elementSymbol = factory.createElementSymbol(getText(child));
                    elementSymbol.setMetadataID(child);
                    elementSymbol.setGroupSymbol(groupSymbol);
                    elementSymbol.setDisplayFullyQualified(true);
                    result[i] = elementSymbol;
                }
            } else {
                // use TransformationSqlEditor to resolve the object
                group = TransformationSqlHelper.getGroupSymbolEObject((IGroupSymbol) parentElement);
                result = getChildren(group);
                // always create element symbols for the tree
                Object[] aliasedElements = new Object[result.length];
                for ( int i=0 ; i<result.length ; ++i ) {
                    IExpression elementSymbol = TransformationSqlHelper.createElemSymbol((EObject) result[i], (IGroupSymbol) parentElement);
                    aliasedElements[i] = elementSymbol;
                }
                result = aliasedElements;                
            }
        } else if (parentElement instanceof IStoredProcedure) {
            String procName = ((IStoredProcedure)parentElement).getGroupName();
            EObject element = TransformationSqlHelper.getStoredProcedureEObject((IStoredProcedure)parentElement);
            if(element!=null && org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(element)) {
                SqlProcedureAspect procAspect = (SqlProcedureAspect)org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(element);
                Object resultSet = procAspect.getResult(element);
                if(org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet((EObject)resultSet)) {
                    SqlColumnSetAspect rsAspect = (SqlColumnSetAspect)org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)resultSet);
                    String rsName = rsAspect.getName((EObject)resultSet);
                    IGroupSymbol rsGroup = factory.createGroupSymbol(procName+'.'+rsName); 
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
    @Override
	public Object getParent(Object element) {
        Object result = null;
        if ( element instanceof EObject ) {
            result = ((EObject)element).eContainer();

            if ( result == null ) {
                result = ModelUtilities.getModelResourceForModelObject((EObject) element);
            }
        } else if (element instanceof IElementSymbol) {
        	IElementSymbol es = (IElementSymbol)element;
        	result = es.getGroupSymbol();
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
	public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
	public Image getImage(Object obj) {
        if ( obj instanceof IGroupSymbol ) {
            EObject group = TransformationSqlHelper.getGroupSymbolEObject((IGroupSymbol) obj);
            return emfLabelProvider.getImage(group);
        } else if ( obj instanceof IElementSymbol ) {
            EObject element = TransformationSqlHelper.getElementSymbolEObject((IElementSymbol) obj);
            return emfLabelProvider.getImage(element);
        } else if (obj instanceof IStoredProcedure) {
            EObject element = TransformationSqlHelper.getStoredProcedureEObject((IStoredProcedure)obj);
            return emfLabelProvider.getImage(element);
        } else if ( obj instanceof MetadataRecord ) {
            return emfLabelProvider.getImage(((MetadataRecord) obj).getEObject());
        }
        return emfLabelProvider.getImage(obj);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
	public String getText(Object obj) {
        if ( obj instanceof IGroupSymbol ) {
            IGroupSymbol symbol = (IGroupSymbol) obj;
            
            String result = null;
            // if symbol has a non-null definition, then it is an alias:
            if ( symbol.getDefinition() == null ) {
                result = symbol.getName();
            } else {
                result = symbol.getDefinition();
                result += " AS " + symbol.getName(); //$NON-NLS-1$  "AS" is SQL, not English - do not internationalize
            }
            
            return result;
        } else if (obj instanceof IElementSymbol ) {
            return ((IElementSymbol) obj).getName();
        } else if (obj instanceof InputSet) {
            return "INPUTS"; //$NON-NLS-1$
        } else if ( obj instanceof ModelResource ) {
            return ModelerCore.getModelEditor().getModelName((ModelResource) obj);
        } if (obj instanceof IMetadataID) {
            return ((IMetadataID) obj).getID();
        } else if ( obj instanceof EObject && 
                    (   TransformationHelper.isSqlColumn(obj) || 
                        TransformationHelper.isSqlTable(obj)) ||
                        TransformationHelper.isSqlProcedure(obj) ||
                        TransformationHelper.isSqlProcedureParameter(obj)) {
            return TransformationHelper.getSqlEObjectFullName((EObject)obj);
        } else if( obj instanceof IStoredProcedure ) {
            IStoredProcedure proc = (IStoredProcedure)obj;
            return proc.getGroupName();
        }
        return emfLabelProvider.getText(obj);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
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
    @Override
	public void dispose() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
	public void addListener(ILabelProviderListener listener) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    @Override
	public boolean isLabelProperty(Object element, String property) {
        return emfLabelProvider.isLabelProperty(element, property);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
	public void removeListener(ILabelProviderListener listener) {

    }

}
