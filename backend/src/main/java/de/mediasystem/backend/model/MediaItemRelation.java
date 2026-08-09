package de.mediasystem.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "media_item_relation")
@IdClass(MediaItemRelation.Key.class)
@Getter
@Setter
@NoArgsConstructor
public class MediaItemRelation {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_media_id")
    private MediaItem sourceItem;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_media_id")
    private MediaItem targetItem;

    @Id
    @Column(name = "relation_type", length = 30)
    private String relationType;

    /** Zusammengesetzter Schlüssel für {@link IdClass}. Feldnamen müssen den {@code @Id}-Feldern entsprechen. */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private Long sourceItem;
        private Long targetItem;
        private String relationType;
    }
}
