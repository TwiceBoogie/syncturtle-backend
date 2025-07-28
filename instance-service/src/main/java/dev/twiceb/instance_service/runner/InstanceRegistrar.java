package dev.twiceb.instance_service.runner;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.instance_service.AppProperties;
import dev.twiceb.instance_service.enums.InstanceEdition;
import dev.twiceb.instance_service.model.Instance;
import dev.twiceb.instance_service.repository.InstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class InstanceRegistrar implements CommandLineRunner {

    private final InstanceRepository instanceRepository;
    private final AppProperties appProperties;
    private final BuildProperties buildProperties;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // check if instance is registerd
        Optional<Instance> existing = instanceRepository.findFirstByOrderByCreatedAtAsc();
        if (existing.isPresent()) {
            Instance instance = existing.get();
            instance.setLastCheckedAt(Instant.now());
            instance.setCurrentVersion(buildProperties.getVersion());
            instance.setLatestVersion(buildProperties.getVersion());
            instance.setTest(appProperties.isTest());
            instance.setEdition(InstanceEdition.COMMUNITY);
            instanceRepository.save(instance);

            log.info("Instance already registered - updating");
        } else {
            String machineSignature = generateMachineSignature();
            Instance instance = new Instance();
            instance.setSlug(makeSlug("SyncTurtle"));
            instance.setName("SyncTurtle");
            instance.setEdition(InstanceEdition.COMMUNITY);
            instance.setCurrentVersion(buildProperties.getVersion());
            instance.setLatestVersion(buildProperties.getVersion());
            instance.setLastCheckedAt(Instant.now());
            instance.setDomain(appProperties.getWebUrl()); // will change later
            instance.setMachineSignature(machineSignature);

            instanceRepository.save(instance);

            log.info("New instance registered with signature: " + machineSignature);
        }

    }

    private String generateMachineSignature() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        String cpuId = hal.getProcessor().getProcessorIdentifier().getProcessorID();
        String cpuName = hal.getProcessor().getProcessorIdentifier().getName();
        String mac = getFirstMacAddress(hal);
        String motherboard = hal.getComputerSystem().getBaseboard().getSerialNumber();
        String diskSerial = hal.getDiskStores().stream().findFirst().map(HWDiskStore::getSerial)
                .orElse("unknown");

        String raw = cpuId + cpuName + mac + motherboard + diskSerial;

        try {
            MessageDigest digest = MessageDigest.getInstance("sha-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash machine signature", e);
        }
    }

    private String getFirstMacAddress(HardwareAbstractionLayer hal) {
        return hal.getNetworkIFs().stream().filter(net -> {
            String name = net.getName().toLowerCase();
            String display = net.getDisplayName().toLowerCase();
            return !name.contains("loopback") && !display.contains("loopback")
                    && !net.isKnownVmMacAddr();
        }).map(NetworkIF::getMacaddr).filter(mac -> mac != null && !mac.isBlank()).findFirst()
                .orElse("00:00:00:00:00:00");
    }

    private String makeSlug(String name) {
        String base =
                name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return base + "-" + suffix;
    }

}
