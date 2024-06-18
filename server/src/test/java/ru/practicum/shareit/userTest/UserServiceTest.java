package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.ConcurrentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    @Autowired
    private final UserServiceImpl userService;

    @MockBean
    private final UserRepository userRepository;

    @Test
    void createTestOk() {
        User user = User.builder()
                .name("test name")
                .email("test@mail.ru")
                .build();

        when(userRepository.save(Mockito.any()))
                .thenReturn(user);

        UserDto userDto = UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        UserDto savedUserDto = userService.create(userDto);

        assertThat(userDto, is(notNullValue()));
        assertEquals(user.getName(), savedUserDto.getName());
        assertEquals(user.getEmail(), savedUserDto.getEmail());
    }

    @Test
    void findByIdTestOk() {
        User userToCheck = User.builder()
                .id(1L)
                .name("test name")
                .email("test@mail.ru")
                .build();

        when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(userToCheck));


        UserDto userDto = userService.findById(888L);
        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void findByIdTestThrowsNotFoundException() {
        when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> userService.findById(999L));
        assertEquals(notFoundException.getMessage(), "user id 999 not found");
    }

    @Test
    void getAllTest() {
        User user1 = User.builder()
                .id(1L)
                .name("test 1 name")
                .email("test1@mail.ru")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("test 2 name")
                .email("test2@mail.ru")
                .build();
        List<User> userList = List.of(user1, user2);

        when(userRepository.findAll())
                .thenReturn(userList);

        Collection<UserDto> userDtos = userService.getAll();

        assertThat(userDtos, is(notNullValue()));
    }

    @Test
    void updateTestOk() {
        UserDto userDto = UserDto.builder()
                .name("updated test name")
                .email("updated@mail.ru")
                .build();

        User user = User.builder()
                .id(1L)
                .name("test name")
                .email("test@mail.ru")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        when(userRepository.save(any()))
                .thenReturn(user);

        userDto = userService.update(1L, userDto);

        assertThat(userDto, is(notNullValue()));
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getName(), user.getName());
    }

    @Test
    void updateTestThrowsNotFoundException() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test name")
                .email("test@mail.ru")
                .build();
        when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException =
                assertThrows(NotFoundException.class, () -> userService.update(999L, userDto));
        assertEquals(notFoundException.getMessage(), "user not found");
    }

    @Test
    void updateTestThrowsConcurrentException() {
        UserDto userDto = UserDto.builder()
                .name("test name")
                .email("test@mail.ru")
                .build();
        User user = User.builder()
                .name("another name")
                .email("test@mail.ru")
                .build();
        assertEquals(user.getEmail(), userDto.getEmail());
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail(anyString()))
                .thenThrow(ConcurrentException.class);
        assertThrows(ConcurrentException.class, ()-> userService.update(1L, userDto));
    }

    @Test
    void deleteTestOk() {
        User user = User.builder()
                .id(1L)
                .name("test name")
                .email("test@mail.ru")
                .build();

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        userRepository.deleteById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void deleteTestThrowsNotFoundException() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.delete(1L));
        assertEquals(notFoundException.getMessage(), "user id 1 not found");
    }

    @Test
    void isExistTestOk() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        assertTrue(userService.isExist(1L));
    }

    @Test
    void isExistTestFail() {
        when(userRepository.existsById(any()))
                .thenReturn(false);
        assertFalse(userService.isExist(1L));
    }
}
