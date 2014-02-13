package de.avgl.dmp.controller.resources.schema.test;

import org.junit.After;
import org.junit.Assert;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.avgl.dmp.controller.resources.job.test.utils.FiltersResourceTestUtils;
import de.avgl.dmp.controller.resources.schema.test.utils.AttributePathsResourceTestUtils;
import de.avgl.dmp.controller.resources.schema.test.utils.AttributesResourceTestUtils;
import de.avgl.dmp.controller.resources.schema.test.utils.MappingAttributePathInstancesResourceTestUtils;
import de.avgl.dmp.controller.resources.test.BasicResourceTest;
import de.avgl.dmp.persistence.model.job.Filter;
import de.avgl.dmp.persistence.model.schema.Attribute;
import de.avgl.dmp.persistence.model.schema.AttributePath;
import de.avgl.dmp.persistence.model.schema.MappingAttributePathInstance;
import de.avgl.dmp.persistence.model.schema.proxy.ProxyMappingAttributePathInstance;
import de.avgl.dmp.persistence.service.schema.MappingAttributePathInstanceService;
import de.avgl.dmp.persistence.service.schema.test.utils.MappingAttributePathInstanceServiceTestUtils;
import de.avgl.dmp.persistence.util.DMPPersistenceUtil;

public class MappingAttributePathInstancesResourceTest
		extends
		BasicResourceTest<MappingAttributePathInstancesResourceTestUtils, MappingAttributePathInstanceServiceTestUtils, MappingAttributePathInstanceService, ProxyMappingAttributePathInstance, MappingAttributePathInstance, Long> {

	private static final org.apache.log4j.Logger					LOG	= org.apache.log4j.Logger
																				.getLogger(MappingAttributePathInstancesResourceTest.class);

	private final AttributesResourceTestUtils						attributeResourceTestUtils;
	private final AttributePathsResourceTestUtils					attributePathResourceTestUtils;
	private final FiltersResourceTestUtils							filterResourceTestUtils;
	private final MappingAttributePathInstancesResourceTestUtils	mappingAttributePathInstanceResourceTestUtils;

	private Attribute												actualAttribute1;

	private Attribute												actualAttribute2;

	private Attribute												attributeFromUpdate;

	private AttributePath											attributePath;

	private Filter													filter;

	private Filter													filterFromUpdate;

	public MappingAttributePathInstancesResourceTest() {

		super(MappingAttributePathInstance.class, MappingAttributePathInstanceService.class, "mappingattributepathinstances",
				"mapping_attribute_path_instance.json", new MappingAttributePathInstancesResourceTestUtils());

		attributeResourceTestUtils = new AttributesResourceTestUtils();
		attributePathResourceTestUtils = new AttributePathsResourceTestUtils();
		filterResourceTestUtils = new FiltersResourceTestUtils();
		mappingAttributePathInstanceResourceTestUtils = new MappingAttributePathInstancesResourceTestUtils();
	}

	@Override
	public void prepare() throws Exception {

		super.prepare();

		actualAttribute1 = attributeResourceTestUtils.createObject("attribute1.json");
		actualAttribute2 = attributeResourceTestUtils.createObject("attribute2.json");

		// manipulate attribute path attributes
		String attributePathJSONString = DMPPersistenceUtil.getResourceAsString("attribute_path1.json");
		final ObjectNode attributePathJSON = objectMapper.readValue(attributePathJSONString, ObjectNode.class);

		final ArrayNode attributessArray = objectMapper.createArrayNode();

		final String attribute1JSONString = objectMapper.writeValueAsString(actualAttribute1);
		final ObjectNode attribute1JSON = objectMapper.readValue(attribute1JSONString, ObjectNode.class);

		final String attribute2JSONString = objectMapper.writeValueAsString(actualAttribute2);
		final ObjectNode attribute2JSON = objectMapper.readValue(attribute2JSONString, ObjectNode.class);

		attributessArray.add(attribute1JSON);
		attributessArray.add(attribute2JSON);
		attributessArray.add(attribute1JSON);

		attributePathJSON.put("attributes", attributessArray);

		attributePathJSONString = objectMapper.writeValueAsString(attributePathJSON);
		final AttributePath expectedAttributePath = objectMapper.readValue(attributePathJSONString, AttributePath.class);
		attributePath = attributePathResourceTestUtils.createObject(attributePathJSONString, expectedAttributePath);
		attributePathJSONString = objectMapper.writeValueAsString(attributePath);
		final ObjectNode finalAttributePathJSON = objectMapper.readValue(attributePathJSONString, ObjectNode.class);

		filter = filterResourceTestUtils.createObject("filter.json");
		final String filterJSONString = objectMapper.writeValueAsString(filter);
		final ObjectNode finalFilterJSON = objectMapper.readValue(filterJSONString, ObjectNode.class);

		final ObjectNode mappingAttributePathInstanceJSON = objectMapper.readValue(objectJSONString, ObjectNode.class);
		mappingAttributePathInstanceJSON.put("attribute_path", finalAttributePathJSON);
		mappingAttributePathInstanceJSON.put("filter", finalFilterJSON);

		// re-init expect object
		objectJSONString = objectMapper.writeValueAsString(mappingAttributePathInstanceJSON);
		expectedObject = objectMapper.readValue(objectJSONString, pojoClass);
	}

	@Override
	public void testPUTObject() throws Exception {

		super.testPUTObject();

		filterResourceTestUtils.deleteObject(filterFromUpdate);

		attributePathResourceTestUtils.deleteObject(attributePath);
		attributeResourceTestUtils.deleteObject(attributeFromUpdate);
	}

	@After
	public void tearDown2() throws Exception {

		if (attributePath != null) {

			attributePathResourceTestUtils.deleteObject(attributePath);
		}

		attributeResourceTestUtils.deleteObject(actualAttribute1);
		attributeResourceTestUtils.deleteObject(actualAttribute2);
		filterResourceTestUtils.deleteObject(filter);
	}

	@Override
	protected MappingAttributePathInstance updateObject(final MappingAttributePathInstance persistedMappingAttributePathInstance) throws Exception {

		// note: this is a bit messy/over-complicated here - do not reproduce!

		final AttributePath persistedAttributePath = persistedMappingAttributePathInstance.getAttributePath();

		attributeFromUpdate = attributeResourceTestUtils.createObject("attribute4.json");

		persistedAttributePath.removeAttribute(actualAttribute2, 2);
		persistedAttributePath.addAttribute(attributeFromUpdate);

		final String updatedAttributePathJSONString = objectMapper.writeValueAsString(persistedAttributePath);
		final ObjectNode updatedAttributePathJSON = objectMapper.readValue(updatedAttributePathJSONString, ObjectNode.class);

		String updatedMappingAttributePathInstanceJSONString = objectMapper.writeValueAsString(persistedMappingAttributePathInstance);
		final ObjectNode updatedMappingAttributePathInstanceJSON = objectMapper.readValue(updatedMappingAttributePathInstanceJSONString,
				ObjectNode.class);

		final String filterJSONString = DMPPersistenceUtil.getResourceAsString("filter3.json");
		final ObjectNode updateFilterJSON = objectMapper.readValue(filterJSONString, ObjectNode.class);

		// mapping attribute path instance name update
		final String updateMappingAttributePathInstanceNameString = persistedMappingAttributePathInstance.getName() + " update";
		updatedMappingAttributePathInstanceJSON.put("name", updateMappingAttributePathInstanceNameString);
		updatedMappingAttributePathInstanceJSON.put("filter", updateFilterJSON);
		updatedMappingAttributePathInstanceJSON.put("attribute_path", updatedAttributePathJSON);

		updatedMappingAttributePathInstanceJSONString = objectMapper.writeValueAsString(updatedMappingAttributePathInstanceJSON);

		final MappingAttributePathInstance expectedMappingAttributePathInstance = objectMapper.readValue(
				updatedMappingAttributePathInstanceJSONString, MappingAttributePathInstance.class);

		Assert.assertNotNull("the mapping attribute path instance JSON string shouldn't be null", updatedMappingAttributePathInstanceJSONString);

		final MappingAttributePathInstance updatedMappingAttributePathInstance = mappingAttributePathInstanceResourceTestUtils.updateObject(
				updatedMappingAttributePathInstanceJSONString, expectedMappingAttributePathInstance);

		filterFromUpdate = updatedMappingAttributePathInstance.getFilter();

		Assert.assertEquals("persisted and updated attribute should be equal",
				updatedMappingAttributePathInstance.getAttributePath().getAttribute(attributeFromUpdate.getId()), attributeFromUpdate);
		Assert.assertNotEquals("old-persisted and updated filter shouldn't be equal", updatedMappingAttributePathInstance.getFilter(), filter);
		Assert.assertEquals("persisted and updated mapping attribute path name should be equal", updatedMappingAttributePathInstance.getName(),
				updateMappingAttributePathInstanceNameString);

		return updatedMappingAttributePathInstance;
	}
}
