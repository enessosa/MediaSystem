package de.mediasystem.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserEntry {

    private long id;
    private Status status;
    private int rating;
    private String note;
    private LocalDateTime addedAt;


    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updateRating(int rating) {
        this.rating = rating;
    }
}
