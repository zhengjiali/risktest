package com.risk.api.plan;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class RuleGroup {

	String risk_user;
	String name;
	Integer language;
	String preference;
	int productLine=64;
	Map<String,Object> map = new HashMap<String,Object>();
	
	
	@BeforeTest
	public void beforeTest(){
		
		Properties prop = new Properties();  
		SimpleDateFormat ft = new SimpleDateFormat ("yyMMddhhmmss");
		name = "AutoRuleGroup"+ft.format(new Date());
        try{
            //读取属性文件a.properties
            InputStream in = new BufferedInputStream (new FileInputStream("config.properties"));
            prop.load(in);     ///加载属性列表
            risk_user = prop.getProperty("risk_user");
            baseURI = prop.getProperty("baseURI")+"/risk";
            language = Integer.valueOf(prop.getProperty("language")).intValue();
            preference = prop.getProperty("preference");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
	
	@Test(priority=1,enabled=true)
	public void toQuery(){
		given()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.log().all()
		.when()
			.get("/trics/ruleGroup/toQuery")
		.then()
			.statusCode(200)
			.body("statusList.name", hasItems("可编辑","已提交"))
			.body("ruleGroupTypeList.name", hasItems("串行规则集","并行规则集"))
			.body("productLineList.name", hasItems("水果","蔬菜","果蔬"))
			.body("userName", equalTo("tomatoooo"))
			.body("companyId", equalTo(17))
			.assertThat().body(matchesJsonSchemaInClasspath("RG_ruleGroupQuery.json"));
	}
	
	@Test(priority=1,enabled=true)
	public void query(){
		given()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.log().all()
			.formParam("current", 1)
			.formParam("sortType", "desc")
		.when()
			.get("/trics/ruleGroup/query")
		.then()
			.statusCode(200)
			.body("status", equalTo(0))
			.body("data.limit", equalTo(10))
			.assertThat().body(matchesJsonSchemaInClasspath("RG_query.json"));
	}
	
	@Test(priority=1,enabled=true)
	public void queryLimit20(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.formParam("current", 1)
			.formParam("sortType", "desc")
			.formParam("limit", 20)
		.when()
			.get("/trics/ruleGroup/query")
		.then()
			.statusCode(200)
			.body("status", equalTo(0))
			.body("data.limit", equalTo(20))
			.assertThat().body(matchesJsonSchemaInClasspath("query.json"));
	}
	
	@Test(priority=2,enabled=true)
	public void toRuleSetAttrPage(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
		.when()
			.get("/trics/ruleGroup/toRuleSetAttrPage")
		.then()
			.statusCode(200)
			.body("ruleGroupTypeList.name", hasItems("串行规则集","并行规则集"))
			.body("productLineList.name", hasItems("水果","蔬菜","果蔬"))
			.body("userName", equalTo("tomato"))
			.body("companyId", equalTo(17))
			.assertThat().body(matchesJsonSchemaInClasspath("RG_toRuleSetAttrPage.json"));
	}
	
	@Test(priority=3,enabled=true)
	public void nextNullParam(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
		.when()
			.get("/trics/ruleGroup/next")
		.then()
			.statusCode(200)
			.body("status", equalTo(-1));
	}
	
	@Test(priority=4)
	public void next(){
		int moduleId = 
				given()
					.log().all()
					.cookie("_risk_user",risk_user)
					.cookie("_preference",preference)
					.formParam("ruleGroupType", 1)
					.formParam("nameCn", name)
					.formParam("productLine[]", productLine)
				.when()
					.post("/trics/ruleGroup/next")
				.then()
					.statusCode(200)
					.body("status", equalTo(0))
				.extract()
					.path("data.moduleId");
		map.put("ruleGroupId", moduleId);
		
	}
	
	@Test(priority=5,enabled=true)
	public void nextVerify(){
		given()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.log().all()
			.formParam("current", 1)
			.formParam("sortType", "desc")
		.when()
			.get("/trics/ruleGroup/query")
		.then()
			.statusCode(200)
			.body("data.list.nameCn", hasItem(name))
			.body("data.list.id", hasItem(map.get("ruleGroupId")));
	}
	
	@Test(priority=6,enabled=true)
	public void submitFail(){
			
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.formParam("moduleId", map.get("ruleGroupId"))
		.when()
			.post("/trics/ruleGroup/submit")
		.then()
			.statusCode(200)
			.body("status", equalTo(-1));
	}
	
	
	@Test(priority=6)
	public void queryRule(){
		Response response = 
				given()
					.log().all()
					.cookie("_risk_user",risk_user)
					.cookie("_preference",preference)
					.formParam("current", 1)
					.formParam("productLine[]", productLine)
				.when()
					.get("/trics/ruleGroup/queryRule")
				.then()
					.statusCode(200)
					.body("status", equalTo(0))
					.body("message", equalTo("查询规则列表成功"))
				.extract()
					.response();
		int length = response.path("data.count");
		List<Integer> ruleIds = response.path("data.list.id");
		
		if(!ruleIds.isEmpty()){
			map.put("ruleId1", ruleIds.get(1));
			String rule1 = "{\"id\":0,\"copyId\":"+ruleIds.get(1).toString()+",\"selectExpression\":\"1\"}";
			if(ruleIds.size()>1){
				map.put("ruleId2", ruleIds.get(2));
				String rule2 = "{\"id\":0,\"copyId\":"+ruleIds.get(2).toString()+",\"selectExpression\":\"1\"}";
				String rule = "["+rule1+","+rule2+"]";
				System.out.println(rule);
				map.put("ruleString", rule);
			}
			else{
				String rule = "["+rule1+"]";
				System.out.println(rule);
				map.put("ruleString", rule);
				
			}

		}
		System.out.println("-------------- ruleIds are -------------");
		System.out.println(ruleIds.toString());
		System.out.println(map.get("ruleString"));
	}
	
	@Test(priority=7)
	public void save(){
			
		System.out.println("-----------save-----------");
		System.out.println(map.get("ruleString"));


		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.formParam("rootModuleId", map.get("ruleGroupId"))
			.formParam("moduleId", map.get("ruleGroupId"))
			.formParam("rules",map.get("ruleString"))
		.when()
			.post("/trics/ruleGroup/save")
		.then()
			.statusCode(200)
			.body("status", equalTo(0));
		
	}
	
	@Test(priority=8)
	public void listRules(){
			
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.formParam("moduleId", map.get("ruleGroupId"))
		.when()
			.get("/trics/ruleGroup/listRules")
		.then()
			.statusCode(200)
			.body("status", equalTo(0))
			.assertThat().body(matchesJsonSchemaInClasspath("RG_listRules.json"));
		
		
	}
	
	@Test(priority=9,enabled=true)
	public void submit(){
			
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.formParam("moduleId", map.get("ruleGroupId"))
		.when()
			.post("/trics/ruleGroup/submit")
		.then()
			.statusCode(200)
			.body("status", equalTo(0));
	}
	
	@Test(priority=10,enabled=true)
	public void submitVerify(){
		given()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.log().all()
			.formParam("current", 1)
			.formParam("sortType", "desc")
			.formParam("status[]", 2)
		.when()
			.get("/trics/ruleGroup/query")
		.then()
			.statusCode(200)
			.body("data.list.nameCn", hasItem(name))
			.body("data.list.id", hasItem(map.get("ruleGroupId")));
	}
	
	@Test(priority=10)
	public void submitDelete(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.formParam("moduleId", map.get("ruleGroupId"))
		.when()
			.post("/trics/ruleGroup/delete")
		.then()
			.statusCode(200)
			.body("status", equalTo(-1));
	}
	
	@Test(priority=11)
	public void withdraw(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.formParam("moduleId", map.get("ruleGroupId"))
		.when()
			.post("/trics/ruleGroup/withdraw")
		.then()
			.statusCode(200)
			.body("status", equalTo(0));
		
	}
	
	@Test(priority=12,enabled=true)
	public void withdrawVerify(){
		given()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.log().all()
			.formParam("current", 1)
			.formParam("sortType", "desc")
			.formParam("status[]", 1)
		.when()
			.get("/trics/ruleGroup/query")
		.then()
			.statusCode(200)
			.body("data.list.nameCn", hasItem(name))
			.body("data.list.id", hasItem(map.get("ruleGroupId")));
	}
	
	@Test(priority=13)
	public void delete(){
		given()
			.log().all()
			.cookie("_risk_user",risk_user)
			.cookie("_preference",preference)
			.formParam("moduleId", map.get("ruleGroupId"))
		.when()
			.post("/trics/ruleGroup/delete")
		.then()
			.statusCode(200)
			.body("status", equalTo(0));
	}
	
	@Test(priority=14)
	public void deleteVerify(){
		
	}
	
	
	
	
	
}
