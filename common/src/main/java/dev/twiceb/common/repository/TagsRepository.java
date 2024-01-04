package dev.twiceb.common.repository;

import dev.twiceb.common.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagsRepository extends JpaRepository<Tags, Long> {

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN 1 ELSE 0 END FROM Tags t WHERE LOWER(t.tagName) IN :tagNames")
    List<Integer> checkExistingTags(@Param("tagNames") List<String> tagNames);
}
