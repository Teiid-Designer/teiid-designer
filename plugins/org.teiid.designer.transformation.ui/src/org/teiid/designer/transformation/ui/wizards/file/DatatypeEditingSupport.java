package org.teiid.designer.transformation.ui.wizards.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.viewers.ColumnViewer;
import org.teiid.core.types.DataTypeManager;
import org.teiid.designer.ui.common.table.ComboBoxEditingSupport;


/**
 * 
 *
 * @since 8.0
 */
public class DatatypeEditingSupport extends ComboBoxEditingSupport {
	
	private String[] datatypes;
    /**
     * @param viewer
     */
    public DatatypeEditingSupport( ColumnViewer viewer ) {
        super(viewer);
		Set<String> unsortedDatatypes = DataTypeManager.getAllDataTypeNames();
		Collection<String> dTypes = new ArrayList<String>();
		
		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
		Arrays.sort(sortedStrings);
		for( String dType : sortedStrings ) {
			dTypes.add(dType);
		}
		
		datatypes = dTypes.toArray(new String[dTypes.size()]);
		
    }


    @Override
    protected String getElementValue( Object element ) {
    	return ((TeiidColumnInfo)element).getDatatype();
    }

    @Override
    protected String[] refreshItems( Object element ) {
        return datatypes;
    }

    @Override
    protected void setElementValue( Object element,
                                    String newValue ) {
        ((TeiidColumnInfo)element).setDatatype(newValue);
    }
}