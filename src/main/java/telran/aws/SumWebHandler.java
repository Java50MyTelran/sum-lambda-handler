package telran.aws;

import java.io.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class SumWebHandler implements RequestStreamHandler {

	private static final Object OP1 = "op1";
	private static final Object OP2 = "op2";
	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JSONParser parser = new JSONParser();
		Map<String, Object> inputMap = null;
		String response = null;
		LambdaLogger logger = context.getLogger();
		try {
			inputMap = (Map<String, Object>) parser.parse(reader);
			Map<String, Object> parametersMap = (Map<String, Object>) inputMap.get("pathParameters");
			if (parametersMap == null) {
				throw new Exception("no parameters found");
			}
			String op1 = (String) parametersMap.get(OP1);
			if (op1 == null) {
				throw new Exception("no OP1 parameter found");
				
			}
			String op2 = (String) parametersMap.get(OP2);
			if (op2 == null) {
				throw new Exception("no OP2 parameter found");
				
			}
			double number1 = Double.parseDouble(op1);
			double number2 = Double.parseDouble(op2);
			response = createResponse("" + (number1 + number2), 200);
			
		} catch (Exception e) {
			String message = e.getMessage();
			response = createResponse(message, 400);
			logger.log("ERROR: " + message);
		}
		PrintStream printStream = new PrintStream(output);
		printStream.println(response);
		printStream.close();
		

	}
	String createResponse(String body, int status) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> headers = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		if(status < 400) {
			headers.put("Content-Type", "application/json");
			data.put("result", body);
			body = JSONObject.toJSONString(data);
			
		} 
			map.put("body", body);
		
		map.put("headers", headers);
		map.put("statusCode", status);
		return JSONObject.toJSONString(map);
	}

}
