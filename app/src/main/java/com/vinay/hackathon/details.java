package com.vinay.hackathon;

public class details {

    private String topic;
    private String message;

    public details(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }
    public details(){

    }

    public String getTopic(){
        return topic;
    }

    public String getMessage(){
        return message;
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    public void setMessage(String message){
        this.message = message;
    }

}
