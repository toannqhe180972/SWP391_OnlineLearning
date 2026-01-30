package com.fa.training.entities;

import com.fa.training.enums.QuizType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Setting level;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "pass_rate")
    private Double passRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type", length = 50)
    private QuizType quizType;

    @Column(name = "number_of_questions")
    private Integer numberOfQuestions;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String status;
}
