package com.qa.tests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.base.TestBase;
import com.qa.client.RestClient;
import com.qa.data.Users;

public class CustomerWebLoginTest extends TestBase{
	TestBase testBase;
	String serviceUrl;
	String apiUrl;
	String url;
	RestClient restClient;
	CloseableHttpResponse closebaleHttpResponse;
	
	@BeforeMethod
	public void setUp() throws ClientProtocolException, IOException{
		testBase = new TestBase();
		serviceUrl = prop.getProperty("URL");
		apiUrl = prop.getProperty("serviceURL");

		url = serviceUrl + apiUrl;
		
	}
	
	
	@Test
	public void userLoginTest() throws JsonGenerationException, JsonMappingException, IOException{
		restClient = new RestClient();
		HashMap<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Content-Type", "application/json");

		//jackson API:
		ObjectMapper mapper = new ObjectMapper();
		Users users = new Users("test-user", "abc123"); //expected users obejct

		//object to json file:
		mapper.writeValue(new File("/Users/Joby Joseph/Documents/eclipse-workspace/jobythecoder/APIAutomationFramework/src/main/java/com/qa/data/users.json"), users);

		//java object to json in String:
		String usersJsonString = mapper.writeValueAsString(users);
		System.out.println(usersJsonString);

		closebaleHttpResponse = restClient.post(url, usersJsonString, headerMap); //call the API

		//validate response from API:
		//1. status code:
		int statusCode = closebaleHttpResponse.getStatusLine().getStatusCode();
		Assert.assertEquals(statusCode, testBase.RESPONSE_STATUS_CODE_200);

		//2. JsonString:
		String responseString = EntityUtils.toString(closebaleHttpResponse.getEntity(), "UTF-8");

		JSONObject responseJson = new JSONObject(responseString);
		System.out.println("The response from API is:"+ responseJson);

		//json to java object:
		Users usersResObj = mapper.readValue(responseString, Users.class); //actual users object
		System.out.println(usersResObj);

		Assert.assertTrue(users.getUsername().equals(usersResObj.getUsername()));

		Assert.assertTrue(users.getPassword().equals(usersResObj.getPassword()));


	}

}
