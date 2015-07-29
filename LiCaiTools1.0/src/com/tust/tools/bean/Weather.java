package com.tust.tools.bean;

public class Weather {
	private String loaction;//位置
    private String day;//日期
    private String week;//周
    private String Temp;//温度
    private String img1;//图片1
    private String img2;//图片2
    private String condition;//天气
    private String wind;//风力
    private String chuanyi;//穿衣
    private String chenlian;//晨练
	public String getLoaction() {
		return loaction;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getTemp() {
		return Temp;
	}
	public void setTemp(String temp) {
		Temp = temp;
	}
	public String getImg1() {
		return img1;
	}
	public void setImg1(String img1) {
		this.img1 = img1;
	}
	public String getImg2() {
		return img2;
	}
	public void setImg2(String img2) {
		this.img2 = img2;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	public String getChuanyi() {
		return chuanyi;
	}
	public void setChuanyi(String chuanyi) {
		this.chuanyi = chuanyi;
	}
	public String getChenlian() {
		return chenlian;
	}
	public void setChenlian(String chenlian) {
		this.chenlian = chenlian;
	}
	public void setLoaction(String loaction) {
		this.loaction = loaction;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
    
}
