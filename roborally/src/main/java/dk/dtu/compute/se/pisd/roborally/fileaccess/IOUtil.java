/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import java.io.*;

/**
 * A utility class reading strings from resources and arbitrary input streams.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class IOUtil {

    /**
     * Reads a string from some InputStream. The solution is based
     * on google's Guava and a solution from Baeldung:
     * https://www.baeldung.com/convert-input-stream-to-string#guava
     *
     * @param inputStream the input stream
     * @return the string of the input stream
     */
    public static String readString(InputStream inputStream) {

        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return inputStream;
            }
        };

        try {
            return byteSource.asCharSource(Charsets.UTF_8).read();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Reads the content of a resource file from the given relative resource path.
     * This method uses the class loader to find and read the resource file, then converts the input stream to a string.
     * @author David Kasper Vilmann Wellejus s220218
     * @param relativeResourcePath the relative path to the resource file
     * @return String the content of the resource file as a string
     */

    public static String readResource(String relativeResourcePath) {
        ClassLoader classLoader = IOUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(relativeResourcePath);
        return IOUtil.readString(inputStream);
    }

}
