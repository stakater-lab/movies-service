package com.stakater.lab.moviesservice.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@NodeEntity(label = "Movie")
@EqualsAndHashCode
@ToString
@SuppressWarnings("ALL")
public class Movie {

    private @Id @GeneratedValue Long id;
    private @NotNull @NotEmpty String title;

    private @Relationship(type = "ACTED_IN", direction = "INCOMING") Set<Role> roles = new HashSet<>();
    private @Relationship(type = "HAS_GENRE") Set<HasGenre> genres = new HashSet<>();

    public Movie(@NotNull @NotEmpty String title) {
        this.title = title;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addGenre(Genre genre) {
        HasGenre hasGenre = new HasGenre(this, genre);
        this.genres.add(hasGenre);
    }
}
