package org.teiid.datatools.connectivity.ui;

import java.util.HashMap;
import org.eclipse.datatools.sqltools.core.services.SQLEditorUIService;
import org.eclipse.datatools.sqltools.editor.ui.core.SQLDevToolsUIConfiguration;
import org.teiid.datatools.connectivity.ui.plan.TeiidExplainSQLActionDelegate;

public class TeiidDBUIConfiguration extends SQLDevToolsUIConfiguration {

    @Override
    public SQLEditorUIService getSQLEditorUIService() {
        return new SQLEditorUIService() {
            @Override
            public HashMap getAdditionalActions() {
                HashMap additions = super.getAdditionalActions();
                additions.put("", new TeiidExplainSQLActionDelegate()); //$NON-NLS-1$
                return additions;
            }
        };
    }

}
