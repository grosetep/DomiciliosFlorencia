package com.delivery.estrategiamovilmx.domiciliosflorencia.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by administrator on 18/08/2017.
 */
public class UserItem implements Serializable {
    @SerializedName("idUser")
    @Expose
    private String idUser;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("statusUser")
    @Expose
    private String statusUser;
    @SerializedName("profile")
    @Expose
    private String profile;
    @SerializedName("avatarPath")
    @Expose
    private String avatarPath;
    @SerializedName("avatarImage")
    @Expose
    private String avatarImage;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("token")
    @Expose
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatusUser() {
        return statusUser;
    }

    public void setStatusUser(String statusUser) {
        this.statusUser = statusUser;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserItem(String email, String password,String idToken) {
        this.email = email;
        this.password = password;
        this.token = idToken;
    }

    public UserItem() {
    }

    @Override
    public String toString() {
        return "UserItem{" +
                "idUser='" + idUser + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", statusUser='" + statusUser + '\'' +
                ", profile='" + profile + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", avatarImage='" + avatarImage + '\'' +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
