 package dev.twiceb.passwordservice.controller.api;

 import dev.twiceb.passwordservice.mapper.PasswordMapper;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RestController;

 import lombok.RequiredArgsConstructor;

 import static dev.twiceb.common.constants.PathConstants.*;

 @RestController
 @RequiredArgsConstructor
 @RequestMapping(API_V1_PASSWORD)
 public class PasswordApiController {

     private final PasswordMapper passwordMapper;


 }
