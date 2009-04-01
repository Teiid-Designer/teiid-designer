package com.metamatrix.modeler.internal.dqp.ui.workspace;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.ui.celleditor.ExtendedDialogCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

public class ConnectorClasspathPropertyDescriptor implements IPropertyDescriptor {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    static final String PREFIX = I18nUtil.getPropertyPrefix(ConnectorClasspathPropertyDescriptor.class);

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private final String displayName;

    /**
     * The binding whose classpath is being edited.
     * 
     * @since 6.0.0
     */
    private final ConnectorBinding connector;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param connector the connector binding whose properties are being edited
     * @param displayName the localized classpath property display name
     * @since 6.0.0
     */
    public ConnectorClasspathPropertyDescriptor( ConnectorBinding connector,
                                                 String displayName ) {
        this.connector = connector;
        this.displayName = displayName;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     * @since 6.0.0
     */
    @Override
    public CellEditor createPropertyEditor( Composite parent ) {
        // initialize collections
        List<String> allExtensionJars = new ArrayList<String>(DqpPlugin.getInstance().getExtensionsHandler().getExtensionJarNames());
        return new ClasspathEditor(parent, this.connector, allExtensionJars);
    }

    ConnectorBinding getConnector() {
        return this.connector;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getCategory()
     * @since 6.0.0
     */
    @Override
    public String getCategory() {
        // use default category
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDescription()
     * @since 6.0.0
     */
    @Override
    public String getDescription() {
        return UTIL.getString(PREFIX + "description"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getDisplayName()
     * @since 6.0.0
     */
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getFilterFlags()
     * @since 6.0.0
     */
    @Override
    public String[] getFilterFlags() {
        // no filters
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getHelpContextIds()
     * @since 6.0.0
     */
    @Override
    public Object getHelpContextIds() {
        // no help provided
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getId()
     * @since 6.0.0
     */
    @Override
    public Object getId() {
        return ConnectorBindingType.Attributes.CONNECTOR_CLASSPATH;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#getLabelProvider()
     * @since 6.0.0
     */
    @Override
    public ILabelProvider getLabelProvider() {
        // just use default
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertyDescriptor#isCompatibleWith(org.eclipse.ui.views.properties.IPropertyDescriptor)
     * @since 6.0.0
     */
    @Override
    public boolean isCompatibleWith( IPropertyDescriptor anotherProperty ) {
        return false;
    }

    // ===========================================================================================================================
    // Inner Class
    // ===========================================================================================================================

    private class ClasspathEditor extends ExtendedDialogCellEditor {

        // =======================================================================================================================
        // Inner Fields (ClasspathEditor)
        // =======================================================================================================================

        private final String connectorName;

        private final Collection<String> allExtensionJars;

        private final List<String> originalClasspathJars;

        // =======================================================================================================================
        // Inner Constructors (ClasspathEditor)
        // =======================================================================================================================

        public ClasspathEditor( Composite composite,
                                ConnectorBinding connector,
                                Collection<String> allExtensionJars ) {
            super(composite, new LabelProvider());
            this.connectorName = connector.getName();

            String classPath = connector.getProperty(ConnectorBindingType.Attributes.CONNECTOR_CLASSPATH);
            this.originalClasspathJars = new ArrayList<String>(ModelerDqpUtils.getJarNames(classPath));
            this.allExtensionJars = allExtensionJars;
        }

        // =======================================================================================================================
        // Inner Methods (ClasspathEditor)
        // =======================================================================================================================

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
         * @since 6.0.0
         */
        @Override
        protected Object openDialogBox( Control cellEditorWindow ) {
            ClasspathEditorDialog dialog = new ClasspathEditorDialog(cellEditorWindow.getShell(), connectorName,
                                                                     new ArrayList<String>(this.originalClasspathJars),
                                                                     new ArrayList<String>(this.allExtensionJars));

            if (dialog.open() == Window.OK) {
                List<String> classpathJars = Arrays.asList(dialog.getClasspathJars());

                // return empty string if no jars on classpath
                if (classpathJars.isEmpty()) {
                    return StringUtil.Constants.EMPTY_STRING;
                }

                // return new classpath value
                return ModelerDqpUtils.getConnectorClassPathPropertValue(classpathJars);
            }

            return null;
        }
    }

}
