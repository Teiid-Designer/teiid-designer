/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;

/**
 * The <code>OpenEditorListAction</code> adds an navigation editor toolbar button/drop-down containing all the open editors. Dirty
 * editors are indicated by an asterisk after their name.
 */
public class OpenEditorListAction implements IWorkbenchWindowPulldownDelegate {

    IWorkbenchWindow workbenchWindow;

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
     */
    public Menu getMenu( Control theParent ) {
        EditorComparator comparator = new EditorComparator(Locale.getDefault());
        Menu menu = new Menu(theParent);
        final IWorkbenchPage page = workbenchWindow.getActivePage();

        // get list of open editors
        IEditorReference[] editorRefs = page.getEditorReferences();

        // sort list of open editors
        Arrays.sort(editorRefs, comparator);

        // add the sorted editors to the menu
        for (int i = 0; i < editorRefs.length; i++) {
            MenuItem editorMenuItem = new MenuItem(menu, SWT.NONE);
            String text = editorRefs[i].getTitle();

            if (editorRefs[i].isDirty()) {
                text += " *"; //$NON-NLS-1$
            }
            editorMenuItem.setText(text);
            editorMenuItem.setData(editorRefs[i]);

            // disable the entry for the editor that is currently in front to avoid confusing the user
            if (editorRefs[i].getEditor(false) == page.getActiveEditor()) {
                editorMenuItem.setEnabled(false);
            } else {
                // add a SelectionListener that will pop the editor to the front if it is selected in the menu
                editorMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected( SelectionEvent theEvent ) {
                        IEditorReference ref = (IEditorReference)theEvent.widget.getData();
                        IWorkbenchPart editorPart = ref.getPart(true);

                        if (editorPart != null) {
                            workbenchWindow.setActivePage(editorPart.getSite().getPage());
                            page.bringToTop(editorPart);
                        }
                    }
                });
            }
        }

        return menu;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow theWindow ) {
        workbenchWindow = theWindow;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction theAction ) {
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction theAction,
                                  ISelection theSelection ) {
        boolean openEditors = false;
        
        // when shutting down there is no active page
        if (workbenchWindow.getActivePage() != null) {
            openEditors = (workbenchWindow.getActivePage().getEditorReferences().length > 0);
        }

        if (openEditors && !theAction.isEnabled()) {
            theAction.setEnabled(true);
        } else if (!openEditors && theAction.isEnabled()) {
            theAction.setEnabled(false);
        }
    }

    /**
     * Compartor subclass to sort the open editor list by title based on the locale.
     */
    private static class EditorComparator implements Comparator {

        private RuleBasedCollator collator;

        public EditorComparator( Locale theLocale ) {
            collator = (RuleBasedCollator)Collator.getInstance(theLocale);
        }

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare( Object theFirstObject,
                            Object theSecondObject ) {
            IEditorReference editor1 = (IEditorReference)theFirstObject;
            IEditorReference editor2 = (IEditorReference)theSecondObject;
            String title1 = editor1.getTitle();
            String title2 = editor2.getTitle();

            return collator.compare(title1, title2);
        }

    }
}
