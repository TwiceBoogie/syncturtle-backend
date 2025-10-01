package dev.twiceb.userservice.service.util;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.userservice.domain.model.Profile;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.ProfileRepository;
import dev.twiceb.userservice.domain.repository.TenantInviteViewRespository;
import dev.twiceb.userservice.domain.repository.TenantMembershipViewRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedirectionPathHelper {

    private final ProfileRepository profileRepository;
    private final TenantMembershipViewRepository tMembershipViewRepository;
    private final TenantInviteViewRespository tInviteViewRespository;

    @Transactional
    public String getRedirectionPath(User user) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> profileRepository.save(Profile.create(user, null)));

        if (!profile.isOnboarded()) {
            return "/onboarding";
        }

        // redirect to last tenant if user has it
        List<String> slugs = tMembershipViewRepository.findPrefferedSlug(user.getId(),
                profile.getLastTenantId(), PageRequest.of(0, 1)); // limit 1
        if (!slugs.isEmpty()) {
            return "/" + slugs.get(0);
        }

        if (tInviteViewRespository.countByIdEmailIgnoreCaseAndAcceptedFalse(user.getEmail()) > 0) {
            return "/invitations";
        }

        return "/create-workspace";
    }

}
