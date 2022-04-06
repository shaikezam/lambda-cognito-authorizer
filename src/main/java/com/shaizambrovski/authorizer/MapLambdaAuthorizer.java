package com.shaizambrovski.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.shaizambrovski.repository.TenantRepository;
import com.shaizambrovski.util.JWTUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapLambdaAuthorizer implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private TenantRepository tenantRepository = new TenantRepository();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context lambdaContext) {

        LambdaLogger logger = lambdaContext.getLogger();

        Map<String, String> headers = (Map<String, String>) event.get("headers");

        logger.log("headers: " + headers);

        String authorization = headers.get("authorization");

        logger.log("authorization: " + authorization);

        String clientId = JWTUtil.getClaimValue(authorization, "client_id");

        logger.log("clientId: " + clientId);

        logger.log("arn: " + event.get("methodArn"));

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("principalId", "1234");

        Map<String, Object> policyDocument = new HashMap<String, Object>();
        policyDocument.put("Version", "2012-10-17");

        Map<String, String> statement = new HashMap<>();
        statement.put("Action", "execute-api:Invoke");

        String tenantIdAsString = (tenantRepository.getTenantIdByClientId(clientId) != null) ?
                tenantRepository.getTenantIdByClientId(clientId).toString() : null;
        statement.put("Effect", "Deny");
        if (tenantIdAsString != null) {
            statement.put("Effect", "Allow");
        }

        statement.put("Resource", (String) event.get("methodArn"));
        policyDocument.put("Statement", Arrays.asList(statement));

        response.put("policyDocument", policyDocument);

        logger.log(tenantIdAsString);

        Map<String, String> context = new HashMap<String, String>();
        context.put("tenant_id", tenantIdAsString);
        response.put("context", context);

        return response;
    }
}

/*public class MapLambdaAuthorizer implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private String lambdaArn = "arn:aws:lambda:us-east-1:325366286308:function:TestCognitoLambda";
    private TenantRepository tenantRepository = new TenantRepository();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context lambdaContext) {

        String authorizationToken = (String) event.get("Authorization");
        String resource = (String) event.get("methodArn");

        Map<String, Object> response = new HashMap<>();
        response.put("principalId", "1234");

        Map<String, Object> policyDocument = new HashMap<>();
        policyDocument.put("Version", "2012-10-17");

        Map<String, String> statement = new HashMap<>();
        statement.put("Action", "execute-api:Invoke");

        String clientId = JWTUtil.getClaimValue(authorizationToken, "client_id");

        statement.put("Effect", "Allow");

        String tenantIdAsString = (tenantRepository.getTenantIdByClientId(clientId) != null) ?
                tenantRepository.getTenantIdByClientId(clientId).toString() : null;
        if (tenantIdAsString == null) {
            statement.put("Effect", "Deny");
            response.put("tenantId", tenantIdAsString);
        }

        statement.put("Resource", resource);
        policyDocument.put("Statement", Arrays.asList(statement));

        response.put("policyDocument", policyDocument);

        Map<String, String> context = new HashMap<>();
        context.put("now", new Date().toString());
        response.put("context", context);

        return response;
    }
}*/
