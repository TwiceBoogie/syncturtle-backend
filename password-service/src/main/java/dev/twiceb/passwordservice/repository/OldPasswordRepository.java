package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.dto.request.OldPasswordDTO;
import org.springframework.data.repository.CrudRepository;

public interface OldPasswordRepository extends CrudRepository<OldPasswordDTO, String> {
}
