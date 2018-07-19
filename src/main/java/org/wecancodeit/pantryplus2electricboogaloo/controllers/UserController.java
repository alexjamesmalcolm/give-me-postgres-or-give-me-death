package org.wecancodeit.pantryplus2electricboogaloo.controllers;

import javax.annotation.Resource;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.wecancodeit.pantryplus2electricboogaloo.user.PantryUser;
import org.wecancodeit.pantryplus2electricboogaloo.user.UserRepository;

@Controller
public class UserController extends LoginController {

	@Resource
	private UserRepository userRepo;

	@RequestMapping("/user")
	public String receiveRequestOnUser(OAuth2AuthenticationToken token, @RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String address, @RequestParam int familySize,
			@RequestParam String birthdate, @RequestParam int schoolAgeChildren, @RequestParam String zipCode,
			@RequestParam String referral, @RequestParam(required = false) boolean hasInfants) {
		System.out.println(firstName);
		System.out.println(hasInfants);
		PantryUser user = resolveUser(token);
		user.updateFirstName(firstName);
		user.updateLastName(lastName);
		user.updateAddress(address);
		user.updateFamilySize(familySize);
		user.updateBirthdate(birthdate);
		if (familySize >= schoolAgeChildren) {
			user.updateSchoolAgeChildren(schoolAgeChildren);
		}
		user.updateZipCode(zipCode);
		user.updateReferral(referral);
		user.updateHasInfants(hasInfants);
		userRepo.save(user);
		return "redirect:/settings";
	}
}
