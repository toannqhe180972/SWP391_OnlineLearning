package com.fa.training.repository;

import com.fa.training.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByFeaturedTrue();
}
