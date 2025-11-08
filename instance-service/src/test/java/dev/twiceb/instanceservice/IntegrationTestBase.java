package dev.twiceb.instanceservice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

import dev.twiceb.instanceservice.runner.InstanceConfigurator;
import dev.twiceb.instanceservice.runner.InstanceRegistrar;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public abstract class IntegrationTestBase extends PostgresTCBase {

    @Autowired
    protected InstanceRegistrar registrar;
    @Autowired
    protected InstanceConfigurator configurator;

    @BeforeAll
    void seedInstanceAndConfig() {
        registrar.run("TEST_SIG");
        configurator.run();
    }
}
