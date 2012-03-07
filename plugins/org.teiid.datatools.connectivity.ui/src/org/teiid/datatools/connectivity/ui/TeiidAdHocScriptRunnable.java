/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.sqltools.core.DatabaseIdentifier;
import org.eclipse.datatools.sqltools.core.EditorCorePlugin;
import org.eclipse.datatools.sqltools.editor.core.connection.IConnectionTracker;
import org.eclipse.datatools.sqltools.editor.core.result.Messages;
import org.eclipse.datatools.sqltools.result.OperationCommand;
import org.eclipse.datatools.sqltools.sqleditor.result.SimpleSQLResultRunnable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.teiid.datatools.views.ExecutionPlanView;

public class TeiidAdHocScriptRunnable extends SimpleSQLResultRunnable {

	// these are not shared by the base class
    private static int    TASK_TOTAL         = 100;
    private static int    TASK_CONNECTION    = 10;
    private static int    TASK_STATEMENT     = 10;
    private static int    TASK_RUN           = 50;
    private static int    TASK_ITERATE       = 30;
    
	private long _startTime = new Date().getTime(), _endTime = _startTime;
    private String sql;
    private String description;
	
	public TeiidAdHocScriptRunnable( Connection con,
                                     String description,
                                     String sql,
			boolean closeCon, IConnectionTracker tracker,
			IProgressMonitor parentMonitor,
			DatabaseIdentifier databaseIdentifier,
                                     ILaunchConfiguration configuration ) {
		super(con, sql, closeCon, tracker, parentMonitor, databaseIdentifier, configuration);
        this.sql = sql;
        this.description = description;
	}
	
    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
    protected IStatus run(IProgressMonitor monitor)
    {
        if (monitor == null)
        {
            monitor = new NullProgressMonitor();
        }
        _monitor = monitor;

        monitor.beginTask(Messages.ResultSupportRunnable_name, TASK_TOTAL);
        monitor.subTask(Messages.ResultSupportRunnable_task_connection);
        if(_parentOperCommand == null)
        {
    		resultsViewAPI.createNewInstance(getOperationCommand(), getTerminateHandler());
        }
        else
        {    
            resultsViewAPI.createSubInstance(_parentOperCommand, getOperationCommand(), getTerminateHandler());
        }
		
        Connection connection = getConnection();

        initConnection(connection);

        try
        {
            monitor.worked(TASK_CONNECTION);
            if (monitor.isCanceled())
            {
                terminateExecution();
                return Status.CANCEL_STATUS;
            }
            monitor.subTask(Messages.ResultSupportRunnable_task_statement);

            try
            {
                _stmt = prepareStatement(connection);
                
                //try-catch block is used to catch exception considering some database (avaki) can't use this method.
                try
                {
                    _stmt.execute("SET SHOWPLAN DEBUG"); //$NON-NLS-1$
                    _stmt.setMaxFieldSize(16384);
                }
                catch (Exception e)
                {
                    //ignore
                }                
                monitor.worked(TASK_STATEMENT);
                if (monitor.isCanceled())
                {
                    terminateExecution();
                    return Status.CANCEL_STATUS;
                }
                monitor.subTask(Messages.ResultSupportRunnable_task_run);
            }
            catch (Throwable th)
            {
        		synchronized (getOperationCommand()) {
        			resultsViewAPI.appendThrowable(getOperationCommand(), th);
        			resultsViewAPI.appendStatusMessage(getOperationCommand(), th.getMessage());
        			resultsViewAPI.updateStatus(getOperationCommand(), OperationCommand.STATUS_FAILED);
        		}
                return Status.CANCEL_STATUS;
            }

            boolean moreResult = false;
            try
            {
                moreResult = runStatement(_stmt);
                monitor.worked(TASK_RUN);
                if (monitor.isCanceled())
                {
                    terminateExecution();
                    return Status.CANCEL_STATUS;
                }
                monitor.subTask(Messages.ResultSupportRunnable_task_iterate);
            }
            catch (Throwable th)
            {
                resultsViewAPI.appendThrowable(getOperationCommand(), th);
                if (th instanceof SQLException)
                {
                    handleSQLException((SQLException) th);
                }
                else
                {
                	synchronized (getOperationCommand()) {
	                	resultsViewAPI.appendStatusMessage(getOperationCommand(), th.getMessage());
	                	resultsViewAPI.updateStatus(getOperationCommand(), OperationCommand.STATUS_FAILED);
                	}
                }
                return Status.CANCEL_STATUS;
            }
            
            // Create two threads, one is for monitoring whether user cancel execution, the other is to execute handleSuccess() method. 
            MonitorRunnable monitorRunnable = new MonitorRunnable();
            HandleSuccessJob hsJob = new HandleSuccessJob(moreResult, monitorRunnable);
            
            Thread monitorThread = new Thread(monitorRunnable);
            hsJob.schedule();
            monitorThread.start();

            // Suspend current thread before the method handleSuccess is completed and the monitor thread terminates the execution or ends normally.
            try
            {
                hsJob.join();
                monitorThread.join();
            }
            catch (InterruptedException e)
            {
                EditorCorePlugin.getDefault().log(e);
            }
            
            if(monitorRunnable._returnStatus != null)
            {
                return monitorRunnable._returnStatus;
            }
            
            boolean success  = hsJob._moreResult;
            
            //Update status in main thread to avoid deadlock.
            if (success)
            {
                synchronized (getOperationCommand())
                {
                    resultsViewAPI.updateStatus(getOperationCommand(), OperationCommand.STATUS_SUCCEEDED);
                }
                monitor.worked(TASK_ITERATE);
            }
            else
            {
                synchronized (getOperationCommand())
                {
                    resultsViewAPI.updateStatus(getOperationCommand(), OperationCommand.STATUS_FAILED);
                }
                return Status.CANCEL_STATUS;
            }
        }
        finally
        {
            resultsViewAPI.saveElapseTime(_operationCommand, _endTime - _startTime);
            //save the results and parameters.
            resultsViewAPI.saveDetailResults(_operationCommand);

            UpdatePlanViewRunnable upvRunnable = new UpdatePlanViewRunnable(_stmt);
            Display display = (Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent());
            if (Thread.currentThread() != display.getThread()) {
                display.syncExec(upvRunnable);
            } else {
                // Update the Execution Plan
                handleShowExecutionPlan(_stmt);

                // Need to land on the ResultsView
                handleShowResultsView();
            }

            handleEnd(connection, _stmt);
            monitor.done();
        }

        return Status.OK_STATUS;
    }
    
