/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import static com.metamatrix.modeler.internal.ui.PluginConstants.Prefs.General.SORT_MODEL_CONTENTS;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.metamatrix.metamodels.diagram.impl.PresentationEntityImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ImportContainer;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * 
 */
public class ModelContentsSorter extends ViewerSorter {

    private static final int DIAGRAMS_VALUE = -50;
    private static final int IMPORTS_VALUE = -100;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare( Viewer viewer,
                        Object thisObject,
                        Object thatObject ) {
        if (!isSortingModelContents()) {
            return 0;
        }

        if (thisObject instanceof ImportContainer) {
            return IMPORTS_VALUE;
        }

        if ((thisObject instanceof EObject) && (thatObject instanceof EObject)) {
            EObject thisEObject = (EObject)thisObject;
            EObject thatEObject = (EObject)thatObject;

            String thisClass = thisEObject.eClass().getName();
            String thatClass = thatEObject.eClass().getName();
            int result = thisClass.compareTo(thatClass);

            if (result == 0) {
                return super.compare(viewer, ModelerCore.getModelEditor().getName(thisEObject),
                                     ModelerCore.getModelEditor().getName(thatEObject));
            }

            if ((result < 0) && (thisEObject instanceof PresentationEntityImpl)) {
                return DIAGRAMS_VALUE;
            }
        }

        return super.compare(viewer, thisObject, thatObject);
    }

    /**
     * helper method for getting the ModelContents sorting preference
     * 
     * @return 'true' if sort preference is true, 'false' if not.
     */
    private boolean isSortingModelContents() {
        return UiPlugin.getDefault().getPreferenceStore().getBoolean(SORT_MODEL_CONTENTS);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ViewerComparator#sort(org.eclipse.jface.viewers.Viewer, java.lang.Object[])
     */
    @Override
    public void sort( final Viewer viewer,
                      final Object[] elements ) {
        Comparator comparator = new Comparator() {
            @Override
            public int compare( Object thisEObject,
                                Object thatEObject ) {
                return ModelContentsSorter.this.compare(viewer, thisEObject, thatEObject);
            }
        };

        Arrays.sort(elements, comparator);
    }

}
