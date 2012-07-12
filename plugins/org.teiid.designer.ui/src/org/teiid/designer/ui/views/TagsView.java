/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.views;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.properties.ModelObjectPropertySheetPage;
import org.teiid.designer.ui.properties.udp.UserDefinedPropertySourceProvider;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * TagsView is a properties view for the user-defined properties on an EObject.
 */
public class TagsView extends ModelerView implements INotifyChangedListener {

    PropertySheetPage page;

    /**
     * Construct an instance of TagsView.
     */
    public TagsView() {
        super();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        super.createPartControl(parent);
        page = new ModelObjectPropertySheetPage();
        page.setPropertySourceProvider(new UserDefinedPropertySourceProvider());
        page.createControl(parent);
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(page);
        ISelection selection = getSite().getWorkbenchWindow().getSelectionService().getSelection();
        if (selection != null) {
            super.selectionProvider.setSelection(selection);
            page.selectionChanged(this, selection);
        }

        ModelUtilities.addNotifyChangedListener(this);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        page.setFocus();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(page);
        ModelUtilities.removeNotifyChangedListener(this);
        super.dispose();
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification notification ) {
        // compare the current EObject with the notification target
        EObject target = NotificationUtilities.getEObject(notification);

        // changes to extended properties are on the annotation
        if (target instanceof EStringToStringMapEntryImpl) {
            EObject mightBeAnnotation = ((EStringToStringMapEntryImpl)target).eContainer();
            if (mightBeAnnotation instanceof Annotation) {
                target = ((Annotation)mightBeAnnotation).getAnnotatedObject();
            }
        }

        ISelection currentSelection = super.selectionProvider.getSelection();
        EObject object = SelectionUtilities.getSelectedEObject(currentSelection);
        if (object != null) {
            if (object.equals(target)) {
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        page.refresh();
                    }
                });
            }
        }
    }
}
