package com.cymark.estatemanagementsystem.util;

import com.cymark.estatemanagementsystem.model.response.Response;

public class ResponseUtils {

    public static Response createDefaultSuccessResponse(){
        return new Response(0, "Successful");
    }

    public static Response createResponse(int code, String message){
        return new Response(code, message);
    }

    public static Response createSuccessResponse(String message){
        return new Response(0, message);
    }
}


