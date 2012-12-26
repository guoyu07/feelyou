package com.feelyou.util;

public class Config {
    
    /**User account info*/
    public static String account = "31495";
    /**User password info*/
    public static String password = "123";  //"87415";
    /**User phone info*/
    public static String phone = "13598083464";
    /**User usewap info. false for cmnet, true for wap*/
    public static boolean usewap;
    /**System id*/
    public static String sid = "";
    /**host*/
    public static String host = "http://220.248.185.86";
    /**port*/
    public static String port = "8000";
    /**mode for use. true - quick mode, false- trade mode*/
    public static boolean quickmode;
    /**use meeting control*/
    public static boolean meetcontrol;
	/**keep pasword*/
	public static boolean keep_pass;
	/**keep username*/
	public static boolean keep_user;
	/**first boot*/
	public static boolean first_boot;
    /**version*/
    public static String ver = "";
    
    
    /** Creates a new instance of Config */
    public Config() {
        if(account == null)
        {
            account =Const.EMPTY;
            password = Const.EMPTY;
            phone = Const.EMPTY;
            sid = Const.EMPTY;
            host = Const.EMPTY;
            usewap = true;
            
			first_boot=false;
			keep_user=true;
			keep_pass=true;
            ver = Const.EMPTY;
        }
    }
    
}
