package com.Construction.Materials.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.Construction.Materials.Model.MaterialEstimator;
import com.Construction.Materials.Service.MaterialEstimatorService;
import com.Construction.Materials.Dto.UserInputResponse;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialEstimatorService materialEstimatorService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // Endpoint to fetch material estimates based on inputId
    @GetMapping("/{inputId}")
    public ResponseEntity<List<MaterialEstimator>> getMaterialEstimates(
            @PathVariable Long inputId, Long materialId) {  // inputId to fetch material costs
            
                    // Fetch the materialQuality from the Input Microservice
                    String materialQuality = fetchMaterialQualityFromInput(inputId);
            
                    if (materialQuality == null) {
                        return ResponseEntity.badRequest().body(null);  // Return bad request if materialQuality is null
                    }
            
                    // Call service method to calculate material costs using the fetched materialQuality
                    List<MaterialEstimator> materials = materialEstimatorService.calculateMaterialCost(inputId, materialQuality, materialId);

        // Return the material estimates as the response
        return ResponseEntity.ok(materials);
    }

    // Method to fetch the materialQuality from the Input Microservice
    private String fetchMaterialQualityFromInput(Long inputId) {
        String userInputServiceUrl = "http://localhost:8080/api/inputs/getid/";

        // Call the Input Microservice to get the materialQuality
        UserInputResponse inputResponse = webClientBuilder.build()
                .get()
                .uri(userInputServiceUrl + inputId)
                .retrieve()
                .bodyToMono(UserInputResponse.class)
                .block();  // Blocking here to wait for the response (you can handle it asynchronously if needed)

        if (inputResponse != null) {
            return inputResponse.getMaterialQuality();  // Return the materialQuality from Input Microservice
        }
        return null;  // Return null if the response is not valid or inputId does not exist
    }
}
