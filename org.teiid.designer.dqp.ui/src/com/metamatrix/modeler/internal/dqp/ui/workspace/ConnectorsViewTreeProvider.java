/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.workspace.SourceModelInfo;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * Class provides content and label information for ConnectorBindings and ModelInfos in ConnectorsView
 * 
 * @since 5.0
 */
public class ConnectorsViewTreeProvider implements ITreeContentProvider, ILabelProvider {

    private ServerManager serverRegistry;

    private boolean showModelMappings = false;

    private boolean showTypes = false;

    /**
     * @since 5.0
     */
    public ConnectorsViewTreeProvider() {
        this(true);
    }

    /**
     * @since 5.0
     */
    public ConnectorsViewTreeProvider( boolean showModelMappings ) {
        super();
        this.showModelMappings = showModelMappings;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
    public Object[] getChildren( Object parentElement ) {

        if (parentElement instanceof Server) {
            Object[] result = null;

            if (showTypes) {
                try {
                    result = ((Server)parentElement).getAdmin().getConnectorTypes().toArray();
                } catch (Exception e) {
                    DqpPlugin.Util.log(e);
                    return new Object[0];
                }
                return result;
            }
            Collection<Connector> allConnectors = new ArrayList<Connector>();

            try {
                for (ConnectorType type : ((Server)parentElement).getAdmin().getConnectorTypes()) {
                    allConnectors.addAll(type.getAdmin().getConnectors());
                }
            } catch (Exception e) {
                DqpPlugin.Util.log(e);
            }

            return allConnectors.toArray();

        } else if (parentElement instanceof Connector) {
            Connector connector = (Connector)parentElement;
            Collection modelInfos = Collections.EMPTY_LIST;

            if (showModelMappings && serverRegistry != null) {
                modelInfos = DqpPlugin.getInstance().getSourceBindingsManager().getModelsForConnector(connector);
            }

            if (modelInfos.isEmpty()) {
                return new Object[0];
            }

            return modelInfos.toArray();
        } else if (parentElement instanceof SourceModelInfo) {
            return new Object[0];
        } else if (parentElement instanceof ConnectorType) {
            ConnectorType type = (ConnectorType)parentElement;
            Object[] types = null;
            try {
                types = type.getAdmin().getConnectors((ConnectorType)parentElement).toArray();
            } catch (Exception e) {
                types = new Object[0];
            }
            return types;
        }

        return new Object[0];
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    public Object getParent( Object element ) {
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.2
     */
    public boolean hasChildren( Object element ) {
        return getChildren(element).length > 0;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.2
     */
    public Image getImage( Object element ) {
        if (element instanceof Connector) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
        }
        if (element instanceof ConnectorType) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_TYPE_ICON);
        }

        if (element instanceof SourceModelInfo) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SOURCE_CONNECTOR_BINDING_ICON);
        }
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.2
     */
    public String getText( Object element ) {
        if (element instanceof Connector) {
            return ((Connector)element).getName();
        }
        if (element instanceof ConnectorType) {
            return ((ConnectorType)element).getName();
        }
        if (element instanceof SourceModelInfo) {
            SourceModelInfo mInfo = (SourceModelInfo)element;
            return mInfo.getName();
        }
        if (element instanceof String) {
            return (String)element;
        }
        return DqpUiConstants.UTIL.getString(I18nUtil.getPropertyPrefix(ConnectorsViewTreeProvider.class), new Object[] {
            element.toString(), element.getClass().getName()});
    }

    /**
     * Indicates if at least one binding is loaded in the configuration.
     * 
     * @return <code>true</code> if configuration contains at least one binding; <code>false</code>.
     * @since 4.3
     */
    public boolean containsBindings() {
        boolean result = false;

        if (this.serverRegistry != null) {
            Object[] types = getElements(this.serverRegistry);

            if ((types != null) && (types.length != 0)) {
                for (int i = 0; i < types.length; ++i) {
                    if (hasChildren(types[i])) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements( Object inputElement ) {
        if (inputElement instanceof ServerManager) {
            serverRegistry = (ServerManager)inputElement;
            return serverRegistry.getServers().toArray();
        }

        return new Object[0];
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     * @since 4.2
     */
    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void addListener( ILabelProviderListener listener ) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
     * @since 4.2
     */
    public boolean isLabelProperty( Object element,
                                    String property ) {
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     * @since 4.2
     */
    public void removeListener( ILabelProviderListener listener ) {
    }

    public void setShowTypes( boolean value ) {
        this.showTypes = value;
    }

}
