import java.util.Map;

import org.docopt.Docopt;

public final class NavalFate {

	private static final String doc = "Naval Fate.\n"
			+ "\n"
			+ "Usage:\n"
			+ "  naval_fate ship new <name>...\n"
			+ "  naval_fate ship <name> move <x> <y> [--speed=<kn>]\n"
			+ "  naval_fate ship shoot <x> <y>\n"
			+ "  naval_fate mine (set|remove) <x> <y> [--moored | --drifting]\n"
			+ "  naval_fate (-h | --help)\n"
			+ "  naval_fate --version\n"
			+ "\n"
			+ "Options:\n"
			+ "  -h --help     Show this screen.\n"
			+ "  --version     Show version.\n"
			+ "  --speed=<kn>  Speed in knots [default: 10].\n"
			+ "  --moored      Moored (anchored) mine.\n"
			+ "  --drifting    Drifting mine.\n"
			+ "\n";

	public static void main(final String[] args) {
		final Map<String, Object> opts =
				new Docopt(doc).withVersion("Naval Fate 2.0").parse(args);
		System.out.println(opts);
	}
}