package org.youngmonkeys.ezyplatform.test.pagination;

import com.tvd12.ezyfox.collect.Lists;
import com.tvd12.test.assertion.Asserts;
import lombok.AllArgsConstructor;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.pagination.LastUpdatedAtPageToken;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LastUpdatedAtPageTokenWithIdNumberIT {

    @Test
    public void gteTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.plusDays(1);
        List<Record> all = Lists.newArrayList(
            new Record(1, now),
            new Record(2, now),
            new Record(3, now),
            new Record(4, next),
            new Record(5, next),
            new Record(6, next)
        );
        Collections.sort(all);
        LastUpdatedAtPageToken pageToken = LastUpdatedAtPageToken
            .defaultPageToken(now);
        int limit = 2;
        pageToken.setLimit(limit);
        List<Record> records = all
            .stream()
            .filter(it -> {
                if (Objects.compare(
                    it.updatedAt,
                    pageToken.getUpdatedAt(),
                    Comparator.naturalOrder()
                ) > 0) {
                    return true;
                }
                return it.id > pageToken.getIdNumber();
            })
            .collect(Collectors.toList())
            .subList(0, limit);

        // when
        LastUpdatedAtPageToken nextPageToken = pageToken.newLastPageToken(
            records.size(),
            () -> records.get(1).updatedAt,
            () -> records.get(1).id
        );

        // then
        Asserts.assertEquals(
            nextPageToken,
            new LastUpdatedAtPageToken(now, 2L, null, 0, 2, true)
        );
    }

    @Test
    public void gtTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.plusDays(1);
        List<Record> all = Lists.newArrayList(
            new Record(1, now),
            new Record(2, now),
            new Record(3, now),
            new Record(4, next),
            new Record(5, next),
            new Record(6, next)
        );
        Collections.sort(all);
        LastUpdatedAtPageToken pageToken = LastUpdatedAtPageToken
            .defaultPageToken(now);
        int limit = 4;
        pageToken.setLimit(limit);
        List<Record> records = all
            .stream()
            .filter(it -> {
                if (Objects.compare(
                    it.updatedAt,
                    pageToken.getUpdatedAt(),
                    Comparator.naturalOrder()
                ) > 0) {
                    return true;
                }
                return it.id > pageToken.getIdNumber();
            })
            .collect(Collectors.toList())
            .subList(0, limit);

        // when
        LastUpdatedAtPageToken nextPageToken = pageToken.newLastPageToken(
            records.size(),
            () -> records.get(3).updatedAt,
            () -> records.get(3).id
        );

        // then
        Asserts.assertEquals(
            nextPageToken,
            new LastUpdatedAtPageToken(next, 0L, null, 0, 4, false)
        );
    }

    @AllArgsConstructor
    private static class Record implements Comparable<Record> {
        private long id;
        private LocalDateTime updatedAt;

        @Override
        public int compareTo(Record o) {
            int result = Objects.compare(
                updatedAt,
                o.updatedAt,
                Comparator.naturalOrder()
            );
            if (result == 0) {
                result = Long.compare(id, o.id);
            }
            return result;
        }
    }
}
