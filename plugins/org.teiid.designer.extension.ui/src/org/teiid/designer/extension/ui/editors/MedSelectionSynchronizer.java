/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.ui.model.MedModelNode;

/**
 * The
 * <code>MedSelectionSynchronizer<code> synchronizes selection between the MED editor and other views displaying MED information.
 */
public class MedSelectionSynchronizer implements ISelectionChangedListener, PropertyChangeListener {

    private IStructuredSelection currentSelection = StructuredSelection.EMPTY;

    private final ModelExtensionDefinitionEditor medEditor;

    private ModelExtensionDefinition med;

    private MedModelNode medNode;

    private final List<MedSelectionProvider> selectionProviders = new ArrayList<MedSelectionProvider>(5);

    /**
     * @param medEditor the editor where selection synchronization originates (cannot be <code>null</code>)
     */
    public MedSelectionSynchronizer( ModelExtensionDefinitionEditor medEditor ) {
        this.medEditor = medEditor;
        setMed(this.medEditor.getMed());
    }

    /**
     * @param selectionProvider the provider being added (cannot be <code>null</code>)
     * @return <code>true</code> if successfully registered
     */
    public boolean addSelectionProvider( MedSelectionProvider selectionProvider ) {
        boolean added = false;

        if (!this.selectionProviders.contains(selectionProvider)) {
            added = this.selectionProviders.add(selectionProvider);

            if (added) {
                selectionProvider.addSelectionChangedListener(this);
                selectionProvider.setSelection(this.currentSelection);
            }
        }

        return added;
    }

    /**
     * Removes all selection listeners.
     */
    public void dispose() {
        for (MedSelectionProvider selectionProvider : this.selectionProviders) {
            selectionProvider.removeSelectionChangedListener(this);
        }
    }

    /**
     * @return the MED description model node (never <code>null</code>)
     */
    public MedModelNode getDescriptionNode() {
        return getMedModelNode().getDescriptionNode();
    }

    /**
     * @return the MED model node (never <code>null</code>)
     */
    public MedModelNode getMedModelNode() {
        return this.medNode;
    }

    /**
     * @param metaclass the metaclass whose model node is being requested (cannot be <code>null</code>)
     * @return the MED metaclass model node or <code>null</code> if not found
     */
    public MedModelNode getMetaclassNode( String metaclass ) {
        return getMedModelNode().getMetaclassNode(metaclass);
    }

    /**
     * @return the MED metamodel URI model node (never <code>null</code>)
     */
    public MedModelNode getMetamodelUriNode() {
        return getMedModelNode().getMetamodelUriNode();
    }

    /**
     * @return the MED model types model node (never <code>null</code>)
     */
    public MedModelNode getModelTypesNode() {
        return getMedModelNode().getModelTypesNode();
    }

    /**
     * @return the MED namespace prefix model node (never <code>null</code>)
     */
    public MedModelNode getNamespacePrefixNode() {
        return getMedModelNode().getNamespacePrefixNode();
    }

    /**
     * @return the MED namespace prefix model node (never <code>null</code>)
     */
    public MedModelNode getNamespaceUriNode() {
        return getMedModelNode().getNamespaceUriNode();
    }

    /**
     * @param metaclass the metaclass whose property definition model node is being requested (cannot be <code>null</code>)
     * @param propDefn the property definition whose model node is being requested (cannot be <code>null</code>)
     * @return the MED property definition model node or <code>null</code> if not found
     */
    public MedModelNode getPropertyDefinitionNode( String metaclass,
                                                   ModelExtensionPropertyDefinition propDefn ) {
        return getMedModelNode().getPropertyDefinitionNode(metaclass, propDefn);
    }

    /**
     * @return the current selection that is being synchronized (never <code>null</code>)
     */
    public IStructuredSelection getSelection() {
        return this.currentSelection;
    }

    /**
     * @return the MED version model node (never <code>null</code>)
     */
    public MedModelNode getVersionNode() {
        return getMedModelNode().getVersionNode();
    }

    private boolean isSynchronized() {
        // TODO maybe could hook this up to a preference value
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange( PropertyChangeEvent e ) {
        this.medNode = MedModelNode.createMedNode(this.med); // for now recreate model each time

        // inform selection providers of the change
        for (MedSelectionProvider selectionProvider : this.selectionProviders) {
            selectionProvider.refresh();
        }
    }

    /**
     * @param selectionProvider the selection provider being removed from receiving selection events (cannot be <code>null</code>)
     * @return <code>true</code> if successfully removed
     */
    public boolean removeSelectionProvider( MedSelectionProvider selectionProvider ) {
        boolean removed = this.selectionProviders.remove(selectionProvider);

        if (removed) {
            selectionProvider.removeSelectionChangedListener(this);
        }

        return removed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        if (isSynchronized()) {
            MedSelectionProvider source = (MedSelectionProvider)event.getSelectionProvider();
            ISelection newSelection = event.getSelection();

            if (newSelection instanceof IStructuredSelection) {
                this.currentSelection = (IStructuredSelection)newSelection;

                // inform selection providers of selection
                for (MedSelectionProvider selectionProvider : this.selectionProviders) {
                    // don't pass event to source
                    if (selectionProvider != source) {
                        if (selectionProvider.isApplicable(this.currentSelection)) {
                            selectionProvider.setSelection(newSelection);

                            // change the editor tab if necessary
                            MedEditorPage page = selectionProvider.getMedEditorPage();

                            if (page != null) {
                                IWorkbenchPart part = this.medEditor.getSite().getPage().getActivePart();
                                this.medEditor.selectPage(page);

                                // set focus back to the part that had focus before sync'ing selection
                                if (part != this.medEditor) {
                                    part.setFocus();
                                }
                            }

                            break; // just pass to first provider that accepts
                        }
                    }
                }
            }
        }
    }

    /**
     * @param newMed the MED whose selection synchronization needs to be managed (never <code>null</code>)
     */
    void setMed( ModelExtensionDefinition newMed ) {
        if (this.med != null) {
            this.med.removeListener(this);
        }

        this.med = newMed;
        this.medNode = MedModelNode.createMedNode(this.med);
        this.med.addListener(this);

        for (MedSelectionProvider selectionProvider : this.selectionProviders) {
            selectionProvider.refresh();
        }
    }

}
