package com.metamatrix.modeler.internal.core.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.designer.core.ModelResourceMockFactory;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

@RunWith( PowerMockRunner.class )
@PrepareForTest( {ModelerCore.class, ModelResourceContainerFactory.class} )
public class ResourceAnnotationHelperTest {
    @Mock
    private ModelResource testModelResource;
    
    private static final String PROPERTYID = "somePropID"; //$NON-NLS-1$
    private static final String SOMEVALUE = "someValue"; //$NON-NLS-1$
    private static final String KEY_1 = "someNamespace:key_1"; //$NON-NLS-1$
    private static final String KEY_2 = "someNamespace:key_2"; //$NON-NLS-1$
    private static final String VALUE_1 = "value_1"; //$NON-NLS-1$
    private static final String VALUE_2 = "value_2"; //$NON-NLS-1$
    
    private ResourceAnnotationHelper helper;
    
	@Before
    public void beforeEach() throws ModelWorkspaceException {
        MockitoAnnotations.initMocks(this);
        
        helper = new ResourceAnnotationHelper();
	}
	
	private ModelResource setUpModelResourceWithResourceAnnotation() throws ModelWorkspaceException {
		Properties props = new Properties();
		props.put(KEY_1, VALUE_1); 
		props.put(KEY_2, VALUE_2);
		
		ModelResource mr = 
			ModelResourceMockFactory.createModelResourceWithOutResourceAnnotation(
					"SourceA", "ProjectA"); //$NON-NLS-1$ //$NON-NLS-2$
		
		mockStatic(ModelResourceContainerFactory.class);
		Annotation annotation = ModelResourceMockFactory.createAnnotation(true, "Sample Description", //$NON-NLS-1$
				true, props, false, null);
		when(ModelResourceContainerFactory.createNewAnnotation(mr.getModelAnnotation())).thenReturn(annotation);
		
		mockStatic(ModelerCore.class);
		final ModelEditor me = ModelResourceMockFactory.getModelerEditor();
		when(ModelerCore.getModelEditor()).thenReturn(me);
		ModelContents modelContents = ModelResourceMockFactory.getModelContents(false);
		when(me.getModelContents(mr)).thenReturn(modelContents);
		AnnotationContainer ac = ModelResourceMockFactory.getAnnotationContainer();
		when(modelContents.getAnnotationContainer(false)).thenReturn(ac);
		when(me.getModelContents(mr)).thenReturn(modelContents);
		
		return mr;
	}
	
	@Test
	public void shouldGetResourceAnnotationForceCreate() throws ModelWorkspaceException {
		ModelResource mr = setUpModelResourceWithResourceAnnotation();
		
		Annotation annotation = this.helper.getResourceAnnotation(mr, true);
		assertNotNull(annotation);
	}
	
	@Test
	public void shouldGetResourceAnnotationIfExists() throws ModelWorkspaceException {
		Properties props = new Properties();
		props.put("key_1", "value_1");  //$NON-NLS-1$//$NON-NLS-2$
		props.put("key_2", "value_2");  //$NON-NLS-1$//$NON-NLS-2$
		
		ModelResource theModelResource = 
			ModelResourceMockFactory.createModelResourceWithResourceAnnotation(
					"SourceA", "ProjectA",   //$NON-NLS-1$//$NON-NLS-2$
					true, "Sample Description", //$NON-NLS-1$
					true, props, false, null);
		
		this.helper.getResourceAnnotation(theModelResource, false);
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotAllowGetResourceAnnotationForNullModelResource() throws ModelWorkspaceException {
		this.helper.getResourceAnnotation(null, false);
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotAllowGetPropertyValueForNullModelResourceAndKey() throws ModelWorkspaceException {
		this.helper.getPropertyValue(null, PROPERTYID);
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotAllowGetPropertyValueForModelResourceAndNullKey() throws ModelWorkspaceException {
		this.helper.getPropertyValue(testModelResource, null);
	}
	
	@Test
	public void shouldGetPropertyValueForModelResourceAndKey() throws ModelWorkspaceException {
		ModelResource testModelResource = setUpModelResourceWithResourceAnnotation();
		
		Annotation annotation = this.helper.getResourceAnnotation(testModelResource, true);
		
		when(helper.getResourceAnnotation(testModelResource, false)).thenReturn(annotation);
		
		Object value = helper.getPropertyValue(testModelResource, KEY_1);
		assertEquals(VALUE_1, value);
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotAllowSetPropertyForNullModelResource() throws ModelWorkspaceException {
		this.helper.setProperty(null, PROPERTYID, SOMEVALUE);
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotAllowSetPropertyForModelResourceAndNullKey() throws ModelWorkspaceException {
		this.helper.setProperty(testModelResource, null, SOMEVALUE);
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotAllowSetPropertyForModelResourceAndNullValue() throws ModelWorkspaceException {
		this.helper.setProperty(testModelResource, PROPERTYID, null);
	}
	
	@Test
	public void shouldSetPropertyForModelResourceAndKey() throws ModelWorkspaceException {
		ModelResource mr = setUpModelResourceWithResourceAnnotation();
		this.helper.setProperty(mr, PROPERTYID, SOMEVALUE);
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotAllowRemovePropertiesForNullModelResource() throws ModelWorkspaceException {
		this.helper.removeProperties(null, "someNamespace:"); //$NON-NLS-1$
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotAllowRemovePropertiesForModelResourceAndNullNamespace() throws ModelWorkspaceException {
		this.helper.removeProperties(testModelResource, null);
	}
	
	@Test
	public void shouldRemovePropertiesForModelResourceAndNamespace() throws ModelWorkspaceException {
		ModelResource mr = setUpModelResourceWithResourceAnnotation();
		this.helper.removeProperties(mr, "someNamespace"); //$NON-NLS-1$
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotSetPropertiesForNullModelResource() throws ModelWorkspaceException {
		this.helper.setProperties(null, new Properties());
	}
	
	@Test( expected = IllegalArgumentException.class )
	public void shouldNotSetPropertiesForModelResourceAndNullProperties() throws ModelWorkspaceException {
		this.helper.setProperties(testModelResource, null);
	}
	
	@Test
	public void shouldSetPropertiesForModelResourceAndProperties() throws ModelWorkspaceException {
		ModelResource mr = setUpModelResourceWithResourceAnnotation();
		this.helper.setProperties(mr, new Properties());
	}
}
