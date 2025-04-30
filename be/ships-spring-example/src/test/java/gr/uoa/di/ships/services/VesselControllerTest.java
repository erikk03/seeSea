package gr.uoa.di.ships.services;

import gr.uoa.di.ships.controllers.VesselController;
import gr.uoa.di.ships.persistence.repository.VesselRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = VesselController.class,  excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class VesselControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VesselRepository vesselRepository;

//    @Test
//    void getVessels() throws Exception {
//        Vessel vessel1 = new Vessel("1133435535", "cargo");
//        Vessel vessel2 = new Vessel("1133435536", "fishing");
//
//        Mockito.when(vesselRepository.findAll()).thenReturn(List.of(vessel1, vessel2));
//        mockMvc.perform(get("/vessels"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()", is(2)))
//                .andExpect(jsonPath("$[0].mmsi", is("1133435535")))
//                .andExpect(jsonPath("$[0].type", is("cargo")))
//                .andExpect(jsonPath("$[1].mmsi", is("1133435536")))
//                .andExpect(jsonPath("$[1].type", is("fishing")));
//    }
//
//    @Test
//    void getVessel() throws Exception {
//        Vessel vessel1 = new Vessel("1133435535", "cargo");
//
//        Mockito.when(vesselRepository.findById("1133435535")).thenReturn(Optional.of(vessel1));
//        mockMvc.perform(get("/vessels/1133435535"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.mmsi", is("1133435535")))
//                .andExpect(jsonPath("$.type", is("cargo")));
//    }
//
//    @Test
//    void createVessel() throws Exception {
//        Vessel vessel = new Vessel("1133435537", "tanker");
//
//        Mockito.when(vesselRepository.save(Mockito.any(Vessel.class))).thenReturn(vessel);
//
//        mockMvc.perform(
//                        post("/vessels")
//                                .contentType("application/json")
//                                .content("""
//                            {
//                                "mmsi": "1133435537",
//                                "type": "tanker"
//                            }
//                            """)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.mmsi", is("1133435537")))
//                .andExpect(jsonPath("$.type", is("tanker")));
//    }

}
