package dev.codescreen.CodeScreen_rpatlddg.controller;

import static org.junit.jupiter.api.Assertions.*;

import dev.codescreen.CodeScreen_rpatlddg.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

@SpringBootTest
class BalanceControllerIntegrationTest {

    @Autowired
    private BalanceController balanceController;

    @Test
    void testLoadIntegration() {
        //Setup
        LoadRequest request = setupLoadRequest();

        //Service Execution
        ResponseEntity<LoadResponse> addResponse = balanceController.add("message123");
        request.setUserId(addResponse.getBody().getUserId());
        ResponseEntity<LoadResponse> response =   balanceController.load("message234", request);

        //Asserts
        assertAddResponse(addResponse);
        assertLoadResponse(response);
    }

    


    @Test
    void testAuthorizeIntegration() {
        //Setup
        LoadRequest loadRequest = setupLoadRequest();
        AuthorizationRequest request = setupAuthorizationRequest();


        //Service Execution
        ResponseEntity<LoadResponse> addResponse = balanceController.add("message123");
        loadRequest.setUserId(addResponse.getBody().getUserId());
        ResponseEntity<LoadResponse> loadResponse =   balanceController.load("message234", loadRequest);
        request.setUserId(loadResponse.getBody().getUserId());
        ResponseEntity<AuthorizationResponse> response =   balanceController.authorize("message345", request);

        // Asserts
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        assertEquals(HttpStatus.OK, loadResponse.getStatusCode());
        
        assertAddResponse(addResponse);
        assertLoadResponse(loadResponse);
        assertAuthorizationResponse(response);
    }



    //Setup helpers
    private LoadRequest setupLoadRequest() {
        LoadRequest request = new LoadRequest();
        request.setMessageId("message234");
        request.setTransactionAmount(new TransactionAmount("100.00", "USD", "CREDIT"));
        return request;
    }

    private AuthorizationRequest setupAuthorizationRequest() {
        AuthorizationRequest request = new AuthorizationRequest();
        request.setMessageId("message345");
        request.setTransactionAmount(new TransactionAmount("10.00", "USD", "DEBIT"));
        return request;
    }
    
    //Assert helpers
    private static void assertAddResponse(ResponseEntity<LoadResponse> addResponse) {
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());
        LoadResponse addResponseBody = addResponse.getBody();
        assertNotNull(addResponseBody);
        assertEquals("APPROVED", addResponseBody.getResponseCode());
        assertEquals("0.00", addResponseBody.getBalance().getAmount());
        assertEquals("CREDIT", addResponseBody.getBalance().getDebitOrCredit());
        assertEquals("message123", addResponseBody.getMessageId());
        assertEquals("USD", addResponseBody.getBalance().getCurrency());
    }

    private static void assertLoadResponse(ResponseEntity<LoadResponse> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoadResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("APPROVED", body.getResponseCode());
        assertEquals("100.00", body.getBalance().getAmount());
        assertEquals("CREDIT", body.getBalance().getDebitOrCredit());
        assertEquals("message234", body.getMessageId());
        assertEquals("USD", body.getBalance().getCurrency());
    }

    private static void assertAuthorizationResponse(ResponseEntity<AuthorizationResponse> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthorizationResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("APPROVED", body.getResponseCode());
        assertEquals("90.00", body.getBalance().getAmount());
        assertEquals("DEBIT", body.getBalance().getDebitOrCredit());
        assertEquals("message345", body.getMessageId());
        assertEquals("USD", body.getBalance().getCurrency());
    }
    
}

