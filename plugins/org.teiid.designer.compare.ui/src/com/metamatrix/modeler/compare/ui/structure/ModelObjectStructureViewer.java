/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.ui.structure;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.HistoryItem;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.compare.DifferenceProcessor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.compare.ui.UiConstants;
import com.metamatrix.modeler.compare.ui.tree.DifferenceReportsPanel;
import com.metamatrix.modeler.compare.ui.tree.MappingTreeContentProvider;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelFolder;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelFolderImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelFolderInfo;
import com.metamatrix.modeler.internal.core.workspace.ModelProjectInfo;
import com.metamatrix.modeler.internal.core.workspace.ModelResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceItemImpl;

/**
 * @since 4.2
 */
public class ModelObjectStructureViewer extends StructuredViewer /*StructureDiffViewer*/{

    /*
     * jh notes
     *  0. what is the earliest ancestor class this viewer can be to fit in with the immediate
     *     parent class CompareViewerSwitchingPane (see EditionSelectionDialog)?  
     *     Answer: StructuredViewer.
     *  1. This viewer will be defined as a "structureViewers" extension in the CompareUi plugin.zml, 
     *     associated with the ".xmi" file type extension.
     *  2. The structure creator 'ModelObjectStructureCreator' will be explicitly used in this
     *     class and will therefore not need to be defined as a "structureCreators" extension
     *     in the plugin.xml. 
     *  3. At this point it is not clear how much, if any, of StructureDiffViewer this class will
     *     need to override, but having it serves as a useful way to control our implementation and 
     *     allow it to grow if/as needed.
     * 
     */

    private Composite cmpParent;
    // private ModelObjectStructureCreator fStructureCreator;
    private DifferenceProcessor dpProcessor;
    private DifferenceReport drReport;
    private DifferenceReportsPanel pnlDifferenceReport;
    private HistoryItem hiRight;
    private ResourceNode rnLeft;
    private ResourceNode rnRight;
    private int iCompareType = -1;
    private static final int RESOURCE_NODE_TO_INPUT_STREAM = 1;
    private static final int RESOURCE_NODE_TO_RESOURCE_NODE = 2;
    private final static String EMPTY_STRING = ""; //$NON-NLS-1$
    private int iTerminology = DifferenceReportsPanel.USE_OLD_NEW_TERMINOLOGY;

    private static final String DIALOG_TITLE = UiConstants.Util.getString("ModelObjectStructureViewer.dialogTitle"); //$NON-NLS-1$
    private static final String TREE_TITLE = UiConstants.Util.getString("ModelObjectStructureViewer.treeTitle"); //$NON-NLS-1$
    private static final String DIFF_DESCRIPTOR_TITLE = UiConstants.Util.getString("ModelObjectStructureViewer.diffDescriptorTitle"); //$NON-NLS-1$

    /**
     * @param parent
     * @param configuration
     * @since 4.2
     */
    public ModelObjectStructureViewer( Composite parent,
                                       CompareConfiguration configuration ) {

        super();
        this.cmpParent = parent;
        this.setContentProvider(new MappingTreeContentProvider());
        getContentProvider();
        // fStructureCreator = new ModelObjectStructureCreator();
        //        System.out.println("[ModelObjectStructureViewer.ctor #2] BOT"); //$NON-NLS-1$
    }

