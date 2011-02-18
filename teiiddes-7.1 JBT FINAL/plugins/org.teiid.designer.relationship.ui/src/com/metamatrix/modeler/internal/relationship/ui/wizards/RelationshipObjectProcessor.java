/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.relationship.ui.textimport.RelationshipTableRowObject;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractObjectProcessor;

/**
 * @since 4.2
 */
public class RelationshipObjectProcessor extends AbstractObjectProcessor {

    private static final String XMI_EXTENSION = "xmi";//$NON-NLS-1$

    // dialog store id constants
    private static final String I18N_PREFIX = "RelationshipObjectProcessor"; //$NON-NLS-1$
    private static final String SEPARATOR = "."; //$NON-NLS-1$

    // ============================================================================================================================
    // Static Methods

    private static String getString( final String id,
                                     final Object obj1,
                                     final Object obj2 ) {
        return UiConstants.Util.getString(I18N_PREFIX + SEPARATOR + id, obj1, obj2);
    }

    private IProgressMonitor monitor;
    List relationshipTypes = new ArrayList();
    private List otherModifiedResources = new ArrayList();

    /**
     * @since 4.2
     */
    public RelationshipObjectProcessor() {
        super();
    }

    public Collection createRowObjsFromStrings( Collection rowStrings ) {
        Iterator iter = rowStrings.iterator();
        String nextStr = null;

        Collection stringRows = new ArrayList();
        RelationshipTableRowObject nextRow = null;
        while (iter.hasNext()) {
            nextStr = (String)iter.next();
            nextRow = new RelationshipTableRowObject(nextStr);
            if (nextRow.isValid()) stringRows.add(nextRow);
            else {
                logParsingError(nextStr);
            }
        }
        return stringRows;
    }

    public void generateObjsFromRowObjs( final ModelResource targetResource,
                                         final Object location,
                                         final Collection tableRows ) {

        RelationshipFactory factory = RelationshipFactory.eINSTANCE;
        int iRow = 0;
        relationshipTypes.addAll(RelationshipMetamodelPlugin.getBuiltInRelationshipTypeManager().getAllBuiltInRelationshipTypes());

        Iterator iter = tableRows.iterator();
        RelationshipTableRowObject nextRow = null;
        Relationship rel = null;
        String sSize = Integer.toString(tableRows.size());
        Object finalLocation = null;

        while (iter.hasNext()) {
            nextRow = (RelationshipTableRowObject)iter.next();
            iRow++;
            finalLocation = location;
            if (monitor != null) {
                monitor.worked(1);
                monitor.subTask(UiConstants.Util.getString(I18N_PREFIX + SEPARATOR + "incrementalProgress", Integer.toString(iRow), sSize, nextRow.getName())); //$NON-NLS-1$
            }

            // if row has a location defined
            if (nextRow.getLocation() != null) {
                String rowLocation = nextRow.getLocation();
                rowLocation = rowLocation.replaceAll(".xmi", ""); //$NON-NLS-1$ //$NON-NLS-2$

                finalLocation = getOrCreateLocation(rowLocation, factory);

                if (finalLocation != null) {
                    ModelResource actualModelResource = targetResource;
                    if (finalLocation instanceof ModelResource) {
                        actualModelResource = (ModelResource)finalLocation;
                    } else {
                        // get the seg index to the model
                        int modelSegIndex = getModelPathIndex(rowLocation);
                        IPath locPath = new Path(rowLocation);
                        actualModelResource = getModelResource(locPath.uptoSegment(modelSegIndex).toOSString());
                    }
                    if (!targetResource.equals(actualModelResource) && !otherModifiedResources.contains(actualModelResource)) {
                        otherModifiedResources.add(actualModelResource);
                    }
                } else {
                    // If we couldn't create one, we use the selected existing one, just to be safe.
                    finalLocation = location;
                }
            }
            rel = createRelationship(factory, finalLocation, nextRow);

            // Add annotation
            ModelResource modelResrc = null;
            if (finalLocation instanceof ModelResource) {
                modelResrc = (ModelResource)finalLocation;
            } else if (finalLocation instanceof EObject) {
                modelResrc = ModelerCore.getModelEditor().findModelResource((EObject)finalLocation);
            }
            if (modelResrc != null && rel != null) {
                createAnnotation(modelResrc, rel, nextRow.getDescription());
            }
            if (monitor.isCanceled()) {
                break;
            }
        }

    }

