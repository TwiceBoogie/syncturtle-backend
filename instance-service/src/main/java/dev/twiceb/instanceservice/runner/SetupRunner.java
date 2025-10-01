package dev.twiceb.instanceservice.runner;

import java.util.List;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@Profile("setup")
@RequiredArgsConstructor
public class SetupRunner implements CommandLineRunner {

    private final InstanceRegistrar registrar;
    private final InstanceConfigurator configurator;
    private final ApplicationContext context;
    private final ApplicationArguments appArgs;

    @Override
    public void run(String... args) throws Exception {
        String sig = resolveSignature(appArgs);
        registrar.run(sig);
        configurator.run();

        System.exit(SpringApplication.exit(context, () -> 0));
    }

    private static String resolveSignature(ApplicationArguments args) {
        String fromArg = first(args.getOptionValues("machine-signature"));
        String fromEnv = System.getenv("MACHINE_SIGNATURE");
        return fromArg != null ? fromArg
                : fromEnv != null ? fromEnv : UUID.randomUUID().toString().replace("-", "");
    }

    private static String first(List<String> value) {
        return (value == null || value.isEmpty()) ? null : value.get(0);
    }
}
