package com.hyper.connect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupUtil{

    public static String getFileExtension(File file){
        String fileName=file.getName();
        if(fileName.lastIndexOf(".")!=-1 && fileName.lastIndexOf(".")!=0){
            return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        return "";
    }

    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException{
        if(fileToZip.isHidden()){
            return;
        }
        if(fileToZip.isDirectory()){
            if(fileName.endsWith("/")){
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            }
            else{
                zipOut.putNextEntry(new ZipEntry(fileName+"/"));
                zipOut.closeEntry();
            }
            File[] children=fileToZip.listFiles();
            for(File childFile : children){
                zipFile(childFile, fileName+"/"+childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis=new FileInputStream(fileToZip);
        ZipEntry zipEntry=new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes=new byte[1024];
        int length;
        while((length=fis.read(bytes))>=0){
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static void unzipFile(File zipFile){
        File file=new File("").getAbsoluteFile();
        File hyperFilesDir=new File(file, "hyper_files");
        hyperFilesDir.mkdir();
        new File(hyperFilesDir, "carrier").mkdir();
        new File(hyperFilesDir, "database").mkdir();
        new File(hyperFilesDir, "history").mkdir();
        new File(hyperFilesDir, "python").mkdir();

        File dir=file;

        FileInputStream fis;
        byte[] buffer=new byte[1024];
        try{
            fis=new FileInputStream(zipFile);
            ZipInputStream zis=new ZipInputStream(fis);
            ZipEntry ze=zis.getNextEntry();
            while(ze!=null){
                String fileName=ze.getName();

                if(fileName.equals("hyper_files/") || fileName.equals("hyper_files/carrier/") || fileName.equals("hyper_files/database/") || fileName.equals("hyper_files/history/") || fileName.equals("hyper_files/python/")){
                    //System.out.println("main directory");
                }
                else{
                    File newFile=new File(dir, fileName);
                    FileOutputStream fos=new FileOutputStream(newFile);
                    int len;
                    while((len=zis.read(buffer))>0){
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    zis.closeEntry();
                }
                ze=zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
