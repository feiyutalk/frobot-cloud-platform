package org.iceslab.frobot.commons.io;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Neuclil on 17-4-10.
 * 传输文件的类
 */
public class FileSender {
    private static final Logger LOGGER = Logger.getLogger(FileSender.class);
    private static ServerSocket ss = null;

    public FileSender() {
    }

    public void start(int port) throws IOException {
        ss = new ServerSocket(port);
    }

    public void sendFile(String filePath) {
        DataOutputStream dos = null;
        DataInputStream dis = null;
        Socket socket = null;
        try {
            File file = new File(filePath);
            socket = ss.accept();
            LOGGER.debug("连接成功,开始传输" + file.getName() + "....");
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(filePath)));

            int bufferSize = 2 * 1024;
            byte[] buffer = new byte[bufferSize];
            dos.writeUTF(file.getName());
            dos.flush();
            dos.writeLong((long) file.length());
            dos.flush();
            while (true) {
                int read = 0;
                if (dis != null) {
                    read = dis.read(buffer);
                }
                if (read == -1) {
                    break;
                }
                dos.write(buffer, 0, read);
            }
            dos.flush();
            LOGGER.debug(file.getName() + "传输成功!");
        } catch (FileNotFoundException e) {
            LOGGER.error("没有找到文件！", e);
        } catch (IOException e) {
            LOGGER.error("传输文件出错！", e);
        } finally {
            try {
                if (dos != null)
                    dos.close();
            } catch (IOException e) {
                LOGGER.error("关闭通信输出流出错！", e);
            }
            try {
                if (dis != null)
                    dis.close();
            } catch (IOException e) {
                LOGGER.error("关闭文件输入流出错", e);
            }
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                LOGGER.error("关闭socket出错!", e);
            }
        }
    }

    public void close() {
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
