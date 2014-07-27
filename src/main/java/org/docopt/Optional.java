package org.docopt;

import static org.docopt.Python.list;

import java.util.List;

class Optional extends BranchPattern {

	public Optional(final List<? extends Pattern> children) {
		super(children);
	}

	@Override
	protected MatchResult match(List<LeafPattern> left,
			List<LeafPattern> collected) {
		if (collected == null) {
			collected = list();
		}

		for (final Pattern pattern : getChildren()) {
			final MatchResult _ = pattern.match(left, collected);
			left = _.getLeft();
			collected = _.getCollected();
		}

		return new MatchResult(true, left, collected);
	}
}