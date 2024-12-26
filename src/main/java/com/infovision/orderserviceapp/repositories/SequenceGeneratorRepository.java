package com.infovision.orderserviceapp.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.infovision.orderserviceapp.entities.SequenceGenerator;
import reactor.core.publisher.Mono;

public interface SequenceGeneratorRepository extends ReactiveCrudRepository<SequenceGenerator,Long> {
    Mono<SequenceGenerator> findByYear(int year);
}
