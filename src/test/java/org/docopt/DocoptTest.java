package org.docopt;

import static org.docopt.Python.list;
import static org.docopt.Python.partition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class DocoptTest extends TestCase {

	private static final String FILE_PROPERTY = DocoptTest.class.getName()
			+ ".file";

	private static final String VERBOSE_PROPERTY = DocoptTest.class.getName()
			+ ".verbose";

	private static final boolean VERBOSE = Boolean.getBoolean(VERBOSE_PROPERTY);

	private static final String MESSAGE_FORMAT = "\n\"\"\"%s\"\"\"\n$ %s\n\b";

	private static final String USER_ERROR = "\"user-error\"";

	private static final TypeReference<Map<String, Object>> TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {
	};

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static Test suite() {
		String file = System.getProperty(FILE_PROPERTY);

		try {
			final URL url;

			if (file == null) {
				url = DocoptTest.class.getResource("/testcases.docopt");
				file = url.toString();
			}
			else {
				url = url(file);
			}

			return parse(url);
		}
		catch (final IOException e) {
			final String message;

			if (e instanceof FileNotFoundException) {
				message = "No such file";
			}
			else {
				message = e.getMessage();
			}

			throw new DocoptTestError(message);
		}
	}

	private static DocoptTestError readError(final String file,
			final String message) {
		return new DocoptTestError(String.format("Failed to parse %s: %s",
				file, message));
	}

	private static URL url(final String file) throws IOException {
		URI uri;

		try {
			uri = new URI(file);
		}
		catch (final URISyntaxException e) {
			throw readError(file, e.getMessage());
		}

		// If the URI is not absolute, assume that it is a file path.
		if (!uri.isAbsolute()) {
			uri = new File(file).getAbsoluteFile().toURI();
		}

		// If the URI is a file, make sure that it is readable.
		if ("file".equals(uri.getScheme())) {
			final File f = new File(uri);

			// If the file does not exist, an exception will be thrown
			// when we attempt to read it. These errors just give a little
			// more detail.
			if (f.exists()) {
				if (f.isDirectory()) {
					throw readError(file, "Is a directory");
				}

				if (!f.canRead()) {
					throw readError(file, "Permission denied");
				}
			}
		}

		return uri.toURL();
	}

	private static TestSuite parse(final URL url) throws IOException {
		if (VERBOSE) {
			System.out.println("Generating test cases from " + url);
		}

		final String name = pureBaseName(url);

		String raw = read(url);

		raw = Pattern.compile("#.*$", Pattern.MULTILINE).matcher(raw)
				.replaceAll("");

		if (raw.startsWith("\"\"\"")) {
			raw = raw.substring(3);
		}

		int index = 0;

		final TestSuite suite = new TestSuite("docopt");

		for (final String fixture : raw.split("r\"\"\"")) {
			if (fixture.isEmpty()) {
				continue;
			}

			final String doc;
			final String body;

			// >>> doc, _, body = fixture.partition('"""')
			{
				final String[] _ = partition(fixture, "\"\"\"");
				doc = _[0];
				body = _[2];
			}

			boolean first = true;

			for (final String _case : body.split("\\$")) {
				if (first) {
					first = false;
					continue;
				}

				final String argv;
				final String expect;

				// >>> argv, _, expect = case.strip().partition('\n')
				{
					final String[] _ = partition(_case.trim(), "\n");
					argv = _[0];
					expect = _[2];
				}

				suite.addTest(new DocoptTest(String.format("%s_%d", name,
						++index), doc, argv(argv), expect(expect)));
			}
		}

		return suite;
	}

	private static String pureBaseName(final URL url) {
		final String name = url.getPath();

		if (name.isEmpty()) {
			return name;
		}

		return name.replaceFirst("^.+/", "").replaceFirst("\\.[^.]+$", "");
	}

	private static String read(final URL url) throws IOException {
		final InputStream stream = url.openStream();

		final Scanner scanner = new Scanner(stream, "UTF-8");

		try {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
		finally {
			scanner.close();
		}
	}

	private static List<String> argv(final String argv) {
		final List<String> _ = list(argv.trim().split("\\s+"));
		_.remove(0);
		return _;
	}

	private static String argv(final List<String> argv) {
		final StringBuilder sb = new StringBuilder();

		for (final String arg : argv) {
			sb.append("\"");
			sb.append(arg.replaceAll("\"", "\\\""));
			sb.append("\" ");
		}

		if (!argv.isEmpty()) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

	private static Object expect(final String expect) {
		if (USER_ERROR.equals(expect)) {
			return USER_ERROR;
		}

		try {
			return OBJECT_MAPPER.readValue(expect, TYPE_REFERENCE);
		}
		catch (final IOException e) {
			throw new IllegalStateException(
					"could not parse JSON object from:\n" + expect, e);
		}
	}

	private final String doc;

	private final List<String> argv;

	private final Object expected;

	private DocoptTest(final String name, final String doc,
			final List<String> argv, final Object expected) {
		super(name);

		this.doc = doc;
		this.argv = argv;
		this.expected = expected; // TODO: Make a defensive copy?
	}

	@Override
	protected void runTest() throws Throwable {
		Object actual = null;

		try {
			actual = new Docopt(doc).withStdOut(null).withStdErr(null)
					.withExit(false).parse(argv);
		}
		catch (final DocoptExitException e) {
			actual = USER_ERROR;
		}

		final String message = (!VERBOSE) ? null : String.format(
				MESSAGE_FORMAT, doc, argv(argv));

		try {
			assertEquals(message, expected, actual);
		}
		catch (final junit.framework.AssertionFailedError e) {
			e.setStackTrace(new StackTraceElement[0]);
			throw e;
		}
	}
}
