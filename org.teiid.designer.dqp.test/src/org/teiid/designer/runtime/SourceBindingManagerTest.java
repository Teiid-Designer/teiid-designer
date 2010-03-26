/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.teiid.adminapi.Admin;
import com.metamatrix.modeler.core.refactor.RefactorResourceEvent;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.dqp.internal.workspace.SourceBinding;

/**
 * 
 */
public class SourceBindingManagerTest {
    private static final Set<Connector> NULL_CONNECTORS = null;
    private static final Connector NULL_CONNECTOR = null;
    private static final ModelResource NULL_MODEL_RESOURCE = null;
    private static final SourceBinding NULL_SOURCE_BINDING = null;
    private static final String NULL_STRING = null;

    @Mock
    private ExecutionAdmin commonExecutionAdmin;

    private Connector commonConnector;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);

        commonConnector = mock(Connector.class);
        ConnectorType type = mock(ConnectorType.class);
        stub(commonConnector.getType()).toReturn(type);
        stub(type.getAdmin()).toReturn(commonExecutionAdmin);
        Admin admin = mock(Admin.class);
        stub(commonExecutionAdmin.getAdminApi()).toReturn(admin);
        EventManager eventManager = mock(EventManager.class);
        stub(commonExecutionAdmin.getEventManager()).toReturn(eventManager);
    }

    private Connector getMockConnector() {
        Connector conn = mock(Connector.class);
        ConnectorType type = mock(ConnectorType.class);
        ExecutionAdmin executionAdmin = mock(ExecutionAdmin.class);
        Admin admin = mock(Admin.class);
        stub(conn.getType()).toReturn(type);
        stub(type.getAdmin()).toReturn(executionAdmin);
        stub(executionAdmin.getAdminApi()).toReturn(admin);

        return conn;
    }

    private ModelResource getMockModelResource() {
        // SourceBinding newBinding = new SourceBinding(modelName,
        // modelResource.getParent().getPath().makeRelative().toString(),
        // connectors);
        // this.bindingsByModelNameMap.put(modelName, newBinding);

        ModelResource mr = mock(ModelResource.class);
        ModelWorkspaceItem mwi = mock(ModelWorkspaceItem.class);
        IPath iPath = mock(IPath.class);
        IPath relativePath = mock(IPath.class);
        stub(mr.getParent()).toReturn(mwi);
        stub(mwi.getPath()).toReturn(iPath);
        stub(iPath.makeRelative()).toReturn(relativePath);
        stub(relativePath.toString()).toReturn("relativePath");
        stub(mr.getItemName()).toReturn("modelName");

        return mr;
    }

    private Connector getMockConnectorWithCommonAdmin() {
        Connector conn = mock(Connector.class);
        ConnectorType type = mock(ConnectorType.class);
        stub(conn.getType()).toReturn(type);
        stub(type.getAdmin()).toReturn(commonExecutionAdmin);

        return conn;
    }

    private SourceBinding getNewSourceBinding() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnector());
        return new SourceBinding("name", "path", connectors);
    }

    private SourceBinding getNewSourceBindingWithCommonAdmin() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnectorWithCommonAdmin());
        return new SourceBinding("name", "path", connectors);
    }

    private SourceBinding getNewSourceBindingWithMultipleConnectors() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnectorWithCommonAdmin());
        connectors.add(commonConnector);
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
        getNewSBM().createSourceBinding(mock(ModelResource.class), NULL_CONNECTOR);
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
        stub(mr.getEObjects()).toReturn(Collections.EMPTY_LIST);
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
        stub(event.getType()).toReturn(-2);
        getNewSBM().notifyRefactored(event);
    }

    @Test
    public void shouldAllowNotifyRefactoredWithMoveEvent() {
        RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        stub(event.getType()).toReturn(RefactorResourceEvent.TYPE_MOVE);
        getNewSBM().notifyRefactored(event);
    }

    @Test
    public void shouldAllowNotifyRefactoredWithRenameEvent() {
        RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        stub(event.getType()).toReturn(RefactorResourceEvent.TYPE_RENAME);
        getNewSBM().notifyRefactored(event);
    }

    @Test
    public void shouldAllowNotifyRefactoredWithDeleteEvent() {
        RefactorResourceEvent event = mock(RefactorResourceEvent.class);
        stub(event.getType()).toReturn(RefactorResourceEvent.TYPE_DELETE);
        // theEvent.getOriginalPath().lastSegment()
        IPath path = mock(IPath.class);
        stub(path.lastSegment()).toReturn("lastSegment");
        stub(event.getOriginalPath()).toReturn(path);
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
        stub(resource.getName()).toReturn("resourceName");
        stub(event.getResource()).toReturn(resource);
        stub(event.getType()).toReturn(IResourceChangeEvent.PRE_DELETE);
        getNewSBM().resourceChanged(event);
    }

    @Test
    public void shouldAllowResourceChangedWithPrePostChangeEvent() {
        IResourceChangeEvent event = mock(IResourceChangeEvent.class);
        IResource resource = mock(IResource.class);
        stub(resource.getName()).toReturn("resourceName");
        stub(event.getResource()).toReturn(resource);
        stub(event.getType()).toReturn(IResourceChangeEvent.POST_CHANGE);
        // theEvent.getDelta().getResource()
        IResourceDelta delta = mock(IResourceDelta.class);
        stub(delta.getResource()).toReturn(resource);
        stub(event.getDelta()).toReturn(delta);
        getNewSBM().resourceChanged(event);
    }
}
