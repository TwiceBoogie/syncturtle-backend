// package dev.twiceb.common.util;

// import static dev.twiceb.common.constants.ErrorMessage.INTERNAL_SERVER_ERROR;

// import org.springframework.dao.DataAccessException;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.data.domain.Example;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.http.HttpStatus;

// import dev.twiceb.common.exception.ApiRequestException;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;

// @Component
// public class RepositoryWrapper {

// @Transactional
// public <T> T saveEntity(JpaRepository<T, Long> repository, T entity) {
// try {
// return repository.save(entity);
// } catch (DataIntegrityViolationException e) {
// System.err.println("Error saving entity: " + e.getMessage());
// throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
// } catch (DataAccessException e) {
// System.err.println("DB access error: " + e.getMessage());
// throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
// }
// }

// public <T> List<T> findAllByAccountId(JpaRepository<T, Long> repository, T entity) {
// try {
// Example<T> entityExample = Example.of(entity);
// return repository.findAll(entityExample);
// } catch (DataAccessException e) {
// System.err.println("DB access error: " + e.getMessage());
// throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
// }
// }

// @Transactional
// public <T> void deleteEntity(JpaRepository<T, Long> repository, T entity) {
// try {
// repository.delete(entity);
// } catch (DataAccessException e) {
// System.err.println("DB access error: " + e.getMessage());
// throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
// }
// }
// }
