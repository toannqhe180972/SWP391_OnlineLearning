package com.fa.training.repository;

import com.fa.training.entities.Setting;
import com.fa.training.enums.SettingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettingRepository extends JpaRepository<Setting, Long> {

        @Query("SELECT s FROM Setting s WHERE " +
                        "(:type IS NULL OR :type = '' OR s.type = :type) AND " +
                        "(:status IS NULL OR s.status = :status) AND " +
                        "(:search IS NULL OR :search = '' OR s.value LIKE %:search%)")
        Page<Setting> findWithFilters(@Param("type") String type,
                        @Param("status") SettingStatus status,
                        @Param("search") String search,
                        Pageable pageable);
}
