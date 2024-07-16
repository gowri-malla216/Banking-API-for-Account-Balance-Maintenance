package dev.codescreen.CodeScreen_rpatlddg.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import dev.codescreen.CodeScreen_rpatlddg.dto.*;
import dev.codescreen.CodeScreen_rpatlddg.model.*;
import dev.codescreen.CodeScreen_rpatlddg.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.*;

class BalanceControllerTest {

    @InjectMocks
    private BalanceController balanceController;

    @Mock
    private AmountRepos amountRepos;

    @Mock
    private TransactionRepos transactionRepos;

    @Mock
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthorizeApproved() {
        //Setup
        AuthorizationRequest request = setupAuthorizationRequest();
        Amount amount = setupAmountEntity();

        when(amountRepos.findAll(any(Example.class))).thenReturn(List.of(amount));

        //Service Execution
        ResponseEntity<AuthorizationResponse> response = balanceController.authorize("message123", request);

        //Asserts
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthorizationResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("123e4567-e89b-12d3-a456-426655440000", body.getUserId());
        assertEquals("message123", body.getMessageId());
        assertEquals("APPROVED", body.getResponseCode());
        assertEquals("90.00", body.getBalance().getAmount());
        assertEquals("USD", body.getBalance().getCurrency());
        assertEquals("DEBIT", body.getBalance().getDebitOrCredit());
    }

    @Test
    void testAuthorizeDeclined() {
        //Setup
        AuthorizationRequest request = setupAuthorizationRequest();
        request.setTransactionAmount(new TransactionAmount("200.00", "USD", "DEBIT"));
        Amount amount = setupAmountEntity();

        when(amountRepos.findAll(any(Example.class))).thenReturn(List.of(amount));

        //Service Execution
        ResponseEntity<AuthorizationResponse> response = balanceController.authorize("message123", request);

        //Asserts
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthorizationResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("123e4567-e89b-12d3-a456-426655440000", body.getUserId());
        assertEquals("message123", body.getMessageId());
        assertEquals("DECLINED", body.getResponseCode());
        assertEquals("100.00", body.getBalance().getAmount());
        assertEquals("USD", body.getBalance().getCurrency());
        assertEquals("DEBIT", body.getBalance().getDebitOrCredit());
    }

    @Test
    public void testLoadApproved() {
        //Setup
        LoadRequest loadRequest = setupLoadRequest();
        Amount currentAmount = setupAmountEntity();

        when(amountRepos.findAll(any(Example.class))).thenReturn(List.of(currentAmount));
        //Service Execution
        ResponseEntity<LoadResponse> response = balanceController.load("message123", loadRequest);
        //Asserts
        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoadResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("123e4567-e89b-12d3-a456-426655440000", body.getUserId());
        assertEquals("message123", body.getMessageId());
        assertEquals("APPROVED", body.getResponseCode());
        assertEquals("200.00", body.getBalance().getAmount());
        assertEquals("USD", body.getBalance().getCurrency());
        assertEquals("CREDIT", body.getBalance().getDebitOrCredit());
    }


    @Test
    void testAuthorize_ExceptionHandling() throws Exception {
        //Setup
        AuthorizationRequest request = setupAuthorizationRequest();

        when(amountRepos.findAll(any(Example.class))).thenThrow(new RuntimeException("user with id " + request.getUserId() + " not found"));
        //Service Execution
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> balanceController.authorize("message123", request)
        );
        //Asserts
        assertEquals("user with id " + request.getUserId() + " not found", exception.getMessage());
    }

    @Test
    void testLoad_ExceptionHandling() throws Exception {
        //Setup
        LoadRequest request = setupLoadRequest();

        when(amountRepos.findAll(any(Example.class))).thenThrow(new RuntimeException("user with id " + request.getUserId() + " not found"));
        //Service Execution
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> balanceController.load("message123", request)
        );
        //Asserts
        assertEquals("user with id " + request.getUserId() + " not found", exception.getMessage());
    }

    //Setup helpers
    private LoadRequest setupLoadRequest() {
        LoadRequest request = new LoadRequest();
        request.setUserId("123e4567-e89b-12d3-a456-426655440000");
        request.setMessageId("message123");
        request.setTransactionAmount(new TransactionAmount("100.00", "USD", "CREDIT"));
        return request;
    }

    private AuthorizationRequest setupAuthorizationRequest() {
        AuthorizationRequest request = new AuthorizationRequest();
        request.setUserId("123e4567-e89b-12d3-a456-426655440000");
        request.setMessageId("message123");
        request.setTransactionAmount(new TransactionAmount("10.00", "USD", "DEBIT"));
        return request;
    }

    private Amount setupAmountEntity() {
        Amount amount = new Amount();
        amount.setUserId(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"));
        amount.setAmount("100.00");
        amount.setCurrency("USD");
        return amount;
    }
}
