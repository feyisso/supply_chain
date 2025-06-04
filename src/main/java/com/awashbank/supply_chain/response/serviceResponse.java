package com.awashbank.supply_chain.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class serviceResponse extends response {
    private JsonNode data;
}
