package org.wecancodeit.pantryplus2electricboogaloo.controllers;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.wecancodeit.pantryplus2electricboogaloo.LoginService;
import org.wecancodeit.pantryplus2electricboogaloo.category.Category;
import org.wecancodeit.pantryplus2electricboogaloo.category.CategoryRepository;
import org.wecancodeit.pantryplus2electricboogaloo.currency.Currency;
import org.wecancodeit.pantryplus2electricboogaloo.currency.CurrencyRepository;
import org.wecancodeit.pantryplus2electricboogaloo.product.LimitedProduct;
import org.wecancodeit.pantryplus2electricboogaloo.product.PricedProduct;
import org.wecancodeit.pantryplus2electricboogaloo.product.Product;
import org.wecancodeit.pantryplus2electricboogaloo.product.ProductRepository;

@Controller
public class AdminController {

	@Resource
	private CategoryRepository categoryRepo;

	@Resource
	private ProductRepository productRepo;

	@Resource
	private CurrencyRepository currencyRepo;

	@Resource
	private LoginService loginService;

	@GetMapping("/admin")
	public String displayAdminView(@AuthenticationPrincipal OAuth2User googleId, Model model) {
		checkClearance(googleId);
		model.addAttribute("currencyCount", currencyRepo.count());
		model.addAttribute("categoryCount", categoryRepo.count());
		return "admin/index";
	}

	@GetMapping("admin/categories")
	public String displayCategoriesView(@AuthenticationPrincipal OAuth2User googleId, Model model) {
		checkClearance(googleId);
		model.addAttribute("categories", categoryRepo.findAll());
		return "admin/categories";
	}

	@PostMapping("/admin/categories")
	public String receivePostRequestOnCategories(@AuthenticationPrincipal OAuth2User googleId,
			@RequestParam String categoryName,
			@RequestParam(defaultValue = "false") boolean schoolAgeChildrenRequired) {
		checkClearance(googleId);
		categoryRepo.save(new Category(categoryName, schoolAgeChildrenRequired));
		return "redirect:/admin/categories";
	}

	@GetMapping("admin/categories/{categoryId}")
	public String displayCategoryView(@AuthenticationPrincipal OAuth2User googleId, Model model,
			@PathVariable Long categoryId) {
		checkClearance(googleId);
		Optional<Category> potentialCategory = categoryRepo.findById(categoryId);
		if (potentialCategory.isPresent()) {
			model.addAttribute("category", potentialCategory.get());
			model.addAttribute("currencies", currencyRepo.findAll());
			return "admin/category";
		}
		return "redirect:/admin/categories";
	}

	@PostMapping("/admin/categories/{categoryId}/products")
	public String receivePostRequestOnProductsOfCategory(@AuthenticationPrincipal OAuth2User googleId,
			@PathVariable long categoryId, @RequestParam String productName, @RequestParam String type,
			@RequestParam(defaultValue = "0") int maximumQuantity,
			@RequestParam(defaultValue = "0") int valueInCurrency, @RequestParam(defaultValue = "0") long currencyId,
			@RequestParam String image, @RequestParam(defaultValue = "false") boolean infantsRequired) {
		checkClearance(googleId);
		Optional<Category> potentialCategory = categoryRepo.findById(categoryId);
		if (!potentialCategory.isPresent()) {
			return "redirect:/admin/categories";
		}
		Category category = potentialCategory.get();
		if (type.equals("Product")) {
			Product product = new Product(productName, category, image, infantsRequired);
			productRepo.save(product);
		} else if (type.equals("LimitedProduct")) {
			LimitedProduct product = new LimitedProduct(productName, category, image, infantsRequired, maximumQuantity);
			productRepo.save(product);
		} else if (type.equals("PricedProduct")) {
			Optional<Currency> potentialCurrency = currencyRepo.findById(currencyId);
			if (potentialCurrency.isPresent()) {
				Currency currency = potentialCurrency.get();
				PricedProduct product = new PricedProduct(productName, category, image, infantsRequired,
						maximumQuantity, currency, valueInCurrency);
				productRepo.save(product);
			} else {
				return "redirect:/admin/currencies";
			}
		}
		return "redirect:/admin/categories/" + categoryId;
	}

	@GetMapping("/admin/categories/{categoryId}/products/{productId}")
	public String displayProductView(@AuthenticationPrincipal OAuth2User googleId, Model model,
			@PathVariable long categoryId, @PathVariable long productId) {
		checkClearance(googleId);
		Optional<Product> potentialProduct = productRepo.findById(productId);
		if (potentialProduct.isPresent()) {
			Product product = potentialProduct.get();
			model.addAttribute("product", product);
			if (product instanceof PricedProduct) {
				return "admin/priced-product";
			}
			if (product instanceof LimitedProduct) {
				return "admin/limited-product";
			}
			return "admin/product";
		}
		return "redirect:/admin/categories/" + categoryId;
	}

	@GetMapping("/admin/currencies")
	public String displayCurrenciesView(@AuthenticationPrincipal OAuth2User googleId, Model model) {
		checkClearance(googleId);
		model.addAttribute("currencies", currencyRepo.findAll());
		return "admin/currencies";
	}

	@PostMapping("/admin/currencies")
	public String receivePostRequestOnCurrencies(@AuthenticationPrincipal OAuth2User googleId,
			@RequestParam String name, @RequestParam String allowanceMap, @RequestParam String unit) {
		checkClearance(googleId);
		currencyRepo.save(new Currency(name, allowanceMap, unit));
		return "redirect:/admin/currencies";
	}

	@GetMapping("/admin/currencies/{currencyId}")
	public String displayCurrencyView(@AuthenticationPrincipal OAuth2User googleId, Model model,
			@PathVariable long currencyId) {
		checkClearance(googleId);
		Optional<Currency> potentialCurrency = currencyRepo.findById(currencyId);
		if (!potentialCurrency.isPresent()) {
			return "redirect:/admin/currencies";
		}
		model.addAttribute("currency", currencyRepo.findById(currencyId).get());
		return "admin/currency";
	}

	private void checkClearance(OAuth2User googleId) {
		if (!loginService.isAdmin(googleId)) {
			throw new AccessDeniedException("403 returned");
		}
	}

}
