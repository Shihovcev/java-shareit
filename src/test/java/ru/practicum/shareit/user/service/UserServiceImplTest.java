package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;

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

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
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

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

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
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verify(userMapper, never()).toResponseDto(any());
    }

    @Test
    void update_WhenUserExistsAndEmailIsUnique_ShouldUpdateUser() {
        Long userId = 1L;
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName(Optional.of("John Updated"));
        userPatchDto.setEmail(Optional.of("updated@example.com"));

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Old");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("updated@example.com");

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(userId);
        expectedResponse.setName("John Updated");
        expectedResponse.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("updated@example.com", userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedResponse);

        UserResponseDto result = userService.update(userPatchDto, userId);

        assertNotNull(result);
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getEmail(), result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNot("updated@example.com", userId);
        verify(userRepository).save(existingUser);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void update_WhenUserExistsAndEmailNotChanged_ShouldUpdateUser() {
        Long userId = 1L;
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName(Optional.of("John Updated"));
        userPatchDto.setEmail(Optional.of("john@example.com")); // Тот же email

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Old");
        existingUser.setEmail("john@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john@example.com");

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(userId);
        expectedResponse.setName("John Updated");
        expectedResponse.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("john@example.com", userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedResponse);

        UserResponseDto result = userService.update(userPatchDto, userId);

        assertNotNull(result);
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getEmail(), result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNot("john@example.com", userId);
        verify(userRepository).save(existingUser);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void update_WhenUserExistsAndEmailExists_ShouldThrowException() {
        Long userId = 1L;
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setEmail(Optional.of("exists@example.com"));

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("exists@example.com", userId)).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(userPatchDto, userId));

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNot("exists@example.com", userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_WhenUserNotExists_ShouldThrowException() {
        Long userId = 999L;
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName(Optional.of("Updated Name"));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(userPatchDto, userId));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_WhenOnlyNameProvided_ShouldUpdateOnlyName() {
        Long userId = 1L;
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName(Optional.of("John Updated"));
        userPatchDto.setEmail(Optional.empty());

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Old");
        existingUser.setEmail("john@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john@example.com");

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(userId);
        expectedResponse.setName("John Updated");
        expectedResponse.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedResponse);

        UserResponseDto result = userService.update(userPatchDto, userId);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals("john@example.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        verify(userRepository).save(existingUser);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void update_WhenOnlyEmailProvided_ShouldUpdateOnlyEmail() {
        Long userId = 1L;
        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName(Optional.empty());
        userPatchDto.setEmail(Optional.of("new@example.com"));

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Old");
        existingUser.setEmail("old@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Old");
        updatedUser.setEmail("new@example.com");

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setId(userId);
        expectedResponse.setName("John Old");
        expectedResponse.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("new@example.com", userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedResponse);

        UserResponseDto result = userService.update(userPatchDto, userId);

        assertNotNull(result);
        assertEquals("John Old", result.getName());
        assertEquals("new@example.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNot("new@example.com", userId);
        verify(userRepository).save(existingUser);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void deleteById_ShouldDeleteUser() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setName("John Doe");
        expectedUser.setEmail("john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getName(), result.getName());
        assertEquals(expectedUser.getEmail(), result.getEmail());

        verify(userRepository).findById(userId);
    }

    @Test
    void findById_WhenUserNotExists_ShouldThrowException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(userId));

        verify(userRepository).findById(userId);
    }
}