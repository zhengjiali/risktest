package com.risk.api.plan;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import io.restassured.response.Response;

public class PlanRun {

	String risk_user;
	Integer language;
	String preference;
	
	@BeforeTest
	public void beforeTest(){
		
		Properties prop = new Properties();     
        try{
            //读取属性文件a.properties
            InputStream in = new BufferedInputStream (new FileInputStream("config.properties"));
            prop.load(in);     ///加载属性列表
            risk_user = prop.getProperty("risk_user");
            baseURI = prop.getProperty("baseURI");
            language = Integer.valueOf(prop.getProperty("language")).intValue();
            preference = prop.getProperty("preference");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
	
	
	@Test
	public void toQuery(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
		.when()
			.get("/risk/trics/planRun/toQuery")
		.then()
			.statusCode(200)
			.body("language", equalTo(language));
	}
}
