package com.app.tests;

import java.lang.reflect.Method;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.app.common.BaseTest;
import com.app.pages.RegisterUserPage;
import com.app.utils.TestUtils;

public class RegisterUserTests extends BaseTest {
	
	RegisterUserPage registerUserPage;
	JSONObject loginUsers;
	TestUtils utils = new TestUtils();
	
	  @BeforeClass
	  public void beforeClass() throws Exception {
		  loginUsers =readTestData();
		  getDriver().resetApp();
	  }

	  @AfterClass
	  public void afterClass() {
	  }
	  
	  @BeforeMethod
	  public void beforeMethod(Method m) {
		  utils.log().info("\n" + "****** starting test:" + m.getName() + "******" + "\n");
		  registerUserPage = new RegisterUserPage();
	  }

	  @AfterMethod
	  public void afterMethod() {		  
	  }
	  
	  @Test(description = "Register/Login into the app")
	  public void validateAppRegisteration() {
		  registerUserPage.allowPermissions();
		  registerUserPage.userRegisteration(loginUsers.getString("mobileNo"), loginUsers.getString("otp"));
		  String actualLoggedInNoString = registerUserPage.getLoggedInNo();
		  String expectedLoggedInNo=loginUsers.getString("mobileNo");
		  Assert.assertEquals(actualLoggedInNoString, expectedLoggedInNo, "User is Not Logged in with The Provided No");
	  }
}
