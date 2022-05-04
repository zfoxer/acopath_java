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
     * Main programme entry with unused arguments.
     * @param args Not used for now.
     */
    public static void main(String[] args)
    {
        parseInput(TOPO_FILENAME);
        AntSystem as = new AntSystem(edge2distance);
        Vector<Integer> path = as.path(0, 5);
        System.out.println(path);
    }

    /**
     * Parses topology input.
     * @param fileName The JSON topology filename.
     */
    private static void parseInput(String fileName)
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
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
    }

    /*private static void writeOutput(String fileName)
    {
        //  TODO
    }*/

    /**
     * Mapping a Pair of nodes to their Long distance.
     */
    private static Map<Pair<Integer, Integer>, Long> edge2distance = new HashMap<>();

    /**
     * Filename containing the topology in JSON format.
     */
    public static final String TOPO_FILENAME = "topology.json";
}
