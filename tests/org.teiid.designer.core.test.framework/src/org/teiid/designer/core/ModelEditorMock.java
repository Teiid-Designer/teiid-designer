/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.ModelerCoreRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelEditorImpl.AddCommandFactory;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.resource.MMXmiResource;
import org.teiid.designer.core.resource.xmi.MtkXmiResourceImpl;
import org.teiid.designer.core.spi.RegistrySPI;
import org.teiid.designer.core.transaction.UnitOfWork;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelResourceContainerFactory;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.ModelAnnotation;

/**
 * Mock the model editor
 */
public class ModelEditorMock {

    private ModelWorkspaceMock modelWkspMock;

    private ModelEditor modelEditor;

    /**
     * Create instance
     * @param modelWorkspaceMock 
     * @throws Exception
     */
    public ModelEditorMock(ModelWorkspaceMock modelWorkspaceMock) throws Exception {
        this.modelWkspMock = modelWorkspaceMock;
        modelEditor = mock(ModelEditor.class);

        //
        // Registers itself to ModelerCore
        //
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.MODEL_EDITOR_KEY, modelEditor);

        when(modelEditor.getName(isA(EObject.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ModelUtil.getName((EObject)args[0]);
            }
        });
        when(modelEditor.getNameFeature(isA(EObject.class))).thenAnswer(new Answer<EStructuralFeature>() {
            @Override
            public EStructuralFeature answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ModelUtil.getNameFeature((EObject)args[0]);
            }
        });
        when(modelEditor.findModelResource(isA(Resource.class))).thenAnswer(new Answer<ModelResource>() {
            @Override
            public ModelResource answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Resource resource = (Resource) args[0];
                return ModelerCore.getModelWorkspace().findModelResource(resource);
            }
        });
        when(modelEditor.findModelResource(isA(EObject.class))).thenAnswer(new Answer<ModelResource>() {
            @Override
            public ModelResource answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                EObject eObject = (EObject) args[0];
                ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(eObject);
                return modelResource;
            }
        });
        when(modelEditor.findModelResource(isA(IResource.class))).thenAnswer(new Answer<ModelResource>() {
            @Override
            public ModelResource answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                IResource resource = (IResource) args[0];
                return (ModelResource)ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(resource);
            }
        });
        when(modelEditor.getUri(isA(EObject.class))).then(new Answer<URI>() {
           @Override
            public URI answer(InvocationOnMock invocation) throws Throwable {
               Object[] args = invocation.getArguments();
               EObject object = (EObject) args[0];

               if (object instanceof EClass) {
                   Resource eResource = object.eResource();
                   // Do this only if we are in the Eclipse plugin environment since the metamodel
                   // registry will not be loaded otherwise.
                   if (eResource != null && ModelerCore.getPlugin() != null) {
                       URI eResourceUri = eResource.getURI();
                       MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(eResourceUri);
                       if (descriptor != null) {
                           String logicalUri = URI.decode(descriptor.getNamespaceURI());
                           return URI.createURI(logicalUri).appendFragment(eResource.getURIFragment(object));
                       }
                   }
               }

               // Otherwise return the URI based on the EObject's resource URI and
               // it's location within that resource
               return EcoreUtil.getURI(object);
            } 
        });

        when(modelEditor.getModelContents(isA(ModelResource.class))).thenAnswer(new Answer<ModelContents>() {
            @Override
            public ModelContents answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                ModelResource resource = (ModelResource) args[0];
                return ModelContents.getModelContents(resource);
            }
        });

        when(modelEditor.getModelContents(isA(EObject.class))).thenAnswer(new Answer<ModelContents>() {
            @Override
            public ModelContents answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                EObject eObject = (EObject) args[0];
                return modelEditor.getModelContents(eObject.eResource());
            }
        });

        when(modelEditor.getModelContents(isA(Resource.class))).thenAnswer(new Answer<ModelContents>() {
            @Override
            public ModelContents answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Resource resource = (Resource) args[0];
                if (resource instanceof MtkXmiResourceImpl) {
                  return ((MtkXmiResourceImpl)resource).getModelContents();
                }
                final ModelResource modelResource = modelEditor.findModelResource(resource);
                return modelEditor.getModelContents(modelResource);
            }
        });

        when(modelEditor.getModelAnnotation(isA(EObject.class))).thenAnswer(new Answer<ModelAnnotation>() {
            @Override
            public ModelAnnotation answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                EObject eObject = (EObject) args[0];
                final Resource resource = eObject.eResource();
                if (resource instanceof MMXmiResource) {
                    return ((MMXmiResource)resource).getModelAnnotation();
                }
                final ModelResource modelResource = resource == null ? null : modelEditor.findModelResource(resource);
                return modelResource == null ? null : modelResource.getModelAnnotation();
            }
        });

        when(modelEditor.getAnnotation(isA(EObject.class), anyBoolean())).thenAnswer(new Answer<Annotation>() {
            @Override
            public Annotation answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                EObject eObject = (EObject) args[0];
                Boolean forceCreate = (Boolean) args[1];
                
                final Resource eObjectResource = eObject.eResource();
                if (eObjectResource instanceof MMXmiResource) {
                    // Just look up the annotations right off the resource ...
                    final MMXmiResource emfResource = (MMXmiResource)eObjectResource;
                    Annotation annotation = emfResource.getAnnotation(eObject);
                    if (annotation == null && forceCreate) {
                        annotation = ModelResourceContainerFactory.createNewAnnotation(eObject, emfResource.getAnnotationContainer(true));
                    }
                    return annotation;
                }
                return null;
            }
        });

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                addValue(args[0], args[1], (EList) args[2], CommandParameter.NO_INDEX);
                return null;
            }
        }).when(modelEditor).addValue(any(), any(), isA(EList.class));
    }

    public void dispose() {
        Mockito.reset(modelEditor);
        ((RegistrySPI) ModelerCore.getRegistry()).unregister(ModelerCore.MODEL_EDITOR_KEY);
        modelEditor = null;
        modelWkspMock = null;
    }

    /**
     * @return the modelEditor
     */
    public ModelEditor getModelEditor() {
        return this.modelEditor;
    }

    private Container getContainer() {
        return modelWkspMock.getContainer();
    }

    protected void executeCommandInTransaction( final UnitOfWork uow,
                                                final Object owner,
                                                final Command cmd ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(uow);

        if (cmd == null)
            fail("Command was null"); //$NON-NLS-1$

        if (!cmd.canExecute()) {
            fail("Command cannot execute"); //$NON-NLS-1$
        }

        // Execute the command ...
        uow.executeCommand(cmd);
    }

    protected Object executeAsTransaction( final TransactionRunnable runnable,
                                           Container container,
                                           final String operationDescription,
                                           final boolean isSignificant,
                                           final boolean undoable,
                                           final Object source ) throws ModelerCoreException {
        // Use default container if not specified
        if (container == null) {
            container = getContainer();
        }
        // Get the current transaction (this may create one) ...
        final UnitOfWork uow = container.getEmfTransactionProvider().getCurrent();
        boolean startedTxn = false;
        // If the current transaction is new, it must be started ...
        if (uow.requiresStart()) {
            uow.begin();
            startedTxn = true;
            uow.setSignificant(isSignificant);
            uow.setSource(source);
            uow.setUndoable(undoable);
            // Set the description ...
            if (operationDescription != null) {
                uow.setDescription(operationDescription);
            }
        }
        boolean failed = false;
        Object result = null;
        try {
            // Perform the requested work
            result = runnable.run(uow);
        } catch (ModelerCoreException t) {
            // There was a runtime exception while performing the work
            failed = true;
            throw t;
        } catch (RuntimeException t) {
            // There was a runtime exception while performing the work
            failed = true;
            throw new ModelerCoreRuntimeException(t);
        } finally {
            if (startedTxn) {
                // We started the current transaction ...
                if (!failed) {
                    // Commit the transaction ...
                    try {
                        uow.commit();
                    } catch (Throwable e) {
                        ModelerCore.Util.log(e);
                        failed = true;
                    }
                }

                if (failed) {
                    // Rollback the transaction ...
                    // (which may have failed while performing the work or when committing)
                    try {
                        uow.rollback();
                    } catch (ModelerCoreException e1) {
                        ModelerCore.Util.log(e1);
                    }
                }
            }
        }
        return result;
    }

    private String getPresentationValue( final Object obj ) {
        if (obj == null) {
            return ""; //$NON-NLS-1$
        }

        if (obj instanceof EObject) {
            for (Iterator iter = ((EObject)obj).eClass().getEAllStructuralFeatures().iterator(); iter.hasNext();) {
                final EStructuralFeature feature = (EStructuralFeature)iter.next();
                if (ModelEditor.NAME_FEATURE_NAME.equalsIgnoreCase(feature.getName())) {
                    Object val = ((EObject)obj).eGet(feature);
                    if (val == null) {
                        return ((EObject)obj).eClass().getName();
                    }
                    return val.toString();
                }
            }
            return ((EObject)obj).eClass().getName();
        } else if (obj instanceof Resource) {
            return URI.decode(((Resource)obj).getURI().toString());
        } else if (obj instanceof Collection) {
            if (((Collection)obj).size() > 1) {
                return ModelerCore.Util.getString("ModelEditorImpl.many_1"); //$NON-NLS-1$
            } else if (((Collection)obj).size() == 0) {
                return ModelerCore.Util.getString("ModelEditorImpl.empty_list_2"); //$NON-NLS-1$
            }

            Object first = ((Collection)obj).iterator().next();
            return getPresentationValue(first);
        }

        return obj.toString();
    }

    protected void addValue( final Object owner, final Object value, final EList feature,
                                                  final int index ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(owner);
        CoreArgCheck.isNotNull(value);
        CoreArgCheck.isNotNull(feature);

        // convert the value into a collection
        final Collection values = new ArrayList();
        if (value instanceof Collection) {
            values.addAll((Collection)value);
        } else {
            values.add(value);
        }

        final Container cntr = getContainer();
        if (cntr == null) {
            feature.add(index, values);
        } else {
            final boolean isSignificant = true;
            final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Add_values_to_{0}_1", getPresentationValue(owner)); //$NON-NLS-1$
            final TransactionRunnable runnable = new TransactionRunnable() {
                @Override
                public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                    final EditingDomain ed = modelWkspMock.getEditingDomain();
                    Command command = AddCommandFactory.create(owner, ed, feature, values, index);
                    executeCommandInTransaction(uow, owner, command);
                    return null;
                }
            };
            executeAsTransaction(runnable, cntr, operationDescription, isSignificant, true, this);
        }
    }
}
