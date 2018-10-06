package com.stakater.lab.moviesservice.domain;

import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@NodeEntity(label = "Actor")
@EqualsAndHashCode
@ToString
@SuppressWarnings("ALL")
public class Actor {

    private @Id @GeneratedValue Long id;
    private String name;
    private @Relationship(type = "ACTED_IN") Set<Role> roles = new HashSet<>();

    public Actor(String name) {
        this.name = name;
    }

    public void actedIn(Movie movie, String roleName) {
        Role role = new Role(this, roleName, movie);
        roles.add(role);
        movie.addRole(role);
    }
}
