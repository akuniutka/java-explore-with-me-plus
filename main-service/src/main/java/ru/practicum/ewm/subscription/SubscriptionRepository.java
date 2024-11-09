package ru.practicum.ewm.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsBySubscriberIdAndTargetId(long subscriberId, long targetId);

    Optional<Subscription> findBySubscriberIdAndTargetId(long subscriberId, long targetId);

    Set<Subscription> findAllBySubscriberId(long subscriberId);
}
