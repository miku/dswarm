package de.avgl.dmp.controller.resources.test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.avgl.dmp.controller.resources.BasicResource;
import de.avgl.dmp.controller.resources.test.utils.AttributesResourceTestUtils;
import de.avgl.dmp.persistence.model.job.Component;
import de.avgl.dmp.persistence.model.schema.Attribute;
import de.avgl.dmp.persistence.service.schema.AttributeService;

public class AttributesResourceTest extends BasicResourceTest<AttributesResourceTestUtils, AttributeService, Attribute, Long> {

	private static final org.apache.log4j.Logger	LOG	= org.apache.log4j.Logger.getLogger(AttributesResourceTest.class);

	public AttributesResourceTest() {

		super(Attribute.class, AttributeService.class, "attributes", "attribute1.json", new AttributesResourceTestUtils());
	}

	@Test
	public void testUniquenessOfAttributes() {
		
		LOG.debug("start attribute uniqueness test");

		Attribute attribute1 = null;

		try {
			
			attribute1 = pojoClassResourceTestUtils.createObject(objectJSONString, expectedObject);
		} catch (Exception e) {
			
			LOG.error("coudln't create attribute 1 for uniqueness test");
			
			Assert.assertTrue(false);
		}
		
		Assert.assertNotNull("attribute 1 shouldn't be null in uniqueness test", attribute1);

		Attribute attribute2 = null;

		try {
			
			attribute2 = pojoClassResourceTestUtils.createObject(objectJSONString, expectedObject);
		} catch (Exception e) {
			
			LOG.error("coudln't create attribute 2 for uniqueness test");
			
			Assert.assertTrue(false);
		}
		
		Assert.assertNotNull("attribute 2 shouldn't be null in uniqueness test", attribute2);
		
		Assert.assertEquals("the attributes should be equal", attribute1, attribute2);
		
		cleanUpDB(attribute1);
		
		LOG.debug("end attribute uniqueness test");
	}
	
	@Override
	public void testPUTObject() throws Exception {

		LOG.debug("start attribute update test");

		Attribute attribute = null;

		try {
			
			attribute = pojoClassResourceTestUtils.createObject(objectJSONString, expectedObject);
		} catch (Exception e) {
			
			LOG.error("coudln't create attribute for update test");
			
			Assert.assertTrue(false);
		}
		
		Assert.assertNotNull("attribute shouldn't be null in update test", attribute);
		
		//modify attribute for update
		attribute.setName(attribute.getName() + " update");
		
		String attributeJSONString = objectMapper.writeValueAsString(attribute);
		
		Attribute updateAttribute = pojoClassResourceTestUtils.updateObject(attributeJSONString, attribute);
		
		Assert.assertEquals("the persisted attribute shoud be equal to the modified attribute for update", updateAttribute, attribute);
		
		ObjectNode attributeJSON = objectMapper.readValue(attributeJSONString, ObjectNode.class);
		
		Assert.assertNotNull("the attribut JSON shouldn't be null", attributeJSON);

		//uniqueness dosn't allow that
		attributeJSON.put("uri", attribute.getUri().replaceAll("http", "https"));
		
		attributeJSONString = objectMapper.writeValueAsString(attributeJSON);
		
		attribute = objectMapper.readValue(attributeJSONString, Attribute.class);
		
		updateAttribute = pojoClassResourceTestUtils.updateObject(attributeJSONString, attribute);
		
		Assert.assertNotEquals("uniqueness dosn't allow update of uri", updateAttribute.getUri(), attribute.getUri());
		
		//TODO: [@fniederlein] after attribute persistence adjustments the test have to check if a new attribute was created
		
		cleanUpDB(attribute);
		
		LOG.debug("end attribute update test");
	}
}
