/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

/**
 * Content provider and Label provider for the MedOutlineTreeViewer
 */
public class MedOutlineTreeContentProvider extends LabelProvider implements ITreeContentProvider {

    private ModelExtensionDefinitionEditor medEditor;
    private ExtendableMetaclassNameProvider metaclassNameProvider;

    public MedOutlineTreeContentProvider( ModelExtensionDefinitionEditor editor ) {
        super();
        this.medEditor = editor;
        this.metaclassNameProvider = ExtensionPlugin.getInstance().getMetaclassNameProvider(this.medEditor.getMed().getMetamodelUri());
    }

    @Override
    public Object[] getChildren( Object parentElement ) {
        List childList = new ArrayList();
        // Return children of MED
        if (parentElement instanceof ModelExtensionDefinition) {
            ModelExtensionDefinition med = (ModelExtensionDefinition)parentElement;

            // Add TreeNodes for NSPrefix,NSURI,ModelClass,Version,Description
            childList.add(new MedOutlineTreeNode(parentElement, MedOutlineTreeNode.NodeType.NAMESPACE_PREFIX));
            childList.add(new MedOutlineTreeNode(parentElement, MedOutlineTreeNode.NodeType.NAMESPACE_URI));
            childList.add(new MedOutlineTreeNode(parentElement, MedOutlineTreeNode.NodeType.MODEL_CLASS));
            childList.add(new MedOutlineTreeNode(parentElement, MedOutlineTreeNode.NodeType.VERSION));
            childList.add(new MedOutlineTreeNode(parentElement, MedOutlineTreeNode.NodeType.DESCRIPTION));

            String[] extendedMCs = med.getExtendedMetaclasses();
            for (int i = 0; i < extendedMCs.length; i++) {
                MedOutlineTreeNode medNode = new MedOutlineTreeNode(parentElement, MedOutlineTreeNode.NodeType.EXTENDED_METACLASS);
                medNode.setName(extendedMCs[i]);
                childList.add(medNode);
            }
            return childList.toArray();
        } else if (parentElement instanceof MedOutlineTreeNode
                   && ((MedOutlineTreeNode)parentElement).getType() == MedOutlineTreeNode.NodeType.EXTENDED_METACLASS) {
            ModelExtensionDefinition med = this.medEditor.getMed();
            Collection<ModelExtensionPropertyDefinition> propDefns = med.getPropertyDefinitions(((MedOutlineTreeNode)parentElement).getName());
            List<MedOutlineTreeNode> propDefnNodes = new ArrayList<MedOutlineTreeNode>(propDefns.size());
            for (ModelExtensionPropertyDefinition propDefn : propDefns) {
                MedOutlineTreeNode medNode = new MedOutlineTreeNode(parentElement, MedOutlineTreeNode.NodeType.PROPERTY_DEFN);
                medNode.setName(propDefn.getSimpleId());
                propDefnNodes.add(medNode);
            }
            return propDefnNodes.toArray();
        }
        return Collections.EMPTY_LIST.toArray();
    }

    @Override
    public Object getParent( Object element ) {
        if(element instanceof ModelExtensionDefinition) {
            return null;
        } else if (element instanceof MedOutlineTreeNode) {
            return ((MedOutlineTreeNode)element).getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren( Object element ) {
        return getChildren(element).length > 0;
    }

    @Override
    public Object[] getElements( Object inputElement ) {
        if (inputElement instanceof ArrayList) {
            return ((ArrayList)inputElement).toArray();
        }

        return getChildren(inputElement);
    }

    @Override
    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
        // Do nothing
    }

    @Override
    public Image getImage( Object element ) {
        return null;
    }

    @Override
    public String getText( Object element ) {
        if (element instanceof ModelExtensionDefinition) {
            return this.medEditor.getFile().getName();
        } else if (element instanceof MedOutlineTreeNode) {
            if (((MedOutlineTreeNode)element).getType() == MedOutlineTreeNode.NodeType.EXTENDED_METACLASS) {
                return ((MedOutlineTreeNode)element).getName(this.metaclassNameProvider);
            }
            return ((MedOutlineTreeNode)element).getName();
        }
        return element.toString();
    }

}
