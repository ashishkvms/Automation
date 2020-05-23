package com.app.pages;

import java.util.List;

import com.app.BaseTest;
import com.app.utils.TestUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class RegisterUserPage extends BaseTest {

	TestUtils utils = new TestUtils();
	@AndroidFindBy(id = "com.android.permissioncontroller:id/permission_allow_button")
	private MobileElement allowPermissions;

	@AndroidFindBy(id = "actionBtnD")
	private MobileElement registerBtn;

	@AndroidFindBy(id = "edit_phone_number")
	private MobileElement phoneNumberTxtBox;

	@AndroidFindBy(id = "btn_next")
	private MobileElement continueBtn;

	@AndroidFindBy(id = "language_category_selector_image_view")
	private List<MobileElement> selectLanguageCheckBox;

	@AndroidFindBy(id = "confirmBtn")
	private MobileElement confirmBtn;



	public void allowPermissions() {
		try {
			while (isMobileElementDisplayed(allowPermissions)) {
				click(allowPermissions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clickOnRegisterBtn() {
		click(registerBtn);
	}

	public void enterNoToRegister(String mobileNo) {
		clear(phoneNumberTxtBox);
		sendKeys(phoneNumberTxtBox, mobileNo);
	}

	public void clickOnContinueBtn() {
		click(continueBtn);
	}

	public void selectLanguages() {
		waitForElementToBeVisible(confirmBtn);
		click(selectLanguageCheckBox.get(2));
	}

	public void clickOnConfirmBtn() {
		click(confirmBtn);
	}
	
	public boolean checkUserIsRegistered() {
		return isMobileElementDisplayed(registerBtn);
		
	}

	public void userRegisteration(String mobile) {
		if (checkUserIsRegistered()) {
			try {
				allowPermissions();
				clickOnRegisterBtn();
				enterNoToRegister(mobile);
				clickOnContinueBtn();
				selectLanguages();
				clickOnConfirmBtn();
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
