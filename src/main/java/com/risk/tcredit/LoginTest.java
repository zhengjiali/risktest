package com.risk.tcredit;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream; 
import java.util.Iterator;
import java.util.Properties; 

public class LoginTest {
	
	String risk_user;
	
	@Test
	@Parameters({"baseUrl","username","password"})
	public void loginSucces(String baseUrl,String username,String password){
		baseURI=baseUrl;
		Response resLogin=
		given()
			.log().all()
			.contentType("application/x-www-form-urlencoded; charset=UTF-8")
			.formParam("userName",username)
			.formParam("password",password)
		.when()
			.post("/risk/trics/doLogin");
		resLogin.then()
				.statusCode(200)
				.body("status", equalTo(0))
				.assertThat().body(matchesJsonSchemaInClasspath("login.json"));
		risk_user = resLogin.getCookie("_risk_user");
	}
	
	@AfterTest
	public void afterTest(){
		Properties prop = new Properties();
		try{
			FileOutputStream oFile = new FileOutputStream("config.properties", true);//true表示追加打开
            prop.setProperty("risk_user", risk_user);
            prop.setProperty("baseURI", baseURI);
            prop.store(oFile, "The New properties file");
            oFile.close();
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
	
}
