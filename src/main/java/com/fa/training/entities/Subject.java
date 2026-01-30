package com.fa.training.entities;

import com.fa.training.enums.SubjectStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String thumbnail;
    private String tagline;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Setting category;

    private boolean featured;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SubjectStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "subject")
    private List<SubjectDimension> dimensions = new ArrayList<>();

    @OneToMany(mappedBy = "subject")
    private List<PricePackage> pricePackages = new ArrayList<>();

    @OneToMany(mappedBy = "subject")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "subject")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "subject")
    private List<Quiz> quizzes = new ArrayList<>();
}
