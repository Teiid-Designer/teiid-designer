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
import org.komodo.common.io.NullWriter;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.SoaRepositories;
import org.komodo.repository.SoaRepository;
import org.komodo.shell.ShellConstants;
import org.komodo.shell.ShellI18n;
import org.overlord.sramp.shell.api.AbstractShellCommand;
import org.overlord.sramp.shell.api.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class for Komodo commands.
 * 
 * @param <T> the result type
 */
public abstract class KomodoCommand<T> extends AbstractShellCommand implements ShellConstants {

    protected static final long DEFAULT_WAIT_TIME = 1000;

    private final boolean cancelable;
    protected boolean canceled;
    private ExecutorService executor;
    protected final Logger logger;
    protected T result;
    protected boolean stop;
    private FutureTask<T> task;
    private long waitTime = DEFAULT_WAIT_TIME;

    /**
     * Constructs a non-cancelable.
     */
    protected KomodoCommand() {
        this(false);
    }

    /**
     * If cancelable, ignores all command print statements.
     * 
     * @param cancelable indicates if the command is cancelable
     */
    protected KomodoCommand(final boolean cancelable) {
        this.logger = LoggerFactory.getLogger(getClass());
        this.cancelable = cancelable;

        // ignore print statements
        if (this.cancelable) {
            setOutput(NullWriter.SHARED);
        }
    }

    /**
     * @param args the command arguments (can be <code>null</code> or empty)
     * @return the command result (can be <code>null</code>)
     * @throws Exception if there is a problem executing the command
     */
    protected abstract T doExecute(final String... args) throws Exception;

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
            final KomodoCommand<T> thisCommand = this;
            this.executor = Executors.newFixedThreadPool(1);
            this.task = new FutureTask<T>(new Callable<T>() {

                /**
                 * {@inheritDoc}
                 *
                 * @see java.util.concurrent.Callable#call()
                 */
                @Override
                public T call() throws Exception {
                    return thisCommand.doExecute(args);
                }
            });

            this.executor.execute(this.task);
        } else {
            try {
                this.result = doExecute(args);
            } catch (final Exception e) {
                print(e.getLocalizedMessage());
                this.logger.debug(e.getLocalizedMessage(), e);

                if (e instanceof InvalidNumberArgsException) {
                    printUsage();
                }
            }
        }
    }

    /**
     * @return the SOA repository cache (never <code>null</code>)
     * @throws CommandException if SOA repositories cache is not found
     */
    protected final SoaRepositories getRepositories() throws CommandException {
        final SoaRepositories repositories = (SoaRepositories)getContext().getVariable(SOA_REPOSITORIES);

        if (repositories == null) {
            throw new CommandException(this, I18n.bind(ShellI18n.repositoryCacheNotFound, getClass().getSimpleName()));
        }

        return repositories;
    }

    /**
     * @return the SOA repository the shell is connected to (never <code>null</code>)
     * @throws RepositoryNotFoundException if the shell is not connected to a SOA repository
     */
    protected final SoaRepository getRepository() throws RepositoryNotFoundException {
        final SoaRepository repository = (SoaRepository)getContext().getVariable(CONNECTED_SOA_REPOSITORY);

        if (repository == null) {
            throw new RepositoryNotFoundException(this);
        }

        return repository;
    }

    /**
     * Obtains the content of a file resource.
     * 
     * @param fileName the file name relative to the calling class (cannot be <code>null</code> or empty)
     * @return the input stream to the content; may be <code>null</code> if the resource does not exist
     * @throws FileNotFoundException if the file does not exist
     */
    protected InputStream getResourceAsStream(final String fileName) throws FileNotFoundException {
        Precondition.notEmpty(fileName, "fileName"); //$NON-NLS-1$
        return new FileInputStream(fileName);
    }

    /**
     * @return the results of the command (can be <code>null</code> if command has never been executed or the result has not be set)
     * @throws InterruptedException if the command was canceled
     * @throws Exception if there is a problem obtaining the result
     */
    public T getResult() throws Exception {
        if (this.cancelable) {
            try {
                while (!this.task.isDone()) {
                    try {
                        Thread.sleep(this.waitTime);
                    } catch (final Exception e) {
                        if (e instanceof InterruptedException) {
                            this.canceled = true;
                            this.task.cancel(true);
                        }

                        throw e;
                    }

                    if (!this.task.isDone() && this.stop) {
                        this.canceled = true;
                        this.task.cancel(true);
                        throw new InterruptedException(I18n.bind(ShellI18n.commandCanceled, getClass().getSimpleName()));
                    }
                }

                assert (!this.canceled && this.task.isDone());

                try {
                    this.result = this.task.get();
                } catch (final Exception e) {
                    if ((e instanceof InterruptedException) || (e instanceof CancellationException)) {
                        this.canceled = true;
                        this.task.cancel(true);
                    }

                    throw e;
                }
            } catch (final Exception e) {
                this.logger.debug(e.getLocalizedMessage(), e);
                throw e;
            } finally {
                if (this.executor != null) {
                    this.executor.shutdownNow();
                }
            }
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
