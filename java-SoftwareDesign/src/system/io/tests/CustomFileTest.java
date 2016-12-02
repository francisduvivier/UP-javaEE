package system.io.tests;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import system.io.CustomFile;

public class CustomFileTest {
	
	@Test
	public void testWriteObject() {
		CustomFile.writeObject("file.txt", 5);
	}
	
	@Test
	public void testWriteObjectException() {
		CustomFile.writeObject("file.txt", new Object());
	}
	
	@Test
	public void testGetObjectException() {
		CustomFile.getObject("file.txt");
	}
	
	@Test
	public void testGetObject() {
		CustomFile.writeObject("file.txt", 5);
		CustomFile.getObject("file.txt");
	}
	
	@Test
	public void testWriteTxt() {
		CustomFile.writeTxt("file.txt", "test");
	}
	
	@Test
	public void testWriteTxtException() {
		CustomFile.writeTxt("file.txt", null);
	}
	
	@After
	public void deleteFile() {
		File file = new File("file.txt");
		file.delete();
	}

}
