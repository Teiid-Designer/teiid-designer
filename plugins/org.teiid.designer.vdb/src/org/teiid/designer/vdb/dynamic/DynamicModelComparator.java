/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.dynamic;

import java.util.Comparator;
import java.util.List;

import org.teiid.designer.vdb.dynamic.DynamicModel.Type;

/**
 * Comparator that sorts {@link DynamicModel}s based on their DDL
 * metadata, especially determining order based on inclusion of model
 * names in the DDL.
 */
public class DynamicModelComparator implements Comparator<DynamicModel> {

    /** (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(DynamicModel dynModel1, DynamicModel dynModel2) {
        Type model1Type = dynModel1.getModelType();
        Type model2Type = dynModel2.getModelType();

        if (Type.PHYSICAL.equals(model1Type) && ! Type.PHYSICAL.equals(model2Type))
            return -1;

        if (! Type.PHYSICAL.equals(model1Type) && Type.PHYSICAL.equals(model2Type))
            return 1;

        String model1Name = dynModel1.getName();
        String model2Name = dynModel2.getName();

        List<Metadata> mdata1 = dynModel1.getMetadata();
        List<Metadata> mdata2 = dynModel2.getMetadata();
        if (mdata1 == null && mdata2 != null)
            return -1;
        else if (mdata1 == null && mdata2 == null)
            return 0;
        else if (mdata1 != null && mdata2 == null)
            return 1;
        
        if( mdata1.size() == 1 && mdata2.size() == 1) {
        	Metadata mDataA = mdata1.get(0);
        	Metadata mDataB = mdata1.get(0);
        	
	        String schema1Text = mDataA.getSchemaText();
	        String schema2Text = mDataB.getSchemaText();
	        if (schema1Text == null && schema2Text != null)
	            return -1;
	
	        if (schema1Text == null && schema2Text == null)
	            return 0;
	
	        if (schema1Text != null && schema2Text == null)
	            return 1;
	
	        if (schema1Text.contains(model2Name))
	            return 1;
	
	        if (schema2Text.contains(model1Name))
	            return -1;
        } else {
        	if( mdata1.size() > mdata2.size() ) return 1;
        	else return -1;
        }

        return 0;
    }

}
