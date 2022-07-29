/*
 * AcoPath for Java: Shortest path calculation using Ant Colony Optimization
 * Copyright (C) 2021-2022 by Constantine Kyriakopoulos
 * zfox@users.sourceforge.net
 * @version 1.0.2
 *
 * @section LICENSE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package acopath_java;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.lang.String;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

/**
 * Main entry point, the front-end.
 */
public class AcoServe
{
    /**
     * Hardcoded version number.
     */
    private static final String VERSION = "1.0.2";

    /**
     * Mapping a 'Pair' of nodes to their 'Long' distance.
     */
    private static Map<Pair<Integer, Integer>, Long> edge2distance = new HashMap<>();

    /**
     * Provides programme usage details.
     * @return String Usage details.
     */
    private static String usage()
    {
        return "AcoPath for Java " + VERSION
                + "\n(C) 2021-2022 by Constantine Kyriakopoulos"
                + "\nReleased under GNU GPL v2"
                + "\n\nUsage: java -jar acopath.jar [src node] [dest node] [topology file]\n";
    }

    /**
     * Main programme entry.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        if(args.length != 3)
        {
            System.out.println(usage());
            System.exit(0);
        }

        int nds[] = new int[args.length - 1];
        try
        {
            String topoFilename;
            int i = 0;
            while(i < args.length - 1)
                nds[i] = Integer.valueOf(args[i++]);
            topoFilename = args[i];
            parseInput(topoFilename);
        }
        catch(Exception e)
        {
            System.out.println(usage());
            System.exit(1);
        }

        AntSystem as = new AntSystem(edge2distance, AntSystem.ANTS, AntSystem.ITERATIONS);
        Vector<Integer> path = as.path(nds[0], nds[1]);
        System.out.println(path);
    }

    /**
     * Parses topology input.
     * @param fileName The JSON topology filename.
     * @throws IOException I/O failed.
     * @throws ParseException Parsing failed.
     */
    private static void parseInput(String fileName) throws IOException, ParseException
    {
        JSONParser parser = new JSONParser();

        try(Reader reader = new FileReader(fileName))
        {
            JSONObject jsonObj = (JSONObject)parser.parse(reader);
            JSONArray links = (JSONArray)jsonObj.get("links");
            Iterator<JSONObject> it = links.iterator();

            while(it.hasNext())
            {
                JSONObject data = (JSONObject)it.next();
                long length = (Long)data.get("length");

                String startStr = null;
                String endStr = null;

                JSONArray nodes = (JSONArray)data.get("nodes");
                Iterator<JSONObject> it2 = nodes.iterator();

                if(it2.hasNext())
                    startStr = "" + it2.next();
                if(it2.hasNext())
                    endStr = "" + it2.next();

                Pair<Integer, Integer> edge = new Pair<>(Integer.valueOf(startStr), Integer.valueOf(endStr));
                edge2distance.put(edge, length);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch(ParseException e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}
