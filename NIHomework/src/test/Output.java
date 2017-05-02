package test;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Output implements Closeable {
	private OutputStreamWriter out;

	public final String HEADER = "ID,PARENT ID,LEVEl,URL,NUMBER OF LINKS \n";

	public Output(String fileName) {
		try {
			out = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8");
			out.write(HEADER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(String param) {
		try {
			out.write(param);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
