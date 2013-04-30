/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.mapping.factory.MappingClassFactory;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * GenerateMappingClassesAction
 *
 * @since 8.0
 */
public class GenerateMappingClassesAction extends MappingAction {
    private static final String ACTION_DESCRIPTION = "Generate Mapping Classes"; //$NON-NLS-1$
    private static final boolean AUTO_POPULATE_ATTRIBUTES = true;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public GenerateMappingClassesAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.GENERATE_MAPPING_CLASSES));
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        super.selectionChanged(thePart, theSelection);
        determineEnablement();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        // doRun should call MappingClassFactory.generateMappingClasses(EObject node, boolean autoPopulateAttributes)
        // where node is the root (beneath the Document) and autoPopulateAttributes is true.
        if (getMappingClassFactory() != null) {

            // jh Defect 21513 - dropped irrelevant test for selected node.

            boolean canUndo = IMappingDiagramActionConstants.DiagramActions.UNDO_GENERATE_MAPPING_CLASSES;

            /*
             * jh Defect 21513 - 'generateMappingClasses()' expects to be handed an XmlRootImpl as the
             *                   root of a tree it can walk in order to determine what mapping classes
             *                   to create.  This action was giving it the tree root, which was an
             *                   XmlDocumentImpl whose child was an XmlRootImpl, not an XmlRootImpl itself.
             *                   Fixing this method to pass the XmlRootImpl.
             */
            EObject eoXmlRoot = null;
            EObject eoTreeRoot = getMappingClassFactory().getTreeRoot();

            if (eoTreeRoot instanceof XmlDocument) {
                eoXmlRoot = ((XmlDocument)eoTreeRoot).getRoot();
            } else {
                eoXmlRoot = eoTreeRoot;
            }

            // start txn
            boolean requiredStart = ModelerCore.startTxn(true, canUndo, ACTION_DESCRIPTION, this);
            boolean succeeded = false;

            try {
                getMappingClassFactory().generateMappingClasses(eoXmlRoot,
                                                                MappingClassFactory.getDefaultStrategy(),
                                                                AUTO_POPULATE_ATTRIBUTES);
                succeeded = true;
            } finally {

                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        setEnabled(false);
    }

    public void determineEnablement() {
        // Enables when there is no MappingClassSet targeting the Document,
        // or when the MappingClassSet targeting the Document is empty
        // (and the Document is not read-only, of course).
        boolean enable = false;

        if (this.getPart() instanceof ModelEditor && getMappingClassFactory() != null) {
            // We don't Check that the selection is a document node or the root under the node?
            EObject treeRootEObject = getMappingClassFactory().getTreeRoot();
            if (!ModelObjectUtilities.isReadOnly(treeRootEObject) && getMappingClassFactory().canGenerateMappingClasses()) {
                enable = true;
            }
        }

        setEnabled(enable);
    }

}
