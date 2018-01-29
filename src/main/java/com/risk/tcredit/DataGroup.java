package com.risk.tcredit;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.path.json.JsonPath.from;
import static io.restassured.matcher.RestAssuredMatchers.*;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.*;
import java.util.*;
import java.util.Properties;
import io.restassured.response.Response;

public class DataGroup {
	
String risk_user;
String name;
	
	@BeforeTest
	public void beforeTest(){
		SimpleDateFormat ft = new SimpleDateFormat ("yyMMddhhmmss");
		name = "AutoGroupName"+ft.format(new Date());
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
	
	@Test(priority=1)
	public void createGroup(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.formParam("funcode", 604)
			.formParam("productLine[]", 64,67)
			.formParam("dataGroupName", name)
		.when()
			.post("/riskData/trics/dataGroup/addNext")
		.then()
			.statusCode(200)
			.body("status" , equalTo(0))
			.assertThat().body(matchesJsonSchemaInClasspath("createGroup.json"));
	}
	
	@Test(enabled=false)
	public void checkDataGroupName(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.formParam("dataGroupName", "水果之王水果之王水果之王123")
			.formParam("productLine[]",65,66)
			.formParam("id", 51)
		.when()
			.get("/riskData/trics/dataGroup/checkDataGroupName")
		.then()
			.statusCode(200)
			.body("status", equalTo(-1))
			.assertThat().body(matchesJsonSchemaInClasspath("checkDataGroupName.json"));
	}
	
	@Test(enabled=false)
	public void checkGroupName(){
		Response resp = 
				given()
					.log().all()
					.cookie("_risk_user",risk_user)
					.formParam("dataGroupName", "Group1001")
					.formParam("productLine[]", 64)
				.when()
					.get("/riskData/trics/dataGroup/checkDataGroupName");
		resp.getBody().prettyPrint();
		resp.then()
			.statusCode(200)
			.body("status", equalTo(-1))
			.assertThat().body(matchesJsonSchemaInClasspath("checkDataGroupName.json"));			
	}
	
	@Test(priority=3)
	public void checkData(){
		Response resp = 
				given()
					.log().all()
					.cookie("_risk_user",risk_user)
					.formParam("dataGroupName", name)
					.formParam("productLine[]", 64)
					.formParam("id", 51)
				.when()
					.get("/riskData/trics/dataGroup/checkDataGroupName");
		resp.getBody().prettyPrint();
		resp.then()
			.statusCode(200)
			.body("status", equalTo(-1))
			.assertThat().body(matchesJsonSchemaInClasspath("checkDataGroupName.json"));			
	}
	
	@Test(priority=4)
	public void checkDataGroupNo(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.formParam("checkDataGroupNo", "SJZ18Ez52a64h")
			.formParam("productLine[]",64)
		.when()
			.get("/riskData/trics/dataGroup/checkDataGroupNo")
		.then()
			.statusCode(200)
			.body("status", equalTo(-1))
			.assertThat().body(matchesJsonSchemaInClasspath("checkDataGroupName.json"));
	}
	
	@Test(priority=5)
	public void submit(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.formParam("funcode", 603)
			.formParam("id",120)
		.when()
			.get("/riskData/trics/dataGroup/submit")
		.then()
			.statusCode(200)
			.body("status", equalTo(-1))
			.assertThat().body(matchesJsonSchemaInClasspath("submit.json"));
	}
	
	@Test(priority=6)
	public void checkDataGroupNoSuc(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.formParam("dataGroupNo", "SJZ18Ez52a64h_")
		.when()
			.get("/riskData/trics/dataGroup/checkDataGroupNo")
		.then()
			.statusCode(200)
			.body("status", equalTo(0))
			.assertThat().body(matchesJsonSchemaInClasspath("checkDataGroupName.json"));
	}
	
}
