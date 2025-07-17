package com.bookStore.bookstore;

import com.bookStore.bookstore.repositories.UserServiceRepository;
import com.bookStore.bookstore.controllers.AdminController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminControllerTests {

	@Mock
	private UserServiceRepository userService;

	@Mock
	private Model model;

	@InjectMocks
	private AdminController adminController;

	@Test
	void addEmployee_ShouldCallServiceAndRedirect() {
		String username = "newEmp";
		String password = "password123";

		String redirectUrl = adminController.addEmployee(username, password);

		assertThat(redirectUrl).isEqualTo("redirect:/admin/employees");
		verify(userService).registerEmployee(username, password);
	}

	@Test
	void dashboard_ShouldAddCountsAndReturnView() {
		when(userService.countByRole("ROLE_USER")).thenReturn(10L);
		when(userService.countByRole("ROLE_EMPLOYEE")).thenReturn(5L);
		when(userService.countBooks()).thenReturn(100L);

		String viewName = adminController.dashboard(model);

		assertThat(viewName).isEqualTo("admin/dashboard");
		verify(model).addAttribute("clientsCount", 10L);
		verify(model).addAttribute("employeesCount", 5L);
		verify(model).addAttribute("booksCount", 100L);
	}

	@Test
	void deleteEmployee_ShouldCallServiceAndRedirect() {
		Long id = 1L;

		String redirectUrl = adminController.deleteEmployee(id);

		assertThat(redirectUrl).isEqualTo("redirect:/admin/employees");
		verify(userService).unregisterEmployee(id);
	}
}