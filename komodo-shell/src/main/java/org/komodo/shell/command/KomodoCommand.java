/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.shell.command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import org.komodo.common.i18n.I18n;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.RepositoryManager;
import org.komodo.shell.ShellConstants;
import org.komodo.shell.ShellI18n;
import org.overlord.sramp.shell.api.AbstractShellCommand;
import org.overlord.sramp.shell.api.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class for Komodo commands.
 */
public abstract class KomodoCommand extends AbstractShellCommand implements ShellConstants {

    /**
     * A result indicating the command was successfully canceled.
     */
    public static final Object CANCELED = new Object();

    protected static final long DEFAULT_WAIT_TIME = 1000;

    /**
     * A result indicating the command had an unexpected error.
     */
    public static final Object ERROR = new Object();

    private final boolean cancelable;
    protected boolean canceled;
    private ExecutorService executor;
    protected final Logger logger;
    protected Object result;
    protected boolean stop;
    private FutureTask<Object> task;
    private long waitTime = DEFAULT_WAIT_TIME;

    /**
     * Constructs a non-cancelable.
     */
    protected KomodoCommand() {
        this(false);
    }

    /**
     * @param cancelable indicates if the command is cancelable
     */
    protected KomodoCommand(final boolean cancelable) {
        this.logger = LoggerFactory.getLogger(getClass());
        this.cancelable = cancelable;
    }

    /**
     * @param args the command arguments (can be <code>null</code> or empty)
     * @return the command result (can be <code>null</code>)
     * @throws Exception if there is a problem executing the command
     */
    protected abstract Object doExecute(final String... args) throws Exception;

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommand#execute()
     */
    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.shell.api.ShellCommand#execute()
     */
    @Override
    public final void execute() throws Exception {
        this.canceled = false;
        this.executor = null;
        this.result = null;
        this.stop = false;
        this.task = null;

        final Arguments arguments = getArguments();
        final String[] args = (((arguments == null) || arguments.isEmpty()) ? StringUtil.EMPTY_ARRAY : arguments.toArray(new String[arguments.size()]));

        if (this.cancelable) {
            final KomodoCommand thisCommand = this;
            this.executor = Executors.newFixedThreadPool(1);
            this.task = new FutureTask<Object>(new Callable<Object>() {

                /**
                 * {@inheritDoc}
                 *
                 * @see java.util.concurrent.Callable#call()
                 */
                @Override
                public Object call() throws Exception {
                    return thisCommand.doExecute(args);
                }
            });

            this.executor.execute(this.task);
        } else {
            this.result = doExecute(args);
        }
    }

    protected final RepositoryManager getRepositoryManager() {
        return (RepositoryManager)getContext().getVariable(KOMODO_CLIENT_QNAME);
    }

    /**
     * Obtains the content of a file resource.
     * 
     * @param fileName the file name relative to the calling class (cannot be <code>null</code> or empty)
     * @return the input stream to the content; may be <code>null</code> if the resource does not exist
     * @throws FileNotFoundException 
     */
    protected InputStream getResourceAsStream(final String fileName) throws FileNotFoundException {
        Precondition.notEmpty(fileName, "fileName"); //$NON-NLS-1$
        return new FileInputStream(fileName);
    }

    /**
     * @return the results of the command (can be <code>null</code>)
     */
    public Object getResult() {
        if (this.cancelable) {
            while (!this.task.isDone()) {
                if (this.stop) {
                    this.canceled = this.task.cancel(true);

                    if (this.canceled) {
                        this.result = CANCELED;
                    } else {
                        this.result = ERROR;
                    }

                    break;
                }

                try {
                    Thread.sleep(this.waitTime);
                } catch (final InterruptedException e) {
                    this.canceled = this.task.cancel(true);

                    if (this.canceled) {
                        this.result = CANCELED;
                    } else {
                        this.result = ERROR;
                        print(e.getMessage());
                    }

                    break;
                }
            }

            if (!this.task.isCancelled()) {
                try {
                    this.result = this.task.get();
                } catch (final Exception e) {
                    if ((e instanceof InterruptedException) || (e instanceof CancellationException)) {
                        this.canceled = this.task.cancel(true);

                        if (this.canceled) {
                            this.result = CANCELED;
                        } else {
                            this.result = ERROR;
                            print(e.getMessage());
                        }
                    } else {
                        this.result = ERROR;
                        this.logger.error(I18n.bind(ShellI18n.commandError, getClass().getSimpleName()), e);
                        print(e.getMessage());
                    }
                }
            }

            this.executor.shutdown();
            return this.result;
        }

        return this.result;
    }

    /**
     * @return the time to wait when checking if a cancelable command is done or has been canceled
     */
    public long getWaitTime() {
        return this.waitTime;
    }

    /**
     * @return <code>true</code> if this command is cancelable
     */
    public boolean isCancelable() {
        return this.cancelable;
    }

    /**
     * @param newWaitTime the time to wait between checking if the command has been canceled or if it is done
     */
    public final void setWaitTime(final long newWaitTime) {
        this.waitTime = newWaitTime;
    }

    /**
     * If possible, stops the command.
     */
    public final void stop() {
        this.stop = true;
    }

}
