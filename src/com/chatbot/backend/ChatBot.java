package com.chatbot.backend;
import okhttp3.*;
import java.io.FileInputStream;
import java.util.*;
import org.json.*;

public class ChatBot {

    public static void main(String[] args) throws Exception {
        
         Properties props = new Properties();
         props.load(new FileInputStream("chatbot.properties"));

         String OPEN_AI_KEY = props.getProperty("OPEN_AI_KEY");

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        Scanner sc = new Scanner(System.in);

        JSONArray history = new JSONArray();

    
        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content", "You are a helpful assistant. When the user requests reasoning, explain step by step clearly.");
        history.put(system);

        while (true) {

            System.out.print("\nYou: ");
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("exit"))
                break;

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", input);
            history.put(userMsg);

         
            JSONObject payload = new JSONObject();
            payload.put("model", "gpt-4o-mini");
            payload.put("messages", history);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    payload.toString()
            );

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + OPEN_AI_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String res = response.body().string();

            JSONObject obj = new JSONObject(res);
            String reply = obj
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

  
            System.out.println("AI: " + reply);

            JSONObject botMsg = new JSONObject();
            botMsg.put("role", "assistant");
            botMsg.put("content", reply);
            history.put(botMsg);
        }
    }
}