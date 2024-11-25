package ru.practicum.ewm.stats;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Objects;

final class TestModels {

    public static final Long NO_ID = null;
    static final long HIT_ID = 1L;
    static final String APP = "mainService";
    static final String ENDPOINT = "endpointA";
    static final String IP = "127.0.0.1";
    static final long HITS = 99L;
    static final LocalDateTime TIMESTAMP = LocalDateTime.of(2000, Month.JANUARY, 31, 13, 30, 55);
    static final LocalDateTime START = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 1);
    static final LocalDateTime END = LocalDateTime.of(2000, Month.FEBRUARY, 2, 0, 0, 2);

    private TestModels() {
    }

    static NewHitDto makeTestNewHitDto() {
        return NewHitDto.builder()
                .app(APP)
                .uri(ENDPOINT)
                .ip(IP)
                .timestamp(TIMESTAMP)
                .build();
    }

    static ViewStatsDto makeTestViewStatsDto() {
        return ViewStatsDto.builder()
                .app(APP)
                .uri(ENDPOINT)
                .hits(HITS)
                .build();
    }

    static Hit makeTestHit() {
        return makeTestHit(HIT_ID);
    }

    static Hit makeTestHit(final Long id) {
        final Hit hit = new Hit();
        hit.setId(id);
        hit.setApp(APP);
        hit.setUri(ENDPOINT);
        hit.setIp(IP);
        hit.setTimestamp(TIMESTAMP);
        return hit;
    }

    static HitDto makeTestHitDto() {
        return HitDto.builder()
                .id(HIT_ID)
                .app(APP)
                .uri(ENDPOINT)
                .ip(IP)
                .timestamp(TIMESTAMP)
                .build();
    }

    static ViewStats makeTestViewStats() {
        return makeTestViewStats(APP, ENDPOINT, HITS);
    }

    static ViewStats makeTestViewStats(final ViewStats viewStats) {
        return makeTestViewStats(viewStats.getApp(), viewStats.getUri(), viewStats.getHits());
    }

    static ViewStats makeTestViewStats(final String app, final String uri, final Long hits) {
        return new ViewStatsImpl(app, uri, hits);
    }

    private record ViewStatsImpl(String app, String uri, Long hits) implements ViewStats {

        @Override
        public String getApp() {
            return app;
        }

        @Override
        public String getUri() {
            return uri;
        }

        @Override
        public Long getHits() {
            return hits;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ViewStats other)) {
                return false;
            }
            return Objects.equals(this.getApp(), other.getApp())
                    && Objects.equals(this.getUri(), other.getUri())
                    && Objects.equals(this.getHits(), other.getHits());
        }
    }
}
