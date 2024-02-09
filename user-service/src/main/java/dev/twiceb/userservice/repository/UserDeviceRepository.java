package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {


}
