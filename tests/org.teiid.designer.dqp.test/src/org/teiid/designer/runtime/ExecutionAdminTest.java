/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.vdb.Vdb;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * 
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( {ModelerCore.class, ModelWorkspaceManager.class, ResourcesPlugin.class, WorkspaceResourceFinderUtil.class} )
public class ExecutionAdminTest {
	
    private static Collection<PropertyDefinition> PROP_DEFS;
    
    @Mock
    private Admin admin;
    
    @Mock 
    private Server server;
    
    @Mock
    private EventManager eventManager;
    
    @Before
    public void beforeEach() {
    	new ModelWorkspaceMock();
        
        PROP_DEFS = new ArrayList<PropertyDefinition>(1);
        PropertyDefinition propDef = mock(PropertyDefinition.class);
        when(propDef.getName()).thenReturn("name");
        PROP_DEFS.add(mock(PropertyDefinition.class));
    }

    private ExecutionAdmin getNewAdmin() throws Exception {
        return new ExecutionAdmin(admin, server, eventManager);
    }

    private TeiidTranslator getNewTeiidTranslator() throws Exception {
        return new TeiidTranslator(mock(Translator.class), PROP_DEFS, getNewAdmin());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullAdmin() throws Exception {
        assertThat(new ExecutionAdmin(null, null, null), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServer() throws Exception {
        assertThat(new ExecutionAdmin(mock(Admin.class), null, null), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullEventManager() throws Exception {
        assertThat(new ExecutionAdmin(mock(Admin.class), mock(Server.class), null), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullIFileVdbForDeployVdb() throws Exception {
        IFile nullFile = null;
        getNewAdmin().deployVdb(nullFile);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullVdbForDeployVdb() throws Exception {
        Vdb nullVdb = null;
        getNewAdmin().deployVdb(nullVdb);
    }

    @Test
    public void shouldDeployVdb() throws Exception {
    	String vdbName = "MyVdb.vdb";
    	IFile vdbFile = mock(IFile.class);
    	//vdbFile.getFullPath().lastSegment();
    	IPath vdbPath = mock(IPath.class);
    	when(vdbPath.lastSegment()).thenReturn(vdbName);
    	when(vdbFile.getFullPath()).thenReturn(vdbPath);
    	
    	//admin.deployVDB(vdbName, vdbFile.getContents());
    	InputStream inputStream = mock(InputStream.class);
    	when(vdbFile.getContents()).thenReturn(inputStream);
    	
        getNewAdmin().deployVdb(vdbFile);
    }

    @Test
    public void shouldAllowGetAdminApi() throws Exception {
        getNewAdmin().getAdminApi();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetTranslatorWithNull() throws Exception {
        getNewAdmin().getTranslator(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetTranslatorWithZeroLengthName() throws Exception {
        getNewAdmin().getTranslator("");
    }

    @Test
    public void shouldAllowGetTranslator() throws Exception {
        getNewAdmin().getTranslator("name");
    }

    @Test
    public void shouldAllowGetTranslators() throws Exception {
        getNewAdmin().getTranslators();
    }
    
    @Test
    public void shouldAllowGetEventManager() throws Exception {
        assertThat(getNewAdmin().getEventManager(), notNullValue());
    }

    @Test
    public void shouldAllowGetServer() throws Exception {
        assertThat(getNewAdmin().getServer(), notNullValue());
    }

    @Test
    public void shouldAllowGetVdbs() throws Exception {
        getNewAdmin().getVdbs();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetVdbWithNullName() throws Exception {
        getNewAdmin().getVdb(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetVdbWithEmptyName() throws Exception {
        getNewAdmin().getVdb("");
    }

    @Test
    public void shouldAllowGetVdbWithName() throws Exception {
        getNewAdmin().getVdb("name");
    }

    @Test
    public void shouldAllowRefresh() throws Exception {
    	mockStatic(WorkspaceResourceFinderUtil.class);
    	when(WorkspaceResourceFinderUtil.getAllWorkspaceResources()).thenReturn(Collections.EMPTY_LIST);
        getNewAdmin().refresh();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithNullTranslator() throws Exception {
        getNewAdmin().setPropertyValue(null, null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithNullName() throws Exception {
        getNewAdmin().setPropertyValue(getNewTeiidTranslator(), null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithZeroLengthName() throws Exception {
        getNewAdmin().setPropertyValue(getNewTeiidTranslator(), "", null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithNullValue() throws Exception {
        getNewAdmin().setPropertyValue(getNewTeiidTranslator(), "name", null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithZeroLengthValue() throws Exception {
        getNewAdmin().setPropertyValue(getNewTeiidTranslator(), "name", "");
    }

    @Test
    public void shouldAllowSetPropertyValue() throws Exception {
        TeiidTranslator mockTranslator = mock(TeiidTranslator.class);
        when(mockTranslator.isValidPropertyValue("name", "value")).thenReturn(null);
        getNewAdmin().setPropertyValue(mockTranslator, "name", "value");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithNullTranslator() throws Exception {
        getNewAdmin().setProperties(null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithNullProperties() throws Exception {
        getNewAdmin().setProperties(getNewTeiidTranslator(), null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithEmptyProperties() throws Exception {
        getNewAdmin().setProperties(getNewTeiidTranslator(), new Properties());
    }

    @Test
    public void shouldAllowSetProperties() throws Exception {
        ExecutionAdmin admin = mock(ExecutionAdmin.class);
        TeiidTranslator teiidTranslator = getNewTeiidTranslator();

        Properties newProps = new Properties();
        newProps.put("prop_1", "value_1");

        admin.setProperties(teiidTranslator, newProps);
    }
}
