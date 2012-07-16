package org.teiid.designer.core.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelResourceMockFactory;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.spi.RegistrySPI;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;

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
		ModelResource mr = 
			ModelResourceMockFactory.createModelResourceWithOutResourceAnnotation(
					"SourceA", "ProjectA"); //$NON-NLS-1$ //$NON-NLS-2$
				
		final ModelEditor me = ModelResourceMockFactory.getModelerEditor();
		((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.MODEL_EDITOR_KEY, me);
		
		ModelContents modelContents = ModelResourceMockFactory.getModelContents(false);
		when(me.getModelContents(mr)).thenReturn(modelContents);
		AnnotationContainer ac = ModelResourceMockFactory.getAnnotationContainer();
		when(modelContents.getAnnotationContainer(false)).thenReturn(ac);
		when(me.getModelContents(mr)).thenReturn(modelContents);
		
		return mr;
	}
	
	@After
	public void tearDown() {
	    ((RegistrySPI) ModelerCore.getRegistry()).unregister(ModelerCore.MODEL_EDITOR_KEY);
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
	
		Properties props = new Properties();
        props.put(KEY_1, VALUE_1); 
        props.put(KEY_2, VALUE_2);
        Annotation annotation = ModelResourceMockFactory.createAnnotation(true, "Sample Description", true, props, false, null); //$NON-NLS-1$
		
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
