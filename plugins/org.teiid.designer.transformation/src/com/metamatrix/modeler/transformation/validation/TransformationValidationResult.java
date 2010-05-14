/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.validation;

import java.util.Collections;
import java.util.List;

/**
 * TransformationValidationResult.  Result object returned when entire transformation is validated.
 */
public class TransformationValidationResult {
    private List statusList = null;

    private boolean insertAllowed;
    private boolean updateAllowed;
    private boolean deleteAllowed;
	private SqlTransformationResult selectStatus;
	private SqlTransformationResult insertStatus;
	private SqlTransformationResult updateStatus;
	private SqlTransformationResult deleteStatus;

    /**
     * Constructor
     */
    public TransformationValidationResult() {}

	/**
	 * Constructor
	 * @param statusList the List of Status objects
	 */
	public TransformationValidationResult(List statusList) {
		this.statusList = statusList;
	}

	/**
	 * Get the status List
	 * @return the List of Status objects
	 */
	public List getStatusList() {
		if(statusList!=null) {
			return statusList;
		}
		return Collections.EMPTY_LIST;
	}

    /**
     * Get the isValid status
     * @return 'true' if valid, 'false' if not.
     */
    public boolean isValid() {
    	boolean valid = false;
		if(getStatusList().isEmpty()) {
			// should always have select result
			valid = hasSelectResult();			
	    	if(valid) {
				valid = getSelectResult().isValidatable();
	    	}

			if(valid && hasInsertResult()) {
				valid = getInsertResult().isValidatable();
			}
			if(valid && hasUpdateResult()) {
				valid = getUpdateResult().isValidatable();
			}
			if(valid && hasDeleteResult()) {
				valid = getDeleteResult().isValidatable();
			}
    	}
        return valid;
    }

    /**
     * @return
     */
    public SqlTransformationResult getSelectResult() {
        return selectStatus;
    }

    /**
     * @return
     */
    public SqlTransformationResult getDeleteResult() {
        return deleteStatus;
    }

    /**
     * @return
     */
    public SqlTransformationResult getInsertResult() {
        return insertStatus;
    }

    /**
     * @return
     */
    public SqlTransformationResult getUpdateResult() {
        return updateStatus;
    }

	/**
	 * @return
	 */
	public boolean hasSelectResult() {
		return (selectStatus != null);
	}

    /**
     * @return
     */
    public boolean hasDeleteResult() {
        return (deleteStatus != null);
    }

    /**
     * @return
     */
    public boolean hasInsertResult() {
        return (insertStatus != null);
    }

    /**
     * @return
     */
    public boolean hasUpdateResult() {
        return (updateStatus != null);
    }

	/**
	 * @param result
	 */
	public void setSelectResult(SqlTransformationResult selectStatus) {
		this.selectStatus = selectStatus;
	}

	/**
	 * @param result
	 */
	public void setInsertResult(SqlTransformationResult insertStatus) {
		this.insertStatus = insertStatus;
	}

	/**
	 * @param result
	 */
	public void setUpdateResult(SqlTransformationResult updateStatus) {
		this.updateStatus = updateStatus;
	}

	/**
	 * @param result
	 */
	public void setDeleteResult(SqlTransformationResult deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	/**
	 * @param result
	 */
	public void setStatuses(List statusList) {
		this.statusList = statusList;
	}

    /**
     * @return
     */
    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    /**
     * @return
     */
    public boolean isInsertAllowed() {
        return insertAllowed;
    }

    /**
     * @return
     */
    public boolean isUpdateAllowed() {
        return updateAllowed;
    }

    /**
     * @param b
     */
    public void setDeleteAllowed(boolean b) {
        deleteAllowed = b;
    }

    /**
     * @param b
     */
    public void setInsertAllowed(boolean b) {
        insertAllowed = b;
    }

    /**
     * @param b
     */
    public void setUpdateAllowed(boolean b) {
        updateAllowed = b;
    }

}
