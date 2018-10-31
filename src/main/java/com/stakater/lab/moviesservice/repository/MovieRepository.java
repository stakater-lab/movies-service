package com.stakater.lab.moviesservice.repository;

import com.stakater.lab.moviesservice.domain.Movie;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "movies", path = "movies")
@SuppressWarnings("unused")
public interface MovieRepository extends PagingAndSortingRepository<Movie, Long> {
    List<Movie> findByTitle(@Param("0") String title);

    @Query("MATCH (m:Movie) WHERE m.title =~ ('(?i).*'+{title}+'.*') RETURN m")
    Collection<Movie> findByTitleContaining(@Param("title") String title);
}