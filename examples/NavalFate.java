import java.util.Map;

import org.docopt.Docopt;

public final class NavalFate {

	private static final String doc = new StringBuilder()
			.append("Naval Fate.\n")
			.append("\n")
			.append("Usage:\n")
			.append("  naval_fate ship new <name>...\n")
			.append("  naval_fate ship <name> move <x> <y> [--speed=<kn>]\n")
			.append("  naval_fate ship shoot <x> <y>\n")
			.append("  naval_fate mine (set|remove) <x> <y> [--moored | --drifting]\n")
			.append("  naval_fate (-h | --help)\n")
			.append("  naval_fate --version\n").append("\n")
			.append("Options:\n").append("  -h --help     Show this screen.\n")
			.append("  --version     Show version.\n")
			.append("  --speed=<kn>  Speed in knots [default: 10].\n")
			.append("  --moored      Moored (anchored) mine.\n")
			.append("  --drifting    Drifting mine.\n").append("\n").toString();

	public static void main(String[] args) {
		Map<String, Object> opts = new Docopt(doc)
				.withVersion("Naval Fate 2.0").parse(args);
		System.out.println(opts);
	}
}