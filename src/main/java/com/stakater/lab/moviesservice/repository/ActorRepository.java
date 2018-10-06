package com.stakater.lab.moviesservice.repository;

import com.stakater.lab.moviesservice.domain.Actor;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ActorRepository extends PagingAndSortingRepository<Actor, Long> {
}