    @Override
    protected void inputChanged( Object input,
                                 Object oldInput ) {
        //        System.out.println("[ModelObjectStructureViewer.inputChanged] TOP"); //$NON-NLS-1$

        // input might be null...
        if (input == null) {
            return;
        }

        InputStream isRight = null;
        IPath pathRight = null;
        IResource resLeft = null;
        IResource resRight = null;

        // capture the ResourceNode
        DiffNode node = (DiffNode)input;

        // get ModelResource for Left
        ITypedElement teLeft = node.getLeft();

        if (teLeft instanceof ResourceNode) {
            rnLeft = (ResourceNode)teLeft;
            resLeft = rnLeft.getResource();
        }

        // get ModelResource for Right
        ITypedElement teRight = node.getRight();

        if (teRight instanceof ResourceNode) {
            iCompareType = RESOURCE_NODE_TO_RESOURCE_NODE;
            rnRight = (ResourceNode)teRight;
            resRight = rnRight.getResource();
        } else
        // or get InputStream for right
        if (teRight instanceof HistoryItem) {
            iCompareType = RESOURCE_NODE_TO_INPUT_STREAM;
            rnRight = null;
            hiRight = (HistoryItem)teRight;
            pathRight = new Path(hiRight.getName());
            try {

                isRight = hiRight.getContents();
            } catch (CoreException ce) {
                UiConstants.Util.log(ce);
            }
        }

        // create the processor from two resources
        if (iCompareType == RESOURCE_NODE_TO_RESOURCE_NODE) {
            try {
                ModelResource mrLeft = findModelResource(resLeft, true);
                ModelResource mrRight = findModelResource(resRight, true);

                //               System.out.println("[ModelObjectStructureViewer.inputChanged] resLeft: " + resLeft.getName() ); //$NON-NLS-1$
                //               System.out.println("[ModelObjectStructureViewer.inputChanged] resRight: " + resRight.getName() ); //$NON-NLS-1$

                dpProcessor = ModelerComparePlugin.createDifferenceProcessor(mrLeft, mrRight);
            } catch (ModelWorkspaceException mwe) {
                UiConstants.Util.log(mwe);
            }
        } else {
            // OR, create the processor from a resource and an input stream
            IProject project = resLeft.getProject();

            if ((project != null) && project.isOpen() && ModelerCore.hasModelNature(project)
                && (resLeft.getType() == IResource.FILE)) {

                try {
                    ModelResource mrLeft = findModelResource(resLeft, true);

                    if (mrLeft != null) {
                        dpProcessor = ModelerComparePlugin.createDifferenceProcessor(isRight,
                                                                                     pathRight,
                                                                                     mrLeft,
                                                                                     mrLeft.getDescription());
                    }
                } catch (ModelWorkspaceException mwe) {
                    UiConstants.Util.log(mwe);
                }
            }
        }

        // run the processor
        // IStatus status =
        dpProcessor.execute(new NullProgressMonitor());
        //        System.out.println("[ModelObjectStructureViewer.inputChanged] status after processor.execute is: " + status.getCode() ); //$NON-NLS-1$

        drReport = dpProcessor.getDifferenceReport();
        //        System.out.println("[ModelObjectStructureViewer.inputChanged] drReport.getTotalAdditions(): " + drReport.getTotalAdditions() ); //$NON-NLS-1$
        //        System.out.println("[ModelObjectStructureViewer.inputChanged] drReport.getTotalChanges(): " + drReport.getTotalChanges() ); //$NON-NLS-1$
        //        System.out.println("[ModelObjectStructureViewer.inputChanged] drReport.getTotalDeletions(): " + drReport.getTotalDeletions() ); //$NON-NLS-1$

        getDifferenceReportsPanel().setTerminologyStyle(getTerminologyStyle(iCompareType));
        getDifferenceReportsPanel().setDifferenceReports(Collections.singletonList(drReport));

        if (iCompareType == RESOURCE_NODE_TO_RESOURCE_NODE) {
            getDifferenceReportsPanel().setObjectNames(getLeftObjectName(), getRightObjectName());
        } else if (iCompareType == RESOURCE_NODE_TO_INPUT_STREAM) {
            getDifferenceReportsPanel().setObjectNames(getRightObjectName(), getLeftObjectName());
        }
    }

    private int getTerminologyStyle( int iCompareType ) {

        int iTerminology = DifferenceReportsPanel.USE_OLD_NEW_TERMINOLOGY;

        // determine terminology setting
        if (iCompareType == ModelObjectStructureViewer.RESOURCE_NODE_TO_INPUT_STREAM) {
            iTerminology = DifferenceReportsPanel.USE_OLD_NEW_TERMINOLOGY;
        } else if (iCompareType == ModelObjectStructureViewer.RESOURCE_NODE_TO_RESOURCE_NODE) {
            iTerminology = DifferenceReportsPanel.USE_FIRST_SECOND_TERMINOLOGY;
        }

        return iTerminology;
    }

