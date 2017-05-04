package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Output file contains output operations: create, write and close the
 * <code>OutputStreamWriter</code>
 * 
 * @author Melinda, Gáborné Darvasi
 *
 */
public class CsvOutput implements SiteMapOutput {
	/**
	 * Log4j Logger for this class
	 */
	private static Logger logger = LogManager.getLogger(CsvOutput.class);

	/**
	 * <code>OutputStreamWriter</code> for the output csv file
	 */
	private OutputStreamWriter out;

	/**
	 * Header of the output csv file
	 */
	public final String HEADER = "ID,PARENT ID,LEVEl,URL,NUMBER OF LINKS \n";

	/**
	 * Creates a file output stream to write to the file with the given name.
	 * Firstly write the header string.
	 * 
	 * @param fileName
	 */
	public CsvOutput(String fileName) {
		try {
			out = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8");
			out.write(HEADER);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Create a string from the given parameters and call write method
	 */
	@Override
	public void write(Integer id, int parentId, final int level, final String url) {
		String pId = parentId < 0 ? "" : String.valueOf(parentId);
		write(id + "," + pId + "," + level + "," + url + ",\n");
	}

	/**
	 * Create a string from the given parameters and call write method
	 */
	@Override
	public void write(Integer id, int parentId, final int level, final String url, int urlListSize) {
		String pId = parentId < 0 ? "" : String.valueOf(parentId);
		write(id + "," + pId + "," + level + "," + url + "," + urlListSize + "\n");
	}

	/**
	 * Write the given string to the file
	 * 
	 * @param param
	 */
	private void write(String param) {
		try {
			// write the parameter in the file
			out.write(param);
			// flush the stream
			out.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Close the opened <code>OutputStreamWriter</code>
	 */
	@Override
	public void close() {
		try {
			// close the file
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
