package org.springframework.samples.petclinic.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.Hairdressing;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.TipoCuidado;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.service.AuthorizationService;
import org.springframework.samples.petclinic.service.HairdressingService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

@WebMvcTest(controllers = HairdressingController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)

public class HairdressingControllerTests {

	private static final int HAIRDRESSING_ID = 99;

	private static final int PET_ID = 1;

	private static final int OWNER_ID = 1;

	@MockBean
	private PetService petService;

	@MockBean
	private HairdressingService hairdressingService;

	@MockBean
	private AuthorizationService authorizationService;

	@MockBean
	private Authentication auth;

	@MockBean
	private SecurityContext securityContext;

	@Autowired
	private HairdressingController hairdressingController;

	@Autowired
	private MockMvc mockMvc;

	private Pet pet;
	
	private Hairdressing hairdressing;

	@BeforeEach
	void setUp() {
		hairdressing = new Hairdressing();
		pet = new Pet();

		hairdressing.setId(HAIRDRESSING_ID);
		hairdressing.setCuidado(TipoCuidado.PELUQUERIA);
		hairdressing.setDescription("TEST");
		hairdressing.setPet(pet);
		hairdressing.setDate(LocalDate.of(2022, 02, 02));
		hairdressing.setTime("9.00");

		pet.setBirthDate(LocalDate.now());
		pet.setId(PET_ID);
		pet.setName("elpotro");

		given(this.petService.findPetById(PET_ID)).willReturn(this.pet);
//		given(this.hairdressingService.findHairdressingById(HAIRDRESSING_ID)).willReturn(this.hairdressing);

		given(securityContext.getAuthentication()).willReturn(auth);
		SecurityContextHolder.setContext(securityContext);
	}

	@WithMockUser(value = "spring")
	@Test
	public void testInitNewHairdressingForm() throws Exception {
		given(this.authorizationService.canUserModifyBooking(anyString(), eq(this.PET_ID))).willReturn(true);

		mockMvc.perform(get("/hairdressings/new", PET_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("hairdressingDTO"))
				.andExpect(view().name("hairdressings/createOrUpdateHairdressingForm"));
	}

	@WithMockUser(value = "spring")
	@Test // TODO
	public void testProcessNewHairdressingForm() throws Exception {
		mockMvc.perform(
				post("/owners/{ownerId}/pets/{petId}/hairdressing/new", OWNER_ID, PET_ID).param("date", "2024-04-04")
						.param("description", "TESTO").param("type", "ESTETICA").param("time", "6:00").with(csrf()))
				.andExpect(status().is2xxSuccessful());
//				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@WithMockUser(value = "spring")
	@Test // TODO
	public void testInitUpdateHairdressingForm() throws Exception {
		given(this.authorizationService.canUserModifyBooking(anyString(), eq(this.PET_ID))).willReturn(true);

		mockMvc.perform(get("")).andExpect(status().isOk()).andExpect(view().name(""));
	}

	@WithMockUser(value = "spring")
	@Test // TODO
	public void testProcessUpdateHairdressingForm() throws Exception {
		given(this.authorizationService.canUserModifyBooking(anyString(), eq(this.PET_ID))).willReturn(true);

		mockMvc.perform(post("").with(csrf()).param("", "")).andExpect(model().attributeHasErrors(""))
				.andExpect(status().isOk()).andExpect(view().name(""));
	}

	public void testShowHairdressing() throws Exception {
		given(this.authorizationService.canUserModifyBooking(anyString(), eq(this.PET_ID))).willReturn(true);

		mockMvc.perform(get("")).andExpect(status().isOk()).andExpect(view().name(""));
	}

	public void testShowHairdressingList() throws Exception {
		mockMvc.perform(get("/hairdressings")).andExpect(view().name("/hairdressings/hairdressingList"))
				.andExpect(status().isOk()).andExpect(model().attributeExists("results"));
	}
	
	public void testDeleteHairdressingForm() throws Exception{
		given(this.hairdressingService.findHairdressingById(HAIRDRESSING_ID)).willReturn(this.hairdressing);
	   	given(this.authorizationService.canUserModifyBooking(anyString(), eq(this.PET_ID))).willReturn(true);
		
		mockMvc.perform(get( "/hairdressings/{hairdressingId}/delete", this.HAIRDRESSING_ID)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/hairdressings"));
	}
	
	@WithMockUser(value = "spring", authorities = "admin")
	@Test
	void testProcessDeleteFormUnauthorized() throws Exception {
    	
    	given(this.authorizationService.canUserModifyBooking(anyString(), eq(PET_ID))).willReturn(false);
    	
		mockMvc.perform(get("/hairdressings/{hairdressingId}/delete", HAIRDRESSING_ID)
							.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("errors/accessDenied"));
	}

	// @WithMockUser(value = "spring")
	// public void testDeleteHairdressing() throws Exception {

	// }

	// @WithMockUser(value = "spring")
	// @Test
	/// public void testPopulateTiposCuidado() throws Exception {

	// }
}
