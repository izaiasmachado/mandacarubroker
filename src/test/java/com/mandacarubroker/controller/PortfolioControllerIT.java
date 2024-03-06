package com.mandacarubroker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandacarubroker.domain.auth.RequestAuthUserDTO;
import com.mandacarubroker.domain.auth.ResponseAuthUserDTO;
import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.service.AuthService;
import com.mandacarubroker.service.PortfolioService;
import com.mandacarubroker.service.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import java.time.LocalDate;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc()
public class PortfolioControllerIT {

    @MockBean
    private PortfolioService portfolioService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockService stockService;

    @Autowired
    private AuthService authService;

    private final RequestUserDTO validRequestUserDTO = new RequestUserDTO(
            "marcosloiola@yahoo.com",
            "Marcos22",
            "passmarco123",
            "Marcos",
            "Loiola",
            LocalDate.of(2002, 2, 26),
            0.25);
    private final User validUser = new User(validRequestUserDTO);
    private ResponseStockDTO stockToOperate;
    private final RequestStockOwnershipDTO shares = new RequestStockOwnershipDTO(2);
    private String urlRequestToBuyStock;
    private String urlRequestToSellStock;

    private RequestAuthUserDTO validRequestAuthUserDTO;

    @BeforeEach
    void setUp() {
        validRequestAuthUserDTO = new RequestAuthUserDTO(
                "admin",
                "admin"
        );
        stockToOperate = stockService.getAllStocks().get(0);

        urlRequestToBuyStock = "/portfolio/stock/" + stockToOperate.id() + "/buy";
        urlRequestToSellStock = "/portfolio/stock/" + stockToOperate.id() + "/buy";
    }

    @AfterEach
    void tearDown() {

    }


    @Test
    void itShouldReturnOkStatusAfterSucessfulBuyStock() throws Exception{

        Optional<ResponseAuthUserDTO> user = authService.login(validRequestAuthUserDTO);
        String token = user.get().token();

        String sharesJsonString = objectMapper.writeValueAsString(shares);


        RequestBuilder requestBuilder = post(urlRequestToBuyStock)
                .contentType("application/json")
                .header("Authorization", token)
                .content(sharesJsonString);
        ResultMatcher matchStatus = status().isOk();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);

    }



}