    public ModelResource findModelResource( final IResource resource,
                                            final boolean createIfRequired ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(resource);

        // Get the project and the path of the model file relative to the project ...
        final IProject proj = resource.getProject();

        // check if this is a model project
        if (!ModelerCore.hasModelNature(proj)) {
            return null;
        }

        final IPath pathInWorkspace = resource.getProjectRelativePath();

        // Find the ModelProject
        final ModelProject modelProject = ModelerCore.getModelWorkspace().getModelProject(proj);
        ModelUtil.getModelResource((IFile)resource, false);

        // Iterate over the segments, finding the corresponding model folder(s) and model resource
        ModelWorkspaceItem parent = modelProject;
        int numFolders = pathInWorkspace.segmentCount(); // should be at least 1
        if (resource instanceof IFile) {
            // See if the file is a model ...
            if (!ModelUtil.isModelFile(resource) && !ModelUtil.isVdbArchiveFile(resource)) {
                return null; // it's a non-model resource
            }
            --numFolders;
        }

        for (int i = 0; i < numFolders; ++i) {
            final String folderName = pathInWorkspace.segment(i);
            final ModelWorkspaceItem child = parent.getChild(folderName);

            if (child == null) {

                // get the workspace resource
                final IFolder underlyingFolder = proj.getFolder(folderName);
                CoreArgCheck.isNotNull(underlyingFolder);
                final ModelFolder newFolder = new ModelFolderImpl(underlyingFolder, parent);
                final Object parentInfo = ((ModelWorkspaceItemImpl)parent).getItemInfo();
                if (parentInfo instanceof ModelFolderInfo) {
                    ((ModelFolderInfo)parentInfo).addChild(newFolder);
                    parent = newFolder;
                } else if (parentInfo instanceof ModelProjectInfo) {
                    ((ModelProjectInfo)parentInfo).addChild(newFolder);
                    parent = newFolder;
                }
            } else {
                parent = child;
            }
        }

        if (resource instanceof IFile) {
            // Get the ModelResource ...
            ModelWorkspaceItem result = parent.getChild(resource);
            if (result == null && createIfRequired) {
                final String name = resource.getName();
                result = new ModelResourceImpl(parent, name);
            }
            return (ModelResource)result;
        }
        // else return the folder
        return (ModelResource)parent;
    }

    private DifferenceReportsPanel getDifferenceReportsPanel() {
        if (pnlDifferenceReport == null) {

            // determine terminology setting
            if (iCompareType == ModelObjectStructureViewer.RESOURCE_NODE_TO_INPUT_STREAM) {
                iTerminology = DifferenceReportsPanel.USE_OLD_NEW_TERMINOLOGY;
            } else if (iCompareType == ModelObjectStructureViewer.RESOURCE_NODE_TO_RESOURCE_NODE) {
                iTerminology = DifferenceReportsPanel.USE_FIRST_SECOND_TERMINOLOGY;
            }

            // in this case, 'drReport' is probably null
            pnlDifferenceReport = new DifferenceReportsPanel(cmpParent, TREE_TITLE, DIFF_DESCRIPTOR_TITLE, false, false, false,
                                                             drReport, iTerminology);
        }

        // refresh the title
        String sTitle = DIALOG_TITLE;

        // but will they pick up this change after the panel has been created?
        pnlDifferenceReport.setData(CompareUI.COMPARE_VIEWER_TITLE, sTitle);

        return pnlDifferenceReport;
    }

    private String getRightObjectName() {
        String sResult = EMPTY_STRING;
        if (iCompareType == RESOURCE_NODE_TO_RESOURCE_NODE) {
            sResult = rnRight.getResource().getFullPath().toString();
        } else {
            sResult = hiRight.getName() + " - " //$NON-NLS-1$
                      + DateFormat.getDateTimeInstance().format(new Date(hiRight.getModificationDate()));
        }
        return sResult;
    }

    private String getLeftObjectName() {
        return rnLeft.getResource().getFullPath().toString();
    }

    // ====================================
    @Override
    public Widget doFindInputItem( Object o ) {
        return null;
    }

    @Override
    public Widget doFindItem( Object o ) {
        return null;
    }

    @Override
    public void doUpdateItem( Widget w,
                              Object o,
                              boolean b ) {

    }

    @Override
    public List getSelectionFromWidget() {
        return null;
    }

    @Override
    public void internalRefresh( Object o ) {

    }

    @Override
    public void reveal( Object o ) {

    }

    @Override
    public void setSelectionToWidget( List l,
                                      boolean b ) {

    }

    @Override
    public Control getControl() {
        // this temp version must return a DifferenceReportsPanel
        return getDifferenceReportsPanel();
    }
}
