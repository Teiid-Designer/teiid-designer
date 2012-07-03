/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.connection.ModelConnectionMapper;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.connections.SourceHandler;
import org.teiid.designer.vdb.connections.VdbSourceConnection;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * Implements the SourceHandler interface which provides the VDB Editor the ability to access DQP-related connection info.
 * 
 */
public class VdbSourceConnectionHandler implements SourceHandler {
    static final String PREFIX = I18nUtil.getPropertyPrefix(VdbSourceConnectionHandler.class);

    private static SelectTranslatorAction selectTranslatorAction;

    private static SelectJndiDataSourceAction selectJndiDataSourceAction;

    private static Object[] actions;

    private static boolean initialized = false;

    static String getString( final String stringId ) {
        return UTIL.getString(PREFIX + stringId);
    }

    @Override
    public VdbSourceConnection ensureVdbSourceConnection( String sourceModelname,
                                                          Properties properties ) throws Exception {
        CoreArgCheck.isNotNull(properties, "properties"); //$NON-NLS-1$

        ModelConnectionMapper mapper = new ModelConnectionMapper(sourceModelname, properties);

        VdbSourceConnection vdbSourceConnection = null;

        ExecutionAdmin defaultAdmin = getDefaultServer().getAdmin();

        String uuid = ModelerCore.workspaceUuid().toString();

        try {
            vdbSourceConnection = mapper.getVdbSourceConnection(defaultAdmin, uuid);
        } catch (ModelWorkspaceException e) {
            UTIL.log(IStatus.ERROR,
                     e,
                     UTIL.getString("VdbSourceConnectionHandler.Error_could_not_find_source_connection_info_for_{0}_model", sourceModelname)); //$NON-NLS-1$
        }

        // TODO: vdbSourceConnection may be NULL, so query the user for translator name & jndi name

        return vdbSourceConnection;
    }

