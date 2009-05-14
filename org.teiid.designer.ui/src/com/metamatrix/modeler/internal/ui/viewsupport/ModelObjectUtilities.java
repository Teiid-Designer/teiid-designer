/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDResourceImpl;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectAdapterFactoryContentProvider;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertyDescriptor;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ModelObjectUtilities is a set of static utility methods for working with EObjects.
 */
public abstract class ModelObjectUtilities {

    public static final String SET_DESCRIPTION = UiConstants.Util.getString("ModelObjectUtilities.setDescription"); //$NON-NLS-1$
    public static final String DELETE = UiConstants.Util.getString("ModelObjectUtilities.delete"); //$NON-NLS-1$
    public static final String DELETES = UiConstants.Util.getString("ModelObjectUtilities.deletePlural"); //$NON-NLS-1$
    public static final String RENAME = UiConstants.Util.getString("ModelObjectUtilities.rename"); //$NON-NLS-1$
    public static final String BACK_SLASH = "/"; //$NON-NLS-1$
    private static final HashMap iconMap = new HashMap();
    private static ComposedAdapterFactory adapterFactory;
    private static final String CREATE = "create";  //$NON-NLS-1$
    private static final String ADAPTER = "Adapter";  //$NON-NLS-1$
    private static final Class[] NO_CLASSES = new Class[0];
    private static final Object[] NO_ARGS = new Object[0];

    public static IPropertySourceProvider propertySourceProvider;

    /**
     * Indicates if the given <code>EObject</code> is contained within a read-only resource.
     * @param theEObject the object being checked
     * @return <code>true</code> if the object is read-only; <code>false</code> otherwise.
     */
    public static boolean isReadOnly(EObject theEObject) {
        // consider it read-only until proven otherwise
        boolean result = true;
        ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(theEObject);
        if ( modelResource != null ) {
            result = ModelUtil.isIResourceReadOnly(modelResource.getResource());
            // Old code.  Now the actions take care of this via ModelObjectAction class
//            // the modelResource must be open in an editor or else it is read-only
//            if ( OpenEditorMap.getInstance().isEditorOpen(modelResource) ) {
//                // then check the read-only status on the file
//                result = modelResource.getResource().isReadOnly();
//            }
        }
        return result;
    }

    /**
     * Helper method to get the UmlAspect given an EObject
     */
    public static MetamodelAspect getUmlAspect(EObject eObject) {
        return AspectManager.getUmlDiagramAspect(eObject);
    }

    /**
     * Helper method to get the SqlAspect given an EObject
     */
    public static MetamodelAspect getSqlAspect(EObject eObject) {
		return AspectManager.getSqlAspect(eObject);
    }

    /**
     * Obtain an IPropertySourceProvider that can display Extension Properties for a selected EObject.
     */
    public static IPropertySourceProvider getEmfPropertySourceProvider() {
        if ( propertySourceProvider == null ) {
        	AdapterFactory factory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            propertySourceProvider = new ModelObjectAdapterFactoryContentProvider(factory);
    	}
        return propertySourceProvider;
    }

    /**
     * Determine if the specified EObject supports the Description property.
     * @param eObject
     * @return
     */
    public static boolean supportsDescription(EObject eObject) {
        if ( eObject.eResource() instanceof XSDResourceImpl ) {
            if ( eObject instanceof XSDConcreteComponent ) {
                return XsdUtil.canAnnotate((XSDConcreteComponent) eObject);
            }
            return false;
        }
        return true;
    }

