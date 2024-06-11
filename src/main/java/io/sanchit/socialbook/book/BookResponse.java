package io.sanchit.socialbook.book;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {

    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopses;
    private String owner;
    private byte[] cover;
    private double rate;
    private boolean archived;
    private boolean sharable;
    // todo -- add cover
}
