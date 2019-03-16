package com.cc.core.wechat.model.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Friend implements Serializable {
    @SerializedName(value = "wechatId", alternate = "field_username")
    private String wechatId;
    // 微信号
    @SerializedName(value = "alias", alternate = "field_alias")
    private String alias;
    @SerializedName(value = "nickname", alternate = "field_nickname")
    private String nickname;
    @SerializedName(value = "remark", alternate = "field_conRemark")
    private String remark;
    @SerializedName("sex")
    private String sex;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("signature")
    private String signature;
    @SerializedName(value = "region", alternate = "cvt")
    private String region;

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
