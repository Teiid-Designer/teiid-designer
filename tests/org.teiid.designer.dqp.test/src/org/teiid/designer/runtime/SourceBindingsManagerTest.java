/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Collection;
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
import org.teiid.designer.core.ModelWorkspaceMock;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.RefactorResourceEvent;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.internal.workspace.SourceBinding;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * 
 */
@RunWith( PowerMockRunner.class ) @PrepareForTest( {ModelerCore.class, ModelWorkspaceManager.class, ResourcesPlugin.class} ) public class SourceBindingsManagerTest {
    private static final Connector NULL_CONNECTOR = null;
    private static final ModelResource NULL_MODEL_RESOURCE = null;
    private static final SourceBinding NULL_SOURCE_BINDING = null;
    private static final String NULL_STRING = null;

    private SourceBindingsManager sourceBindingsMgr;

    @Before public void beforeEach() {
        new ModelWorkspaceMock();
        this.sourceBindingsMgr = new SourceBindingsManager(MockObjectFactory.createExecutionAdmin());
    }

    private Connector getMockConnector() {
        return MockObjectFactory.createConnector("connectorName", "connectorTypeName");
    }

    private ModelResource getMockModelResource() {
        return MockObjectFactory.createModelResource("modelName", "relativePath");
    }

    private SourceBinding getNewSourceBinding() {
        final Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnector());
        return new SourceBinding("name", "path", connectors);
    }

    @Test public void shouldAllowGetConnectorsForModelWithEmptyName() {
        this.sourceBindingsMgr.getConnectorsForModel("modelName");
    }

    @Test public void shouldAllowGetJdbcSourceWithModelResource() throws ModelWorkspaceException {
        final ModelResource mr = getMockModelResource();
        when(mr.getEObjects()).thenReturn(Collections.EMPTY_LIST);
        this.sourceBindingsMgr.getJdbcSource(mr);
    }

    @Test public void shouldAllowGetSourceBindingsWithWithConnector() {
        this.sourceBindingsMgr.getSourceBindings(getMockConnector());
    }

    @Test public void shouldAllowGetSourceBindingWithWithModelName() {
        this.sourceBindingsMgr.getSourceBinding("modelName");
    }

    @Test public void shouldAllowHasSourceBindingWithWithModelResource() {
        this.sourceBindingsMgr.hasSourceBinding(getMockModelResource());
    }

    @Test public void shouldAllowNotifyRefactoredWithBogusEvent() {
        final RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        when(event.getType()).thenReturn(-2);
        this.sourceBindingsMgr.notifyRefactored(event);
    }

    @Test public void shouldAllowNotifyRefactoredWithDeleteEvent() {
        final RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        when(event.getType()).thenReturn(RefactorResourceEvent.TYPE_DELETE);
        // theEvent.getOriginalPath().lastSegment()
        final IPath path = mock(IPath.class);
        when(path.lastSegment()).thenReturn("lastSegment");
        when(event.getOriginalPath()).thenReturn(path);
        this.sourceBindingsMgr.notifyRefactored(event);
    }

    @Test public void shouldAllowNotifyRefactoredWithMoveEvent() {
        final RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        when(event.getType()).thenReturn(RefactorResourceEvent.TYPE_MOVE);
        this.sourceBindingsMgr.notifyRefactored(event);
    }

    @Test public void shouldAllowNotifyRefactoredWithRenameEvent() {
        final RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        when(event.getType()).thenReturn(RefactorResourceEvent.TYPE_RENAME);
        this.sourceBindingsMgr.notifyRefactored(event);
    }

    @Test public void shouldAllowRefresh() throws Exception {

        this.sourceBindingsMgr.refresh();
    }

    @Test public void shouldAllowRemoveSourceBindingWithModelName() {
        this.sourceBindingsMgr.removeSourceBinding("modelName");
    }

    @Test public void shouldAllowRemoveSourceBindingWithModelResource() {
        this.sourceBindingsMgr.removeSourceBinding(getMockModelResource());
    }

    @Test public void shouldAllowRemoveSourceBindingWithSourceBinding() {
        this.sourceBindingsMgr.removeSourceBinding(getNewSourceBinding());
    }

    @Test public void shouldAllowResourceChangedWithEvent() {
        final IResourceChangeEvent event = mock(IResourceChangeEvent.class);
        this.sourceBindingsMgr.resourceChanged(event);
    }

    @Test public void shouldAllowResourceChangedWithPreDeleteEvent() {
        final IResourceChangeEvent event = mock(IResourceChangeEvent.class);
        final IResource resource = mock(IResource.class);
        when(resource.getName()).thenReturn("resourceName");
        when(event.getResource()).thenReturn(resource);
        when(event.getType()).thenReturn(IResourceChangeEvent.PRE_DELETE);
        this.sourceBindingsMgr.resourceChanged(event);
    }

    @Test public void shouldAllowResourceChangedWithPrePostChangeEvent() {
        final IResourceChangeEvent event = mock(IResourceChangeEvent.class);
        final IResource resource = mock(IResource.class);
        when(resource.getName()).thenReturn("resourceName");
        when(event.getResource()).thenReturn(resource);
        when(event.getType()).thenReturn(IResourceChangeEvent.POST_CHANGE);
        // theEvent.getDelta().getResource()
        final IResourceDelta delta = mock(IResourceDelta.class);
        when(delta.getResource()).thenReturn(resource);
        when(event.getDelta()).thenReturn(delta);
        this.sourceBindingsMgr.resourceChanged(event);
    }

    @Test public void shouldCreateSourceBinding() {
        final ModelResource modelResource = getMockModelResource();
        this.sourceBindingsMgr.createSourceBinding(modelResource, getMockConnector());
        assertThat(this.sourceBindingsMgr.getSourceBinding(modelResource.getItemName()), is(notNullValue()));
    }

    @Test public void shouldFindAllSourceBindingsHavingSpecifiedConnector() {
        // setup
        final Connector connector = getMockConnector();
        final int COUNT = 3;

        for (int i = 0; i < COUNT; ++i) {
            this.sourceBindingsMgr.createSourceBinding(MockObjectFactory.createModelResource("model" + i, "path"), connector);
        }

        // test
        assertThat(this.sourceBindingsMgr.getSourceBindings(connector).size(), is(COUNT));
    }

    @Test public void shouldFindSingleConnectorForModel() {
        // setup
        final ModelResource modelResource = getMockModelResource();
        final Connector connector = getMockConnector();
        this.sourceBindingsMgr.createSourceBinding(modelResource, connector);
        final Collection<Connector> connectors = this.sourceBindingsMgr.getConnectorsForModel(modelResource.getItemName());

        // tests
        assertThat(connectors.size(), is(1));
        assertThat(connectors.iterator().next(), is(sameInstance(connector)));
    }

    @Test public void shouldHaveSourceBinding() {
        // setup
        final ModelResource modelResource = getMockModelResource();
        this.sourceBindingsMgr.createSourceBinding(modelResource, getMockConnector());

        // tests
        assertThat(this.sourceBindingsMgr.hasSourceBinding(modelResource), is(true));
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowCreateSourceBindingManagerNullAdmin() {
        new SourceBindingsManager(null);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowCreateSourceBindingWithNullConnector() {
        this.sourceBindingsMgr.createSourceBinding(getMockModelResource(), NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowCreateSourceBindingWithNullModelResource() {
        this.sourceBindingsMgr.createSourceBinding(null, NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowGetConnectorsForModelWithEmptyName() {
        this.sourceBindingsMgr.getConnectorsForModel("");
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowGetConnectorsForModelWithNullName() {
        this.sourceBindingsMgr.getConnectorsForModel(null);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowGetJdbcSourceWithNullModelResource() {
        this.sourceBindingsMgr.getJdbcSource(null);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowGetSourceBindingsWithNullConnector() {
        this.sourceBindingsMgr.getSourceBindings(null);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowGetSourceBindingWithEmptyModelName() {
        this.sourceBindingsMgr.getSourceBinding("");
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowGetSourceBindingWithNullModelName() {
        this.sourceBindingsMgr.getSourceBinding(null);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowHasSourceBindingWithNullModelResource() {
        this.sourceBindingsMgr.hasSourceBinding(null);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowNotifyRefactoredWithNullEvent() {
        this.sourceBindingsMgr.notifyRefactored(null);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowRemoveSourceBindingWithEmptyModelName() {
        this.sourceBindingsMgr.removeSourceBinding("");
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowRemoveSourceBindingWithNullModelName() {
        this.sourceBindingsMgr.removeSourceBinding(NULL_STRING);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowRemoveSourceBindingWithNullModelResource() {
        this.sourceBindingsMgr.removeSourceBinding(NULL_MODEL_RESOURCE);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowRemoveSourceBindingWithNullSourceBinding() {
        this.sourceBindingsMgr.removeSourceBinding(NULL_SOURCE_BINDING);
    }

    @Test( expected = IllegalArgumentException.class ) public void shouldNotAllowResourceChangedWithNullEvent() {
        this.sourceBindingsMgr.resourceChanged(null);
    }

    @Test public void shouldNotHaveSourceBindingIfOneNeverCreated() {
        assertThat(this.sourceBindingsMgr.hasSourceBinding(getMockModelResource()), is(false));
    }

    @Test public void shouldRemoveSourceBindingUsingModelName() {
        // setup
        final ModelResource modelResource = getMockModelResource();
        this.sourceBindingsMgr.createSourceBinding(modelResource, getMockConnector());
        this.sourceBindingsMgr.removeSourceBinding(modelResource.getItemName());

        // tests
        assertThat(this.sourceBindingsMgr.hasSourceBinding(modelResource), is(false));
    }

    @Test public void shouldRemoveSourceBindingUsingModelResource() {
        // setup
        final ModelResource modelResource = getMockModelResource();
        this.sourceBindingsMgr.createSourceBinding(modelResource, getMockConnector());
        this.sourceBindingsMgr.removeSourceBinding(modelResource);

        // tests
        assertThat(this.sourceBindingsMgr.hasSourceBinding(modelResource), is(false));
    }

    @Test public void shouldRemoveSourceBindingUsingSourceBinding() {
        // setup
        final ModelResource modelResource = getMockModelResource();
        this.sourceBindingsMgr.createSourceBinding(modelResource, getMockConnector());
        final SourceBinding binding = this.sourceBindingsMgr.getSourceBinding(modelResource.getItemName());
        this.sourceBindingsMgr.removeSourceBinding(binding);

        // tests
        assertThat(this.sourceBindingsMgr.hasSourceBinding(modelResource), is(false));
    }

}
