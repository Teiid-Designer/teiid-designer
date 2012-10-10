/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelResourceContainerFactory;
import org.teiid.designer.mapping.PluginConstants;
import org.teiid.designer.metamodels.transformation.FragmentMappingRoot;


/**
 * The <code>FragmentMappingAdapter</code> class is responsible for managing the
 * {@link org.teiid.designer.metamodels.transformation.FragmentMappingRoot) of a tree root.
 *
 * @since 8.0
 */
public class FragmentMappingAdapter implements PluginConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Properties file key prefix. Used for logging and localization. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(FragmentMappingAdapter.class);

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** The fragment root. */
    private FragmentMappingRoot fragmentRoot;

    /** The tree root. */
    private EObject root;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a <code>FragmentMappingAdapter</code> for the specified tree root. The fragment roots are found by using the
     * transformation container.
     * 
     * @param theTreeRoot the tree root
     * @throws IllegalArgumentException if input is <code>null</code>
     */
    public FragmentMappingAdapter( EObject theTreeRoot ) {
        CoreArgCheck.isNotNull(theTreeRoot);

        root = theTreeRoot;

        // get all mapping roots for the tree root. cache each fragment root. however, should only have
        // one fragment root. this fragment root will have nested mappings each of which is a fragment.
        List fragmentRoots = new ArrayList();
        ModelContents mc = ModelerCore.getModelEditor().getModelContents(root);

        if (mc != null) {
            List mappingRoots = mc.getTransformations(theTreeRoot);

            if ((mappingRoots == null) || mappingRoots.isEmpty()) {

            } else {

                for (int size = mappingRoots.size(), i = 0; i < size; i++) {
                    MappingRoot mappingRoot = (MappingRoot)mappingRoots.get(i);

                    if (mappingRoot instanceof FragmentMappingRoot) {
                        fragmentRoots.add(mappingRoot);
                    }
                }
            }

            setFragmentRoots(fragmentRoots);
        }
    }

    /**
     * Constructs a <code>TreeMappingAdapter</code> for the specified tree root.
     * 
     * @param theTreeRoot the tree root
     * @param theFragmentRoots the fragment roots
     * @throws IllegalArgumentException if either input is <code>null</code>
     */
    public FragmentMappingAdapter( EObject theTreeRoot,
                                   List theFragmentRoots ) {
        CoreArgCheck.isNotNull(theTreeRoot);

        root = theTreeRoot;
        setFragmentRoots(theFragmentRoots);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds a fragment mapping.
     * 
     * @param theFragmentReference the reference to the fragment
     * @param theFragment the fragment
     * @throws IllegalArgumentException if either input is <code>null</code>
     */
    public void addFragment( EObject theFragmentReference, // NO_UCD
                             EObject theFragment ) {
        CoreArgCheck.isNotNull(theFragmentReference);
        CoreArgCheck.isNotNull(theFragment);

        if (fragmentRoot == null) {
            ModelContents mc = ModelerCore.getModelEditor().getModelContents(root);

            if (mc != null) {
                // bmlTODO: We need to refactor the createFragmentMapping() logic similar to
                // the createSqlTransformationMapping() and createTreeMapping() so the additional
                // work to allow full "Undoable" events to be added.
                fragmentRoot = ModelResourceContainerFactory.createNewFragmentMappingRoot(root);
            }
        }

        boolean createFragment = true;
        List mappings = getMappings();

        // make sure fragment reference is not already in a fragment
        if (!mappings.isEmpty()) {
            for (int size = 0, i = 0; i < size; i++) {
                Mapping mapping = (Mapping)mappings.get(i);
                EObject fragmentReference = getFragmentReference(mapping);

                if (theFragmentReference.equals(fragmentReference)) {
                    createFragment = false;
                    break;
                }
            }
        }

        // create or log error
        if (createFragment) {
            fragmentRoot.createMapping(Collections.singletonList(theFragment), Collections.singletonList(theFragmentReference));
        } else {
            Util.log(IStatus.ERROR, Util.getString(PREFIX + "duplicateFragment", new Object[] {theFragmentReference})); //$NON-NLS-1$
        }
    }

    /**
     * Indicates if fragments exist for this adapter's tree root.
     * 
     * @return <code>true</code> if contains one or more fragments; <code>false</code> otherwise.
     */
    public boolean containsFragments() {
        return (getFragmentCount() > 0);
    }

    /**
     * Obtains the fragment for the specified fragment reference.
     * 
     * @param theFramentReference the fragment reference whose fragment is being requested
     * @return the fragment or <code>null</code> if none exists
     * @throws IllegalArgumentException if input is <code>null</code>
     */
    public EObject getFragment( EObject theFragmentReference ) {
        CoreArgCheck.isNotNull(theFragmentReference);

        EObject result = null;

        if (containsFragments()) {
            List fragmentMappings = getMappings();

            if (!fragmentMappings.isEmpty()) {
                for (int size = fragmentMappings.size(), i = 0; i < size; i++) {
                    Mapping mapping = (Mapping)fragmentMappings.get(i);
                    EObject fragmentReference = getFragmentReference(mapping);

                    // find the fragment reference. each output list should only have one entry.
                    if (theFragmentReference.equals(fragmentReference)) {
                        result = getFragment(mapping);
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Obtains the fragment of the specified <code>Mapping</code>.
     * 
     * @param theMapping the <code>Mapping</code> whose fragment is being requested
     * @return the fragment (should never be <code>null</code>)
     * @throws IllegalArgumentException if input is <code>null</code>
     */
    private EObject getFragment( Mapping theMapping ) {
        CoreArgCheck.isNotNull(theMapping);

        List inputs = theMapping.getInputs();

        return (inputs.isEmpty()) ? null : (EObject)inputs.get(0);
    }

    /**
     * Obtains the number of fragment <code>Mapping</code>s.
     * 
     * @return the count of fragment <code>Mapping</code>s
     */
    private int getFragmentCount() {
        return (fragmentRoot == null) ? 0 : getMappings().size();
    }

    /**
     * Obtains the fragment reference of the specified <code>Mapping</code>.
     * 
     * @param theMapping the <code>Mapping</code> whose fragment reference is being requested
     * @return the fragment reference (should never be <code>null</code>)
     * @throws IllegalArgumentException if input is <code>null</code>
     */
    private EObject getFragmentReference( Mapping theMapping ) {
        CoreArgCheck.isNotNull(theMapping);

        List outputs = theMapping.getOutputs();

        return (outputs.isEmpty()) ? null : (EObject)outputs.get(0);
    }

    public EObject getInputSet() {
        EObject result = null;

        if (fragmentRoot != null) {
            // fragmentRoot.getHelper();
        }

        return result;
    }

    public EObject getInputSet( EObject theFragmentReference ) { // NO_UCD
        CoreArgCheck.isNotNull(theFragmentReference);

        EObject result = null;

        if (containsFragments()) {
            List mappings = getMappings();

            if (!mappings.isEmpty()) {
                for (int size = mappings.size(), i = 0; i < size; i++) {
                    Mapping mapping = (Mapping)mappings.get(i);
                    EObject fragmentReference = getFragmentReference(mapping);

                    if (theFragmentReference.equals(fragmentReference)) {
                        // result = mapping.getHelper();
                    }
                }
            }
        }

        return result;
    }

    /**
     * Obtains the fragment <code>Mapping</code>s.
     * 
     * @return the <code>Mapping</code>s (may be empty) or <code>null</code> if no fragment root exists
     */
    private List getMappings() {
        return (fragmentRoot == null) ? null : fragmentRoot.getNested();
    }

    /**
     * Removes the specified fragment from this adapter's tree root.
     * 
     * @param theFragmentReference the fragment whose fragment mapping is being removed
     * @throws IllegalArgumentException if input is <code>null</code>
     */
    public void removeFragment( EObject theFragmentReference ) { // NO_UCD
        CoreArgCheck.isNotNull(theFragmentReference);

        if (containsFragments()) {
            boolean foundIt = false;
            List mappings = getMappings();

            for (int size = mappings.size(), i = 0; i < size; i++) {
                Mapping mapping = (Mapping)mappings.get(i);
                EObject fragmentReference = getFragmentReference(mapping);

                if (theFragmentReference.equals(fragmentReference)) {
                    foundIt = true;
                    break;
                }
            }

            if (!foundIt) {
                Util.log(IStatus.ERROR,
                         Util.getString(PREFIX + "removeFragmentProblemNotFound", new Object[] {theFragmentReference})); //$NON-NLS-1$
            }
        } else {
            Util.log(IStatus.ERROR, Util.getString(PREFIX + "removeFragmentProblemNoRoot", new Object[] {theFragmentReference})); //$NON-NLS-1$
        }
    }

    /**
     * Sets the {@link FragmentMappingRoot} from the specified input. Should only have one root.
     * 
     * @param theFragmentRoots the collection of fragment roots
     * @throws IllegalArgumentException if input is <code>null</code>
     */
    private void setFragmentRoots( List theFragmentRoots ) {
        CoreArgCheck.isNotNull(theFragmentRoots);

        if (!theFragmentRoots.isEmpty()) {
            for (int size = theFragmentRoots.size(), i = 0; i < size; i++) {
                Object potentialRoot = theFragmentRoots.get(i);

                if (potentialRoot instanceof FragmentMappingRoot) {
                    if (fragmentRoot == null) {
                        fragmentRoot = (FragmentMappingRoot)potentialRoot;
                    } else {
                        Util.log(IStatus.ERROR,
                                 Util.getString(PREFIX + "multipleFragmentRootsFound", new Object[] {potentialRoot})); //$NON-NLS-1$
                    }
                } else {
                    Util.log(IStatus.ERROR, Util.getString(PREFIX + "notFragmentRoot", new Object[] {potentialRoot})); //$NON-NLS-1$
                }
            }
        }
    }

}