    private Relationship createRelationship( RelationshipFactory factory,
                                             Object location,
                                             RelationshipTableRowObject tableRow ) {
        final Relationship rel = factory.createRelationship();
        rel.setName(tableRow.getName());

        RelationshipType relType = getRelationshipType(tableRow.getRelType());

        if (relType != null) {
            rel.setType(relType);
        }

        if (location instanceof ModelResource) {
            addValue(location, rel, getModelResourceContents((ModelResource)location));
        } else if (location instanceof Relationship) {
            addValue(location, rel, ((Relationship)location).getOwnedRelationships());
        } else if (location instanceof RelationshipFolder) {
            addValue(location, rel, ((RelationshipFolder)location).getOwnedRelationships());
        }

        if (!tableRow.getSourceRoles().isEmpty()) {
            List sourceObjs = tableRow.getSourceRoles();
            for (Iterator iter = sourceObjs.iterator(); iter.hasNext();) {
                String nextObjStr = (String)iter.next();
                EObject eObj = getEObject(nextObjStr);
                if (eObj != null) {
                    addValue(rel, eObj, rel.getSources());
                } else {
                    UiConstants.Util.log(IStatus.WARNING, getString("sourceNotFound", nextObjStr, tableRow.getName())); //$NON-NLS-1$);
                }
            }
        }

        if (!tableRow.getTargetRoles().isEmpty()) {
            List targetObjs = tableRow.getTargetRoles();
            for (Iterator iter = targetObjs.iterator(); iter.hasNext();) {
                String nextObjStr = (String)iter.next();
                EObject eObj = getEObject(nextObjStr);
                if (eObj != null) {
                    addValue(rel, eObj, rel.getTargets());
                } else {
                    UiConstants.Util.log(IStatus.WARNING, getString("targetNotFound", nextObjStr, tableRow.getName())); //$NON-NLS-1$);
                }
            }
        }

        return rel;
    }

    private void createAnnotation( ModelResource targetResource,
                                   EObject eObject,
                                   String description ) {
        if (description != null && description.length() > 0) {
            final ModelContents contents = ModelerCore.getModelEditor().getModelContents(targetResource);
            Annotation newAnnot = ModelResourceContainerFactory.createNewAnnotation(eObject,
                                                                                    contents.getAnnotationContainer(true));
            newAnnot.setDescription(description);
        }
    }

    /**
     * Find the model (if one exists) along the provided path string. Return the index of the path segment that is the model. If
     * no model was found, return value is -1.
     * 
     * @param path the path string which may have a model in it.
     * @return the path segment index of the model, -1 if none found.
     */
    private int getModelPathIndex( String pathStr ) {
        int modelIndex = -1;
        // Walk the segments one at a time, starting at last one.
        ModelResource mr = null;
        IPath path = new Path(pathStr);
        int nSegs = path.segmentCount();
        if (nSegs > 1) {
            // First find and open the project (first segment) and open it.
            String projSeg = path.segment(0);

            // Check if Project exists - if it doesnt, create it.
            IProject existProj = ResourcesPlugin.getWorkspace().getRoot().getProject(projSeg);
            if (!existProj.exists()) {
                existProj = createProject(existProj, new NullProgressMonitor());
            }

            // If project exists, continue processing
            if (existProj.exists()) {
                if (!existProj.isOpen()) {
                    try {
                        existProj.open(new NullProgressMonitor());
                    } catch (CoreException e) {
                        UiConstants.Util.log(e);
                    }
                }
                for (int i = nSegs; i > 1; i--) {
                    IPath workingPath = path.uptoSegment(i);
                    String osPathStr = workingPath.toOSString();
                    if (osPathStr != null && osPathStr.length() > 0) {
                        mr = getModelResource(osPathStr);
                    }
                    if (mr != null) {
                        modelIndex = i;
                        break;
                    }
                }
            }
        }
        return modelIndex;
    }

