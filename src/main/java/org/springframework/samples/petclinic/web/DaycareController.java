package org.springframework.samples.petclinic.web;

import java.util.Map;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Daycare;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.service.DaycareService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DaycareController {
	
	private PetService petService;
	
	private final DaycareService daycareService;
	
	@Autowired
	public DaycareController(PetService petService,final DaycareService daycareService) {
		this.petService = petService;
		this.daycareService = daycareService;
	}
	  
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	//@ModelAttribute("daycare")
	//public Daycare loadPetWithDaycare(@PathVariable("petId") int petId) {
	//	Pet pet = this.petService.findPetById(petId);
	//	Daycare daycare = new Daycare();
	//	pet.addDaycare(daycare);
	//	return daycare;
	//}
	
	@GetMapping(value= "/owners/{ownerId}/pets/{petId}/deleteDaycare/{daycareId}")
	public String deleteDaycare(@PathVariable("ownerId") int ownerId, @PathVariable("daycareId") final int daycareId, final Pet pet, final ModelMap model) {
		System.out.println("DELETE");
		this.daycareService.delete(daycareId);
		System.out.println("END DELETE");
		return "redirect:/owners/" + ownerId;
	}
  
	@GetMapping(value = "/owners/*/pets/{petId}/daycares/new")
	public String initNewDaycareForm(@PathVariable("petId") int petId, Map<String, Object> model) {
		Pet pet = this.petService.findPetById(petId);
		Daycare daycare = new Daycare();
		pet.addDaycare(daycare);
		model.put("daycare", daycare);
		return "daycares/createOrUpdateDaycareForm";
	}
	
	@PostMapping(value = "/owners/{ownerId}/pets/{petId}/daycares/new")
	public String processNewDaycareForm(@PathVariable("petId") int petId, @Valid Daycare daycare, BindingResult result) {
		Pet pet = this.petService.findPetById(petId);
		daycare.setPet(pet);
		if (result.hasErrors()) {
			return "daycares/createOrUpdateDaycareForm";
		}
		else {
			this.petService.saveDaycare(daycare);	
			return "redirect:/owners/{ownerId}";
		}
	}
	
	@GetMapping(value = "/owners/{ownerId}/pets/{petId}/daycares/{daycareId}/edit")
	public String initUpdateDaycareForm(@PathVariable("petId") int petId, @PathVariable("daycareId") int daycareId, Model model) {
		Daycare daycare = this.daycareService.findDaycareById(daycareId);
		Pet pet = this.petService.findPetById(petId);
		daycare.setPet(pet);
		model.addAttribute(daycare);
		return "daycares/createOrUpdateDaycareForm";
	}

	@PostMapping(value = "/owners/{ownerId}/pets/{petId}/daycares/{daycareId}/edit")
	public String processUpdateDaycareForm(@PathVariable("petId") int petId, @Valid Daycare daycare, BindingResult result,
			@PathVariable("daycareId") int daycareId) {
		Pet pet = this.petService.findPetById(petId);
		daycare.setPet(pet);
		if (result.hasErrors()) {
			return "daycares/createOrUpdateDaycareForm";
		}
		else {
			System.out.println("hola");
			daycare.setId(daycareId);
			// Creo que es en la siguiente linea donde peta:
			daycareService.saveDaycare(daycare);
			System.out.println("ase");
			return "redirect:/owners/{ownerId}";
		}
	}
	
	@GetMapping(value = "/owners/*/pets/{petId}/daycares")
	public String showDaycares(@PathVariable int petId, Map<String, Object> model) {
		model.put("daycares", this.petService.findPetById(petId).getDaycares());
		return "daycareList";
	}
	
	@GetMapping(value = "/owners/{ownerId}/pets/{petId}/daycares/{daycareId}")
	public ModelAndView showDaycare(@PathVariable("daycareId") int daycareId) {
		ModelAndView mav = new ModelAndView("daycares/daycareDetails");
		mav.addObject(this.daycareService.findDaycareById(daycareId));
		return mav;
	}
}