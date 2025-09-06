package dev.twiceb.instanceservice.runner;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.instanceservice.domain.model.Instance;
import dev.twiceb.instanceservice.domain.repository.InstanceRepository;
import dev.twiceb.instanceservice.service.util.AppProperties;
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
        Instance instance =
                instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class).orElse(null);

        // if instance is null then register this instance
        if (instance == null) {
            String machineSignature = generateMachineSignature();
            instance = Instance.register(buildProperties.getVersion(), buildProperties.getVersion(),
                    machineSignature, appProperties.isTest());

            instanceRepository.save(instance);

            log.info("New instance registered with signature: " + machineSignature);
        } else {
            // update instance details
            instance.updateInstanceDetails(buildProperties.getVersion(),
                    buildProperties.getVersion(), appProperties.isTest());
            instanceRepository.save(instance);

            log.info("Instance already registered - updating");
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
}
