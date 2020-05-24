package com.app.pages;

import java.util.List;

import com.app.common.BaseTest;
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
	
	@AndroidFindBy(id = "edit_pin_code")
	private MobileElement otpTxtBox;
	
	@AndroidFindBy(id = "verify_btn")
	private MobileElement verifyBtn;

	@AndroidFindBy(id = "language_category_selector_image_view")
	private List<MobileElement> selectLanguageCheckBox;

	@AndroidFindBy(id = "confirmBtn")
	private MobileElement confirmBtn;
	
	@AndroidFindBy(id = "tv_bottom_tab_text")
	private List<MobileElement> bottomTabs;
	
	@AndroidFindBy(id = "profileImage")
	private MobileElement userImg;
	
	@AndroidFindBy(id = "edit_profile")
	private MobileElement loggedInNumber;
	
	


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
	
	public void enterOtp(String otp) {
		clear(otpTxtBox);
		sendKeys(otpTxtBox,otp);
	}

	public void clickOnVerifyBtn() {
		click(verifyBtn);
	}
	
	public void selectLanguages() {
		waitForElementToBeVisible(confirmBtn);
		click(selectLanguageCheckBox.get(2));
	}

	public void clickOnConfirmBtn() {
		click(confirmBtn);
	}
	
	public boolean checkUserIsLoggedOut() {
		return isMobileElementDisplayed(registerBtn);
		
	}

	public void userRegisteration(String mobile, String otp) {
		if (checkUserIsLoggedOut()) {
			try {
				clickOnRegisterBtn();
				enterNoToRegister(mobile);
				clickOnContinueBtn();
				enterOtp(otp);
				clickOnVerifyBtn();
				selectLanguages();
				clickOnConfirmBtn();
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getLoggedInNo() {
		click(bottomTabs.get(1));
		click(userImg);
		return getText(loggedInNumber,"Logged in no. is: ");
	}

}
