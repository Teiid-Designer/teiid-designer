/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.manifest.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.teiid.designer.komodo.vdb.DynamicModel.Type;

/**
 *
 */
public class XmlVdbAdapters {

    /**
     * Attribute adapter that allows manipulation of Boolean attribute
     */
    private static abstract class BooleanAttributeAdapter extends XmlAdapter<Boolean, Boolean> {

        private boolean inverse;

        /**
         * Inverse the logic of the value, ie. if value == false then return null
         */
        protected void inverse() {
            inverse = true;
        }

        @Override
        public Boolean unmarshal(Boolean value) throws Exception {
            return value;
        }

        @Override
        public Boolean marshal(Boolean value) throws Exception {
            if ((value && ! inverse) || (! value && inverse))
                return null;

            return value;
        }

    }

    /**
     * Adapter for the model element's visible attribute, eg. <model visible="true">
     * Default value: "true"
     */
    public static class VisibleAttributeAdapter extends BooleanAttributeAdapter {
        // Default parent logic
    }

    /**
     * Adapter for the condition element's constraint attribute, eg. <condition constraint="true">
     * Default value: "true"
     */
    public static class ConstraintAttributeAdapter extends BooleanAttributeAdapter {
        // Default parent logic
    }

    /**
     * Adapter for the model elements model type attribute, eg. <model type="PHYSICAL">
     * Default value: "PHYSICAL" 
     */
    public static class ModelTypeAttributeAdapter extends XmlAdapter<String, String> {

        @Override
        public String unmarshal(String value) throws Exception {
            return value;
        }

        @Override
        public String marshal(String value) throws Exception {
            if (Type.PHYSICAL.toString().equals(value))
                return null;

            return value;
        }
        
    }
}
