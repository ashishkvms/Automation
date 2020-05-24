# Android App Automation

# 1. ASSUMPTIONS:
    # app is pre-installed in the device
    # appium is pre-installed into the sysmtem using npm command
    # JAVA is pre-installed into the system
    # environment path variables like JAVA_HOME, ANDROID_HOME is configured
  
# 2. Features included:
     # Extent Reporter for reporting
     # log4j2 for logging server/application logs
     # maven as a build tool
     # testNG for test execution
     # ThreadLocal for achieving parallel execution
     # Video Recording of complete test execution is present in video folder.
 
# 3. Steps to execute test
    # clone the maven project
    # run as maven install
    # update the config.properties file as per your system configuration
    # add/uncomment the no. of test a/c to the no. of device attached in testng.xml file, for e.g. add two test if two devices are attached
    # update the device name, udid in the testng.xml file
    # run the testng.xml file for test execution.
    # open Extent.html for reports
    # open videos folder to see the executed test video recording.
    
 
