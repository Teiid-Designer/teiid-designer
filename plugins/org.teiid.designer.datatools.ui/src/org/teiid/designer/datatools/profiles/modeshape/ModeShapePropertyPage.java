package org.teiid.designer.datatools.profiles.modeshape;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleProfileDetailsPropertyPage;
import org.eclipse.datatools.help.ContextProviderDelegate;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;

public class ModeShapePropertyPage extends ExtensibleProfileDetailsPropertyPage 
    implements IContextProvider {

    private ContextProviderDelegate contextProviderDelegate = new ContextProviderDelegate(
            DatatoolsUiPlugin.getDefault().getBundle().getSymbolicName());

    public ModeShapePropertyPage() {
        super(IModeShapeDriverConstants.MODESHAPE_CATEGORY);
    }

    @Override
    public IContext getContext( Object target ) {
        return contextProviderDelegate.getContext(target);
    }

    @Override
    public int getContextChangeMask() {
        return contextProviderDelegate.getContextChangeMask();
    }

    @Override
    public String getSearchExpression( Object target ) {
        return contextProviderDelegate.getSearchExpression(target);
    }

    @Override
    protected Control createContents( Composite parent ) {
        Control contents = super.createContents(parent);
        return contents;
    }
}
