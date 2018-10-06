package com.stakater.lab.moviesservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * A Role relationship entity between an actor and movie.
 *
 * @author Rasheed Waraich
 */
@RelationshipEntity(type = "ACTED_IN")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class Role {

    private @Id @GeneratedValue Long id;
    private @StartNode Actor actor;
    private String role;
    private @EndNode Movie movie;

    Role(Actor actor, String role, Movie movie) {
        this.actor = actor;
        this.role = role;
        this.movie = movie;
    }
}
