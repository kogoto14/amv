package dev.aulait.amv.domain.extractor.java.SpringDataJpaAdjusterTestResources;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {}
