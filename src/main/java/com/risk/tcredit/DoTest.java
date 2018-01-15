package com.risk.tcredit;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream; 
import java.util.Iterator;
import java.util.Properties;
import io.restassured.response.Response;
public class DoTest {	

	String risk_user;
	
	@BeforeTest
	public void beforeTest(){
		
		Properties prop = new Properties();     
        try{
            //读取属性文件a.properties
            InputStream in = new BufferedInputStream (new FileInputStream("config.properties"));
            prop.load(in);     ///加载属性列表
            risk_user = prop.getProperty("risk_user");
            baseURI = prop.getProperty("baseURI");
        }
        catch(Exception e){
            System.out.println(e);
        }
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
	
	@Test(enabled=false)
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
	@Test
	public void toDataDefinePage(){
		given()
			.log().all()
			.contentType("application/x-www-form-urlencoded; charset=UTF-8")
			.cookie("_risk_user",risk_user)
		.when()
			.get("/risk/trics/definedVariable/toDataDefinePage")
		.then()
			.statusCode(200)
			.assertThat().body(matchesJsonSchemaInClasspath("toDataDefinePage.json"));
	}

}
