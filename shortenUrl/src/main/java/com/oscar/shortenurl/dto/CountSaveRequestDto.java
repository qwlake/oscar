package com.oscar.shortenurl.dto;

import com.oscar.shortenurl.domain.Counts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CountSaveRequestDto {

    private String path;
    private int count;

    public Counts toEntity() {
        return Counts.builder()
                .path(path)
                .count(count)
                .build();
    }

    @Builder
    public CountSaveRequestDto(String path, int count) {
        this.path = path;
        this.count = count;
    }
}