    /**
     * Get the user description set on the specified model object.
     * @param eObject
     * @return the description on this model object.  will not return null.
     */
    public static String getDescription(EObject eObject) {
        String result = PluginConstants.EMPTY_STRING;

        try {
            String description = null;
            if ( eObject instanceof XSDSimpleTypeDefinition ) {
                DatatypeManager manager = ModelerCore.getDatatypeManager(eObject,true);
                if ( manager.isBuiltInDatatype(eObject) ) {
                    description = manager.getDescription(eObject);
                } else {
                    description = XsdUtil.getDescription((XSDConcreteComponent) eObject);
                }
            } else if ( eObject instanceof XSDConcreteComponent ) {
                description = XsdUtil.getDescription((XSDConcreteComponent) eObject);
            } else {
                description = ModelerCore.getModelEditor().getDescription(eObject);
            }
            if ( description != null ) {
                result = description;
            }
        } catch (ModelerCoreException ex) {
            String message = UiConstants.Util.getString("ModelObjectUtilities.getDescriptionError", eObject.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, ex, message);
        }
        return result;
    }

    /**
     * Set the description on the specified model object.
     * @param eObject
     * @param description the description for this model object.  if null or zero-length, the
     * underlying annotation will be removed from the object.
     */
    public static void setDescription(EObject eObject, String description, Object eventSource) {
        if ( ! ModelObjectUtilities.isReadOnly(eObject) ) {
            boolean requiredStart = ModelerCore.startTxn(SET_DESCRIPTION, eventSource);
            boolean succeeded = false;
            try {
                if ( eObject.eResource() instanceof XSDResourceImpl ) {
                    if ( eObject instanceof XSDConcreteComponent ) {
                        XsdUtil.addUserInfoAttribute((XSDConcreteComponent) eObject, description);
                    }
                } else {
                    ModelerCore.getModelEditor().setDescription(eObject, description);
                }
                succeeded = true;
            } catch (ModelerCoreException ex) {
                String message = UiConstants.Util.getString("ModelObjectUtilities.errorSetDescription", eObject.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, ex, message);
            } finally {
                if(requiredStart){
                    if ( succeeded ) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * Return the virtual model state of the specified model object.
     * @param eObject
     * @return true if model object is in virtual model.
     */
    public static boolean isVirtual(EObject eObject) {
        final Resource resource = eObject.eResource();
        if ( resource instanceof EmfResource ) {
            return ModelType.VIRTUAL_LITERAL.equals(((EmfResource) resource).getModelAnnotation().getModelType());
        } else if( resource == null && eObject.eIsProxy() ) {
            URI theUri= ((InternalEObject)eObject).eProxyURI().trimFragment();
            if( theUri.isFile() ) {
                File newFile = new File(theUri.toFileString());
                XMIHeader header = ModelUtil.getXmiHeader(newFile);
                if( header != null && ModelType.VIRTUAL_LITERAL.equals(ModelType.get(header.getModelType())) )
                    return true;
            }
        }

        return false;
    }

    /**
     * Return the logical model state of the specified model object.
     * @param eObject
     * @return true if model object is in logical model.
     */
    public static boolean isLogical(EObject eObject) {
        final Resource resource = eObject.eResource();
        if ( resource instanceof EmfResource ) {
            return ModelType.LOGICAL_LITERAL.equals(((EmfResource) resource).getModelAnnotation().getModelType());
        } else if( resource == null && eObject.eIsProxy() ) {
            URI theUri= ((InternalEObject)eObject).eProxyURI().trimFragment();
            if( theUri.isFile() ) {
                File newFile = new File(theUri.toFileString());
                XMIHeader header = ModelUtil.getXmiHeader(newFile);
                if( header != null && ModelType.LOGICAL_LITERAL.equals(ModelType.get(header.getModelType())) )
                    return true;
            }
        }

        return false;
    }

    /**
     * Return the extension model state of the specified model object.
     * @param eObject
     * @return true if model object is in extension model.
     */
    public static boolean isExtension(EObject eObject) {
        final Resource resource = eObject.eResource();
        if ( resource instanceof EmfResource ) {
            return ModelType.EXTENSION_LITERAL.equals(((EmfResource) resource).getModelAnnotation().getModelType());
        } else if( resource == null && eObject.eIsProxy() ) {
            URI theUri= ((InternalEObject)eObject).eProxyURI().trimFragment();
            if( theUri.isFile() ) {
                File newFile = new File(theUri.toFileString());
                XMIHeader header = ModelUtil.getXmiHeader(newFile);
                if( header != null && ModelType.EXTENSION_LITERAL.equals(ModelType.get(header.getModelType())) )
                    return true;
            }
        }

        return false;
    }

    /**
     * Return the function model state of the specified model object.
     * @param eObject
     * @return true if model object is in function model.
     */
    public static boolean isFunction(EObject eObject) {
        final Resource resource = eObject.eResource();
        if ( resource instanceof EmfResource ) {
            if( ModelType.UNKNOWN_LITERAL.equals(((EmfResource) resource).getModelAnnotation().getModelType())) {
                // Check the URI
                String pmmURI = ((EmfResource) resource).getModelAnnotation().getPrimaryMetamodelUri();
                if( pmmURI != null && pmmURI.equals(ModelIdentifier.FUNCTION_MODEL_URI) ) {
                    return true;
                }

            }
            // Else return the standard check on ModelType
            return ModelType.FUNCTION_LITERAL.equals(((EmfResource) resource).getModelAnnotation().getModelType());
        } else if( resource == null && eObject.eIsProxy() ) {
            URI theUri= ((InternalEObject)eObject).eProxyURI().trimFragment();
            if( theUri.isFile() ) {
                File newFile = new File(theUri.toFileString());
                XMIHeader header = ModelUtil.getXmiHeader(newFile);
                if( header != null && ModelType.FUNCTION_LITERAL.equals(ModelType.get(header.getModelType())) )
                    return true;
            }
        }

        return false;
    }

    public static boolean isTable(EObject eObject) {
        return TransformationHelper.isSqlTable(eObject);
    }

    public static boolean isExecutable(EObject eObject) {
        // JUST RETURN FALSE if target object is Mapping Class Input Set or XQuery Procedure
        if( (TransformationHelper.isMappingClass(eObject) && !TransformationHelper.isStagingTable(eObject)) ||
            TransformationHelper.isSqlInputSet(eObject) || 
            TransformationHelper.isXQueryProcedure(eObject) ) {
            return false;
        }
        
        boolean hasValidQuery = true;
        if (TransformationHelper.isVirtualSqlTable(eObject) && !TransformationHelper.isXmlDocument(eObject) && !TransformationHelper.tableIsMaterialized(eObject)) {
            hasValidQuery = TransformationHelper.isValidQuery(TransformationHelper.getTransformationMappingRoot(eObject)) ||
            TransformationHelper.isValidSetQuery(TransformationHelper.getTransformationMappingRoot(eObject));
        } else if (TransformationHelper.isOperation(eObject)) {
            hasValidQuery = TransformationHelper.isValidUpdateProcedure(TransformationHelper.getTransformationMappingRoot(eObject));
        }
        
        return hasValidQuery
               && ((!TransformationHelper.isXmlDocument(eObject) && !TransformationHelper.tableIsMaterialized(eObject) && TransformationHelper.isSqlTable(eObject)) || TransformationHelper.isSqlProcedure(eObject) ); 
    }

    /**
     * Not appropriate to use for Web Service operations.
     */
    public static String getSQL(EObject eObject, Object[] params, List accessPatternColumns) {

        if (TransformationHelper.isSqlTable(eObject) || TransformationHelper.isXmlDocument(eObject)) {
            StringBuffer executeSQL = new StringBuffer();
            executeSQL.append("select * from "); //$NON-NLS-1$
            executeSQL.append(TransformationHelper.getSqlEObjectFullName(eObject)); 

            if (accessPatternColumns != null && !accessPatternColumns.isEmpty()) {
                executeSQL.append(" where "); //$NON-NLS-1$

                for (int size = accessPatternColumns.size(), i = 0; i < size; ++i) {
                    EObject column = (EObject)accessPatternColumns.get(i);
                    
                    // add SQL for column
                    executeSQL.append(TransformationHelper.getSqlEObjectFullName(column));
                    executeSQL.append((params[i] == null) ? " IS NULL" : " = ?"); //$NON-NLS-1$ //$NON-NLS-2$

                    if ((i + 1) < size) {
                        executeSQL.append(" and "); //$NON-NLS-1$
                    }
                }
            }
            return executeSQL.toString();
        } else if (TransformationHelper.isSqlProcedure(eObject)) {
            StringBuilder sb = new StringBuilder();
            sb.append("select * from ( exec ").append(TransformationHelper.getSqlEObjectFullName(eObject)).append("("); //$NON-NLS-1$ //$NON-NLS-2$
            if (params != null && params.length > 0) {
                for (int i = 0 ; i < params.length-1; i++) {
                    sb.append("?,"); //$NON-NLS-1$
                }
                sb.append("?"); //$NON-NLS-1$
            }
            sb.append(") ) AS X_X"); //$NON-NLS-1$
            return sb.toString();
        }
        return null;
    }

    public static String[] getDependentPhysicalModelNames(EObject eObject) throws ModelWorkspaceException {
        ArrayList<String> names = new ArrayList<String>();

        // given the object get all the dependent physical models that this
        // is dependent upon
        ArrayList<ModelResource> physicals = new ArrayList<ModelResource>();
        ModelResource model = ModelUtilities.getModelResourceForModelObject(eObject);
        if( model != null ) {
            if( ModelUtilities.isPhysical(model) ) {
                physicals.add(model);
            }
            ModelUtilities.getDependentPhysicalModelResources(model, physicals);
        }
        for (ModelResource physical:physicals) {
            names.add(physical.getItemName());
        }
        return names.toArray(new String[names.size()]);
    }
    /**
     * Determine if the specified EObject is in the primary metamodel for it's model.
     *
     * @param eObject
     * @return
     */
    public static boolean isPrimaryMetamodelObject(EObject eObject) {
        MetamodelDescriptor descriptor = ModelerCore.getModelEditor().getMetamodelDescriptor(eObject);
        if ( descriptor != null && descriptor.isPrimary() ) {
            return true;
        }
        return false;
    }

    public static List getFeaturePropertyList(EObject eObject) {
        List features = new ArrayList();

        // Let's try it's features.
        IPropertySourceProvider provider = ModelUtilities.getEmfPropertySourceProvider();
        IPropertySource source = provider.getPropertySource(eObject);
        IPropertyDescriptor[] descriptors = source.getPropertyDescriptors();
        ModelObjectPropertyDescriptor propertyDescriptor = null;
        if( descriptors != null ) {
            for(int i=0; i<descriptors.length; i++ ) {
                if( descriptors[i] instanceof ModelObjectPropertyDescriptor ) {
                    propertyDescriptor = (ModelObjectPropertyDescriptor)descriptors[i];
                    Object genericFeature = propertyDescriptor.getFeature();
                    if (genericFeature instanceof EStructuralFeature) {
                        final EStructuralFeature feature = (EStructuralFeature)genericFeature;
                        final EClassifier eType = feature.getEType();
                        if ( ! (eType instanceof EDataType) ) {
                            Object propertyValue = eObject.eGet(feature);
                            if( propertyValue instanceof List ) {
                                List propertyList = (List)propertyValue;
                                Iterator iter = propertyList.iterator();
                                EObject nextEObject = null;
                                while( iter.hasNext() ) {
                                    nextEObject = (EObject)iter.next();
                                    features.add(nextEObject);
                                }
                            } else if( propertyValue instanceof EObject ) {
                                features.add(propertyValue);
                            }
                        }
                    }
                }
            }
        }

        if( features.isEmpty() )
            return Collections.EMPTY_LIST;

        return features;
    }

    public static String getRelativePath(EObject eObject) {
        return ModelerCore.getModelEditor().getModelRelativePath(eObject).toString();
    }

    public static String getTrimmedRelativePath(EObject eObject) {
        IPath relativePath = ModelerCore.getModelEditor().getModelRelativePath(eObject);
        String relativePathString = relativePath.toString();
        int indexOfLastDot = relativePathString.lastIndexOf('/');
        if( indexOfLastDot >= 0 )
            return relativePathString.substring(0, indexOfLastDot);
        return null;
    }

    public static String getTrimmedFullPath(EObject eObject) {
        IPath fullPath = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(eObject);
        String fullPathString = fullPath.toString();
        int indexOfLastDot = fullPathString.lastIndexOf('/');
        if( indexOfLastDot >= 0 )
            return fullPathString.substring(0, indexOfLastDot);
        return fullPathString;
    }

    public static boolean shareCommonParent(List eObjectList) {
        if( eObjectList == null || eObjectList.isEmpty() )
            return false;

        if( eObjectList.size() == 1 )
            return true;

        boolean sameParent = true;

        Object firstParent = ((EObject)eObjectList.get(0)).eContainer();
        int nObjects = eObjectList.size();
        for(int i=1; i<nObjects; i++) {
            if( ((EObject)eObjectList.get(0)).eContainer().equals(firstParent)) {
                //allOK
            } else {
                sameParent = false;
                break;
            }
        }

        return sameParent;
    }

    /**
     * Returns whether a given EObject is a descendant of another EObject.  Also
     * returns true if the two are the same object.
     *
     * @param  possibleAncestor		possible ancestor object
     * @param  possibleDescendant	possible descendant object
     * @returns true if descendant
     */
  	public static boolean isDescendant(EObject possibleAncestor,
  			EObject possibleDescendant) {
  		boolean isDescendant = false;
  		if ((possibleAncestor != null) && (possibleDescendant != null)) {
  			if (possibleAncestor == possibleDescendant) {
  				isDescendant = true;
  			} else {
  				EObject curParent = possibleDescendant.eContainer();
  				while ((!isDescendant) && (curParent != null)) {
  					if (curParent == possibleAncestor) {
  						isDescendant = true;
  					} else {
  						curParent = curParent.eContainer();
  					}
  				}
  			}
  		}
  		return isDescendant;
  	}

    public static boolean isJdbcSource(EObject eObject) {
        return eObject instanceof JdbcSource;
    }
    public static boolean isNonDrawableEObject(EObject eObject) {
        boolean result = false;

        if( isJdbcSource(eObject) ) {
            result = true;
        }

        return result;
    }

    // ----------------------------------------------------------
    // Wrapped ModelEditor methods.....
    // ----------------------------------------------------------


    public static void delete(final EObject eObject, final boolean significance, final boolean undoable, final Object source) {
        ArgCheck.isNotNull(eObject);
        // Call the checkResource method call with "TRUE" default
        delete(eObject, significance, undoable, source, true);

    }

    public static void delete(final EObject eObject, final boolean significance, final boolean undoable, final Object source, final boolean checkResource) {
        ArgCheck.isNotNull(eObject);

        boolean requiredStart = ModelerCore.startTxn(significance, undoable, DELETE, source);
        boolean succeeded = false;
        try {
            ModelerCore.getModelEditor().delete(eObject, checkResource);
            succeeded = true;
        } catch (ModelerCoreException ex) {
            String message = UiConstants.Util.getString("ModelObjectUtilities.errorDelete", eObject.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, ex, message);
        } finally {
            if(requiredStart){
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

    }

    public static void delete(final List eObjectList, final boolean significance, final boolean undoable, final Object source) {
//      Call the checkResource method call with "TRUE" default
        delete(eObjectList, significance, undoable, source, true);
    }

    public static void delete(final List eObjectList, final boolean significance, final boolean undoable, final Object source, final boolean checkResource) {

        boolean requiredStart = ModelerCore.startTxn(significance, undoable, DELETES, source);
        boolean succeeded = false;
        try {
            Iterator iter = eObjectList.iterator();
            EObject nextEObject = null;
            while( iter.hasNext()) {
                nextEObject = (EObject)iter.next();
                ModelerCore.getModelEditor().delete(nextEObject, checkResource);
            }
            succeeded = true;
        } catch (ModelerCoreException ex) {
            String message = UiConstants.Util.getString("ModelObjectUtilities.errorDelete", eObjectList.toArray()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, ex, message);
        } finally {
            if(requiredStart){
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    public static void rename(EObject eObject, String newName, Object source) {
        ArgCheck.isNotNull(eObject);

        boolean requiredStart = ModelerCore.startTxn(
            PluginConstants.Transactions.SIGNIFICANT,
            PluginConstants.Transactions.UNDOABLE,
            RENAME,
            source);
        boolean succeeded = false;
        try {
            if( !DatatypeUtilities.renameSqlColumn(eObject, newName) )
                ModelerCore.getModelEditor().rename(eObject, newName);
            succeeded = true;
        } catch (ModelerCoreException ex) {
            String message = UiConstants.Util.getString("ModelObjectUtilities.errorRename", eObject.toString()); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, ex, message);
        } finally {
            if(requiredStart){
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

	public static Image getImageFromObject(final Object object) {
        return ExtendedImageRegistry.getInstance().getImage(object);
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public static Image getImage( EClass eClass ) {
        Image result = null;
        if ( adapterFactory == null ) {
            adapterFactory
                = (ComposedAdapterFactory) ModelerCore.getMetamodelRegistry().getAdapterFactory();
        }

        if (eClass != null)  {
            result = (Image) iconMap.get(eClass);
            if ( result == null ) {
                Adapter adapter = null;
                EPackage ePackage = eClass.getEPackage();
                Collection types = new ArrayList();
                types.add(ePackage);
                types.add(IItemLabelProvider.class);
                AdapterFactory delegateAdapterFactory = adapterFactory.getFactoryForTypes(types);
                if (delegateAdapterFactory != null) {
                    String methodName = CREATE + eClass.getName() + ADAPTER;
                    Method m = null;
                    try {
                        m = delegateAdapterFactory.getClass().getMethod(methodName, NO_CLASSES);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    if ( m != null ) {
                        try {
                            adapter = (Adapter) m.invoke(delegateAdapterFactory, NO_ARGS);
                        } catch (IllegalArgumentException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                        }
                        if ( adapter instanceof ItemProviderAdapter ) {

                            Object o = null;
                            // For most cases, the item providers can handle an eClass.
                            // However some may not.  In this case, like the XSDSimpleTypeDefinitionItemProvider,
                            // it cannot.  This will cause a ClassCastException. In these cases, we new up a temporary
                            // eObject and pass it in to satisfy the method.
                            try {
                                  o = ((ItemProviderAdapter)adapter).getImage(eClass);
                            } catch (ClassCastException cce) {
                                EObject eObject = eClass.getEPackage().getEFactoryInstance().create(eClass);
                                o = ((ItemProviderAdapter)adapter).getImage(eObject);
                            }

                            if ( o instanceof Image ) {
                                result = (Image) o;
                            } else if ( o instanceof URL ) {
                                result = ExtendedImageRegistry.getInstance().getImage(o);
                            }

                        }
                    }
                }
                if ( result != null ) {
                    iconMap.put(eClass, result);
                }
            }
        }
        return result;
    }

    public static EObject getMarkedEObject(IMarker iMarker) {
        EObject target = null;
        String uri = (String)MarkerUtilities.getMarkerAttribute(iMarker, ModelerCore.MARKER_URI_PROPERTY);

        if( uri != null ) {
            URI theURI = URI.createURI(uri);
            if( theURI != null ) {
                try {
                    target = ModelerCore.getModelContainer().getEObject(theURI, true);
                    // Need to
                } catch (CoreException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return target;
    }



    public static String getUuid(EObject eObj) {
        String fullUri = ModelerCore.getModelEditor().getUri(eObj).toString();
        if( fullUri != null ) {
            int index = fullUri.lastIndexOf(BACK_SLASH) + 1;
            if( index <= fullUri.length())
                return fullUri.substring(index);
        }

        return null;
    }

    public static String getFullUuid(EObject eObj) {
        return ModelerCore.getModelEditor().getUri(eObj).toString();
    }

    public static EObject getRealEObject(EObject eObj) {
        EObject realEObj = null;
        if( eObj != null ) {
            try {
                if( eObj.eIsProxy() ) {
                    // NOTE:  EcoreUtil.resolve() will
                    realEObj = EcoreUtil.resolve(eObj, ModelerCore.getModelContainer());
                    // note that it may be valid or desireable for realEObj to still
                    //  be a proxy, especially in cases where the eobject is not available
                    //  (in a closed project, etc)
                } else {
                    realEObj = eObj;
                } // endif -- starting is proxy
                if( realEObj.eIsProxy() )
                    realEObj = null;
            } catch (CoreException ce) {
                UiConstants.Util.log(IStatus.ERROR, ce, ce.getMessage());
            } // endtry
        } // endif -- eobj not null

        return realEObj;
    }

    public static boolean isStale(EObject eObj) {
        ArgCheck.isNotNull(eObj);
        return eObj.eResource() == null;
    }

    /** Check to see if the specified IResource is or was associated
      *  with any EObjects in the collection.  If the resource has been deleted
      *  or is no longer available, check the EObjects to see if they
      *  were created from the resource.
      * @param res
      * @param eobjs A collection of EObjects
      * @return true if res contains any of the EObjects in the collection, or
      *         if any of the Eobjects have a URI pointing to res.
      */
    public static boolean didResourceContainAny(IResource res, Collection eobjs) {
        Iterator itor = eobjs.iterator();
        while (itor.hasNext()) {
            EObject e = (EObject) itor.next();
            // if an object lives in the passed resource, we can assume we
            //  depend upon that resource.
            if (didResourceContain(res, e)) {
                return true;
            } // endif
        } // endwhile -- all eobjects in diagram

        return false;
    }

    /** Check to see if the specified IResource is or was associated
      *  with the specified EObject.  If the resource has been deleted
      *  or is no longer available, check the EObject to see if it
      *  was created from the resource.
      * @param res
      * @param e
      * @return true if res contains e, or if e has a URI pointing to res.
      */
    public static boolean didResourceContain(IResource res, EObject e) {
        ModelResource editorMR = ModelUtilities.getModelResourceForModelObject(e);
        // if necessary, resolve the proxy to the real object to see if
        //  we care about it:
        if (editorMR == null) {
            // try harder to resolve, in case it is a proxy:
            EObject realEObject = getRealEObject(e);
            editorMR = ModelUtilities.getModelResourceForModelObject(realEObject);
        } // endif -- modRes was null

        if (editorMR != null) {
            if (editorMR.getResource().equals(res)) {
                // break on first match:
                return true;
            } // endif -- has a modelResource
        } else {
            // couldn't find a MR for this object.  The MR could have been deleted,
            //  so instead get the URIs and check those:
            URI u = ModelerCore.getModelEditor().getUri(e);
            if (u != null
             && u.isFile()) {
                // compare paths:
                IPath pth = new Path(u.device(), u.path());
                if (pth.equals(res.getRawLocation())) {
                    return true;
                } // endif -- path equal
            } // endif -- URI is file
        } // endif -- modelResource not null

        return false;
    }
}
