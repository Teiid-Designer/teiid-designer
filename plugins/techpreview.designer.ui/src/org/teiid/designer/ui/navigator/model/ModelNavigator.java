/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.viewToolTip;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySourceProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.search.IModelObjectMatch;
import com.metamatrix.modeler.ui.search.MetadataMatchInfo;

/**
 * 
 */
public final class ModelNavigator extends CommonNavigator {

    /**
     * 
     */
    public ModelNavigator() {
        setTitleToolTip(viewToolTip);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonNavigator#dispose()
     */
    @Override
    public void dispose() {
        CommonViewer viewer = getCommonViewer();

        if (viewer.getContentProvider() != null) {
            viewer.getContentProvider().dispose();
        }

        if (viewer.getLabelProvider() != null) {
            viewer.getLabelProvider().dispose();
        }

        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonNavigator#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class key ) {
        if (key.equals(IPropertySheetPage.class)) {
            ModelObjectPropertySourceProvider propertySourceProvider = ModelUtilities.getPropertySourceProvider();
            return propertySourceProvider.getPropertySheetPage();
        }

        return super.getAdapter(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonNavigator#show(org.eclipse.ui.part.ShowInContext)
     */
    @Override
    public boolean show( ShowInContext context ) {
        Set<EObject> toSelect = new HashSet<EObject>();
        ISelection sel = context.getSelection();

        if (sel instanceof IStructuredSelection) {
            for (Object obj : ((IStructuredSelection)sel).toArray()) {

                // a search results has been selected
                if (obj instanceof IModelObjectMatch) {
                    EObject eObj = ((IModelObjectMatch)obj).getEObject();

                    if (eObj != null) {
                        toSelect.add(eObj);
                    }
                } else if (obj instanceof MetadataMatchInfo) {
                    // a resource in the search result has been selected
                    Match[] matches = ((MetadataMatchInfo)obj).getMatches();

                    for (Match match : matches) {
                        if (match instanceof IModelObjectMatch) {
                            EObject eObj = ((IModelObjectMatch)match).getEObject();

                            if (eObj != null) {
                                toSelect.add(eObj);
                            }
                        }
                    }
                }
            }
        }

        // select in tree
        if (!toSelect.isEmpty()) {
            CommonViewer viewer = getCommonViewer();
            viewer.getControl().setRedraw(false);
            viewer.setSelection(new StructuredSelection(toSelect.toArray()), true);
            viewer.getControl().setRedraw(true);
            return true;
        }

        // if no EObjects let the superclass decide if they can be selected
        return super.show(context);
    }

}
