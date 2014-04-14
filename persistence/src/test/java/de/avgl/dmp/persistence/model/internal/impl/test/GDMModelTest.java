package de.avgl.dmp.persistence.model.internal.impl.test;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.Charsets;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import de.avgl.dmp.graph.json.Model;
import de.avgl.dmp.graph.json.util.Util;
import de.avgl.dmp.persistence.model.internal.gdm.GDMModel;
import de.avgl.dmp.persistence.util.DMPPersistenceUtil;

/**
 * @author tgaengler
 */
public class GDMModelTest {

	@Test
	public void testToJSON() {

		testToJSONInternal("test-mabxml.gson", "http://data.slub-dresden.de/records/e9e1fa5a-3350-43ec-bb21-6ccfa90a4497", "test-mabxml.json");
	}

	@Test
	public void testToJSON2() {

		testToJSONInternal("test-complex-xml.gson", "http://data.slub-dresden.de/records/7fc13720-2859-477d-9127-5ec65f82220e",
				"test-complex-xml.json");
	}

	@Test
	public void testGetSchema() {

		testGetSchemaInternal("test-mabxml.gson", "http://data.slub-dresden.de/records/e9e1fa5a-3350-43ec-bb21-6ccfa90a4497", "mabxml_schema.json");
	}

	private void testToJSONInternal(final String fileName, final String resourceURI, final String expectedFileName) {

		// prepare
		final ObjectMapper mapper = Util.getJSONObjectMapper();

		final URL fileURL = Resources.getResource(fileName);

		Assert.assertNotNull(fileURL);

		String fileContent = null;
		try {
			fileContent = Resources.toString(fileURL, Charsets.UTF_8);
		} catch (IOException e1) {

			Assert.assertFalse(true);
		}

		Assert.assertNotNull(fileContent);

		Model model = null;
		try {
			model = mapper.readValue(fileContent, Model.class);
		} catch (JsonParseException e1) {
			Assert.assertFalse(true);
		} catch (JsonMappingException e1) {
			Assert.assertFalse(true);
		} catch (IOException e1) {
			Assert.assertFalse(true);
		}

		Assert.assertNotNull(model);

		final GDMModel gdmModel = new GDMModel(model, resourceURI);
		final JsonNode jsonNode = gdmModel.toJSON();

		String jsonString = null;
		try {
			jsonString = DMPPersistenceUtil.getJSONObjectMapper()
			// .configure(SerializationFeature.INDENT_OUTPUT, true)
					.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}

		Assert.assertNotNull("the JSON string shouldn't be null", jsonString);

		// System.out.println(jsonString);

		String expectedJsonString = null;

		try {

			expectedJsonString = DMPPersistenceUtil.getResourceAsString(expectedFileName);
		} catch (IOException e) {

			e.printStackTrace();
		}

		Assert.assertNotNull("the JSON string shouldn't be null", expectedJsonString);

		Assert.assertEquals("the lengths of transformed JSON for FE should be equal", expectedJsonString.length(), jsonString.length());

		Assert.assertEquals("the content of transformed JSON for FE should be equal", expectedJsonString, jsonString);
	}

	private void testGetSchemaInternal(final String fileName, final String resourceURI, final String expectedFileName) {

		// prepare
		final ObjectMapper mapper = Util.getJSONObjectMapper();

		final URL fileURL = Resources.getResource(fileName);

		Assert.assertNotNull(fileURL);

		String fileContent = null;
		try {
			fileContent = Resources.toString(fileURL, Charsets.UTF_8);
		} catch (IOException e1) {

			Assert.assertFalse(true);
		}

		Assert.assertNotNull(fileContent);

		Model model = null;
		try {
			model = mapper.readValue(fileContent, Model.class);
		} catch (JsonParseException e1) {
			Assert.assertFalse(true);
		} catch (JsonMappingException e1) {
			Assert.assertFalse(true);
		} catch (IOException e1) {
			Assert.assertFalse(true);
		}

		Assert.assertNotNull(model);

		final GDMModel gdmModel = new GDMModel(model, resourceURI);
		final JsonNode jsonNode = gdmModel.getSchema();

		String jsonString = null;
		try {
			jsonString = DMPPersistenceUtil.getJSONObjectMapper()
			// .configure(SerializationFeature.INDENT_OUTPUT, true)
					.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}

		Assert.assertNotNull("the schema JSON string shouldn't be null", jsonString);

		// System.out.println(jsonString);

		String expectedJsonString = null;

		try {

			expectedJsonString = DMPPersistenceUtil.getResourceAsString(expectedFileName);
		} catch (IOException e) {

			e.printStackTrace();
		}

		Assert.assertNotNull("the schema JSON string shouldn't be null", expectedJsonString);

		Assert.assertEquals("the lengths of the schema JSON should be equal", expectedJsonString.length(), jsonString.length());

		Assert.assertEquals("the content of the schema JSON should be equal", expectedJsonString, jsonString);
	}
}
