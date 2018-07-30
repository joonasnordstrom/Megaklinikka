package megaklinikka;
import static spark.Spark.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;
import spark.Response;
import spark.Route;

public class Main {
    public static void main(String[] args) {
    	JsonParser parser = new JsonParser();
    	port(getHerokuAssignedPort());
    	
    	post("/hash", (request, response) -> {
    		JsonObject reqObject = null;
    		JsonObject resultObject = new JsonObject();
    		
    		// bad request
    		int responseStatus = 400;
    		String result = "Invalid request body.";
    		String property = "Error";
    		
    		response.type("application/json");
    		JsonElement reqBody = parser.parse(request.body());
    	   
    		if ( reqBody.isJsonObject() )
    		{
    		   reqObject = reqBody.getAsJsonObject();
    		   
    		   // checks if correct valid param exists in request body
    		   if ( reqObject.has("text"))
    		   {
    			   result = convertStringToSHA256(reqObject.get("text").getAsString());
    			   property = "hash";
    			   
        		   responseStatus = 200;
    		   }
    		}
    		
    		resultObject.addProperty(property, result);
	
    		response.status(responseStatus);

    		return resultObject;
    	   
//    	   return new Gson()
//    	    	      .toJson(resultObject);
    	});
    	
    	get("/hello", (req, res) -> "Hello");
    }

	static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    
    static String convertStringToSHA256(String text) {
    	MessageDigest digest = null;
    	
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
    	byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
    	
        return bytesToHexString(hash);
    }
    
    private static String bytesToHexString(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        
        for (int i = 0; i < hash.length; i++) {
	        String hex = Integer.toHexString(0xff & hash[i]);
	        if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
        }
        return hexString.toString();
    }
}

