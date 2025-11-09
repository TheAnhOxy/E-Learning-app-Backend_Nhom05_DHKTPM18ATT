package com.elearning.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @ToString.Exclude
    private Section section;

    @Column(length = 50)
    private String provider;

    @Column(name = "provider_video_id", length = 255)
    private String providerVideoId;

    @Lob
    @Column(name = "playback_url", columnDefinition = "TEXT")
    private String playbackUrl;

    @Lob
    @Column(name = "video_description", columnDefinition = "TEXT")
    private String videoDescription;

    @Column(name = "duration_in_seconds", columnDefinition = "INTEGER DEFAULT 0")
    private Integer durationInSeconds;

    @Lob
    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "views_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long viewsCount;

    @Lob
    @Column(name = "video_preview_url", columnDefinition = "TEXT")
    private String videoPreviewUrl;

    @Column(name = "is_free", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isFree;

    @Column(name = "order_index", columnDefinition = "INTEGER DEFAULT 0")
    private Integer orderIndex;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Progress> progresses;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Quiz> quizzes;
}