package com.stakater.lab.moviesservice.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Genre Theory. Genre theory is used in the study of films in order to facilitate the categorization of films. Genre
 * are dependent on various factors such as story line, whom the director is, what are the audience expectations et cetera.
 * e.g. Action, Adventure, Comedy, etc.
 */
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@NodeEntity(label = "Genre")
@EqualsAndHashCode
@ToString
@SuppressWarnings("ALL")
public class Genre {

    private @Id @GeneratedValue Long id;
    private String name;

    public Genre(String name) {
        this.name = name;
    }
}
