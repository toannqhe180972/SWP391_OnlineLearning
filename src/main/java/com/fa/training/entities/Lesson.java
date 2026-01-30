package com.fa.training.entities;

import com.fa.training.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Lesson topic;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LessonType type;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;

    @Column(length = 20)
    private String status;
}
