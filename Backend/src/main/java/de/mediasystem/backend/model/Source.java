package de.mediasystem.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Source {

    private SourceType sourceType;
    private String externalId;
}
