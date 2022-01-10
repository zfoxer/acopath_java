
package acoserve;

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
import java.util.TreeMap;

public class AcoServe
{

    public static void main(String[] args)
    {
        parseInput(TOPO_FILENAME);
    }

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
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private static void writeOutput(String fileName)
    {
        //  TODO
    }

    private static Map<Pair<Integer, Integer>, Long> edge2distance = new TreeMap<>();

    public static final String TOPO_FILENAME = "topology.json";
}
