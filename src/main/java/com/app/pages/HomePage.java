package com.app.pages;

import java.util.List;

import org.testng.Assert;

import com.app.common.BaseTest;
import com.app.utils.TestUtils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class HomePage extends BaseTest{
	TestUtils utils = new TestUtils();
	@AndroidFindBy (id = "imgLogo") 
	private MobileElement closeDrawer;
	
	@AndroidFindBy (id = "home_title") 
	private MobileElement xStreamLogo;
	
	@AndroidFindBy (id = "row_header_title_text_view") 
	private List<MobileElement> headerTitle;
	
	@AndroidFindBy (id = "actionBtn") 
	private MobileElement xStreamPremiumBtn;
	
	public boolean verifyHomePage() {
		return isMobileElementDisplayed(xStreamLogo);
	}
	
	public void closeDrawer() {
		try {
			if(isMobileElementDisplayed(xStreamPremiumBtn)) {
		dragElement(closeDrawer, xStreamPremiumBtn);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getTitleOfBottomMostHeader() {
		scrollToBottomOfPage(headerTitle);
		return getText(headerTitle.get(headerTitle.size()-1), "Retrieve the bottommost header title is : ");
	}
}
