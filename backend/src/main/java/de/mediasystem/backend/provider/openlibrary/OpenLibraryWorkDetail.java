package de.mediasystem.backend.provider.openlibrary;

import java.util.List;

/**
 * Antwort von /works/{key}.json.
 * "description" ist bei OpenLibrary inkonsistent: mal ein reiner String, mal ein Objekt
 * {"type": "/type/text", "value": "..."} - deshalb hier bewusst als Object typisiert
 * und in OpenLibraryProvider manuell auf beide Fälle geprüft.
 */
public record OpenLibraryWorkDetail(String key,
                                     String title,
                                     Object description,
                                     List<Long> covers
) { }
