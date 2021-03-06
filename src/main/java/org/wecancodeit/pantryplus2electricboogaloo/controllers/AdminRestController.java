package org.wecancodeit.pantryplus2electricboogaloo.controllers;

import javax.annotation.Resource;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wecancodeit.pantryplus2electricboogaloo.LoginService;
import org.wecancodeit.pantryplus2electricboogaloo.category.Category;
import org.wecancodeit.pantryplus2electricboogaloo.category.CategoryRepository;
import org.wecancodeit.pantryplus2electricboogaloo.currency.Currency;
import org.wecancodeit.pantryplus2electricboogaloo.currency.CurrencyRepository;
import org.wecancodeit.pantryplus2electricboogaloo.product.ProductRepository;

@RestController
public class AdminRestController {

	@Resource
	private CurrencyRepository currencyRepo;

	@Resource
	private CategoryRepository categoryRepo;

	@Resource
	private ProductRepository productRepo;

	@Resource
	private LoginService loginService;

	@DeleteMapping("/admin/currencies/{currencyId}")
	public void receiveDeleteOnCurrency(@AuthenticationPrincipal OAuth2User googleId, @PathVariable long currencyId) {
		if (loginService.isAdmin(googleId)) {
			currencyRepo.deleteById(currencyId);
		}
	}

	@DeleteMapping("/admin/categories/{categoryId}")
	public void receiveDeleteOnCategory(@AuthenticationPrincipal OAuth2User googleId, @PathVariable long categoryId) {
		if (loginService.isAdmin(googleId)) {
			categoryRepo.deleteById(categoryId);
		}
	}

	@DeleteMapping("/admin/categories/{categoryId}/products/{productId}")
	public void receiveDeleteOnProduct(@AuthenticationPrincipal OAuth2User googleId, @PathVariable long productId) {
		if (loginService.isAdmin(googleId)) {
			productRepo.deleteById(productId);
		}
	}

	@PutMapping("/admin/categories/{categoryId}")
	public void receivePutOnCategory(@AuthenticationPrincipal OAuth2User googleId, @PathVariable long categoryId,
			@RequestParam String name, boolean schoolAgeChildrenRequired) {
		if (loginService.isAdmin(googleId)) {
			Category category = categoryRepo.findById(categoryId).get();
			category.updateName(name);
			category.updateSchoolAgeChildrenRequired(schoolAgeChildrenRequired);
			categoryRepo.save(category);
		}
	}

	@PutMapping("/admin/currencies/{currencyId}")
	public void receivePutOnCurrency(@AuthenticationPrincipal OAuth2User googleId, @PathVariable long currencyId,
			@RequestParam String name, @RequestParam String unit, @RequestParam String allowanceMap) {
		if (loginService.isAdmin(googleId)) {
			Currency currency = currencyRepo.findById(currencyId).get();
			currency.setName(name);
			currency.setUnit(unit);
			currency.setAllowanceMap(allowanceMap);
			currencyRepo.save(currency);
		}
	}

}
