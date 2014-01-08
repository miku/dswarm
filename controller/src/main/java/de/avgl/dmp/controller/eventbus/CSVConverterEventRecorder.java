package de.avgl.dmp.controller.eventbus;

import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.avgl.dmp.converter.DMPConverterException;
import de.avgl.dmp.converter.flow.CSVResourceFlowFactory;
import de.avgl.dmp.converter.flow.CSVSourceResourceTriplesFlow;
import de.avgl.dmp.persistence.DMPPersistenceException;
import de.avgl.dmp.persistence.model.internal.impl.MemoryDBInputModel;
import de.avgl.dmp.persistence.model.resource.DataModel;
import de.avgl.dmp.persistence.service.InternalModelServiceFactory;

@Singleton
public class CSVConverterEventRecorder {

	private final InternalModelServiceFactory	internalServiceFactory;

	@Inject
	public CSVConverterEventRecorder(final InternalModelServiceFactory internalServiceFactory, final EventBus eventBus) {

		this.internalServiceFactory = internalServiceFactory;
		eventBus.register(this);
	}

	@Subscribe
	public void convertConfiguration(final CSVConverterEvent event) {
		final DataModel dataModel = event.getDataModel();

		List<org.culturegraph.mf.types.Triple> result = null;
		try {
			final CSVSourceResourceTriplesFlow flow = CSVResourceFlowFactory.fromDataModel(dataModel, CSVSourceResourceTriplesFlow.class);

			final String path = dataModel.getDataResource().getAttribute("path").asText();
			result = flow.applyFile(path);

		} catch (DMPConverterException | NullPointerException e) {
			e.printStackTrace();
		}

		if (result != null) {
			for (final org.culturegraph.mf.types.Triple triple : result) {

				final MemoryDBInputModel mdbim = new MemoryDBInputModel(triple);

				try {

					internalServiceFactory.getMemoryDbInternalService().createObject(dataModel.getId(), mdbim);
				} catch (final DMPPersistenceException e) {

					e.printStackTrace();
				}
			}
		}
	}
}