    /*
     * Update the Execution Plan View
     */
    private void handleShowExecutionPlan( Statement stmt ) {
        String planStr = getExecutionPlan(stmt);
        IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
        IViewPart viewPart = null;
        try {
            if (window != null) {
                viewPart = window.getActivePage().showView(ExecutionPlanView.VIEW_ID);
                if (viewPart instanceof ExecutionPlanView) {
                    ((ExecutionPlanView)viewPart).updateContents(this.description, this.sql, planStr);
                }
            }
        } catch (PartInitException e) {
            String message = org.teiid.datatools.connectivity.ui.Messages.getString("TeiidAdHocScriptRunnable.initViewError"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
            Activator.getDefault().getLog().log(status);
        }
    }

    private void handleShowResultsView() {
        String RESULTS_VIEW = "org.eclipse.datatools.sqltools.result.resultView"; //$NON-NLS-1$
        IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
        try {
            if (window != null) {
                window.getActivePage().showView(RESULTS_VIEW);
            }
        } catch (PartInitException e) {
            String message = org.teiid.datatools.connectivity.ui.Messages.getString("TeiidAdHocScriptRunnable.initViewError"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
            Activator.getDefault().getLog().log(status);
        }
    }

    private class UpdatePlanViewRunnable implements Runnable {

        Statement statement;

        public UpdatePlanViewRunnable( Statement stmt ) {
            this.statement = stmt;
        }

        public void run() {
            handleShowExecutionPlan(this.statement);
            handleShowResultsView();
        }
    }

    private class MonitorRunnable implements Runnable
    {
        // Flag expresses whether this thread should be end.
        volatile boolean _end = true;
        IStatus _returnStatus = null;
        
        public void run()
        {
            if(_monitor == null)
            {
                return;
            }
            
            while(_end){
                if (_monitor.isCanceled() || (_parentMonitor != null && _parentMonitor.isCanceled()))
                {   
                    getTerminateHandler().run();
                    _returnStatus = Status.CANCEL_STATUS;
                    return;
                }
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    EditorCorePlugin.getDefault().log(e);
                }
            }
        }
    }
    
    private String getExecutionPlan( Statement stmt ) {
        String executionPlan = null;
        if (stmt != null) {
            try {
                ResultSet planRs = stmt.executeQuery("SHOW PLAN"); //$NON-NLS-1$
                planRs.next();
                executionPlan = planRs.getString("PLAN_XML"); //$NON-NLS-1$
            } catch (SQLException e) {
                String message = org.teiid.datatools.connectivity.ui.Messages.getString("TeiidAdHocScriptRunnable.getPlanError"); //$NON-NLS-1$
                 IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
                 Activator.getDefault().getLog().log(status);
            }
        }
        return executionPlan;
    }

    private class HandleSuccessJob extends Job
    {
        boolean _moreResult;
        MonitorRunnable _monitorThread;
        
        HandleSuccessJob(boolean moreResult, MonitorRunnable monitorThread)
        {
            super(Messages.ResultSupportRunnable_handseccess_name);
            _moreResult = moreResult;
            _monitorThread = monitorThread;
        }
        
        protected IStatus run(IProgressMonitor monitor)
        {
            monitor.beginTask(Messages.ResultSupportRunnable_handseccess_task, TASK_TOTAL);
            monitor.worked(TASK_STATEMENT);
            _moreResult = handleSuccess(_moreResult);
            monitor.worked(TASK_RUN + TASK_ITERATE);
            _monitorThread._end = false;
            return Status.OK_STATUS;
        }
    }
	
    public void loopThroughResults(Statement cstmt, boolean moreResult)
    throws SQLException
{
    boolean hasException = false;//if there are some Exception, we should thrown it out to triger finishFail
    boolean lastException = false;//if the last time is an Exception, we can't getResultSet immediately,should use
    // getMoreResult()
    SQLException exception = null;
    ResultSet rs = null;
    ArrayList updateCountList = new ArrayList();//to keep the updateCount number temporarily
    while (!isTerminated() && needLoopThroughResults())
    {
        if (isCanceled())
        {
            terminateExecution();
            throw new SQLException(Messages.ResultSupportRunnable_exception_terminated);
        }
        int updateCount = 0;
        if (!lastException)
        {
            try
            {
                if (moreResult)
                {
                    rs = cstmt.getResultSet();
                    if (rs != null)
                    {
                        if(_lastUpdateCount != -1)
                        {
                        	resultsViewAPI.appendUpdateCountMessage(getOperationCommand(), _lastUpdateCount);
                            _lastUpdateCount = -1;
                        }
                        ResultSetMetaData metadata = rs.getMetaData();
                        if(metadata.getColumnCount() == 1 && metadata.getColumnType(1) == Types.SQLXML) {
                        	try {
                        	rs.next();
                        	SQLXML xml = rs.getSQLXML(1);
                        	
                        	Reader reader = xml.getCharacterStream();
                        	BufferedReader bReader = new BufferedReader(reader);
//                        	char[] cbuf = new char[65536];

                        	StringBuffer stringbuf = new StringBuffer();
                        	char[] ch = new char[1];
                        	while (bReader.read(ch) != -1) {
                        	stringbuf.append(ch);

                        	}
                        	resultsViewAPI.appendXMLResultSet(getOperationCommand(), stringbuf.toString());
                        	} catch (SQLException e) {
            					// TODO Auto-generated catch block
            					e.printStackTrace();
            				} catch (TransformerFactoryConfigurationError e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        } else {
                        	resultsViewAPI.appendResultSet(getOperationCommand(), rs);
                        }
                    }
                }
                updateCount = cstmt.getUpdateCount();
                if (updateCount >= 0)
                {
                    updateCountList.add(new Integer(updateCount));
                    if(_lastUpdateCount != -1)
                    {
                    	resultsViewAPI.appendUpdateCountMessage(getOperationCommand(), _lastUpdateCount);
                    }
                    _lastUpdateCount = updateCount;
                }
                if (updateCount >= 0 || rs != null)
                {
                    /* 
                     * Notes: the code of the following line would step thread when the result set is too large.
                     * This maybe has relations with the implementation of database driver.
                     */ 
                    moreResult = cstmt.getMoreResults();
                    rs = null;
                    continue;
                }
                break;
            }
            catch (SQLException ex)
            {
            	resultsViewAPI.appendStatusMessage(getOperationCommand(), ex.getMessage());
                exception = ex;
                hasException = true;
                lastException = true;
                boolean isClosed = cstmt == null || getConnection() == null;
                if (!isClosed)
                {
                    try
                    {
                        getConnection().getMetaData();
                    }
                    catch (SQLException e)
                    {
                        isClosed = true;                        
                    }
                }
                if (isClosed)
                {
                    break;
                }
            }
        }

        try
        {
            moreResult = cstmt.getMoreResults();
            lastException = false;
        }
        catch (SQLException ex)
        {
            //Hui Cao: when there're 2 continuous exceptions with the same SQL state, we need to break to avoid deadloop.
            //Although this is not a exact condition of deadloop, we have to compromise. 
            if (ex.getSQLState() != null && exception != null && ex.getSQLState().equals(exception.getSQLState()))
            {
                break;
            }
        	resultsViewAPI.appendStatusMessage(getOperationCommand(), ex.getMessage());

            exception = ex;
            hasException = true;
            lastException = true;
            boolean isClosed = cstmt == null || getConnection() == null;
            if (!isClosed)
            {
                try
                {
                    getConnection().getMetaData();
                }
                catch (SQLException e)
                {
                    isClosed = true;                        
                }
            }
            if (isClosed)
            {
                break;
            }
        }
    }
    /**
     * The following code is to handle the updateCount. if it's a CallableStatement,we should discard the last one
     * because it's duplicated. From experiment we know that this is a defect of JConnect.
     */
    int count = updateCountList.size();

    if (!(cstmt instanceof CallableStatement))
    {
        if (_lastUpdateCount != -1)
        {
        	resultsViewAPI.appendUpdateCountMessage(getOperationCommand(), _lastUpdateCount);
            _lastUpdateCount = -1;
        }
    }
    else
    {
        /**
         * If the last two update count equal to each other, we discard the last one. Else display the last one.
         */
        if (count < 2
        || ((Integer) updateCountList.get(count - 1)).intValue() != ((Integer) updateCountList
            .get(count - 2)).intValue())
        {
            if (_lastUpdateCount != -1)
            {
            	resultsViewAPI.appendUpdateCountMessage(getOperationCommand(), _lastUpdateCount);
                _lastUpdateCount = -1;
            }
        }
    }
    //we must have these lines to throw exceptions out,
    //or some errors will not be displayed in result set view
    if (hasException)
    {
        throw exception;
    }
}


}
