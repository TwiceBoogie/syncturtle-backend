package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.Category;
import dev.twiceb.passwordservice.repository.projection.CategoryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT cat FROM Category cat")
    List<CategoryProjection> findAllReadOnly();
}