    private Object getOrCreateLocation( String location,
                                        RelationshipFactory factory ) {
        ModelResource mr = null;
        location = location.replaceAll(".xmi", ""); //$NON-NLS-1$ //$NON-NLS-2$

        IPath locPath = new Path(location);
        int nSegs = locPath.segmentCount();

        int modelSegIndex = getModelPathIndex(location);

        // If model was found along the path, use it
        if (modelSegIndex != -1) {
            mr = getModelResource(locPath.uptoSegment(modelSegIndex).toOSString());
            // Provided path goes beyond the Model - need to create schema under it.
            if (nSegs > modelSegIndex) {
                // If we are here, then we need to create folders
                int nFolders = nSegs - modelSegIndex;
                EObject folderEObject = null;
                Object parent = mr;
                for (int i = 0; i < nFolders; i++) {
                    // First find folder EObject
                    String sFolderPath = locPath.uptoSegment(modelSegIndex + 1 + i).toOSString();
                    folderEObject = getEObject(sFolderPath);
                    if (folderEObject == null) {
                        // Create a Folder here
                        folderEObject = createFolder(parent, locPath.segment(modelSegIndex + i), factory);
                        if (i == nFolders - 1) return folderEObject;
                        parent = folderEObject;
                    } else {
                        parent = folderEObject;
                        if (i == nFolders - 1) return folderEObject;
                    }
                }
                // Provided path stops at the Model, just return the Model.
            } else {
                return mr;
            }
            // Model not found, assume a new model is to be created under project
        } else {
            String projSeg = locPath.segment(0);
            // Check if Project exists
            IProject existProj = ResourcesPlugin.getWorkspace().getRoot().getProject(projSeg);

            if (!existProj.exists()) existProj = createProject(existProj, new NullProgressMonitor());

            if (existProj.exists()) {
                if (!existProj.isOpen()) {
                    try {
                        existProj.open(new NullProgressMonitor());
                    } catch (CoreException e) {
                        UiConstants.Util.log(e);
                    }
                }
                // We shouldn't have to create one and we should expect the model to not exist
                IPath modelPath = new Path(locPath.segment(1)).addFileExtension(XMI_EXTENSION);
                if (!existProj.exists(modelPath)) { // new Path(locPath.segment(1)) )) {
                    // Need to create model here with this name
                    mr = createRelationshipModel(existProj, locPath.segment(1));
                } else {
                    mr = getModelResource(locPath.uptoSegment(2).toOSString());
                }

                if (nSegs == 2) return mr;

                // If we are here, then we need to create folders
                int nFolders = nSegs - 2;
                EObject folderEObject = null;
                Object parent = mr;
                for (int i = 0; i < nFolders; i++) {
                    // First find folder EObject
                    String sFolderPath = locPath.uptoSegment(3 + i).toOSString();
                    folderEObject = getEObject(sFolderPath);
                    if (folderEObject == null) {
                        // Create a Folder here
                        folderEObject = createFolder(parent, locPath.segment(2 + i), factory);
                        if (i == nFolders - 1) return folderEObject;
                        parent = folderEObject;
                    } else {
                        parent = folderEObject;
                        if (i == nFolders - 1) return folderEObject;
                    }
                }
            }

        }

        // We shouldn't get here, so log a message

        UiConstants.Util.log(IStatus.ERROR,
                             "Problems creating non existing folder or model for new relationship.  Path = " + location); //$NON-NLS-1$
        return null;
    }

    private RelationshipType getRelationshipType( String someType ) {

        // Walk through the built in types, and if you can't find it....
        RelationshipType nextType = null;
        for (Iterator iter = relationshipTypes.iterator(); iter.hasNext();) {
            nextType = (RelationshipType)iter.next();
            String typeString = nextType.getName();
            if (typeString != null && someType != null && someType.equalsIgnoreCase(typeString)) return nextType;
        }

        EObject eObj = getEObject(someType);
        if (eObj instanceof RelationshipType) {
            nextType = (RelationshipType)eObj;
        }

        return nextType;
    }

    public void setProgressMonitor( IProgressMonitor monitor ) {
        this.monitor = monitor;
    }

    public List getOtherModifiedResources() {
        if (otherModifiedResources.isEmpty()) return Collections.EMPTY_LIST;

        return otherModifiedResources;
    }

    /**
     * handler for Create Relationships Model Button pressed
     */
    private ModelResource createRelationshipModel( IResource targetRes,
                                                   String sNewRelationshipModelName ) {
        ModelResource mrRelationshipModel = constructRelationshipModel(targetRes, sNewRelationshipModelName);

        // Save Relationship Model
        try {
            if (mrRelationshipModel != null) {
                mrRelationshipModel.save(null, false);

            }
        } catch (ModelWorkspaceException mwe) {
            UiConstants.Util.log(mwe);
        }

        return mrRelationshipModel;
    }

    /**
     * Create a Relationships Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    public ModelResource constructRelationshipModel( IResource targetRes,
                                                     String sModelName ) {
        IPath relativeModelPath = targetRes.getProjectRelativePath().append(sModelName).addFileExtension(XMI_EXTENSION);
        final IFile modelFile = targetRes.getProject().getFile(relativeModelPath);
        final ModelResource resrc = ModelerCore.create(modelFile);
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri(RelationshipPackage.eNS_URI);
            resrc.getModelAnnotation().setModelType(ModelType.LOGICAL_LITERAL);
            ModelUtilities.initializeModelContainers(resrc, "Create Model Containers", this); //$NON-NLS-1$ 
        } catch (ModelWorkspaceException mwe) {
            mwe.printStackTrace();
        }

        return resrc;
    }

    private EObject createFolder( Object parent,
                                  String folderName,
                                  RelationshipFactory factory ) {
        RelationshipFolder newFolder = factory.createRelationshipFolder();
        newFolder.setName(folderName);

        if (parent instanceof EObject) {
            // newFolder = factory.createRelationshipFolder();
            // newFolder.setName(folderName);
            addValue(parent, newFolder, ((RelationshipFolder)parent).getOwnedRelationshipFolders());
        } else if (parent instanceof ModelResource) {
            addValue(parent, newFolder, getModelResourceContents((ModelResource)parent));
        }

        return newFolder;
    }

}
