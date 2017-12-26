package com.risk.tcredit;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.*;

import io.restassured.response.Response;

public class ParamTest {
	
	String risk_user;
	
	@BeforeTest
	@Parameters({"baseUrl","username","password"})
	public void beforeTest(String baseUrl,String username,String password){
				
		baseURI = baseUrl;
		Response resLogin = 
				given()
					.contentType("application/x-www-form-urlencoded;charset=UTF-8")
					.formParam("userName",username)
					.formParam("password",password)
				.when()
					.post("/risk/trics/doLogin");
		risk_user = resLogin.getCookie("_risk_user");
	}
	
	@Test(enabled=false)
	public void createParam(){
		
		Response createRes = 
				given()
					.cookie("_risk_user",risk_user)
					.formParam("funCode", 302)
					.formParam("nameEn", "test122102")
					.formParam("nameCn", "test122102")
					.formParam("dataType", 1)
					.formParam("productLine[]", 64,65)
				.when()
					.post("/riskData/trics/param/addAttr")
				.then()
					.statusCode(200)
					.body("status", equalTo(0))
				.extract()
					.response();
	}
	
	@Test
	public void doTest(){
		String str1="[{\"id\":\"7770\",\"nameEn\":\"AutoApi1\",\"dataType\":\"1\",\"value\":\"1\",\"variableType\":\"1\"},{\"id\":\"7985\",\"nameEn\":\"AutoParam50\",\"dataType\":\"1\",\"value\":\"-1\",\"variableType\":\"1\"}]";
		String str2="[{\"id\":\"2518\",\"nameEn\":\"switchswww3333\",\"dataType\":\"4\",\"expected\":\"\"}]";

				given()
					.log().all()
					.contentType("application/x-www-form-urlencoded; charset=UTF-8")
					.cookie("_risk_user",risk_user)
					.formParam("testType",1)
					.formParam("moduleId",3814)
					.formParam("variablesIn",str1)
					.formParam("variablesIn",str2)
				.when()
					.post("/risk/trics/rule/doTest")
				.then()
					.statusCode(200)
					.body("status", equalTo(0))
					.assertThat().body(matchesJsonSchemaInClasspath("doTest.json"));

	}
}
