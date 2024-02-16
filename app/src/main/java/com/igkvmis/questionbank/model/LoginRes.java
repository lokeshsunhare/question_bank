
package com.igkvmis.questionbank.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRes implements Serializable
{

    @SerializedName("Response")
    @Expose
    private List<Response> response = null;
    private final static long serialVersionUID = -6402670507984179347L;

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }

}
