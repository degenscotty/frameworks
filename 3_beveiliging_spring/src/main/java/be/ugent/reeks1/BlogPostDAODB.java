package be.ugent.reeks1;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Profile("!test")
public class BlogPostDAODB implements BlogPostDAO {
    private final BlogPostRepository repository;

    public BlogPostDAODB(BlogPostRepository repository) {
        this.repository = repository;
    }

    public List<BlogPost> getAllPosts() {
        return repository.findAll();
    }

    public void addPost(BlogPost blogPost) {
        repository.save(blogPost);
    }

    public void updatePost(long id, BlogPost blogPost) {
        repository.save(blogPost);
    }

    public Optional<BlogPost> getPost(long id) {
        return repository.findById(id);
    }

    public void deletePost(long id) {
        repository.deleteById(id);
    }

    public List<BlogPost> searchPostsByTitleContaining(String keyword) {
        return repository.findByTitleContaining(keyword);
    }
}
