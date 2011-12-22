/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.teiid.designer.extension.ui.model.MedModelNode;
import org.teiid.designer.extension.ui.model.MedModelNode.ModelType;

/**
 * The <code>MedSelectionProvider</code> works with the MED selection synchronizer.
 */
public interface MedSelectionProvider extends ISelectionProvider {

    /**
     * @return the MED editor page associated with this provider or <code>null</code> if not associated with one
     */
    MedEditorPage getMedEditorPage();

    /**
     * @param type the type of model node (cannot be <code>null</code>)
     * @return the associated model node or <code>null</code> if not found
     */
    MedModelNode getSelectedNode( ModelType type );

    /**
     * @param selection the selection being checked (cannot be <code>null</code>)
     * @return <code>true</code> if this provider can select the selection
     */
    boolean isApplicable( IStructuredSelection selection );

    /**
     * Informs the provider that the MED has had property changes or a new MED is being used
     */
    void refresh();

}
