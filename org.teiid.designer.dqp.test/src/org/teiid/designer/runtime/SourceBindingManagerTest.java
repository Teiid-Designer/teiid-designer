/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.RefactorResourceEvent;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.internal.workspace.SourceBinding;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * 
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( {ModelerCore.class, ModelWorkspaceManager.class, ResourcesPlugin.class} )
public class SourceBindingManagerTest {
    private static final Connector NULL_CONNECTOR = null;
    private static final ModelResource NULL_MODEL_RESOURCE = null;
    private static final SourceBinding NULL_SOURCE_BINDING = null;
    private static final String NULL_STRING = null;

    private ExecutionAdmin commonExecutionAdmin;
    private Connector commonConnector;

    @Before
    public void beforeEach() {
        MockObjectFactory.initializeStaticWorkspaceClasses();

        this.commonExecutionAdmin = MockObjectFactory.createExecutionAdmin();
        this.commonConnector = MockObjectFactory.createConnector("connectorName", "connectorTypeName");
    }

    private Connector getMockConnector() {
        return MockObjectFactory.createConnector("connectorName", "connectorTypeName");
    }

    private ModelResource getMockModelResource() {
        return MockObjectFactory.createModelResource("modelName", "relativePath");
    }

    private SourceBinding getNewSourceBinding() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnector());
        return new SourceBinding("name", "path", connectors);
    }

    private SourceBindingsManager getNewSBM() {
        return new SourceBindingsManager(commonExecutionAdmin);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingManagerNullAdmin() {
        new SourceBindingsManager(null);
    }

    @Test
    public void shouldAllowCreateSourceBindingManagerWithAdmin() {
        new SourceBindingsManager(commonExecutionAdmin);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingWithNullModelResource() {
        getNewSBM().createSourceBinding(null, NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingWithNullConnector() {
        getNewSBM().createSourceBinding(getMockModelResource(), NULL_CONNECTOR);
    }

    @Test
    public void shouldAllowCreateSourceBindingWithConnector() {
        getNewSBM().createSourceBinding(getMockModelResource(), commonConnector);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetConnectorsForModelWithNullName() {
        getNewSBM().getConnectorsForModel(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetConnectorsForModelWithEmptyName() {
        getNewSBM().getConnectorsForModel("");
    }

    @Test
    public void shouldAllowGetConnectorsForModelWithEmptyName() {
        getNewSBM().getConnectorsForModel("modelName");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetJdbcSourceWithNullModelResource() {
        getNewSBM().getJdbcSource(null);
    }

    @Test
    public void shouldAllowGetJdbcSourceWithModelResource() throws ModelWorkspaceException {
        ModelResource mr = getMockModelResource();
        when(mr.getEObjects()).thenReturn(Collections.EMPTY_LIST);
        getNewSBM().getJdbcSource(mr);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetSourceBindingWithNullModelName() {
        getNewSBM().getSourceBinding(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetSourceBindingWithEmptyModelName() {
        getNewSBM().getSourceBinding("");
    }

    @Test
    public void shouldAllowGetSourceBindingWithWithModelName() {
        getNewSBM().getSourceBinding("modelName");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetSourceBindingsWithNullConnector() {
        getNewSBM().getSourceBindings(null);
    }

    @Test
    public void shouldAllowGetSourceBindingsWithWithConnector() {
        getNewSBM().getSourceBindings(getMockConnector());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowHasSourceBindingWithNullModelResource() {
        getNewSBM().hasSourceBinding(null);
    }

    @Test
    public void shouldAllowHasSourceBindingWithWithModelResource() {
        getNewSBM().hasSourceBinding(getMockModelResource());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNotifyRefactoredWithNullEvent() {
        getNewSBM().notifyRefactored(null);
    }

    @Test
    public void shouldAllowNotifyRefactoredWithBogusEvent() {
        RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        when(event.getType()).thenReturn(-2);
        getNewSBM().notifyRefactored(event);
    }

    @Test
    public void shouldAllowNotifyRefactoredWithMoveEvent() {
        RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        when(event.getType()).thenReturn(RefactorResourceEvent.TYPE_MOVE);
        getNewSBM().notifyRefactored(event);
    }

    @Test
    public void shouldAllowNotifyRefactoredWithRenameEvent() {
        RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        when(event.getType()).thenReturn(RefactorResourceEvent.TYPE_RENAME);
        getNewSBM().notifyRefactored(event);
    }

    @Test
    public void shouldAllowNotifyRefactoredWithDeleteEvent() {
        RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        when(event.getType()).thenReturn(RefactorResourceEvent.TYPE_DELETE);
        // theEvent.getOriginalPath().lastSegment()
        IPath path = mock(IPath.class);
        when(path.lastSegment()).thenReturn("lastSegment");
        when(event.getOriginalPath()).thenReturn(path);
        getNewSBM().notifyRefactored(event);
    }

    @Test
    public void shouldAllowRefresh() throws Exception {

        getNewSBM().refresh();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveSourceBindingWithNullModelResource() {
        getNewSBM().removeSourceBinding(NULL_MODEL_RESOURCE);
    }

    @Test
    public void shouldAllowRemoveSourceBindingWithModelResource() {
        getNewSBM().removeSourceBinding(getMockModelResource());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveSourceBindingWithNullModelName() {
        getNewSBM().removeSourceBinding(NULL_STRING);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveSourceBindingWithEmptyModelName() {
        getNewSBM().removeSourceBinding("");
    }

    @Test
    public void shouldAllowRemoveSourceBindingWithModelName() {
        getNewSBM().removeSourceBinding("modelName");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveSourceBindingWithNullSourceBinding() {
        getNewSBM().removeSourceBinding(NULL_SOURCE_BINDING);
    }

    @Test
    public void shouldAllowRemoveSourceBindingWithSourceBinding() {
        getNewSBM().removeSourceBinding(getNewSourceBinding());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowResourceChangedWithNullEvent() {
        getNewSBM().resourceChanged(null);
    }

    @Test
    public void shouldAllowResourceChangedWithEvent() {
        IResourceChangeEvent event = mock(IResourceChangeEvent.class);
        getNewSBM().resourceChanged(event);
    }

    @Test
    public void shouldAllowResourceChangedWithPreDeleteEvent() {
        IResourceChangeEvent event = mock(IResourceChangeEvent.class);
        IResource resource = mock(IResource.class);
        when(resource.getName()).thenReturn("resourceName");
        when(event.getResource()).thenReturn(resource);
        when(event.getType()).thenReturn(IResourceChangeEvent.PRE_DELETE);
        getNewSBM().resourceChanged(event);
    }

    @Test
    public void shouldAllowResourceChangedWithPrePostChangeEvent() {
        IResourceChangeEvent event = mock(IResourceChangeEvent.class);
        IResource resource = mock(IResource.class);
        when(resource.getName()).thenReturn("resourceName");
        when(event.getResource()).thenReturn(resource);
        when(event.getType()).thenReturn(IResourceChangeEvent.POST_CHANGE);
        // theEvent.getDelta().getResource()
        IResourceDelta delta = mock(IResourceDelta.class);
        when(delta.getResource()).thenReturn(resource);
        when(event.getDelta()).thenReturn(delta);
        getNewSBM().resourceChanged(event);
    }
}
