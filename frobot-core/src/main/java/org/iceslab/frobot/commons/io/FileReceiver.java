package org.iceslab.frobot.commons.io;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Neuclil on 17-4-10.
 */
public class FileReceiver {
    private static final Logger LOGGER = Logger.getLogger(FileReceiver.class);
    private FileReceiver(){}

    public static void receiveFile(String savePath, String ip, int port){
        Socket socket = null;
        try{
            System.out.println("receiveFile->  "+ip+":"+port);
            socket = new Socket(ip, port);
        }catch(UnknownHostException e){
            LOGGER.error("未知的发送方地址",e);
        }catch (IOException e){
            LOGGER.error("连接发送方出错！",e);
        }

        DataInputStream dis = null;
        try{
            dis = new DataInputStream(new BufferedInputStream(
                    socket.getInputStream()));
        }catch(IOException e){
            LOGGER.error("建立通信传输流出错!", e);
        }

        int bufferSize = 2*1024;
        byte[] buf = new byte[bufferSize];
        int passedlen = 0;
        long len = 0;
        DataOutputStream fileOut = null;
        try{
            String fileName = dis.readUTF();
            savePath = savePath + File.separator+  fileName;
            fileOut = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(savePath)));
            len = dis.readLong();
            LOGGER.debug("开始接收文件:" + fileName);
            LOGGER.debug("文件的长度为:" + len/1024 + " KB");
            while(true){
                int read = 0;
                if(dis != null){
                    read = dis.read(buf);
                }
                if(read == -1){
                    break;
                }
                passedlen += read;
                fileOut.write(buf, 0, read);
            }
            LOGGER.debug("文件接收完成,文件存为" + savePath);
            fileOut.close();
        }catch(Exception e){
            LOGGER.error("文件传输出错！");
            return;
        }finally {
            try{
                if(dis != null){
                    dis.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            try{
                if(fileOut != null){
                    fileOut.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
