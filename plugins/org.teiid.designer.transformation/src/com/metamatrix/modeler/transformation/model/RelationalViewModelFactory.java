/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalModelFactory;
import org.teiid.designer.relational.model.RelationalReference;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.transformation.TransformationPlugin;

/**
 * Class provides building EMF Relational Metamodel objects from Relational Model objects
 */
public class RelationalViewModelFactory extends RelationalModelFactory {
    public static final String RELATIONAL_PACKAGE_URI = RelationalPackage.eNS_URI;
    public static final RelationalFactory FACTORY = RelationalFactory.eINSTANCE;

    public RelationalViewModelFactory() {
        super();
    }

    public void build( ModelResource modelResource,
                       RelationalModel model,
                       IProgressMonitor progressMonitor ) {

        try {
            RelationalViewModelFactory builder = new RelationalViewModelFactory();

            builder.buildFullModel(model, modelResource, progressMonitor);

            modelResource.save(new NullProgressMonitor(), true);
        } catch (ModelerCoreException e) {
            RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    public EObject buildObject( RelationalReference obj,
                                ModelResource modelResource,
                                IProgressMonitor progressMonitor ) throws ModelWorkspaceException {
        EObject newEObject = null;

        String msg = TransformationPlugin.Util.getString("RelationalViewModelFactory.relationalModelFactory_creatingModelChild", obj.getName()); //$NON-NLS-1$

        progressMonitor.setTaskName(msg);
        switch (obj.getType()) {
            case TYPES.MODEL: {
                // NOOP. Shouldn't get here
            }
                break;
            case TYPES.SCHEMA: {
                // NOOP. Shouldn't get here
            }
                break;
            case TYPES.CATALOG: {
                // NOOP. Shouldn't get here
            }
                break;
            case TYPES.TABLE: {
                // Create the Table
                EObject baseTable = createBaseTable(obj, modelResource);
                modelResource.getEmfResource().getContents().add(baseTable);

                // Set the transformation SQL
                RelationalViewTable viewTable = (RelationalViewTable)obj;
                SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(baseTable);
                if (root != null) {
                    TransformationHelper.setSqlString(root,
                                                      viewTable.getTransformationSQL(),
                                                      QueryValidator.SELECT_TRNS,
                                                      false,
                                                      this);
                }
            }
                break;
            case TYPES.VIEW: {
                // Create the View
                EObject view = createView(obj, modelResource);
                modelResource.getEmfResource().getContents().add(view);

                // Set the transformation SQL
                RelationalViewView viewView = (RelationalViewView)obj;
                SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(view);
                if (root != null) {
                    TransformationHelper.setSqlString(root,
                                                      viewView.getTransformationSQL(),
                                                      QueryValidator.SELECT_TRNS,
                                                      false,
                                                      this);
                }
            }
                break;
            case TYPES.PROCEDURE: {
                EObject procedure = createProcedure(obj, modelResource);
                modelResource.getEmfResource().getContents().add(procedure);

                // Set the transformation SQL
                RelationalViewProcedure viewProc = (RelationalViewProcedure)obj;
                SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(procedure);
                if (root != null) {
                    TransformationHelper.setSqlString(root,
                                                      viewProc.getTransformationSQL(),
                                                      QueryValidator.SELECT_TRNS,
                                                      false,
                                                      this);
                }
            }
                break;
            case TYPES.INDEX: {
                // NOOP. Shouldn't get here
            }
                break;

            case TYPES.UNDEFINED:
            default: {
                RelationalPlugin.Util.log(IStatus.WARNING,
                                          NLS.bind(Messages.relationalModelFactory_unknown_object_type_0_cannot_be_processed,
                                                   obj.getName()));
            }
                break;
        }

        return newEObject;
    }

}
