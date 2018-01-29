package com.risk.tcredit;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties; 

public class LoginTest {
	
	String risk_user;
	String preference;
	static String language;
	
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
				.body("status", equalTo(0));
//				.assertThat().body(matchesJsonSchemaInClasspath("login.json"));
		risk_user = resLogin.getCookie("_risk_user");
		preference = resLogin.getCookie("_preference");
		System.out.println(preference);
	}
	
	public  static void checkPreference(String str){
		byte[] bytes = Base64.decode(str);
		if(bytes ==null||bytes.length == 0){
			System.out.println("cookie为空，生成默认cookie");
			return ;
		}
		String str2 = "";
		try {
			str2 = new String(Base64.decode(str),"utf-8");
			System.out.println(str2);
		} catch (UnsupportedEncodingException e) {
			System.out.println("cookie解码失败，生成默认cookie");
		}
		String [] strs = str2.split("&");
		String jsonStr = strs[0];
		JSONObject json = JSONObject.parseObject(jsonStr);
		language = json.get("language").toString();
		System.out.println(language);
	}
	
	@AfterTest
	public void afterTest(){
		Properties prop = new Properties();
		try{
			checkPreference(preference);
			FileOutputStream oFile = new FileOutputStream("config.properties");//true表示追加打开
            prop.setProperty("risk_user", risk_user);
            prop.setProperty("preference", preference);
            prop.setProperty("language", language);
            prop.setProperty("baseURI", baseURI);
            prop.store(oFile, "The New properties file");
            oFile.close();
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
	
}
