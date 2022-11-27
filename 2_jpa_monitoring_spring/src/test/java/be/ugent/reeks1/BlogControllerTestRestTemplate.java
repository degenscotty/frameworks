package be.ugent.reeks1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BlogControllerTestRestTemplate {

    /**
     * By using SpringBootTest.WebEnvironment.RANDOM_PORT the test will start the server
     * on a random port and Spring Boot automatically provides us with a TestRestTemplate.
     */
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void listBlogPosts() {
        BlogPost[] posts = restTemplate.getForObject("/posts", BlogPost[].class);
        assertThat(posts)
                .isNotNull()
                .hasSizeGreaterThan(0);
    }

    @Test
    public void getBlogPost() {
        BlogPost post = restTemplate.getForObject("/posts/{id}", BlogPost.class, 1L);
        assertThat(post)
                .isNotNull()
                .isEqualTo(BlogPostDAOMemory.helloWorldPost);
    }

    @Test
    public void createBlogPosts() {
        BlogPost post = new BlogPost(2L, "title", "content");
        URI newPostLocation = restTemplate.postForLocation("/posts", post);

        BlogPost retrievedPost = restTemplate.getForObject(newPostLocation, BlogPost.class);
        assertThat(retrievedPost).isEqualTo(post);
    }

    @Test
    public void getUnExistingPost() {
        ResponseEntity<String> response = restTemplate.getForEntity("/posts/{id}", String.class, 999L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updatePost() {
        BlogPost post = new BlogPost(2L, "new title", "new content");
        restTemplate.put("/posts/{id}", post, 2L);

        BlogPost retrievedPost = restTemplate.getForObject("/posts/{id}", BlogPost.class, 2L);
        assertThat(retrievedPost).isEqualTo(post);
    }

    @Test
    public void deletePost() {
        restTemplate.delete("/posts/{id}", 2L);

        ResponseEntity<String> response = restTemplate.getForEntity("/posts/{id}", String.class, 2L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
