package store.management.controllers;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import store.management.entities.SubCategory;

@RunWith(SpringRunner.class)
@SpringBootTest
class SubCategoryControllerTest {
	private MockMvc mockMvc;
	ObjectMapper om = new ObjectMapper();
	
	@Autowired
	private WebApplicationContext context;
	
	@BeforeEach
	public void setUp() {
		System.err.println("dsfa");
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	

	@Test
	public void addSubcategory() throws Exception{
		try {
			SubCategory subCategory = new SubCategory();
			subCategory.setCategoryId(1);
			subCategory.setSubCategoryName("Trimmer");
			
			String jsonRequest = om.writeValueAsString(subCategory);
			MvcResult result = mockMvc.perform(post("/subCategory").content(jsonRequest)
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()).andReturn();
			
			String resultContent = result.getResponse().getContentAsString();
			String response = om.readValue(resultContent, String.class);
			Assert.assertTrue(response.equalsIgnoreCase("Sub Category Added Successfully"));
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	@Test
	public void getSubcategories() throws Exception{
		MvcResult result = mockMvc
				.perform(get("/subCategories").content(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andReturn();
		String resultContent = result.getResponse().getContentAsString();
		List<SubCategory> subCategoryList = om.readValue(resultContent, List.class);
		Assert.assertTrue(subCategoryList != null);
	}

}
