package com.shakil.aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.shakil.aws.model.Candidate;

public class CandidateLambdaHandler implements RequestStreamHandler {

	private String DYNAMO_TABLE = "Candidates";

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JSONParser parser = new JSONParser();
		JSONObject responseObject = new JSONObject();
		JSONObject responseBody = new JSONObject();

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
		DynamoDB dynamoDB = new DynamoDB(client);

		int id;
		Item resItem = null;

		try {
			JSONObject reqObject = (JSONObject) parser.parse(reader);
			if (reqObject.get("pathParameters") != null) {
				JSONObject pps = (JSONObject) reqObject.get("pathParameters");
				if (pps.get("id") != null) {
					id = Integer.parseInt((String) pps.get("id"));
					resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("id", id);
				}
			} else if (reqObject.get("queryStringParameters") != null) {
				JSONObject qps = (JSONObject) reqObject.get("queryStringParameters");
				if (qps.get("id") != null) {
					id = Integer.parseInt((String) qps.get("id"));
					resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("id", id);
				}
			}

			if (resItem != null) {
				Candidate candidate = new Candidate(resItem.toJSON());
				responseBody.put("candidate", candidate);
				responseObject.put("statusCode", 200);
			} else {
				responseBody.put("message", "No candidates found");
				responseObject.put("statusCode", 404);
			}

			responseObject.put("body", responseBody.toString());

		} catch (Exception e) {
			context.getLogger().log("ERROR : " + e.getMessage());
		}

		writer.write(responseObject.toString());
		reader.close();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	public void handlePutRequest(InputStream input, OutputStream output, Context context) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JSONParser parser = new JSONParser();
		JSONObject responseObject = new JSONObject();
		JSONObject responseBody = new JSONObject();

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
		DynamoDB dynamoDB = new DynamoDB(client);

		try {
			JSONObject reqObject = (JSONObject) parser.parse(reader);

			if (reqObject.get("body") != null) {
				Candidate candidate = new Candidate((String) reqObject.get("body"));
				dynamoDB.getTable(DYNAMO_TABLE)
						.putItem(new PutItemSpec().withItem(new Item().withNumber("id", candidate.getId())
								.withString("name", candidate.getName()).withString("party", candidate.getParty())));
				responseBody.put("message", "New Candidate created");
				responseObject.put("statusCode", 200);
				responseObject.put("body", responseBody.toString());
			}
		} catch (Exception e) {
			responseObject.put("statusCode", 400);
			responseObject.put("error", e);
		}
		writer.write(responseObject.toString());
		reader.close();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	public void handleDeleteRequest(InputStream input, OutputStream output, Context context) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JSONParser parser = new JSONParser();
		JSONObject responseObject = new JSONObject();
		JSONObject responseBody = new JSONObject();

		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
		DynamoDB dynamoDB = new DynamoDB(client);

		try {
			JSONObject reqObject = (JSONObject) parser.parse(reader);
			if (reqObject.get("pathParameters") != null) {
				JSONObject pps = (JSONObject) reqObject.get("pathParameters");
				if (pps.get("id") != null) {
					int id = Integer.parseInt((String) pps.get("id"));
					dynamoDB.getTable(DYNAMO_TABLE).deleteItem("id", id);
				}
			}
			responseBody.put("message", "Candidate deleted");
			responseObject.put("statusCode", 200);
			responseObject.put("body", responseBody.toString());
		} catch (Exception e) {
			responseObject.put("statusCode", 400);
			responseObject.put("error", e);
		}
		writer.write(responseObject.toString());
		reader.close();
		writer.close();
	}
}
