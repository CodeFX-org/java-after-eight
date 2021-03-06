package org.codefx.java_after_eight.genealogists.silly;

import org.codefx.java_after_eight.genealogist.Genealogist;
import org.codefx.java_after_eight.genealogist.RelationType;
import org.codefx.java_after_eight.genealogist.TypedRelation;
import org.codefx.java_after_eight.post.Post;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.round;
import static java.util.stream.Collectors.toSet;

public class SillyGenealogist implements Genealogist {

	private static final RelationType TYPE = new RelationType("silly");

	@Override
	public TypedRelation infer(Post post1, Post post2) {
		Set<Integer> post1Letters = titleLetters(post1);
		Set<Integer> post2Letters = titleLetters(post2);
		Set<Integer> intersection = new HashSet<>(post1Letters);
		intersection.retainAll(post2Letters);
		long score = round((100.0 * intersection.size()) / post1Letters.size());

		return new TypedRelation(post1, post2, TYPE, score);
	}

	private static Set<Integer> titleLetters(Post post) {
		return post
				.title()
				.text()
				.toLowerCase()
				.chars().boxed()
				.collect(toSet());
	}

}
