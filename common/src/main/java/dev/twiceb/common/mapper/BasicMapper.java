package dev.twiceb.common.mapper;

import dev.twiceb.common.dto.response.CursorIdentifiable;
import dev.twiceb.common.dto.response.CursorPageResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BasicMapper {

    private final ModelMapper modelMapper;

    public <T, S> void updateEntityWithCondition(T request, S exisitingEntity) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(request, exisitingEntity);
    }

    public <T, S> S convertToResponse(T data, Class<S> type) {
        return modelMapper.map(data, type);
    }

    public <T, S> List<S> convertToResponseList(List<T> lists, Class<S> type) {
        return lists.contains(null) ? new ArrayList<>()
                : lists.stream().map(list -> convertToResponse(list, type)).toList();
    }

    public <T, S> HeaderResponse<S> getHeaderResponse(Page<T> pageableItems, Class<S> type) {
        List<S> responses = convertToResponseList(pageableItems.getContent(), type);
        return constructHeaderResponse(responses, pageableItems.getTotalPages());
    }

    public <T, S> HeaderResponse<S> getHeaderResponse(List<T> items, Integer totalPages,
            Class<S> type) {
        List<S> responses = convertToResponseList(items, type);
        return constructHeaderResponse(responses, totalPages);
    }

    public <T, S extends CursorIdentifiable> CursorPageResponse<S> convertToCursorPageResponse(
            List<T> items, int perPage, Class<S> type) {
        String nextCursor = "";
        // 1: convert to desired class type
        List<S> response = convertToResponseList(items, type);
        // 2: check if it has next;
        boolean hasNext = items.size() > perPage;
        // 3: if it has next items grab perPage amount
        if (hasNext) {
            response = response.subList(0, perPage);
        }
        if (hasNext && !response.isEmpty()) {
            S last = response.getLast();
            nextCursor = encodeCursor(last.getId(), last.getCreatedAt());
        }

        CursorPageResponse<S> cursorPageRes = new CursorPageResponse<>();
        cursorPageRes.setCount(response.size());
        cursorPageRes.setNextCursor(nextCursor);
        cursorPageRes.setNextPageResults(hasNext);
        cursorPageRes.setPrevCursor("");
        cursorPageRes.setPrevPageResults(false);
        cursorPageRes.setPerPage(perPage);
        cursorPageRes.setTotalResults(0L);
        cursorPageRes.setResults(response);
        return cursorPageRes;
    }

    private <S> HeaderResponse<S> constructHeaderResponse(List<S> responses, Integer totalPages) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("page-total-count", String.valueOf(totalPages));
        return new HeaderResponse<S>(responses, responseHeaders);
    }

    private String encodeCursor(UUID id, Instant createdAt) {
        try {
            // var json = "{\"createdAt\":\"" + p.createdAt() + "\",\"id\":\"" + p.id() + "\"}";
            String json = "{\"id\":\"" + id + "\",\"createdAt\":\"" + createdAt + "\"}";
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalArgumentException("encode failed", e);
        }
    }
}
