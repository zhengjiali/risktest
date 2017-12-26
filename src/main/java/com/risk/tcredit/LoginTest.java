package com.risk.tcredit;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.*;


public class LoginTest {
	
	public String baseURI;
	
	@BeforeTest
	@Parameters("baseUrl")
	public void beforeTest(String baseUrl){
		baseURI = baseUrl;
	}
	
	@Test
	@Parameters({"username","password"})
	public void loginSucces(String username,String password){
		given()
			.log().all()
			.contentType("application/x-www-form-urlencoded; charset=UTF-8")
			.formParam("userName",username)
			.formParam("password",password)
		.when()
			.post(baseURI+"/engine/trics/doLogin")
		.then()
			.statusCode(200)
			.body("status", equalTo(0))
			.assertThat().body(matchesJsonSchemaInClasspath("login.json"));
	}
	
	
}
