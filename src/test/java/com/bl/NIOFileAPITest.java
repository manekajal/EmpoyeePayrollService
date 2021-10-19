package com.bl;


import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.*;
import java.util.stream.IntStream;

public class NIOFileAPITest {

    private static String HOME=System.getProperty("user.home");
    private static String PLAY_WITH_NIO="TempPlayGround";

    @Test
    public void givenPathWhenCheckedThenConfire()throws IOException{

        //Chcek file exist
        Path homepath= Paths.get(HOME);
        Assert.assertTrue(Files.exists(homepath));

        //Delete File or Check File not Exist
        Path playpath=Paths.get(HOME +"/"+PLAY_WITH_NIO);
        if(Files.exists(playpath)) FileUtils.deleteFiles(playpath.toFile());

        //Create Directory
        Files.createDirectories(playpath);
        Assert.assertTrue(Files.exists(playpath));

        //Create File
        IntStream.range(1,10).forEach(cntr -> {
            Path tempFile=Paths.get(playpath +"/temp" +cntr);
            Assert.assertTrue(Files.notExists(tempFile));
        });

        Files.list(playpath).filter(Files::isRegularFile).forEach(System.out::println);
        Files.newDirectoryStream(playpath).forEach(System.out::println);
        Files.newDirectoryStream(playpath,path -> path.toFile().isFile() && path.toString().startsWith("temp"))
                .forEach(System.out::println);


    }
    @Test
    public void givenDirectoryWhenWatchedListsAllTheActivity()throws IOException
    {
        Path dir =Paths.get(HOME+ "/"+PLAY_WITH_NIO);
        Files.list(dir).filter(Files::isRegularFile).forEach(System.out::println);
        new Java8WatchServiceExample(dir).processEvents();
    }

}
