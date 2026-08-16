package com.stackit_db.mainpro.repository;

import com.stackit_db.mainpro.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.user.id = :userId ORDER BY q.id DESC")
    List<Question> findQuestionsByUserId(@Param("userId") Long userId);
}
