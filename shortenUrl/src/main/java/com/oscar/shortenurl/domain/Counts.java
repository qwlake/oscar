package com.oscar.shortenurl.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(indexes = {
    @Index(name = "path_idx", columnList = "path")
})
public class Counts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String path;

    @Column(nullable = false)
    private int count;

    @Builder
    public Counts(String path, int count) {
        this.path = path;
        this.count = count;
    }
}
