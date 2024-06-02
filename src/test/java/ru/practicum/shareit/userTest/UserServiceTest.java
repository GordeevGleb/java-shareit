package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    @Autowired
    private final UserServiceImpl userService;

    @MockBean
    private final UserRepository userRepository;

    @Test
    void createTest() {
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
    void NotFoundExceptionTest() {
        when(userRepository.findById(Mockito.any()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    void findByIdTest() {
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
    void updateTest() {
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
    }

//    @Test
//    void deleteTest() {
//        User user = User.builder()
//                .id(1L)
//                .name("test name")
//                .email("test@mail.ru")
//                .build();
//        userRepository.save(user);
//
//        userService.delete(user.getId());
//        verify(userRepository, Mockito.times(1)).deleteById(user.getId());
//    }
}
