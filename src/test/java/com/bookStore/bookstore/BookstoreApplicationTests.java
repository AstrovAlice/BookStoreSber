package com.bookStore.bookstore;

import com.bookStore.bookstore.controllers.BookWebController;
import com.bookStore.bookstore.model.Book;
import com.bookStore.bookstore.repositories.BookRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookWebControllerTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private Model model;

	@InjectMocks
	private BookWebController bookWebController;

	private Book testBook;
	private MockHttpSession session;

	@BeforeEach
	void setUp() {
		testBook = new Book();
		testBook.setId(1L);
		testBook.setTitle("Test Book");
		testBook.setAuthor("Test Author");
		testBook.setCoverImagePath("/uploads/test.jpg");

		session = new MockHttpSession();
	}

	@Test
	void listBook_ShouldReturnListView() {
		// Arrange
		List<Book> books = Collections.singletonList(testBook);
		when(bookRepository.findAll()).thenReturn(books);

		// Act
		String viewName = bookWebController.listBook(model, session);

		// Assert
		assertEquals("list", viewName);
		verify(model).addAttribute("books", books);
		verify(model).addAttribute("cartMap", anyMap());
		verify(model).addAttribute("cartSize", 0);
	}

	@Test
	void listBook_WithItemsInCart_ShouldShowCartSize() {
		// Arrange
		List<Book> books = Collections.singletonList(testBook);
		when(bookRepository.findAll()).thenReturn(books);

		List<Book> cart = Collections.singletonList(testBook);
		session.setAttribute("cart", cart);

		// Act
		String viewName = bookWebController.listBook(model, session);

		// Assert
		assertEquals("list", viewName);
		verify(model).addAttribute("cartSize", 1);
	}

	@Test
	void showCreateForm_ShouldReturnFormViewWithNewBook() {
		// Act
		String viewName = bookWebController.showCreateForm(model);

		// Assert
		assertEquals("form", viewName);
		verify(model).addAttribute(eq("book"), any(Book.class));
	}

	@Test
	void showEditForm_WithValidId_ShouldReturnFormViewWithBook() {
		// Arrange
		when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

		// Act
		String viewName = bookWebController.showEditForm(1L, model);

		// Assert
		assertEquals("form", viewName);
		verify(model).addAttribute("book", testBook);
	}

	@Test
	void showEditForm_WithInvalidId_ShouldThrowException() {
		// Arrange
		when(bookRepository.findById(99L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> {
			bookWebController.showEditForm(99L, model);
		});
	}

	@Test
	void deleteBook_ShouldCallRepositoryAndRedirect() {
		// Act
		String viewName = bookWebController.deleteBook(1L);

		// Assert
		assertEquals("redirect:/books", viewName);
		verify(bookRepository).deleteById(1L);
	}

	@Test
	void viewBookDetails_WithValidId_ShouldReturnDetailsView() {
		// Arrange
		when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

		// Act
		String viewName = bookWebController.viewBookDetails(1L, model, session);

		// Assert
		assertEquals("details", viewName);
		verify(model).addAttribute("book", testBook);
		verify(model).addAttribute("quantityInCart", 0);
	}

	@Test
	void viewBookDetails_WithBookInCart_ShouldShowQuantity() {
		// Arrange
		when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

		Map<Long, Integer> cart = new HashMap<>();
		cart.put(1L, 2);
		session.setAttribute("cart", cart);

		// Act
		String viewName = bookWebController.viewBookDetails(1L, model, session);

		// Assert
		assertEquals("details", viewName);
		verify(model).addAttribute("quantityInCart", 2);
	}

	@Test
	void viewBookDetails_WithInvalidId_ShouldThrowException() {
		// Arrange
		when(bookRepository.findById(99L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> {
			bookWebController.viewBookDetails(99L, model, session);
		});
	}

	@Test
	void saveBook_WithNewBookAndNoImage_ShouldUseDefaultImage() throws IOException {
		// Arrange
		Book newBook = new Book();
		newBook.setTitle("New Book");

		MultipartFile emptyFile = new MockMultipartFile(
				"coverImage",
				new byte[0]);

		// Act
		String viewName = bookWebController.saveBook(newBook, emptyFile);

		// Assert
		assertEquals("redirect:/books", viewName);
		assertEquals("/uploads/default-cover.jpg", newBook.getCoverImagePath());
		verify(bookRepository).save(newBook);
	}
}