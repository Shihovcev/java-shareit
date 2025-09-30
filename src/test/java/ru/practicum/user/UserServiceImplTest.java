package ru.practicum.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_ShouldCreateUserAndReturnResponseDto() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setName("John Doe");
        expectedResponse.setEmail("john@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(expectedResponse);

        UserResponseDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getEmail(), result.getEmail());

        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userMapper).toUser(userDto);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDto(user);
    }

    @Test
    void create_WhenEmailExists_ShouldThrowException() {
        UserDto userDto = new UserDto();
        userDto.setEmail("exists@example.com");

        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(userDto));

        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userMapper, never()).toUser(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(userId);
        expectedResponse.setName("John Doe");
        expectedResponse.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(expectedResponse);

        UserResponseDto result = userService.getById(userId);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getEmail(), result.getEmail());

        verify(userRepository).findById(userId);
        verify(userMapper).toResponseDto(user);
    }

    @Test
    void getById_WhenUserNotExists_ShouldThrowException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenThrow(new NotFoundException("Пользователь не найден"));

        assertThrows(NotFoundException.class, () -> userService.getById(userId));

        verify(userRepository).findById(userId);
        verify(userMapper, never()).toResponseDto(any());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");

        UserResponseDto response1 = new UserResponseDto();
        response1.setId(1L);
        response1.setName("John Doe");
        response1.setEmail("john@example.com");

        UserResponseDto response2 = new UserResponseDto();
        response2.setId(2L);
        response2.setName("Jane Smith");
        response2.setEmail("jane@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toResponseDto(user1)).thenReturn(response1);
        when(userMapper.toResponseDto(user2)).thenReturn(response2);

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(userRepository).findAll();
        verify(userMapper, times(2)).toResponseDto(any(User.class));
    }

    @Test
    void update_WhenEmailIsUnique_ShouldUpdateUser() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("John Updated");
        userDto.setEmail("updated@example.com");

        User userFromDto = new User();
        userFromDto.setId(userId);
        userFromDto.setName(userDto.getName());
        userFromDto.setEmail(userDto.getEmail());
        when(userMapper.toUser(userDto)).thenReturn(userFromDto);

        when(userRepository.existsByEmailAndIdNot(userDto.getEmail(), userId)).thenReturn(false);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName(userDto.getName());
        updatedUser.setEmail(userDto.getEmail());
        when(userRepository.update(any(User.class))).thenReturn(updatedUser);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setName(userDto.getName());
        responseDto.setEmail(userDto.getEmail());
        when(userMapper.toResponseDto(updatedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.update(userDto, userId);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository).existsByEmailAndIdNot(userDto.getEmail(), userId);
        verify(userMapper).toUser(userDto);
        verify(userRepository).update(userFromDto);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void update_WhenEmailExists_ShouldThrowException() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setEmail("exists@example.com");

        when(userRepository.existsByEmailAndIdNot(userDto.getEmail(), userId)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(userDto, userId));

        verify(userRepository).existsByEmailAndIdNot(userDto.getEmail(), userId);
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).update(any());
    }

    @Test
    void deleteById_ShouldDeleteUser() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteById(userId);

        verify(userRepository).deleteById(userId);
        // findById не вызывается в сервисе, поэтому его не нужно проверять
    }

    @Test
    void deleteById_WhenUserNotExists_ShouldThrowException() {
        Long userId = 999L;

        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userRepository).deleteById(userId);

        assertThrows(NotFoundException.class, () -> userService.deleteById(userId));

        verify(userRepository).deleteById(userId);
    }
}