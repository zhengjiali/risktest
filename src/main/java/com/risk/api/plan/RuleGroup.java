package com.risk.api.plan;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import io.restassured.response.Response;

public class RuleGroup {

	String risk_user;
	
	@BeforeTest
	public void beforeTest(){
		
		Properties prop = new Properties();     
        try{
            //读取属性文件a.properties
            InputStream in = new BufferedInputStream (new FileInputStream("config.properties"));
            prop.load(in);     ///加载属性列表
            risk_user = prop.getProperty("risk_user");
            baseURI = prop.getProperty("baseURI")+"/risk";
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
	
	@Test(priority=1)
	public void toQuery(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
		.when()
			.get("/trics/ruleGroup/toQuery")
		.then()
			.statusCode(200)
			.body("statusList.name", hasItems("可编辑","已提交"))
			.body("ruleGroupTypeList.name", hasItems("串行规则集","并行规则集"))
			.body("productLineList.name", hasItems("水果","蔬菜","果蔬"))
			.body("userName", equalTo("tomato"))
			.body("companyId", equalTo(17))
			.assertThat().body(matchesJsonSchemaInClasspath("ruleGroupQuery.json"));
	}
	
	@Test(priority=1)
	public void query(){
		given()
		.log().all()
		.cookie("_risk_user",risk_user)
		.formParam("current", 1)
		.formParam("sortType", "desc")
	.when()
		.get("/trics/ruleGroup/query")
	.then()
		.statusCode(200)
		.body("status", equalTo(0))
		.body("limit", equalTo(10))
		.assertThat().body(matchesJsonSchemaInClasspath("query.json"));
	}
	
	@Test(priority=1)
	public void queryLimit20(){
		given()
		.log().all()
		.cookie("_risk_user",risk_user)
		.formParam("current", 1)
		.formParam("sortType", "desc")
		.formParam("limit", 20)
	.when()
		.get("/trics/ruleGroup/query")
	.then()
		.statusCode(200)
		.body("status", equalTo(0))
		.body("limit", equalTo(20))
		.assertThat().body(matchesJsonSchemaInClasspath("query.json"));
	}
	
	@Test(priority=2)
	public void toRuleSetAttrPage(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
		.when()
			.get("/trics/ruleGroup/toRuleSetAttrPage")
		.then()
			.statusCode(200)
			.body("ruleGroupTypeList.name", hasItems("串行规则集","并行规则集"))
			.body("productLineList.name", hasItems("水果","蔬菜","果蔬"))
			.body("userName", equalTo("tomato"))
			.body("companyId", equalTo(17))
			.assertThat().body(matchesJsonSchemaInClasspath("toRuleSetAttrPage.json"));
	}
	
	@Test(priority=3)
	public void nextNullParam(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
		.when()
			.get("/trics/ruleGroup/next")
		.then()
			.statusCode(200)
			.body("status", equalTo(-1));
	}
	
	@Test(priority=3)
	public void next(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.formParam(parameterName, parameterValues)
		.when()
			.get("/trics/ruleGroup/next")
		.then()
			.statusCode(200)
			.body("status", equalTo(0));
	}
	
	
	
	
	
}
