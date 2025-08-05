package com.example.reminder.repository;

import com.example.reminder.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Сохранение и поиск пользователя по username")
    void testFindByUsername() {
        User user = new User();
        user.setUsername("testuser1");
        user.setPassword("password1");
        user.setEmail("test1@example.com");

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("testuser1");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test1@example.com");
    }
}