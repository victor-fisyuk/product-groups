package com.victorfisyuk.productgroups.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select g from Group g where g.id = :id")
    Optional<Group> findByIdAndOptimisticForceIncrement(Long id);
}
