package de.avgl.dmp.converter.flow;

import de.avgl.dmp.converter.reader.QucosaReader;
import de.avgl.dmp.converter.sink.ObjectBufferWriter;
import org.culturegraph.mf.morph.Metamorph;
import org.culturegraph.mf.stream.converter.JsonEncoder;
//import org.culturegraph.mf.stream.pipe.StreamFlattener;
import org.culturegraph.mf.stream.source.ResourceOpener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TransformationFlow {

	private static String flow(Metamorph transformer) {
		final ResourceOpener opener = new ResourceOpener();
		final QucosaReader reader = new QucosaReader();

//		final StreamFlattener flattener = new StreamFlattener();

		final JsonEncoder converter = new JsonEncoder();
		final ObjectBufferWriter writer = new ObjectBufferWriter();

		opener
				.setReceiver(reader)
//				.setReceiver(flattener)
				.setReceiver(transformer)
				.setReceiver(converter)
				.setReceiver(writer);

		opener.process("qucosa_record.xml");

		return writer.toString();
	}

	public static String flow(File file) throws FileNotFoundException {
		final FileInputStream is = new FileInputStream(file);
		final Metamorph transformer = new Metamorph(is);

		return flow(transformer);
	}

	public static void main(String[] args) {
		final InputStream morph = TransformationFlow.class.getClassLoader().getResourceAsStream("qucosa-morph.xml");
		final Metamorph transformer = new Metamorph(morph);

		System.out.println(flow(transformer));
	}
}
