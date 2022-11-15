package com.iog;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Main implements Runnable {

	public static void main(String[] args)  {
		Thread thread = new Thread(new Main());
		thread.start();
	}

	@Override
	public void run() {
		Tika tika = new Tika();
		String destination = "./discord_cache";

		File directory = new File(destination);
		if (!directory.exists()){
			if (!directory.mkdir()) {
				System.err.println("ERROR: Couldn't create the destination folder in this directory");
				System.exit(-1);
			}
		}

		String user = System.getProperty("user.home");
		File folder = new File(user + "\\AppData\\Roaming\\discord\\Cache");
		File[] listOfFiles = folder.listFiles();

		float done = 0.0f;
		float p = 0;

		if (listOfFiles == null) {
			System.err.println("No files in discord cache folder");
			System.exit(-2);
		}

		for (File file : listOfFiles) {
			System.out.println((int)done + "% - " + file.getName());

			if (file.isFile()) {
				File new_file = new File(destination + "\\" + file.getName());

				try {
					Files.copy(file.toPath(), new_file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			p++;
			done = (p / (listOfFiles.length * 2)) * 100;
		}

		folder = new File(destination);
		listOfFiles = folder.listFiles();
		assert listOfFiles != null;


		for (File file : listOfFiles) {
			System.out.println((int)done + "% - " + file.getName());

			if (file.isFile()) {
				String format;
				try {
					format = tika.detect(file);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				format = format.substring(format.lastIndexOf("/") + 1);

				File new_file = new File(destination + "\\" + file.getName() +"."+ format);
				boolean ignored = file.renameTo(new_file);
			}
			p++;
			done = (p / (listOfFiles.length * 2)) * 100;
		}
		System.out.println("DONE - 100%");
	}
}
