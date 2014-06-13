package de.avgl.dmp.converter.flow.test.xml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Provider;
import de.avgl.dmp.converter.GuicedTest;
import de.avgl.dmp.converter.flow.TransformationFlow;
import de.avgl.dmp.persistence.model.job.Task;
import de.avgl.dmp.persistence.service.InternalModelServiceFactory;
import de.avgl.dmp.persistence.util.DMPPersistenceUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tgaengler
 *
 * Created by tgaengler on 16/05/14.
 */
public class XMLTransformationFlowTest extends GuicedTest {

	@Test
	public void testMabxmlComplexMapping() throws Exception {

		testXMLTaskWithTuples("mabxml_dmp.complex-mapping.result.json", "mabxml_dmp.complex-mapping.task.json", "mabxml_dmp.tuples.json");
	}

	@Test
	public void testMabxmlWFilterMapping() throws Exception {

		testXMLTaskWithTuples("mabxml_w_filter.task.result.json", "mabxml_w_filter.task.json", "test-mabxml.tuples.json");
	}

	@Test
	public void testMabxmlWFilterMappings() throws Exception {

		testXMLTaskWithTuples("tgtest_mabxml_mo_proj.task.result.json", "tgtest_mabxml_mo_proj.task.json", "test-mabxml.tuples.json");
	}
	
	@Test
	public void testMabxmlOneMappingWithFilterAndMultipleFunctions() throws Exception {

		testXMLTaskWithTuples("dd-528.mabxml.task.result.json", "dd-528.mabxml.task.json", "mabxml_dmp.tuples.json");
	}
	
	@Test
	public void testMetsmodsXmlWithFilterAndMapping() throws Exception {

		testXMLTaskWithTuples("metsmods_small.xml.task.result.json", "metsmods_small.xml.task.json", "metsmods_small.xml.tuples.json");
	}
	
	private void testXMLTaskWithTuples(final String taskResultJSONFileName, final String taskJSONFileName, final String tuplesJSONFileName) throws Exception {
		
		final String expected = DMPPersistenceUtil.getResourceAsString(taskResultJSONFileName);

		final Provider<InternalModelServiceFactory> internalModelServiceFactoryProvider = GuicedTest.injector
				.getProvider(InternalModelServiceFactory.class);

		final String finalTaskJSONString = DMPPersistenceUtil.getResourceAsString(taskJSONFileName);
		final ObjectMapper objectMapper = DMPPersistenceUtil.getJSONObjectMapper();

		// looks like that the utilised ObjectMappers getting a bit mixed, i.e., actual sometimes delivers a result that is not in
		// pretty print and sometimes it is in pretty print ... (that's why the reformatting of both - expected and actual)
		final ObjectMapper objectMapper2 = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).configure(
				SerializationFeature.INDENT_OUTPUT, true);

		final Task task = objectMapper.readValue(finalTaskJSONString, Task.class);

		final TransformationFlow flow = TransformationFlow.fromTask(task, internalModelServiceFactoryProvider);

		flow.getScript();

		final String actual = flow.applyResource(tuplesJSONFileName);
		final ArrayNode array = objectMapper2.readValue(actual, ArrayNode.class);
		final String finalActual = objectMapper2.writeValueAsString(array);

		final ArrayNode expectedArray = objectMapper2.readValue(expected, ArrayNode.class);
		final String finalExpected = objectMapper2.writeValueAsString(expectedArray);

		Assert.assertEquals(finalExpected.length(), finalActual.length());
		
	}
	
}
