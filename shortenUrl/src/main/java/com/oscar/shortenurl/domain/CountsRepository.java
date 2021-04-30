package com.oscar.shortenurl.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountsRepository extends JpaRepository<Counts, Long> {
    Optional<Counts> findByPath(String path);
}
