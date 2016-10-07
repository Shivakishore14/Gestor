package com.sksp.gestor;

/**
 * Created by root on 23/9/16.
 */
public class ListModel {

    private  String SysName="";
    private  String Image="";
    private  String Ip="";

    /*********** Set Methods ******************/

    public void setSysName(String n)
    {
        this.SysName = n;
    }

    public void setImage(String Image)
    {
        this.Image = Image;
    }

    public void setIp(String Url)
    {
        this.Ip = Url;
    }

    /*********** Get Methods ****************/

    public String getSysName()
    {
        return this.SysName;
    }

    public String getIp()
    {
        return this.Ip;
    }

    public String getImage()
    {
        return this.Image  ;
    }
}