package org.docopt;

/**
 * 
 * Exit in case user invoked program with incorrect arguments.
 */
public final class DocoptExitException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final int exitCode;

	private final boolean printUsage;

	DocoptExitException(final int exitCode, final String message,
			final boolean printUsage) {
		super(message);
		this.exitCode = exitCode;
		this.printUsage = printUsage;
	}

	DocoptExitException(final int exitCode) {
		this(exitCode, null, false);
	}

	public int getExitCode() {
		return exitCode;
	}

	public boolean getPrintUsage() {
		return printUsage;
	}
}