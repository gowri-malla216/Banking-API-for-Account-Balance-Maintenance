package dev.codescreen.CodeScreen_rpatlddg.controller;

import dev.codescreen.CodeScreen_rpatlddg.dto.*;
import dev.codescreen.CodeScreen_rpatlddg.model.*;
import dev.codescreen.CodeScreen_rpatlddg.repository.AmountRepos;
import dev.codescreen.CodeScreen_rpatlddg.repository.TransactionRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BalanceController {

    @Autowired
    private AmountRepos amountRepos;
    @Autowired
    private TransactionRepos transactionRepos;

    @PutMapping("/authorization/{messageId}")
    public ResponseEntity<AuthorizationResponse> authorize(@PathVariable String messageId, @RequestBody AuthorizationRequest authorizationRequest) {
        if(!authorizationRequest.getMessageId().equals(messageId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (authorizationRequest.getTransactionAmount().getDebitOrCredit().equals("CREDIT")){
            throw new RuntimeException("AUTHORIZATION transaction amount cannot have credit.");
        }

        Amount exampleEntity = new Amount();
        exampleEntity.setUserId(UUID.fromString(authorizationRequest.getUserId()));

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Amount> example = Example.of(exampleEntity, matcher);
        List<Amount> amounts = amountRepos.findAll(example);
        if(!amounts.isEmpty()) {
            TransactionAmount transactionAmount = authorizationRequest.getTransactionAmount();
            Amount currentAmount =amounts.get(0);

            BigDecimal decimalRequestAmount = new BigDecimal(transactionAmount.getAmount());
            BigDecimal decimalCurrentAmount = new BigDecimal(currentAmount.getAmount());
            if(decimalRequestAmount.compareTo(decimalCurrentAmount) <= 0 && currentAmount.getCurrency().equals(transactionAmount.getCurrency())) {
                currentAmount.setAmount(decimalCurrentAmount.subtract(decimalRequestAmount).toString());
                amountRepos.save(currentAmount);
                Transaction transaction = mapDto(currentAmount,transactionAmount, "APPROVED");
                AuthorizationResponse authorizationResponse = getAuthorizationResponse(authorizationRequest, transaction, currentAmount, transactionAmount, "APPROVED");
                return ResponseEntity.ok(authorizationResponse);
            }
            else {
                Transaction transaction = mapDto(currentAmount,transactionAmount, "DECLINED");
                AuthorizationResponse authorizationResponse = getAuthorizationResponse(authorizationRequest, transaction, currentAmount, transactionAmount, "DECLINED");
                return ResponseEntity.ok(authorizationResponse);
            }

        }
        throw new RuntimeException("user with id " + authorizationRequest.getUserId() + " not found");

    }



    @PutMapping("/load/{messageId}")
    public ResponseEntity<LoadResponse> load(@PathVariable String messageId, @RequestBody LoadRequest loadRequest) {

        if(!loadRequest.getMessageId().equals(messageId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (loadRequest.getTransactionAmount().getDebitOrCredit().equals("DEBIT")){
            throw new RuntimeException("Load transaction amount cannot have debit.");
        }

        Amount exampleEntity = new Amount();
        exampleEntity.setUserId(UUID.fromString(loadRequest.getUserId()));

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Amount> example = Example.of(exampleEntity, matcher);
        List<Amount> amounts = amountRepos.findAll(example);
        TransactionAmount requestAmount = loadRequest.getTransactionAmount();
        if(!amounts.isEmpty()) {
            Amount currentAmount = amounts.get(0);
            BigDecimal decimalRequestAmount = new BigDecimal(requestAmount.getAmount());
            BigDecimal decimalCurrentAmount = new BigDecimal(currentAmount.getAmount());
            currentAmount.setAmount(decimalCurrentAmount.add(decimalRequestAmount).toString());
            amountRepos.save(currentAmount);
            Transaction transaction = mapDto(currentAmount, requestAmount, "APPROVED");
            transactionRepos.save(transaction);
            LoadResponse loadResponse = new LoadResponse();
            loadResponse.setUserId(loadRequest.getUserId());
            loadResponse.setMessageId(loadRequest.getMessageId());
            requestAmount.setAmount(currentAmount.getAmount());
            loadResponse.setBalance(requestAmount);
            loadResponse.setResponseCode("APPROVED");
            return ResponseEntity.ok(loadResponse);
        }
        throw new RuntimeException("user with id " + loadRequest.getUserId() + " not found");

    }

    @PutMapping("/add/{messageId}")
    public ResponseEntity<LoadResponse> add(@PathVariable String messageId) {
        if (messageId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Amount amount = new Amount();
        amount.setAmount("0.00");
        amount.setCurrency("USD");
        amountRepos.save(amount);
        LoadResponse loadResponse = new LoadResponse();
        TransactionAmount transactionAmount = new TransactionAmount("0.00", "USD", "CREDIT");
        loadResponse.setBalance(transactionAmount);
        loadResponse.setResponseCode("APPROVED");
        loadResponse.setMessageId(messageId);
        loadResponse.setUserId(String.valueOf(amount.getUserId()));
        return ResponseEntity.ok(loadResponse);

    }


    @GetMapping("/ping")
    public ResponseEntity<Ping> ping() {
        Ping ping = new Ping();
        ping.setServerTime(String.valueOf(LocalDateTime.now()));
        return ResponseEntity.ok(ping);
    }

    private AuthorizationResponse getAuthorizationResponse(AuthorizationRequest authorizationRequest, Transaction transaction, Amount currentAmount, TransactionAmount transactionAmount, String responseCode) {
        transaction.setAmount(currentAmount);
        transactionRepos.save(transaction);
        AuthorizationResponse authorizationResponse = new AuthorizationResponse();
        authorizationResponse.setUserId(authorizationRequest.getUserId());
        authorizationResponse.setMessageId(authorizationRequest.getMessageId());
        transactionAmount.setAmount(currentAmount.getAmount());
        authorizationResponse.setBalance(transactionAmount);
        authorizationResponse.setResponseCode(responseCode);
        return authorizationResponse;
    }

    private Transaction mapDto(Amount currentAmount, TransactionAmount transactionAmount, String responseCode) {
        Transaction transaction = new Transaction();
        transaction.setBalance( currentAmount.getAmount() );
        transaction.setCurrency(transactionAmount.getCurrency());
        transaction.setAmount(currentAmount);
        transaction.setTransactionAmount(transactionAmount.getAmount());
        transaction.setDebitOrCredit(DebitCredit.valueOf(transactionAmount.getDebitOrCredit()));
        transaction.setResponseCode(ResponseCode.valueOf(responseCode));
        return transaction;
    }
}
