package dev.twiceb.passwordservice.controller.api;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.passwordservice.dto.response.CategoryListResponse;
import dev.twiceb.passwordservice.dto.response.ExpiryPoliciesResponse;
import dev.twiceb.passwordservice.dto.response.PasswordVaultHealthResponse;
import dev.twiceb.passwordservice.service.PasswordClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_PASSWORD)
public class PasswordApiController {

  private final PasswordClientService passwordClientService;

  @GetMapping("/isEmpty")
  public ResponseEntity<Boolean> isPasswordVaultEmpty() {
    return ResponseEntity.ok(passwordClientService.isPasswordVaultEmpty());
  }

  @GetMapping("/categories")
  public ResponseEntity<List<CategoryListResponse>> getCategories() {
    HeaderResponse<CategoryListResponse> res = passwordClientService.getCategories();
    return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
  }

  @GetMapping("/policies")
  public ResponseEntity<List<ExpiryPoliciesResponse>> findAllExpiryPolicies() {
    HeaderResponse<ExpiryPoliciesResponse> res = passwordClientService.getExpiryPolicies();
    return ResponseEntity.ok().headers(res.getHeaders()).body(res.getItems());
  }

  @GetMapping("/health")
  public ResponseEntity<PasswordVaultHealthResponse> getPasswordVaultHealth() {
    return ResponseEntity.ok().body(passwordClientService.getPasswordVaultHealth());
  }

}
