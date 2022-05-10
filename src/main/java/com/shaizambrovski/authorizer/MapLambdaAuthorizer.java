package com.shaizambrovski.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.shaizambrovski.util.JWTUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapLambdaAuthorizer implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context lambdaContext) {

        LambdaLogger logger = lambdaContext.getLogger();

        logger.log("event: " + event);

        String authorization = (String) event.get("authorizationToken");

        logger.log("authorization: " + authorization);

        String clientId = JWTUtil.getClaimValue(authorization, "client_id");

        logger.log("clientId: " + clientId);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("principalId", "1234");

        Map<String, Object> policyDocument = new HashMap<String, Object>();
        policyDocument.put("Version", "2012-10-17");

        Map<String, String> statement = new HashMap<>();
        statement.put("Action", "execute-api:Invoke");

        statement.put("Effect", "Deny");
        if(clientId != null) {
            statement.put("Effect", "Allow");
        }


        statement.put("Resource", (String) event.get("methodArn"));
        policyDocument.put("Statement", Arrays.asList(statement));

        response.put("policyDocument", policyDocument);


        return response;
    }
}