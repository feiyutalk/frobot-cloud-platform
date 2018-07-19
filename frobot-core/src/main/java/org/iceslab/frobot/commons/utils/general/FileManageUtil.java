package org.iceslab.frobot.commons.utils.general;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * File Utils for basic file operation.
 * @auther Neuclil
 */
public class FileManageUtil {
	private static final Logger LOGGER = Logger.getLogger(FileManageUtil.class);

	private FileManageUtil() {
		throw new AssertionError();
	}

	/**
	 * Get bytes from input file
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File inputFile) {
		InputStream is = null;
		try {
			is = new FileInputStream(inputFile);
			long length = inputFile.length();
			if (length > Integer.MAX_VALUE) {
				LOGGER.warn("file is too large, read error");
			}
			int offset = 0;
			int numRead = 0;
			byte[] bytes = new byte[(int) length];
			while ((offset < bytes.length) && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				LOGGER.warn("can't read the complete file.");
			}
			return bytes;
		} catch (FileNotFoundException e) {
			LOGGER.error("read the file date stream, occur exception ", e);
		} catch (IOException e) {
			LOGGER.error("read file error", e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				LOGGER.error("close file stream error！");
			}
		}
		return null;
	}

	/**
	 * 写文件
	 * @param bytes
	 * @param outputFile
	 */
	public static boolean writeBytesToFile(byte[] bytes, String outputFile){
		File file = new File(outputFile);
		file.delete();
		if (file.exists()) {
			return false;
		}
		return writeBytesToFile(bytes, file);
	}

	public static boolean writeBytesToFile(byte[] bytes, File outputFile) {
		FileOutputStream fileStream = null;
		BufferedOutputStream stream = null;
		try {
			fileStream = new FileOutputStream(outputFile);
			stream = new BufferedOutputStream(fileStream);
			stream.write(bytes);
			return true;
		} catch (FileNotFoundException fnf) {
			LOGGER.debug("write byte[] to file error, the exception is: ",fnf);
		} catch (IOException io) {
			LOGGER.debug("write byte[] to file error, the exception is: ",io);
		} finally {
			try {
				if (stream != null)
					stream.close();
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (IOException e) {
				LOGGER.debug("close file stream error");
			}
		}
		return false;
	}

	/**
	 * delete all file and directory in the root path , the root directory will also
	 * be deleted.
	 * for example, if you want to delete all file in root directory, such as:
	 *             <p>
	 *             root/home/allen/1.file
	 *             root/home/allen/2.file
	 *             root/home/allen/...
	 *             root/usr/readme.doc
	 *             </p>
	 *             then, all the file and directory(including 1.file, 2.file, read.doc
	 *             /allen,  /home/allen/, root/) will be delete.(root directory will also
	 *             be deleted).
	 * @param path the root path to delete.
	 */
	public static void deleteAll(String path) {
		File file = new File(path);
		if(file.exists()){
			if(file.isDirectory()){
				for(String str: file.list()){
					String cpath = path + File.separator + str;
					File cfile = new File(path);
					if(cfile.isDirectory())
						deleteAll(cpath);
					else
						cfile.delete();
				}
				file.delete();
			}else{
				file.delete();
			}
		}
	}

	/**
	 * 遍历一个文件夹中的所有扩展名为extension的文件 不访问文件夹的子文件夹
	 * 
	 * @param path
	 *            (eg: "/home/whkeep/config")
	 * @param extension
	 * @return 文件路径集合
	 */
	public static Map<String,String> getFileFromFolder(String path, String extension) {
		File file = new File(path);
		File content[] = file.listFiles();
		Map<String,String> fileMap = new HashMap<>();
		if (content != null && content.length != 0) {
			for (int i = 0; i < content.length; i++) {
				if (!content[i].isDirectory()) {
					if (content[i].getName().endsWith(extension)) {
						fileMap.put(content[i].getName(),content[i].getAbsolutePath());
					}
				}
			}
			if (fileMap.size() != 0) {
				return fileMap;
			} else {
				LOGGER.debug("The file with " + extension + " doesn't exist.");
			}
		} else {
			LOGGER.debug("Folder is empty.");
		}
		return null;
	}
	
	/**
	 * according to the path, generate a directory
	 * @param path the directory path to be generate.
	 */
	public static boolean generateDirectory(String path){
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		File folder = new File(path);
		folder.mkdirs();
		return folder.exists() && folder.isDirectory();
	}

	/**
	 *
	 * @param file
	 * @return
	 */
	public static boolean deleteFile(String file){
		File deleteFile = new File(file);
		deleteFile.delete();
		if (deleteFile.exists()) {
			LOGGER.debug("delete file error！");
			return false;
		}
		return true;
	}
	
	/**
	 * copy all files in a directory to other directoy
	 */
	public static void copy(String originDiretory, String targetDirectory) {
		File origindiretory = new File(originDiretory);
		File targetdirectory = new File(targetDirectory);
		if(!origindiretory.isDirectory()) {
			LOGGER.warn("wrong origin diretory");
			return;
		}else if(!targetdirectory.isDirectory()) {
			targetdirectory.mkdirs();
		}
		File[] fileList = origindiretory.listFiles();
		for(File file: fileList) {
			if(!file.isFile()) {
				continue;
			}
			LOGGER.warn(file.getName());
			try {
				FileInputStream fin = new FileInputStream(file);
				BufferedInputStream bin = new BufferedInputStream(fin);
				PrintStream pout = new PrintStream(targetdirectory.getAbsolutePath() + "/" + file.getName());
				BufferedOutputStream bout = new BufferedOutputStream(pout);
				
				while(bin.available()!=0) {
					int c = bin.read();
					bout.write((char)c);
				}
				bout.close();
				pout.close();
				bin.close();
				fin.close();
			}catch (IOException e) {
				LOGGER.error("copy error!",e);
			}
		}
	
	}
}