function initialize() {
	const manyCategoryItemsDivs = document.querySelectorAll(".category-items");
	for (let i = 0; i < manyCategoryItemsDivs.length; i++) {
		const categoryDiv = manyCategoryItemsDivs[i];
		const categorySection = categoryDiv.querySelector(".category");
		const accordionIcons = categorySection.querySelectorAll(".icon.accordion");
		const items = categoryDiv.querySelector(".items");
		categorySection.addEventListener("click", () => {
			toggleVisibility(items);
			accordionIcons.forEach(indicator => {
				toggleClasses(indicator, "collapsed", "expanded");
			});
		});
	}
	const dichotomousProductButtons = document.querySelectorAll(".icon.dichotomous-product");
	dichotomousProductButtons.forEach(button => {
		const productId = button.parentElement.parentElement.value;
		button.addEventListener("click", () => {
			const callback = response => {
				toggleClasses(button, "plus", "x");
			};
			if (button.classList.contains("plus")) {
				request(callback, "POST", `/cart/products/${productId}`);
			}
			if (button.classList.contains("x")) {
				request(callback, "DELETE", `/cart/products/${productId}`);
			}
		});
	});
	const updatedQuantifiedButtonVisibility = (interface) => {
		const quantity = parseInt(interface.querySelector(".quantity").textContent);
		const maxQuantity = parseInt(interface.querySelector(".maximum").textContent);
		const plusButton = interface.querySelector(".plus");
		const minusButton = interface.querySelector(".minus");
		if (quantity > 0) {
			minusButton.classList.remove("hidden");
		} else if (quantity === 0) {
			minusButton.classList.add("hidden");
		}
		if (quantity === maxQuantity) {
			plusButton.classList.add("hidden");
		} else {
			plusButton.classList.remove("hidden");
		}
	};
	document.querySelectorAll(".interface").forEach(interface => {
		const productId = interface.parentElement.value;
		const quantitySpan = interface.querySelector(".quantity");
		if (interface.querySelector(".quantified-product")) {
			updatedQuantifiedButtonVisibility(interface);
			interface.querySelector(".quantified-product.plus").addEventListener("click", () => {
				putEventListener(quantity => quantity + 1);
			});
			interface.querySelector(".quantified-product.minus").addEventListener("click", () => {
				putEventListener(quantity => quantity - 1);
			});
		}
		const successfulAjaxPut = response => {
			const json = JSON.parse(response);
			quantitySpan.textContent = json.quantity;
			if (json.currencyId > 0) {
				const amountUsedSpan = document.querySelector(`#currency-${json.currencyId} .amountUsed`);
				amountUsedSpan.textContent = json.amountUsed;
			}
			updatedQuantifiedButtonVisibility(interface);
		};
		const putEventListener = quantityCallback => {
			const quantity = parseInt(quantitySpan.textContent);
			request(successfulAjaxPut, "PUT", `/cart/products/${productId}?quantity=${quantityCallback(quantity)}`);
		};
	});
}

function toggleClasses(element) {
	for (let i = 1; i < arguments.length; i++) {
		element.classList.toggle(arguments[i]);
	}
}

function toggleVisibility(element) {
	toggleClasses(element, "hidden", "visible");
}

const request = (callback, method, url) => {
	const xhr = new XMLHttpRequest();
	xhr.onreadystatechange = () => {
		if (xhr.readyState === 4 && xhr.status === 200) {
			callback(xhr.response);
		}
	};
	xhr.open(method, url, true);
	const token = getMetaContent("name", "_csrf");
	const header = getMetaContent("name", "_csrf_header");
	xhr.setRequestHeader(header, token);
	xhr.send();
};

const getMetaContent = (property, name) => document.head.querySelector("[" + property + "=" + name + "]").content;