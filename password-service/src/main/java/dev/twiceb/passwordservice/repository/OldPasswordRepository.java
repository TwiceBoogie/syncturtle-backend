package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.dto.request.OldPasswordDTO;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface OldPasswordRepository extends CrudRepository<OldPasswordDTO, String> {

    Optional<OldPasswordDTO> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}
