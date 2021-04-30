package com.oscar.shortenurl.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlsRepository extends JpaRepository<Urls, Long> {
    Optional<Urls> findById(Long id);
}
