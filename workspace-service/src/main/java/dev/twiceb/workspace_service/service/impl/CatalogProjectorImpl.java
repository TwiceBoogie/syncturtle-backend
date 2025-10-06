// package dev.twiceb.workspace_service.service.impl;

// import java.time.Instant;
// import java.util.UUID;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.transaction.annotation.Transactional;
// import dev.twiceb.common.enums.InstanceEdition;
// import dev.twiceb.workspace_service.model.InstanceView;
// import dev.twiceb.workspace_service.model.PlanView;
// import dev.twiceb.workspace_service.repository.InstanceViewRepository;
// import dev.twiceb.workspace_service.repository.PlanViewRepository;
// import dev.twiceb.workspace_service.service.CatalogProjector;
// import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
// public class CatalogProjectorImpl implements CatalogProjector {

// private final InstanceViewRepository iViewRepository;
// private final PlanViewRepository pViewRepository;

// @Override
// @Transactional
// public void applyInstanceUpsert(UUID instanceId, String slug, InstanceEdition edition,
// long version, Instant updatedAt) {
// int updated =
// iViewRepository.upsertActiveIfNewer(instanceId, slug, edition, version, updatedAt);
// if (updated > 0) {
// return;
// }

// // new or event is stale
// if (!iViewRepository.existsById(instanceId)) {
// try {
// InstanceView instance = new InstanceView();
// instance.setId(instanceId);
// instance.setSlug(slug);
// instance.setEdition(edition);
// instance.setVersion(version);
// instance.setUpdatedAt(updatedAt);

// iViewRepository.save(instance);
// } catch (DataIntegrityViolationException e) {
// // inserted concurrently or stale event; either way safe to ignore
// }
// }
// }

// @Override
// @Transactional
// public void applyPlanUpsert(UUID planId, String key, long version, Instant updatedAt) {
// int updated = pViewRepository.upsertActiveIfNewer(planId, version, updatedAt);
// if (updated > 0) {
// return;
// }

// if (!pViewRepository.existsById(planId)) {
// try {
// PlanView plan = new PlanView();
// plan.setId(planId);
// plan.setKey(key);
// plan.setVersion(version);
// plan.setUpdatedAt(updatedAt);

// pViewRepository.save(plan);
// } catch (DataIntegrityViolationException e) {
// // ignore
// }
// }
// }

// }
