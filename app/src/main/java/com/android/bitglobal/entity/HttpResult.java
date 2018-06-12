package com.android.bitglobal.entity;
/**
 * xiezuofei
 * 2016-08-02 13:20
 * 793169940@qq.com
 */
public class HttpResult<T> {

    private ResMsg resMsg;
    //用来模仿Data
    private T datas;

    public ResMsg getResMsg() {
        return resMsg;
    }

    public void setResMsg(ResMsg resMsg) {
        this.resMsg = resMsg;
    }

    public T getDatas() {
        return datas;
    }

    public void setDatas(T datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("");
        if (null != resMsg) {
            sb.append(" resMsg:" + resMsg.toString());
        }
        if (null != datas) {
            sb.append(" datas:" + datas.toString());
        }
        return sb.toString();
    }
}
