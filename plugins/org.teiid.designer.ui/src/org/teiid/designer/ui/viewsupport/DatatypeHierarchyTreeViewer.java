/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.util.ArrayList;
import java.util.Collections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.ui.UiConstants;


/**
 * DatatypeHierarchyTreeViewer is a reusable TreeViewer to display the Datatype hierarchy.
 *
 * @since 8.0
 */
public class DatatypeHierarchyTreeViewer extends TreeViewer {

//    private EObject selectedDatatype;

    /**
     * Construct an instance of DatatypeHierarchyTreeViewer.
     * @param parent
     */
    public DatatypeHierarchyTreeViewer(Composite parent) {
        this(parent, SWT.MULTI);
    }

    public DatatypeHierarchyTreeViewer(Composite parent, int style) {
        super(parent, style | SWT.H_SCROLL | SWT.V_SCROLL);
        setContentProvider(new DatatypeTreeContentProvider());
        setLabelProvider(ModelUtilities.getEMFLabelProvider());
        super.setInput(Collections.EMPTY_LIST);
    }

    /**
     * Construct an instance of DatatypeHierarchyTreeViewer.
     * @param parent
     */
    public DatatypeHierarchyTreeViewer(Composite parent, int style, EObject selectedDatatype) {
        this(parent, style);
//        this.selectedDatatype = selectedDatatype;
        ArrayList list = new ArrayList(1);
        list.add(selectedDatatype);
        super.setInput(list);
    }

    class DatatypeTreeContentProvider implements ITreeContentProvider {
        
        private DatatypeManager datatypeManager;
        
        public DatatypeTreeContentProvider() {
            datatypeManager = ModelerCore.getWorkspaceDatatypeManager();    // Assumed to be in workspace!!!
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        @Override
		public void dispose() {

        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        @Override
		public Object[] getChildren(Object parentElement) {
            Object[] result = new Object[0];
            try {
                result = datatypeManager.getSubtypes((EObject)parentElement);
            } catch (ModelerCoreException e) {
                UiConstants.Util.log(e);
            }
            return result;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        @Override
		public Object[] getElements(Object inputElement) {
            Object[] result = new Object[1];
            try {
                result[0] = datatypeManager.getAnySimpleType();
            } catch (ModelerCoreException e) {
                UiConstants.Util.log(e);
            }
            return result;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        @Override
		public Object getParent(Object element) {
            return datatypeManager.getBaseType((EObject) element);
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        @Override
		public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        @Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

    }

}
