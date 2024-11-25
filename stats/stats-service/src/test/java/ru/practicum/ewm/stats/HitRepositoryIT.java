package ru.practicum.ewm.stats;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static ru.practicum.ewm.stats.TestModels.APP;
import static ru.practicum.ewm.stats.TestModels.END;
import static ru.practicum.ewm.stats.TestModels.ENDPOINT;
import static ru.practicum.ewm.stats.TestModels.START;
import static ru.practicum.ewm.stats.TestModels.makeTestViewStats;

@DataJpaTest
class HitRepositoryIT {

    private static final String ENDPOINT_2 = "endpointB";
    private static final String ENDPOINT_3 = "endpointC";

    @Autowired
    private HitRepository repository;

    @Test
    void whenGetHitsFromStartToEnd_ThenReturnHitCountForAllEndpointsInDescendingOrder() {

        final List<ViewStats> viewStats = repository.getHits(START, END);

        assertThat(viewStats, contains(
                equalTo(viewStatsC()),
                equalTo(viewStatsB()),
                equalTo(viewStatsA())
        ));
    }

    @Test
    void whenGetUniqueHitsFromStartToEnd_ThenReturnUniqueHitCountForAllEndpointsInDescendingOrder() {

        final List<ViewStats> viewStats = repository.getUniqueHits(START, END);

        assertThat(viewStats, contains(
                equalTo(viewStatsC()),
                equalTo(viewStatsBUnique()),
                equalTo(viewStatsA())
        ));
    }

    @Test
    void whenGetHitsFromStartToEndForSelectedEndpoints_ThenReturnHitCountForSelectedEndpointsInDescendingOrder() {
        final List<String> uris = List.of(ENDPOINT, ENDPOINT_2);

        final List<ViewStats> viewStats = repository.getHits(START, END, uris);

        assertThat(viewStats, contains(
                equalTo(viewStatsB()),
                equalTo(viewStatsA())
        ));
    }

    @Test
    void whenGetUniqueHitsFromStartToEndForSelectedEndpoints_ThenReturnUniqueHitCountForEndpointsInDescendingOrder() {
        final List<String> uris = List.of(ENDPOINT, ENDPOINT_2);

        final List<ViewStats> viewStats = repository.getUniqueHits(START, END, uris);

        assertThat(viewStats, contains(
                equalTo(viewStatsBUnique()),
                equalTo(viewStatsA())
        ));
    }

    @Test
    void whenAddNewHitForEndpoint_ThenHitCountForThatEndpointIncreases() {

        repository.save(TestModels.makeTestHit());
        final List<ViewStats> viewStats = repository.getHits(START, END);

        assertThat(viewStats, contains(
                equalTo(viewStatsC()),
                equalTo(viewStatsB()),
                equalTo(viewStatsAIncreased())
        ));
    }

    private ViewStats viewStatsA() {
        return makeTestViewStats(APP, ENDPOINT, 1L);
    }

    private ViewStats viewStatsAIncreased() {
        return makeTestViewStats(APP, ENDPOINT, 2L);
    }

    private ViewStats viewStatsB() {
        return makeTestViewStats(APP, ENDPOINT_2, 3L);
    }

    private ViewStats viewStatsBUnique() {
        return makeTestViewStats(APP, ENDPOINT_2, 2L);
    }

    private ViewStats viewStatsC() {
        return makeTestViewStats(APP, ENDPOINT_3, 4L);
    }

    private Matcher<ViewStats> equalTo(final ViewStats viewStats) {
        return new TypeSafeMatcher<>() {

            private final ViewStats expected = makeTestViewStats(viewStats);

            @Override
            protected boolean matchesSafely(final ViewStats actual) {
                return expected.equals(actual);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("app=%s, uri=%s, hits=%s"
                        .formatted(expected.getApp(), expected.getUri(), expected.getHits()));
            }
        };
    }
}