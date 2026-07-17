package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.Account;
import com.example.demo.model.CreditAssessment;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerKycVault;
import com.example.demo.model.IdempotencyIntent;
import com.example.demo.model.TransactionRecord;
import com.example.demo.model.enums.AccountStatus;
import com.example.demo.model.enums.AccountType;
import com.example.demo.model.enums.CustomerStatus;
import com.example.demo.model.enums.IntentState;
import com.example.demo.model.enums.OperationType;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;
import com.example.demo.model.enums.VerificationStatus;
import com.example.demo.model.request.AccountMovementRequest;
import com.example.demo.model.request.TransferRequest;
import com.example.demo.service.AccountService;
import com.example.demo.service.CreditAssessmentService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.IntentService;
import com.example.demo.service.KycService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.AdminAnalyticsService;
import com.example.demo.config.DatabaseInitializer;
import com.example.demo.model.TransactionSummary;
import com.example.demo.model.LedgerMismatch;


@WebMvcTest(
        controllers = {
                CustomerController.class,
                AccountController.class,
                TransactionController.class,
                KycController.class,
                CreditAssessmentController.class,
                IntentController.class,
                AdminController.class,
                AdminAnalyticsController.class
        })
class WalletControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private KycService kycService;

    @MockBean
    private CreditAssessmentService creditAssessmentService;

    @MockBean
    private IntentService intentService;

    @MockBean
    private AdminAnalyticsService analyticsService;

    @MockBean
    private DatabaseInitializer databaseInitializer;

    @Test
    void shouldReturnCustomerById() throws Exception {
        given(customerService.getCustomerById(anyLong())).willReturn(Optional.of(new Customer(1L, "ext-1", "alice@example.com", "555-1234", "Alice", "Smith", null, null, CustomerStatus.ACTIVE)));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"customerId\":1,\"externalCustomerId\":\"ext-1\",\"email\":\"alice@example.com\"}"));
    }

    @Test
    void shouldReturnAccountByCustomerId() throws Exception {
        given(accountService.getAccountsByCustomer(anyLong())).willReturn(List.of(new Account(1L, 1L, AccountType.WALLET, "USD", null, null, null, AccountStatus.OPEN, null, null)));

        mockMvc.perform(get("/api/accounts/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'accountId':1,'customerId':1,'accountType':'WALLET'}]"));
    }

    /*
    @Test
    void shouldReturnTransactionsByCustomerId() throws Exception {
        given(transactionService.getTransactionsByCustomer(anyLong(), anyLong())).willReturn(List.of(new TransactionRecord(1L, 1L, 1L, null, TransactionType.PAYMENT, null, "USD", TransactionStatus.COMPLETED, null, null, null)));

        mockMvc.perform(get("/api/transactions/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'transactionId':1,'customerId':1,'transactionType':'PAYMENT'}]"));
    }
    */

    @Test
    void shouldReturnKycRecordsForCustomer() throws Exception {
        given(kycService.getKycRecordsForCustomer(anyLong())).willReturn(List.of(new CustomerKycVault(1L, 1L, "PASSPORT", "REF123", "/vault/ref123", VerificationStatus.PENDING, null, null)));

        mockMvc.perform(get("/api/kyc/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'kycId':1,'documentType':'PASSPORT'}]"));
    }

    @Test
    void shouldReturnCreditAssessmentsForCustomer() throws Exception {
        given(creditAssessmentService.getAssessmentsForCustomer(anyLong())).willReturn(List.of(new CreditAssessment(1L, 1L, 720, true, null, "SYSTEM", "{\"score\":720}")));

        mockMvc.perform(get("/api/credit-assessments/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'assessmentId':1,'score':720,'eligibility':true}]"));
    }

    @Test
    void shouldReturnIntentByKey() throws Exception {
        given(intentService.findByKey(anyString())).willReturn(Optional.of(new IdempotencyIntent(1L, "idemp-key-1", 1L, OperationType.PAYMENT, "hash", IntentState.COMPLETED, null, null)));

        mockMvc.perform(get("/api/intents/idemp-key-1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"idempotencyKey\":\"idemp-key-1\"}"));
    }

    @Test
    void shouldCreditAccountForSelf() throws Exception {
        given(accountService.creditAccount(anyLong(), any(AccountMovementRequest.class))).willReturn(new com.example.demo.model.response.MoneyMovementResponse(100L, "COMPLETED", "credited"));

        mockMvc.perform(post("/api/accounts/1/credit")
                        .header("Idempotency-Key", "credit-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.00,\"currency\":\"USD\",\"description\":\"top up\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionId\":100,\"status\":\"COMPLETED\"}"));
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() throws Exception {
        given(transactionService.transferMoney(any(TransferRequest.class))).willReturn(new com.example.demo.model.response.MoneyMovementResponse(200L, "COMPLETED", "transferred"));

        mockMvc.perform(post("/api/transactions/transfer")
                        .header("Idempotency-Key", "transfer-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderAccountId\":1,\"receiverAccountId\":2,\"amount\":75.50,\"currency\":\"USD\",\"description\":\"rent\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionId\":200,\"status\":\"COMPLETED\"}"));
    }

    @Test
    void shouldReturnAnalyticsSummary() throws Exception {
        given(analyticsService.getTransactionSummary(anyString())).willReturn(
                new TransactionSummary("weekly", 100L, new java.math.BigDecimal("50000.00"), 95L, 5L, 95.0, 5.0)
        );

        mockMvc.perform(get("/admin/analytics/txns/summary?range=weekly"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"range\":\"weekly\",\"totalCount\":100,\"totalVolume\":50000.00,\"successCount\":95,\"failureCount\":5,\"successRate\":95.0,\"failureRate\":5.0}"));
    }

    @Test
    void shouldTriggerDatabaseSeeding() throws Exception {
        mockMvc.perform(post("/admin/seed-db"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("database seeding triggered"));
    }

    @Test
    void shouldServeDashboardPage() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML));
    }
}

