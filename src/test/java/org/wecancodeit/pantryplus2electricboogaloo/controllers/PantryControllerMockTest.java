package org.wecancodeit.pantryplus2electricboogaloo.controllers;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.wecancodeit.pantryplus2electricboogaloo.cart.Cart;
import org.wecancodeit.pantryplus2electricboogaloo.category.Category;
import org.wecancodeit.pantryplus2electricboogaloo.category.CategoryRepository;
import org.wecancodeit.pantryplus2electricboogaloo.lineitem.CountedLineItem;
import org.wecancodeit.pantryplus2electricboogaloo.lineitem.LineItem;
import org.wecancodeit.pantryplus2electricboogaloo.user.PantryUser;
import org.wecancodeit.pantryplus2electricboogaloo.user.UserRepository;

public class PantryControllerMockTest {

	@InjectMocks
	private PantryController underTest;

	@Mock
	private Category category;

	@Mock
	private Category anotherCategory;

	@Mock
	private CategoryRepository categoryRepo;

	@Mock
	private Model model;

	@Mock
	private OAuth2AuthenticationToken token;

	@Mock
	private OAuth2User authenticatedUser;

	@Mock
	private UserRepository userRepo;

	@Mock
	private PantryUser user;

	@Mock
	private Cart cart;

	String googleName;

	@Mock
	private LineItem lineItem;

	@Mock
	private LineItem anotherLineItem;

	@Mock
	private CountedLineItem countedLineItem;

	@Mock
	private CountedLineItem anotherCountedLineItem;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(token.getPrincipal()).thenReturn(authenticatedUser);
		Map<String, Object> attributes = new HashMap<>();
		googleName = "12345";
		attributes.put("sub", googleName);
		when(authenticatedUser.getAttributes()).thenReturn(attributes);
		when(userRepo.findByGoogleName(googleName)).thenReturn(Optional.of(user));
		when(user.getCart()).thenReturn(cart);
	}

	@Test
	public void shouldHaveDisplayUserFormReturnUserForm() {
		String templateName = "user-form";
		String actual = underTest.displayUserForm(model, token);
		assertThat(actual, is(templateName));
	}

	@Test
	public void shouldHaveDisplayShoppingReturnShopping() {
		String templateName = "shopping";
		String actual = underTest.displayShopping(model, token);
		assertThat(actual, is(templateName));
	}

	@Test
	public void shouldHaveDisplayCartReturnCart() {
		String templateName = "cart";
		when(user.getCart()).thenReturn(cart);
		Set<LineItem> lineItems = new HashSet<>();
		lineItems.addAll(asList(lineItem, anotherLineItem, countedLineItem));
		when(cart.getAllLineItems()).thenReturn(lineItems);
		String actual = underTest.displayCart(model, token);
		assertThat(actual, is(templateName));
	}

	@Test
	public void shouldHaveDisplayShoppingAddCategoriesToModel() {
		Iterable<Category> categories = asList(category, anotherCategory);
		when(categoryRepo.findAll()).thenReturn(categories);
		underTest.displayShopping(model, token);
		verify(model).addAttribute("categories", categories);
	}

	@Test
	public void shouldAttachCartToModelWhenDisplayingShopping() {
		Iterable<Category> categories = asList(category, anotherCategory);
		when(categoryRepo.findAll()).thenReturn(categories);
		when(user.getCart()).thenReturn(cart);

		underTest.displayShopping(model, token);
		verify(model).addAttribute("cart", cart);
	}

	@Test
	public void shouldReturnTemplateNameForAboutUs() {
		String templateName = "about-us";
		String actual = underTest.displayAboutUs();
		assertThat(actual, is(templateName));
	}

	@Test
	public void shouldHaveDisplayCartAttachLineItemsAndNoCountedLineItems() {
		HashSet<LineItem> lineItems = new HashSet<>(asList(lineItem, anotherLineItem));
		when(cart.getLineItems()).thenReturn(lineItems);
		underTest.displayCart(model, token);
		verify(model).addAttribute("lineItems", lineItems);
	}

	@Test
	public void shouldHaveDisplayCartAttachCountedLineItemsAndNoLineItems() {
		HashSet<CountedLineItem> countedLineItems = new HashSet<>(asList(countedLineItem, anotherCountedLineItem));
		when(cart.getCountedLineItems()).thenReturn(countedLineItems);
		underTest.displayCart(model, token);
		verify(model).addAttribute("countedLineItems", countedLineItems);
	}

	@Test
	public void shouldHaveDisplayUserFormAttachUserToModel() {
		underTest.displayUserForm(model, token);
		verify(model).addAttribute("user", user);
	}

	@Test
	public void shouldHaveDisplayCartAttachCartToModel() {
		underTest.displayCart(model, token);
		verify(model).addAttribute("cart", cart);
	}

	@Test
	public void shouldHaveDisplayWelcomePageReturnWelcome() {
		String templateName = underTest.displayWelcomeView(model, token);
		assertThat(templateName, is("welcome"));
	}

	@Test
	public void shouldAttachAuthenticatedAsTrueToModelIfUserIsSignedIn() {
		when(token.isAuthenticated()).thenReturn(true);
		underTest.displayWelcomeView(model, token);
		verify(model).addAttribute("authenticated", true);
	}

	@Test
	public void shouldAttachAuthenticatedAsFalseToModelIfUserIsNotSignedIn() {
		token = null;
		underTest.displayWelcomeView(model, token);
		verify(model).addAttribute("authenticated", false);
	}
	
	@Test
	public void shouldAttachUserIfAuthenticated() {
		underTest.displayWelcomeView(model, token);
		verify(model).addAttribute("user", user);
	}

}
