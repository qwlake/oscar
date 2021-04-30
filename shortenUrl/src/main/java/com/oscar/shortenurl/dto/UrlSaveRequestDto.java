package com.oscar.shortenurl.dto;

import com.oscar.shortenurl.domain.Urls;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UrlSaveRequestDto {

    private String url;

    public Urls toEntity() {
        return Urls.builder()
                .url(url)
                .build();
    }

    @Builder
    public UrlSaveRequestDto(String url) {
        this.url = url;
    }
}
