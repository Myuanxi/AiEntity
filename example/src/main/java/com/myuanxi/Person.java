package com.myuanxi;

import com.myuanxi.AiEntity;
import com.myuanxi.AiField;

@AiEntity(
    model = "${OPENAI_MODEL:gpt-3.5-turbo}",
    url = "${OPENAI_API_URL:https://api.openai.com/v1/chat/completions}",
    apiKey = "${OPENAI_API_KEY}"
)
public class Person {
    @AiField(description = "人的名字，2-4个汉字")
    private String name;
    
    @AiField(description = "年龄，范围在18-100之间")
    private int age;
    
    @AiField(description = "职业描述，例如：工程师、医生、教师等")
    private String occupation;
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", occupation='" + occupation + '\'' +
                '}';
    }
} 