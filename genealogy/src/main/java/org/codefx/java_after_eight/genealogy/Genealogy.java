package org.codefx.java_after_eight.genealogy;

import org.codefx.java_after_eight.genealogist.Genealogist;
import org.codefx.java_after_eight.genealogist.TypedRelation;
import org.codefx.java_after_eight.post.Post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class Genealogy {

	private final Collection<Post> posts;
	private final Collection<Genealogist> genealogists;
	private final Weights weights;

	public Genealogy(Collection<Post> posts, Collection<Genealogist> genealogists, Weights weights) {
		this.posts = requireNonNull(posts);
		this.genealogists = requireNonNull(genealogists);
		this.weights = requireNonNull(weights);
	}

	public Stream<Relation> inferRelations() {
		return aggregateTypedRelations(inferTypedRelations());
	}

	private Stream<Relation> aggregateTypedRelations(Stream<TypedRelation> typedRelations) {
		Map<Post, Map<Post, Collection<TypedRelation>>> sortedTypedRelations = new HashMap<>();
		typedRelations.forEach(relation -> sortedTypedRelations
				.computeIfAbsent(relation.post1(), __ -> new HashMap<>())
				.computeIfAbsent(relation.post2(), __ -> new ArrayList<>())
				.add(relation));
		return sortedTypedRelations
				.values().stream()
				.flatMap(postWithRelations -> postWithRelations.values().stream())
				.map(relations -> Relation.aggregate(relations.stream(), weights));
	}

	private Stream<TypedRelation> inferTypedRelations() {
		return posts.stream()
				.flatMap(post1 -> posts.stream()
						.map(post2 -> new Posts(post1, post2)))
				// no need to compare posts with themselves
				.filter(posts -> posts.post1 != posts.post2)
				.flatMap(posts -> genealogists.stream()
						.map(genealogist -> new PostResearch(genealogist, posts)))
				.map(PostResearch::infer);
	}

	private static class Posts {

		final Post post1;
		final Post post2;

		Posts(Post post1, Post post2) {
			this.post1 = post1;
			this.post2 = post2;
		}

	}

	private static class PostResearch {

		final Genealogist genealogist;
		final Posts posts;

		PostResearch(Genealogist genealogist, Posts posts) {
			this.genealogist = genealogist;
			this.posts = posts;
		}

		TypedRelation infer() {
			return genealogist.infer(posts.post1, posts.post2);
		}

	}

}
