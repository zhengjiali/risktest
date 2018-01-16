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
	
	@Test
	public void toInterfaceDoc(){
		
	}
}
