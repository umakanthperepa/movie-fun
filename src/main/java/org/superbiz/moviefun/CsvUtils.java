package org.superbiz.moviefun;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvUtils {

    private static String readFile(String fileName) {

        try {
            InputStream inputStream = CsvUtils.class.getClassLoader().getResourceAsStream(fileName);
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return "";
            }

        } catch (Exception e) {
            throw new RuntimeException("Error Reading from Classpath Resource",e);
        }
    }

    public static String readFileFromPath(String path) {
        try {

            Scanner scanner = new Scanner(new File(path)).useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return "";
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public static <T> List<T> readFromCsv(ObjectReader objectReader, String path) {
        try {
            List<T> results = new ArrayList<>();

            MappingIterator<T> iterator = objectReader.readValues(readFile(path));

            while (iterator.hasNext()) {
                results.add(iterator.nextValue());
            }

            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
