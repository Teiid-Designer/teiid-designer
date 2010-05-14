/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.wizards.CheckboxTreeViewerWizardPanel;
import com.metamatrix.modeler.internal.ui.wizards.StructuralCopyModelFeaturePopulator;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.widget.ICheckableController;
import com.metamatrix.ui.internal.widget.INodeDescendantsDeselectionHandler;
import com.metamatrix.ui.internal.widget.InheritanceCheckboxTreeViewer;

/**
 * TransformationCheckboxTreeViewerWizardPanel
 */
public class TransformationCheckboxTreeViewerWizardPanel extends CheckboxTreeViewerWizardPanel implements ICheckableController {

    private static final String CLEAR_SUPPORTS_UPDATE = UiConstants.Util.getString("TransformationCheckboxTreeViewerWizardPanel.clearSupportsUpdate"); //$NON-NLS-1$

    /** Viewer filter. */
    private TransformationViewerFilter filter;

    /** Collection of aspect classes whose checked state can't be changed. */
    private Collection unsupportedClasses;

    /** Indicates if the default filter is being used. */
    private boolean useFilter = true;

    private Button clearSupportUpdate;

    public TransformationCheckboxTreeViewerWizardPanel( Composite parent,
                                                        WizardPage wizardPage,
                                                        INodeDescendantsDeselectionHandler deselectionHandler,
                                                        MetamodelDescriptor metamodelDescriptor,
                                                        ModelResource selection,
                                                        boolean targetIsVirtual ) {
        super(parent, wizardPage, deselectionHandler, metamodelDescriptor, selection, targetIsVirtual);

        // add types whose checked state can't be changed
        unsupportedClasses = new ArrayList();
        unsupportedClasses.add(SqlColumnAspect.class);
        unsupportedClasses.add(SqlProcedureParameterAspect.class);

        // viewer setup
        InheritanceCheckboxTreeViewer viewer = getViewer();
        viewer.setCheckableController(this);

        // setup filter
        this.filter = new TransformationViewerFilter();
        viewer.addFilter(this.filter);

    }

    /**
     * @see com.metamatrix.modeler.internal.ui.wizards.CheckboxTreeViewerWizardPanel#addOptions(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    @Override
    protected void addOptions( Composite parent ) {
        super.addOptions(parent);

        // add a checkbox to allow the user to not support Update in new tables.
        clearSupportUpdate = new Button(this, SWT.CHECK);
        clearSupportUpdate.setSelection(true);
        clearSupportUpdate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        clearSupportUpdate.setText(CLEAR_SUPPORTS_UPDATE);
    }

    @Override
    protected StructuralCopyModelFeaturePopulator getFeaturePopulator( IFile sourceFile ) {
        return new TransformationCopyModelFeaturePopulator(sourceFile, this);
    }

    public boolean isClearSupportsUpdate() {
        return clearSupportUpdate.getSelection();
    }

    /**
     * @see com.metamatrix.ui.internal.widget.ICheckableController#isEditable(java.lang.Object)
     * @since 4.2
     */
    public boolean isEditable( Object theObject ) {
        boolean result = true;

        if (theObject instanceof EObject) {
            SqlAspect aspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)theObject);

            if (aspect != null) {
                Iterator itr = this.unsupportedClasses.iterator();

                while (itr.hasNext()) {
                    Class c = (Class)itr.next();

                    if (c.isInstance(aspect)) {
                        result = false;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Indicates if the default {@link ViewerFilter} is being used.
     * 
     * @return <code>true</code>if default filter is being used; <code>false</code> otherwise.
     * @since 4.2
     */
    protected boolean isUsingDefaultFilter() {
        return this.useFilter;
    }

    /**
     * Sets whether the default filter should be used. By default it is used.
     * 
     * @param theUseFlag the flag indicating if the default filter should be used
     * @since 4.2
     */
    protected void setUseDefaultFilter( boolean theUseFlag ) {
        if (theUseFlag && !this.useFilter) {
            getViewer().addFilter(this.filter);
        } else if (!theUseFlag) {
            getViewer().removeFilter(this.filter);
        }

        this.useFilter = theUseFlag;
    }

    /**
     * Viewer filter that will only show children having a <code>SqlColumnAspect</code> when the parent has an aspect of
     * <code>SqlTableAspect</code>. And only show children having a <code>SqlProcedureParameterAspect</code> when the parent has
     * an aspect of <code>SqlProcedureAspect</code>. All children of other parent types are always shown.
     * 
     * @since 4.2
     */
    static class TransformationViewerFilter extends ViewerFilter {
        /**
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.2
         */
        @Override
        public boolean select( Viewer theViewer,
                               Object theParentElement,
                               Object theElement ) {
            boolean result = true;

            if ((theParentElement != null) && (theParentElement instanceof EObject)) {
                EObject parent = (EObject)theParentElement;
                SqlAspect aspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(parent);

                if (aspect != null) {
                    if ((aspect instanceof SqlTableAspect) && (theElement instanceof EObject)) {
                        result = true;
                    } else if ((aspect instanceof SqlProcedureAspect) && (theElement instanceof EObject)) {
                        result = (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)theElement) instanceof SqlProcedureParameterAspect);
                    }
                }
            }

            return result;
        }
    }
}