    @Override
    public Object[] getApplicableActions( Object obj ) {
        if (!initialized) {
            initialize();
        }
        Server defServer = getDefaultServer();
        if (defServer == null || !defServer.isConnected()) {
            return null;
        }

        if (obj instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection)obj;
            if (sel.getFirstElement() instanceof VdbModelEntry) {
            	if (((VdbModelEntry)sel.getFirstElement()).getType() == ModelType.PHYSICAL_LITERAL.getName()) {
                    selectTranslatorAction.setSelection((VdbModelEntry)sel.getFirstElement());
                    selectJndiDataSourceAction.setSelection((VdbModelEntry)sel.getFirstElement());
                    return actions;
                }
            }
        }
        selectTranslatorAction.setSelection(null);
        selectJndiDataSourceAction.setSelection(null);
        return null;
    }

    private void initialize() {
        // Construct the two actions
        selectTranslatorAction = new SelectTranslatorAction(getString("selectTranslatorAction.label")); //$NON-NLS-1$

        selectJndiDataSourceAction = new SelectJndiDataSourceAction(getString("selectJndiDataSourceAction.label")); //$NON-NLS-1$

        Collection<IAction> actionsList = new ArrayList();
        actionsList.add(selectTranslatorAction);
        actionsList.add(selectJndiDataSourceAction);

        actions = actionsList.toArray();
    }

    class SelectJndiDataSourceAction extends Action {

        public SelectJndiDataSourceAction( String text ) {
            super(text);
            // TODO Auto-generated constructor stub
        }

        private VdbModelEntry vdbModelEntry;

        public void setSelection( VdbModelEntry vdbModelEntry ) {
            this.vdbModelEntry = vdbModelEntry;
        }

        @Override
        public void run() {
            // Get available servers and launch SelectTranslatorDialog
            // vdbModelEntry should not be null and should be a Physical model only
            if (vdbModelEntry != null) {
                String jndiName = vdbModelEntry.getJndiName();

                SelectJndiDataSourceDialog dialog = new SelectJndiDataSourceDialog(Display.getCurrent().getActiveShell());

                TeiidDataSource initialSelection = null;
                Server defServer = getDefaultServer();
                if (defServer != null && defServer.isConnected()) {
                    try {
                        initialSelection = defServer.getAdmin().getDataSource(jndiName);
                    } catch (Exception e) {
                        UTIL.log(IStatus.ERROR,
                                 e,
                                 UTIL.getString("VdbSourceConnectionHandler.Error_could_not_find_data_source_for_name", jndiName)); //$NON-NLS-1$
                    }
                    dialog.setInitialSelection(initialSelection);
                }

                dialog.open();

                if (dialog.getReturnCode() == Window.OK) {
                    Object result = dialog.getFirstResult();
                    if (result != null && result instanceof TeiidDataSource) {
                        vdbModelEntry.setJndiName(((TeiidDataSource)result).getName());
                    }
                }
            }
        }
    }

    class SelectTranslatorAction extends Action {

        public SelectTranslatorAction( String text ) {
            super(text);
            // TODO Auto-generated constructor stub
        }

        private VdbModelEntry vdbModelEntry;

        public void setSelection( VdbModelEntry vdbModelEntry ) {
            this.vdbModelEntry = vdbModelEntry;
        }

        @Override
        public void run() {
            // Get available servers and launch SelectTranslatorDialog
            // vdbModelEntry should not be null and should be a Physical model only

            if (vdbModelEntry != null) {
                String transName = vdbModelEntry.getTranslator();

                SelectTranslatorDialog dialog = new SelectTranslatorDialog(Display.getCurrent().getActiveShell());

                TeiidTranslator initialSelection = null;
                Server defServer = getDefaultServer();
                if (defServer != null && defServer.isConnected()) {
                    try {
                        initialSelection = defServer.getAdmin().getTranslator(transName);
                    } catch (Exception e) {
                        UTIL.log(IStatus.ERROR,
                                 e,
                                 UTIL.getString("VdbSourceConnectionHandler.Error_could_not_find_translator_for_name", transName)); //$NON-NLS-1$
                    }
                    dialog.setInitialSelection(initialSelection);
                }

                dialog.open();

                if (dialog.getReturnCode() == Window.OK) {
                    Object result = dialog.getFirstResult();
                    if (result != null && result instanceof TeiidTranslator) {
                        vdbModelEntry.setTranslator(((TeiidTranslator)result).getName());
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.connections.SourceHandler#getDataSourceNames()
     */
    @Override
    public String[] getDataSourceNames() {
        Server defaultServer = getDefaultServer();

        if ((defaultServer != null) && defaultServer.isConnected()) {
            Collection<TeiidDataSource> dataSources = null;

            try {
                dataSources = defaultServer.getAdmin().getDataSources();
            } catch (Exception e) {
                UTIL.log(IStatus.ERROR,
                         e,
                         UTIL.getString("VdbSourceConnectionHandler.errorObtainingDataSources", defaultServer.getHost())); //$NON-NLS-1$
            }

            if (dataSources != null) {
                Collection<String> dataSourceNames = new ArrayList<String>();

                for (TeiidDataSource dataSource : dataSources) {
                    if (!dataSource.isPreview()) {
                        dataSourceNames.add(dataSource.getName());
                    }
                }

                return dataSourceNames.toArray(new String[dataSourceNames.size()]);
            }
        }

        return null;
    }

    Server getDefaultServer() {
        return DqpPlugin.getInstance().getServerManager().getDefaultServer();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.connections.SourceHandler#getTranslatorDefinitions(java.lang.String)
     */
    @Override
    public PropertyDefinition[] getTranslatorDefinitions( String translatorName ) {
        if (StringUtilities.isEmpty(translatorName)) {
            throw new IllegalArgumentException();
        }

        Server defaultServer = getDefaultServer();

        if ((defaultServer != null) && defaultServer.isConnected()) {
            try {
                TeiidTranslator translator = defaultServer.getAdmin().getTranslator(translatorName);

                if (translator != null) {
                    Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();

                    for (org.teiid.adminapi.PropertyDefinition propDefn : translator.getPropertyDefinitions()) {
                        TranslatorProperty prop = new TranslatorProperty(propDefn.getPropertyTypeClassName());
                        prop.advanced = propDefn.isAdvanced();
                        prop.description = propDefn.getDescription();
                        prop.displayName = propDefn.getDisplayName();
                        prop.id = propDefn.getName();
                        prop.masked = propDefn.isMasked();
                        prop.modifiable = propDefn.isModifiable();
                        prop.required = propDefn.isRequired();

                        prop.defaultValue = (propDefn.getDefaultValue() == null) ? StringUtilities.EMPTY_STRING
                                                                                : propDefn.getDefaultValue().toString();

                        if (propDefn.isConstrainedToAllowedValues()) {
                            Collection values = propDefn.getAllowedValues();
                            prop.allowedValues = new String[values.size()];
                            int i = 0;

                            for (Object value : values) {
                                prop.allowedValues[i++] = value.toString();
                            }
                        } else {
                            // if boolean type turn into allowed values
                            String type = propDefn.getPropertyTypeClassName();

                            if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
                                prop.allowedValues = new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() };
                            }
                        }

                        props.add(prop);
                    }

                    return props.toArray(new PropertyDefinition[props.size()]);
                }
            } catch (Exception e) {
                UTIL.log(IStatus.ERROR, e, UTIL.getString("VdbSourceConnectionHandler.errorObtainingTranslatorProperties", //$NON-NLS-1$
                                                          translatorName,
                                                          defaultServer.getHost()));
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.connections.SourceHandler#getTranslatorTypes()
     */
    @Override
    public String[] getTranslatorTypes() {
        Server defaultServer = getDefaultServer();

        if ((defaultServer != null) && defaultServer.isConnected()) {
            Collection<TeiidTranslator> translators = null;

            try {
                translators = defaultServer.getAdmin().getTranslators();
            } catch (Exception e) {
                UTIL.log(IStatus.ERROR,
                         e,
                         UTIL.getString("VdbSourceConnectionHandler.errorObtainingTranslators", defaultServer.getHost())); //$NON-NLS-1$
            }

            if (translators != null) {
                Collection<String> translatorTypes = new ArrayList<String>();

                for (TeiidTranslator translator : translators) {
                    translatorTypes.add(translator.getName());
                }

                return translatorTypes.toArray(new String[translatorTypes.size()]);
            }
        }

        return null;
    }

    class TranslatorProperty implements PropertyDefinition {

        private final String className;

        boolean advanced;
        String[] allowedValues;
        String defaultValue;
        String description;
        String displayName;
        String id;
        boolean masked;
        boolean modifiable;
        boolean required;

        public TranslatorProperty( String className ) {
            this.className = className;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( Object obj ) {
            if (this == obj) {
                return true;
            }

            if ((obj == null) || (getClass() != obj.getClass())) {
                return false;
            }

            return this.id.equals(((TranslatorProperty)obj).id);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#getAllowedValues()
         */
        @Override
        public String[] getAllowedValues() {
            return this.allowedValues;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#getDefaultValue()
         */
        @Override
        public String getDefaultValue() {
            return this.defaultValue;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#getDescription()
         */
        @Override
        public String getDescription() {
            return this.description;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            return this.displayName;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#getId()
         */
        @Override
        public String getId() {
            return this.id;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.id.hashCode();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#isAdvanced()
         */
        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#isMasked()
         */
        @Override
        public boolean isMasked() {
            return this.masked;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#isModifiable()
         */
        @Override
        public boolean isModifiable() {
            return this.modifiable;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#isRequired()
         */
        @Override
        public boolean isRequired() {
            return this.required;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.core.properties.PropertyDefinition#isValidValue(java.lang.String)
         */
        @Override
        public String isValidValue( String newValue ) {
            // if empty must have a value or a default value if required
            if (StringUtilities.isEmpty(newValue)) {
                // invalid if required and no default value
                if (isRequired() && StringUtilities.isEmpty(getDefaultValue())) {
                    return Util.getString("invalidNullPropertyValue", getDisplayName()); //$NON-NLS-1$
                }

                // OK to be null/empty
                return null;
            }

            if (Boolean.class.getName().equals(this.className) || Boolean.TYPE.getName().equals(this.className)) {
                if (!newValue.equalsIgnoreCase(Boolean.TRUE.toString()) && !newValue.equalsIgnoreCase(Boolean.FALSE.toString())) {
                    return Util.getString("invalidPropertyValueForType", newValue, Boolean.TYPE.getName()); //$NON-NLS-1$
                }
            } else if (Character.class.getName().equals(this.className) || Character.TYPE.getName().equals(this.className)) {
                if (newValue.length() != 1) {
                    return Util.getString("invalidPropertyValueForType", newValue, Character.TYPE.getName()); //$NON-NLS-1$
                }
            } else if (Byte.class.getName().equals(this.className) || Byte.TYPE.getName().equals(this.className)) {
                try {
                    Byte.parseByte(newValue);
                } catch (Exception e) {
                    return Util.getString("invalidPropertyValueForType", newValue, Byte.TYPE.getName()); //$NON-NLS-1$
                }
            } else if (Short.class.getName().equals(this.className) || Short.TYPE.getName().equals(this.className)) {
                try {
                    Short.parseShort(newValue);
                } catch (Exception e) {
                    return Util.getString("invalidPropertyValueForType", newValue, Short.TYPE.getName()); //$NON-NLS-1$
                }
            } else if (Integer.class.getName().equals(this.className) || Integer.TYPE.getName().equals(this.className)) {
                try {
                    Integer.parseInt(newValue);
                } catch (Exception e) {
                    return Util.getString("invalidPropertyValueForType", newValue, Integer.TYPE.getName()); //$NON-NLS-1$
                }
            } else if (Long.class.getName().equals(this.className) || Long.TYPE.getName().equals(this.className)) {
                try {
                    Long.parseLong(newValue);
                } catch (Exception e) {
                    return Util.getString("invalidPropertyValueForType", newValue, Long.TYPE.getName()); //$NON-NLS-1$
                }
            } else if (Float.class.getName().equals(this.className) || Float.TYPE.getName().equals(this.className)) {
                try {
                    Float.parseFloat(newValue);
                } catch (Exception e) {
                    return Util.getString("invalidPropertyValueForType", newValue, Float.TYPE.getName()); //$NON-NLS-1$
                }
            } else if (Double.class.getName().equals(this.className) || Double.TYPE.getName().equals(this.className)) {
                try {
                    Double.parseDouble(newValue);
                } catch (Exception e) {
                    return Util.getString("invalidPropertyValueForType", newValue, Double.TYPE.getName()); //$NON-NLS-1$
                }
            } else if (!String.class.getName().equals(this.className)) {
                return Util.getString("unknownPropertyType", this.displayName, this.className); //$NON-NLS-1$
            }

            // valid value
            return null;
        }

    }
}
