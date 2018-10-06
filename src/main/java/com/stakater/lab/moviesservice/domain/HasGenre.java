package com.stakater.lab.moviesservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="HAS_GENRE")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class HasGenre {

    private @Id @GeneratedValue Long id;
    private @StartNode Movie movie;
    private @EndNode Genre genre;

    HasGenre(Movie movie, Genre genre) {
        this.movie = movie;
        this.genre = genre;
    }
}
