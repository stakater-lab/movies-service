package com.stakater.lab.moviesservice.repository;

import com.stakater.lab.moviesservice.domain.Actor;
import com.stakater.lab.moviesservice.domain.Genre;
import com.stakater.lab.moviesservice.domain.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Loads data for demo
 */
@Component
public class DemoData {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {

        // cleanup first
        genreRepository.deleteAll();
        movieRepository.deleteAll();
        actorRepository.deleteAll();

        // add again
        Genre fantasyFiction = new Genre("Fantasy Fiction");
        genreRepository.save(fantasyFiction);

        Genre adventureFiction = new Genre("Adventure Fiction");
        genreRepository.save(adventureFiction);

        Movie lordOfTheRings = new Movie("Lord of the Rings");
        lordOfTheRings.addGenre(fantasyFiction);
        lordOfTheRings.addGenre(adventureFiction);
        movieRepository.save(lordOfTheRings);

        Actor elijahWood = new Actor("Elijah Wood");
        elijahWood.actedIn(lordOfTheRings, "Frodo Baggins");
        actorRepository.save(elijahWood);
    }
}
