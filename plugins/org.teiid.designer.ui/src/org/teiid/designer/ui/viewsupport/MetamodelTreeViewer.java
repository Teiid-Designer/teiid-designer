/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.teiid.core.designer.util.ArrayUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.product.IModelerProductContexts.Metamodel;


/**
 * MetamodelTreeViewer
 *
 * @since 8.0
 */
public class MetamodelTreeViewer extends TreeViewer {

    public static final EcoreAdapterFactory FACTORY = new EcoreItemProviderAdapterFactory();

    public static SelectionDialog createSelectionDialog(final Shell parentShell,
                                                        final boolean primaryOnly) {
        return createSelectionDialog(parentShell, primaryOnly, null);
    }

    public static SelectionDialog createSelectionDialog(final Shell parentShell,
                                                        final boolean primaryOnly,
                                                        final IContentFilter filter) {
        ElementTreeSelectionDialog dialog =
            new MetamodelSelectionDialog(
                parentShell,
                new MetamodelLabelProvider(FACTORY),
                new MetamodelTreeContentProvider(FACTORY, filter));
        dialog.setValidator(new ISelectionStatusValidator() {
            @Override
			public IStatus validate(Object[] selection) {
                if ( selection.length == 1 && selection[0] instanceof EClass ) {
                    return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.OK, ""); //$NON-NLS-1$
                }
                String message = UiConstants.Util.getString("MetamodelClassDialog.mustSelectClass"); //$NON-NLS-1$
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, message);
            }

        });
        dialog.setComparator(new ViewerComparator() {});
        dialog.setTitle(UiConstants.Util.getString("MetamodelClassDialog.selectClassTitle")); //$NON-NLS-1$
        dialog.setMessage(UiConstants.Util.getString("MetamodelClassDialog.selectClassMessage")); //$NON-NLS-1$
        Collection descriptors = Arrays.asList(ModelerCore.getMetamodelRegistry().getMetamodelDescriptors());
        dialog.setInput(descriptors);
        return dialog;
    }

    /**
     * Construct an instance of MetamodelTreeViewer.
     * @param parent
     */
    public MetamodelTreeViewer(Composite parent) {
        super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        setContentProvider(new MetamodelTreeContentProvider(FACTORY, null));
        setLabelProvider(new MetamodelLabelProvider(FACTORY));
        Collection descriptors = Arrays.asList(ModelerCore.getMetamodelRegistry().getMetamodelDescriptors());
        super.setInput(descriptors);
        // add an alphabetical sorter
        this.setSorter(new ViewerSorter() { });
    }

}

class MetamodelLabelProvider implements ILabelProvider, PluginConstants.Images {

    private AdapterFactoryLabelProvider emfLabelProvider;

    public MetamodelLabelProvider(AdapterFactory factory) {
        emfLabelProvider = new AdapterFactoryLabelProvider(factory);
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
	public void addListener(ILabelProviderListener listener) {
        emfLabelProvider.addListener(listener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    @Override
	public void dispose() {
        emfLabelProvider.dispose();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
	public Image getImage(Object element) {
        if (element instanceof EObject) {
            return emfLabelProvider.getImage(element);
        } else if (element instanceof Resource) {
            return UiPlugin.getDefault().getImage(METAMODEL);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
	public String getText(Object element) {
        if (element instanceof EObject) {
            return emfLabelProvider.getText(element);
        } else if (element instanceof Resource) {
            return ((Resource)element).getURI().toString();
        }
        return element.toString();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     */
    @Override
	public boolean isLabelProperty(Object element, String property) {
        if (element instanceof EObject) {
            return emfLabelProvider.isLabelProperty(element, property);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    @Override
	public void removeListener(ILabelProviderListener listener) {
        emfLabelProvider.removeListener(listener);
    }

}

class MetamodelTreeContentProvider implements ArrayUtil.Constants,
                                              ITreeContentProvider {

    private ITreeContentProvider emfContentProvider;
    private IContentFilter filter;

    public MetamodelTreeContentProvider(final AdapterFactory factory,
                                        final IContentFilter filter) {
        emfContentProvider = new AdapterFactoryContentProvider(factory);
        this.filter = filter;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
	public void dispose() {
        emfContentProvider.dispose();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    @Override
	public Object[] getChildren(Object parentElement) {
        Object[] elems;
        if (parentElement instanceof Resource) {
            EPackage ePackage = (EPackage) ((Resource)parentElement).getContents().get(0);
            elems = ePackage.eContents().toArray();
        } else {
            elems = emfContentProvider.getChildren(parentElement);
        }
        if (this.filter == null) {
            return elems;
        }
        return this.filter.filter(elems);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
	public Object[] getElements(Object inputElement) {
        Collection descriptorList = (Collection)inputElement;
        Collection visibleResults = new ArrayList();

        // Defect 21892 - Removing/hiding metamodels the user shouldn't see.
        for (Iterator iter = descriptorList.iterator(); iter.hasNext();) {
            String stringUri = ((MetamodelDescriptor)iter.next()).getNamespaceURI();
            if( UiPlugin.getDefault().isProductContextValueSupported(Metamodel.USER_VISIBLE_URI, stringUri) ) {
                URI uri = URI.createURI(stringUri);
                visibleResults.add(ModelerCore.getMetamodelRegistry().getResource(uri));
            }
        }

        if( !visibleResults.isEmpty() ) {
            int index = 0;
            Object[] result = new Object[visibleResults.size()];
            for (Iterator iter = visibleResults.iterator(); iter.hasNext();) {
                result[index++] = iter.next();
            }
            return result;
        }

        return new Object[0];
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    @Override
	public Object getParent(Object element) {
        return emfContentProvider.getParent(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    @Override
	public boolean hasChildren(Object element) {
        if (element instanceof Resource) {
            return true;
        }
        return emfContentProvider.hasChildren(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

}
