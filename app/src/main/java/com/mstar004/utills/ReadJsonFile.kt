package com.mstar004.utills

import android.os.Environment
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

class ReadJsonFile {
    fun readFile() {
        try {
            val yourFile = File(Environment.getExternalStorageDirectory(),
                "path/to/the/file/inside_the_sdcard/textarabics.txt")
            val stream = FileInputStream(yourFile)
            var jsonStr: String? = null
            try {
                val fc: FileChannel = stream.channel
                val bb: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
                jsonStr = Charset.defaultCharset().decode(bb).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stream.close()
            }
            /*  String jsonStr = "{\n\"data\": [\n    {\n        \"id\": \"1\",\n        \"title\": \"Farhan Shah\",\n        \"duration\": 10\n    },\n    {\n        \"id\": \"2\",\n        \"title\": \"Noman Shah\",\n        \"duration\": 10\n    },\n    {\n        \"id\": \"3\",\n        \"title\": \"Ahmad Shah\",\n        \"duration\": 10\n    },\n    {\n        \"id\": \"4\",\n        \"title\": \"Mohsin Shah\",\n        \"duration\": 10\n    },\n    {\n        \"id\": \"5\",\n        \"title\": \"Haris Shah\",\n        \"duration\": 10\n    }\n  ]\n\n}\n";
  */
            val jsonObj = JSONObject(jsonStr)

            // Getting data JSON Array nodes
            val data: JSONArray = jsonObj.getJSONArray("data")

            // looping through All nodes
            for (i in 0 until data.length()) {
                val c: JSONObject = data.getJSONObject(i)
                val name: String = c.getString("name")
                val number: String = c.getString("number")
                //use >  int id = c.getInt("duration"); if you want get an int

                // tmp hashmap for single node
                val parsedData = HashMap<String, String>()

                // adding each child node to HashMap key => value
                parsedData["name"] = name
                parsedData["number"] = number


                // do what do you want on your interface
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}