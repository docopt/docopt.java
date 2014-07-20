package org.docopt;

final class DocoptTestError extends AssertionError {

	private static final long serialVersionUID = 1L;

	public DocoptTestError(final String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
