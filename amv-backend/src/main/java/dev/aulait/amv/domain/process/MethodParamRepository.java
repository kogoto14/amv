package dev.aulait.amv.domain.process;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MethodParamRepository
    extends JpaRepository<MethodParamEntity, MethodParamEntityId> {}
