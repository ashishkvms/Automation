package com.app.tests;

import java.lang.reflect.Method;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.app.common.BaseTest;
import com.app.pages.HomePage;
import com.app.pages.RegisterUserPage;
import com.app.utils.TestUtils;

public class HomePageTests extends BaseTest{
	
	HomePage homePage;
	RegisterUserPage registerUser;
	JSONObject loginUsers;
	TestUtils utils = new TestUtils();
	
	  @BeforeClass
	  public void beforeClass() throws Exception {
		  getDriver().launchApp();
		  homePage = new HomePage();
		  registerUser = new RegisterUserPage();
		  loginUsers= readTestData();
		  registerUser.userRegisteration(loginUsers.getString("mobileNo"),loginUsers.getString("otp"));
	  }

	  @BeforeMethod
	  public void beforeMethod(Method m) {
		  utils.log().info("\n" + "****** starting test:" + m.getName() + "******" + "\n");
	  }

	  @Test(description = "Verify user is on homepage and get the bottom most header title")
	  public void validateHomePage() {
		  homePage.closeDrawer();
		  Assert.assertTrue(homePage.verifyHomePage(), "HomePage Verification Failed");
		  Assert.assertNotNull(homePage.getTitleOfBottomMostHeader(), "Get Title of Bottom Most Header Failed");
	  }
	
	
	
}
