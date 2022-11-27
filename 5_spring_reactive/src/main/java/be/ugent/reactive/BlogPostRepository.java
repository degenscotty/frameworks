package be.ugent.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;


public interface BlogPostRepository extends ReactiveMongoRepository<BlogPost, String> {
    Flux<BlogPost> findByTitleContaining(String keyword);
}
